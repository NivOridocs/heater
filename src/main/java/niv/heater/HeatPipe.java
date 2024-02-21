package niv.heater;

import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable.OxidationLevel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public interface HeatPipe {

    Direction[] getConnected(WorldAccess world, BlockPos pos, BlockState state);

    int reducedHeat(WorldAccess world, BlockPos pos, BlockState state, int heat);

    static int reduceHeat(OxidationLevel oxidationLevel, int heat) {
        switch (oxidationLevel) {
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
        return heat < 0 ? 0 : heat;
    }

}
