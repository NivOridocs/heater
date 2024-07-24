package niv.heater.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import niv.heater.adapter.FurnaceAdapter;
import niv.heater.api.Connector;
import niv.heater.api.Furnace;

public class PropagatorUtils {

    public static interface Result {

        default Connector getConnector() {
            throw new NoSuchElementException();
        }

        default Furnace getFurnace() {
            throw new NoSuchElementException();
        }
    }

    public static final class ConnectorResult implements Result {

        private final Connector connector;

        private ConnectorResult(Connector connector) {
            this.connector = Objects.requireNonNull(connector);
        }

        @Override
        public Connector getConnector() {
            return connector;
        }
    }

    public static final class FurnaceResult implements Result {

        private final Furnace furnace;

        private FurnaceResult(Furnace furnace) {
            this.furnace = Objects.requireNonNull(furnace);
        }

        @Override
        public Furnace getFurnace() {
            return furnace;
        }
    }

    private PropagatorUtils() {
    }

    public static final Map<Direction, Result> getConnectedNeighbors(Connector connector,
            Level level, BlockPos pos) {
        var results = new EnumMap<Direction, Result>(Direction.class);
        for (var direction : connector.getConnected(level.getBlockState(pos))) {
            var relative = pos.relative(direction);
            if (connector.canPropagate(level, relative)) {
                Optional.<Result>empty()
                        .or(() -> asFurnace(level, relative))
                        .or(() -> asForwardingFurnace(level, relative))
                        .or(() -> asConnector(level, relative))
                        .ifPresent(result -> results.put(direction, result));
            }
        }
        return results;
    }

    private static Optional<Result> asFurnace(Level level, BlockPos pos) {
        var entity = level.getBlockEntity(pos);
        if (entity != null && entity instanceof Furnace furnace) {
            return Optional.of(new FurnaceResult(furnace));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Result> asForwardingFurnace(Level level, BlockPos pos) {
        var entity = level.getBlockEntity(pos);
        if (entity != null) {
            return FurnaceAdapter.of(level, entity).map(FurnaceResult::new);
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Result> asConnector(Level level, BlockPos pos) {
        var block = level.getBlockState(pos).getBlock();
        if (block instanceof Connector connector) {
            return Optional.of(new ConnectorResult(connector));
        } else {
            return Optional.empty();
        }
    }
}
