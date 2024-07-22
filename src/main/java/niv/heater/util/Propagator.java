package niv.heater.util;

import static niv.heater.util.PropagatorUtils.getConnectedNeighbors;
import static niv.heater.util.PropagatorUtils.reduceHeat;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import niv.heater.api.Connector;
import niv.heater.api.Furnace;
import niv.heater.block.HeaterBlock;
import niv.heater.util.PropagatorUtils.ConnectorResult;
import niv.heater.util.PropagatorUtils.FurnaceResult;
import niv.heater.util.PropagatorUtils.Result;

public class Propagator implements
        Supplier<Set<Propagator.Target>>,
        Iterable<Propagator.Target>,
        Runnable {

    public record Target(BlockPos pos, Furnace entity) implements Comparable<Target> {

        @Override
        public int compareTo(Target that) {
            int result = Furnace.compare(this.entity(), that.entity());
            if (result == 0) {
                result = this.pos().compareTo(that.pos());
            }
            return result;
        }
    }

    private record Source(BlockPos pos, Connector block, int heat) {
    }

    private final BlockGetter getter;

    private final BlockPos startingPos;

    private final int maxHeat;

    private final Queue<Source> sources;

    private final Set<Target> targets;

    private final Set<BlockPos> visited;

    public Propagator(BlockGetter getter, BlockPos startingPos, int maxHeat) {
        this.getter = getter;
        this.startingPos = startingPos;
        this.maxHeat = maxHeat;
        this.sources = new LinkedList<>();
        this.targets = new TreeSet<>();
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

        var state = getter.getBlockState(startingPos);
        if (state.getBlock() instanceof HeaterBlock heater) {
            sources.add(new Source(startingPos, heater, maxHeat));
        }

        while (!sources.isEmpty()) {
            var src = sources.poll();
            getConnectedNeighbors(src.block(), getter, src.pos())
                    .forEach((dir, result) -> visit(src, dir, result));
        }
    }

    private void visit(Source src, Direction dir, Result result) {
        var pos = src.pos().relative(dir);
        var heat = reduceHeat(src.block(), src.heat());
        if (!visited.contains(pos) && heat > 0) {
            if (result instanceof ConnectorResult c) {
                visited.add(pos);
                sources.add(new Source(pos, c.getConnector(), heat));
            } else if (result instanceof FurnaceResult f) {
                visited.add(pos);
                targets.add(new Target(pos, f.getFurnace()));
            }
        }
    }
}
