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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import niv.burning.api.BurningStorage;
import niv.heater.api.Connector;
import niv.heater.block.HeaterBlock;

public class Explorer implements Runnable {

    private static interface Result {
    }

    private static record ConnectorResult(Connector connector) implements Result {
    }

    private static record BurningStorageResult(BurningStorage burning) implements Result {
    }

    private static record HeaterResult(HeaterBlock heater) implements Result {
    }

    private record ConnectorHolder(Connector connector, BlockPos pos, BlockState state, int hops) {
    }

    private final Level level;

    private final BlockPos posZero;

    private final BlockState stateZero;

    private final int hopsZero;

    private final Queue<ConnectorHolder> connectors;

    private final Set<BlockPos> visited;

    private BiConsumer<BurningStorage, BlockPos> onBurningStorageCallback = null;

    private BiConsumer<HeaterBlock, BlockPos> onHeaterCallback = null;

    public Explorer(Level level, BlockPos pos, BlockState state, int hops) {
        this.level = level;
        this.posZero = pos;
        this.stateZero = state;
        this.hopsZero = hops;
        this.connectors = new LinkedList<>();
        this.visited = new HashSet<>();
    }

    public Explorer onBurningStorageCallback(BiConsumer<BurningStorage, BlockPos> callback) {
        this.onBurningStorageCallback = callback;
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
            connectors.add(new ConnectorHolder(connector, posZero, stateZero, hopsZero));
            visited.add(posZero);
        }

        while (!connectors.isEmpty()) {
            var src = connectors.poll();
            getConnectedNeighbors(src.connector(), level, src.pos(), src.state())
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
                connectors.add(new ConnectorHolder(r.connector(), pos, level.getBlockState(pos), hops));
            } else if (result instanceof BurningStorageResult r) {
                if (onBurningStorageCallback != null) {
                    visited.add(pos);
                    onBurningStorageCallback.accept(r.burning(), pos);
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
            Level level, BlockPos pos, BlockState state) {
        var results = new EnumMap<Direction, Result>(Direction.class);
        for (var direction : connector.getConnected(state)) {
            var relative = pos.relative(direction);
            if (connector.canPropagate(level, pos, level.getBlockState(pos), direction)) {
                Optional.<Result>empty()
                        .or(() -> asBurningStorage(level, relative, direction))
                        .or(() -> asConnector(level, relative))
                        .ifPresent(result -> results.put(direction, result));
            } else if (level.getBlockState(relative).getBlock() instanceof HeaterBlock heater) {
                results.put(direction, new HeaterResult(heater));
            }
        }
        return results;
    }

    private static Optional<BurningStorageResult> asBurningStorage(Level level, BlockPos pos, Direction direction) {
        return Optional.ofNullable(BurningStorage.SIDED.find(level, pos, direction.getOpposite()))
                .map(BurningStorageResult::new);
    }

    private static Optional<ConnectorResult> asConnector(BlockGetter getter, BlockPos pos) {
        var block = getter.getBlockState(pos).getBlock();
        if (block instanceof Connector connector) {
            return Optional.of(new ConnectorResult(connector));
        } else {
            return Optional.empty();
        }
    }
}
