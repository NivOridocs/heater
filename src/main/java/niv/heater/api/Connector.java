package niv.heater.api;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface Connector {

    default Set<Direction> getConnected(BlockState state) {
        return Set.of(Direction.values());
    }

    default boolean canPropagate(LevelAccessor level, BlockPos pos, BlockState state, Direction direction) {
        return true;
    }
}
