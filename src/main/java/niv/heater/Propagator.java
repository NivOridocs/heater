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

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Propagator<E> implements
        Supplier<Set<Propagator.Target<E>>>,
        Iterable<Propagator.Target<E>>,
        Runnable {

    public record Target<E>(BlockPos pos, BlockState state, E entity) {
    }

    private record Pipe(BlockPos pos, BlockState state, int heat) {
    }

    private final World world;

    private final BlockPos startingPos;

    private final Function<BlockEntity, Optional<E>> mapToInstance;

    private final Comparator<E> comparator;

    private final int maxHeat;

    private final Queue<Pipe> pipes;

    private final Set<Target<E>> targets;

    private final Set<BlockPos> visited;

    public Propagator(World world, BlockPos startingPos, int maxHeat,
            Function<BlockEntity, Optional<E>> mapToInstance,
            Comparator<E> comparator) {
        this.world = world;
        this.startingPos = startingPos;
        this.mapToInstance = mapToInstance;
        this.comparator = comparator;
        this.maxHeat = maxHeat;
        this.pipes = new LinkedList<>();
        this.targets = new TreeSet<>(this::compare);
        this.visited = new HashSet<>();
    }

    @Override
    public Set<Target<E>> get() {
        return targets;
    }

    @Override
    public Iterator<Target<E>> iterator() {
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
                visit(pipe.pos().offset(direction), pipe.heat() - 1);
            }
        }

        Heater.LOGGER.info("Found targets: {}", targets);
    }

    private void visit(BlockPos pos, int heat) {
        if (visited.contains(pos) || heat < 1) {
            return;
        }

        var state = world.getBlockState(pos);
        var entity = world.getBlockEntity(pos);
        var block = state.getBlock();

        if (block instanceof HeatPipeBlock) {
            visited.add(pos);
            pipes.add(new Pipe(pos, state, heat));
        } else if (entity != null) {
            var cast = mapToInstance.apply(entity);
            if (cast.isPresent()) {
                visited.add(pos);
                targets.add(new Target<>(pos, state, cast.get()));
            }
        }
    }

    private int compare(Target<E> a, Target<E> b) {
        int result = comparator.compare(a.entity(), b.entity());
        if (result == 0) {
            result = a.pos().compareTo(b.pos());
        }
        return result;
    }

}
