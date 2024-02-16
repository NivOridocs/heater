package niv.heater;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Oxidizable.OxidationLevel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Propagator implements
        Supplier<Set<Propagator.Target>>,
        Iterable<Propagator.Target>,
        Runnable {

    public record Target(BlockPos pos, BlockState state, BurnerBlockEntity burner) {
    }

    private record Pipe(BlockPos pos, BlockState state, HeatPipeBlock block, int heat) {
    }

    private final World world;

    private final BlockPos startingPos;

    private final Function<BlockEntity, Optional<BurnerBlockEntity>> mapToBurner;

    private final Comparator<BurnerBlockEntity> comparator;

    private final ToIntFunction<OxidationLevel> mapToWaste;

    private final int maxHeat;

    private final Queue<Pipe> pipes;

    private final Set<Target> targets;

    private final Set<BlockPos> visited;

    public Propagator(World world, BlockPos startingPos, int maxHeat,
            Function<BlockEntity, Optional<BurnerBlockEntity>> mapToBurner,
            Comparator<BurnerBlockEntity> comparator, ToIntFunction<OxidationLevel> mapToWaste) {
        this.world = world;
        this.startingPos = startingPos;
        this.mapToBurner = mapToBurner;
        this.comparator = comparator;
        this.mapToWaste = mapToWaste;
        this.maxHeat = maxHeat;
        this.pipes = new LinkedList<>();
        this.targets = new TreeSet<>(this::compare);
        this.visited = new HashSet<>();
    }

    @Override
    public Set<Target> get() {
        return targets;
    }

    @Override
    public Iterator<Target> iterator() {
        return targets.iterator();
    }

    @Override
    public void run() {
        pipes.clear();
        targets.clear();
        visited.clear();

        for (var direction : Direction.values()) {
            visit(startingPos.offset(direction), maxHeat);
        }

        while (!pipes.isEmpty()) {
            var pipe = pipes.poll();
            for (var direction : HeatPipeBlock.getConnected(pipe.state())) {
                visit(pipe.pos().offset(direction),
                        pipe.heat() - mapToWaste.applyAsInt((pipe.block().getOxidationLevel())));
            }
        }
    }

    private void visit(BlockPos pos, int heat) {
        if (visited.contains(pos) || heat < 1) {
            return;
        }

        var state = world.getBlockState(pos);
        var block = state.getBlock();

        if (block instanceof HeaterBlock) {
            return;
        }

        if (block instanceof HeatPipeBlock pipe) {
            visited.add(pos);
            pipes.add(new Pipe(pos, state, pipe, heat));
        } else if (block instanceof BlockWithEntity) {
            var entity = Optional.ofNullable(world.getBlockEntity(pos)).flatMap(mapToBurner);
            if (entity.isPresent()) {
                visited.add(pos);
                targets.add(new Target(pos, state, entity.get()));
            }
        }
    }

    private int compare(Target a, Target b) {
        int result = comparator.compare(a.burner(), b.burner());
        if (result == 0) {
            result = a.pos().compareTo(b.pos());
        }
        return result;
    }

}
