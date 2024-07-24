package niv.heater.api;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface Connector {

    default Set<Direction> getConnected(BlockState state) {
        return Set.of(Direction.values());
    }

    default boolean canPropagate(BlockGetter getter, BlockPos pos) {
        return canPropagate(getter.getBlockState(pos));
    }

    default boolean canPropagate(BlockState state) {
        return true;
    }
}
