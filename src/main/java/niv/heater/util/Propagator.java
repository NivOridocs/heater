package niv.heater.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import niv.heater.block.HeaterBlock;

public class Propagator implements
        Supplier<Set<Propagator.Target>>,
        Iterable<Propagator.Target>,
        Runnable {

    public record Target(BlockPos pos, BlockState state, HeatSink entity) {
    }

    private record Source(BlockPos pos, BlockState state, HeatSource block, int heat) {
    }

    private final LevelAccessor level;

    private final BlockPos startingPos;

    private final int maxHeat;

    private final Queue<Source> sources;

    private final Set<Target> targets;

    private final Set<BlockPos> visited;

    public Propagator(LevelAccessor level, BlockPos startingPos, int maxHeat) {
        this.level = level;
        this.startingPos = startingPos;
        this.maxHeat = maxHeat;
        this.sources = new LinkedList<>();
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
        sources.clear();
        targets.clear();
        visited.clear();

        visited.add(startingPos);

        var state = level.getBlockState(startingPos);
        if (state.getBlock() instanceof HeaterBlock heater) {
            sources.add(new Source(startingPos, state, heater, maxHeat));
        }

        while (!sources.isEmpty()) {
            var src = sources.poll();
            for (var dir : src.block().getConnected(src.state())) {
                visit(src, dir);
            }
        }
    }

    private void visit(Source src, Direction dir) {
        var pos = src.pos().relative(dir);
        var heat = src.block().reducedHeat(src.heat());
        if (!visited.contains(pos) && heat > 0) {
            var tryAsSource = src.block().getNeighborAsSource(level, src.pos(), dir);
            if (tryAsSource.isPresent()) {
                visited.add(pos);
                sources.add(new Source(pos, level.getBlockState(pos), tryAsSource.get(), heat));
                return;
            }
            var tryAsSink = src.block().getNeighborAsSink(level, src.pos(), dir);
            if (tryAsSink.isPresent()) {
                visited.add(pos);
                targets.add(new Target(pos, level.getBlockState(pos), tryAsSink.get()));
            }
        }
    }

    private int compare(Target a, Target b) {
        int result = HeatSink.compare(a.entity(), b.entity());
        if (result == 0) {
            result = a.pos().compareTo(b.pos());
        }
        return result;
    }

}
