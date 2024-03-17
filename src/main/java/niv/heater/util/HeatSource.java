package niv.heater.util;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockState;
import niv.heater.block.HeaterBlock;
import niv.heater.block.ThermostatBlock;

public interface HeatSource {

    default Direction[] getConnected(BlockState state) {
        return Direction.values();
    }

    default Optional<HeatSource> getNeighborAsSource(LevelAccessor level, BlockPos pos, Direction direction) {
        var targetState = level.getBlockState(pos.relative(direction));
        var target = targetState.getBlock();
        if (target instanceof HeaterBlock) {
            return Optional.empty();
        } else if (target instanceof ThermostatBlock thermostat && targetState.getValue(ThermostatBlock.POWERED).booleanValue()) {
            return Optional.of(thermostat);
        } else if (target instanceof HeatSource source) {
            return Optional.of(source);
        } else {
            return Optional.empty();
        }
    }

    default Optional<HeatSink> getNeighborAsSink(LevelAccessor level, BlockPos pos, Direction direction) {
        var targetPos = pos.relative(direction);
        var targetState = level.getBlockState(targetPos);
        var target = targetState.getBlock();
        if (target instanceof HeaterBlock) {
            return Optional.empty();
        } else if (target instanceof BaseEntityBlock) {
            return Optional.of(level.getBlockEntity(targetPos)).flatMap(entity -> HeatSink.getHeatSink(level, entity));
        } else {
            return Optional.empty();
        }
    }

    int reducedHeat(int heat);

    static int reduceHeat(WeatherState weatherState, int heat) {
        switch (weatherState) {
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
