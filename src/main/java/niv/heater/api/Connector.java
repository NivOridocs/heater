package niv.heater.api;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import niv.burning.api.BurningStorage;
import niv.heater.block.HeaterBlock;

public interface Connector {

    default Set<Direction> getConnected(BlockState state) {
        return Set.of(Direction.values());
    }

    default boolean canPropagate(Level level, BlockPos pos, BlockState state, Direction direction) {
        return !isHeater(level, pos, direction)
                && (isConnector(level, pos, direction) || isBurningStorage(level, pos, direction));
    }

    static boolean isHeater(Level level, BlockPos pos, Direction direction) {
        return level.getBlockState(pos.relative(direction)).getBlock() instanceof HeaterBlock;
    }

    static boolean isConnector(Level level, BlockPos pos, Direction direction) {
        return level.getBlockState(pos.relative(direction)).getBlock() instanceof Connector;
    }

    static boolean isBurningStorage(Level level, BlockPos pos, Direction direction) {
        return BurningStorage.SIDED.find(level, pos.relative(direction), direction.getOpposite()) != null;
    }
}
