package niv.heater.block;

import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Oxidizable.OxidationLevel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import niv.heater.block.entity.HeatSink;

public interface HeatSource {

    default Direction[] getConnected(BlockState state) {
        return Direction.values();
    }

    default Optional<HeatSource> getNeighborAsSource(WorldAccess world, BlockPos pos, Direction direction) {
        var targetState = world.getBlockState(pos.offset(direction));
        var target = targetState.getBlock();
        if (target instanceof HeaterBlock) {
            return Optional.empty();
        } else if (target instanceof ThermostatBlock thermostat && targetState.get(ThermostatBlock.POWERED).booleanValue()) {
            return Optional.of(thermostat);
        } else if (target instanceof HeatSource source) {
            return Optional.of(source);
        } else {
            return Optional.empty();
        }
    }

    default Optional<HeatSink> getNeighborAsSink(WorldAccess world, BlockPos pos, Direction direction) {
        var targetPos = pos.offset(direction);
        var targetState = world.getBlockState(targetPos);
        var target = targetState.getBlock();
        if (target instanceof HeaterBlock) {
            return Optional.empty();
        } else if (target instanceof BlockWithEntity) {
            return Optional.of(world.getBlockEntity(targetPos)).flatMap(HeatSink::getHeatSink);
        } else {
            return Optional.empty();
        }
    }

    int reducedHeat(int heat);

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
