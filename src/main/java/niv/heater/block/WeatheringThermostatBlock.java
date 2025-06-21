package niv.heater.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringThermostatBlock extends ThermostatBlock {

    @SuppressWarnings("java:S1845")
    public static final MapCodec<WeatheringThermostatBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringThermostatBlock::getAge),
                    Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties))
            .apply(instance, WeatheringThermostatBlock::new));

    public WeatheringThermostatBlock(WeatherState weatherState, Properties settings) {
        super(weatherState, settings);
    }

    @Override
    public MapCodec<? extends WeatheringThermostatBlock> codec() {
        return CODEC;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }
}
