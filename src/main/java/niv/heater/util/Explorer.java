package niv.heater.util;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import niv.heater.Tags;
import niv.heater.adapter.FurnaceAdapter;
import niv.heater.api.Connector;
import niv.heater.api.Furnace;
import niv.heater.block.HeaterBlock;

public class Explorer implements Runnable {

    private static interface Result {
    }

    private static record ConnectorResult(Connector connector) implements Result {
    }

    private static record FurnaceResult(Furnace furnace) implements Result {
    }

    private static record HeaterResult(HeaterBlock heater) implements Result {
    }

    private record ConnectorHolder(Connector connector, BlockPos pos, int hops) {
    }

    private final LevelReader level;

    private final BlockPos posZero;

    private final BlockState stateZero;

    private final int hopsZero;

    private final Queue<ConnectorHolder> connectors;

    private final Set<BlockPos> visited;

    private BiConsumer<Furnace, BlockPos> onFurnaceCallback = null;

    private BiConsumer<HeaterBlock, BlockPos> onHeaterCallback = null;

    public Explorer(LevelReader level, BlockPos pos, BlockState state, int hops) {
        this.level = level;
        this.posZero = pos;
        this.stateZero = state;
        this.hopsZero = hops;
        this.connectors = new LinkedList<>();
        this.visited = new HashSet<>();
    }

    public Explorer onFurnaceFound(BiConsumer<Furnace, BlockPos> callback) {
        this.onFurnaceCallback = callback;
        return this;
    }

    public Explorer onHeaterFound(BiConsumer<HeaterBlock, BlockPos> callback) {
        this.onHeaterCallback = callback;
        return this;
    }

    @Override
    public void run() {
        connectors.clear();
        visited.clear();

        if (stateZero.getBlock() instanceof Connector connector) {
            connectors.add(new ConnectorHolder(connector, posZero, hopsZero));
            visited.add(posZero);
        }

        while (!connectors.isEmpty()) {
            var src = connectors.poll();
            getConnectedNeighbors(src.connector(), level, src.pos())
                    .forEach((dir, result) -> visit(src, dir, result));
        }
    }

    @SuppressWarnings("java:S1066")
    private void visit(ConnectorHolder src, Direction dir, Result result) {
        var pos = src.pos().relative(dir);
        var hops = src.hops();
        if (src.connector() instanceof WeatheringCopper copper) {
            hops = Math.max(0, hops - WeatherStateExtra.heatReduction(copper.getAge()));
        } else {
            hops = Math.max(0, hops - 1);
        }
        if (!visited.contains(pos) && hops > 0) {
            if (result instanceof ConnectorResult r) {
                visited.add(pos);
                connectors.add(new ConnectorHolder(r.connector(), pos, hops));
            } else if (result instanceof FurnaceResult r) {
                if (onFurnaceCallback != null) {
                    visited.add(pos);
                    onFurnaceCallback.accept(r.furnace(), pos);
                }
            } else if (result instanceof HeaterResult r) {
                if (onHeaterCallback != null) {
                    visited.add(pos);
                    onHeaterCallback.accept(r.heater(), pos);
                }
            }
        }
    }

    private static final Map<Direction, Result> getConnectedNeighbors(Connector connector,
            LevelReader level, BlockPos pos) {
        var results = new EnumMap<Direction, Result>(Direction.class);
        for (var direction : connector.getConnected(level.getBlockState(pos))) {
            var relative = pos.relative(direction);
            if (connector.canPropagate(level, relative)) {
                Optional.<Result>empty()
                        .or(() -> asFurnace(level, relative))
                        .or(() -> asForwardingFurnace(level, relative))
                        .or(() -> asConnector(level, relative))
                        .ifPresent(result -> results.put(direction, result));
            } else if (level.getBlockState(relative).is(Tags.HEATERS)) {
                asHeater(level, relative)
                        .ifPresent(result -> results.put(direction, result));
            }
        }
        return results;
    }

    private static Optional<Result> asFurnace(BlockGetter getter, BlockPos pos) {
        var entity = getter.getBlockEntity(pos);
        if (entity != null && entity instanceof Furnace furnace) {
            return Optional.of(new FurnaceResult(furnace));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Result> asForwardingFurnace(LevelReader level, BlockPos pos) {
        var entity = level.getBlockEntity(pos);
        if (entity != null) {
            return FurnaceAdapter.of(level, entity).map(FurnaceResult::new);
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Result> asConnector(BlockGetter getter, BlockPos pos) {
        var block = getter.getBlockState(pos).getBlock();
        if (block instanceof Connector connector) {
            return Optional.of(new ConnectorResult(connector));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Result> asHeater(BlockGetter getter, BlockPos pos) {
        var block = getter.getBlockState(pos).getBlock();
        if (block instanceof HeaterBlock heater) {
            return Optional.of(new HeaterResult(heater));
        } else {
            return Optional.empty();
        }
    }
}
