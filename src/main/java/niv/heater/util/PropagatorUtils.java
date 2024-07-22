package niv.heater.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.WeatheringCopper;
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
            BlockGetter getter, BlockPos pos) {
        var result = new EnumMap<Direction, Result>(Direction.class);
        for (var direction : connector.getConnected(getter.getBlockState(pos))) {
            var relative = pos.relative(direction);
            if (connector.canPropagate(getter, relative)) {

                var entity = getter.getBlockEntity(relative);
                if (entity != null && entity instanceof Furnace f) {
                    result.put(direction, new FurnaceResult(f));
                    continue;
                }

                var block = getter.getBlockState(relative).getBlock();
                if (block instanceof Connector c) {
                    result.put(direction, new ConnectorResult(c));
                }
            }
        }
        return result;
    }

    public static int reduceHeat(Connector connector, int heat) {
        if (connector instanceof WeatheringCopper weathering) {
            switch (weathering.getAge()) {
                case UNAFFECTED:
                    heat -= 1;
                    break;
                case EXPOSED:
                    heat -= 2;
                    break;
                case WEATHERED:
                    heat -= 3;
                    break;
                case OXIDIZED:
                    heat -= 4;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown oxidation level");
            }
        } else {
            heat -= 1;
        }
        return heat < 0 ? 0 : heat;
    }
}
