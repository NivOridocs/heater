package niv.heater.block;

import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;

import java.util.ArrayList;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringHeatPipeBlock extends HeatPipeBlock {

    @SuppressWarnings("java:S1845")
    public static final MapCodec<WeatheringHeatPipeBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringHeatPipeBlock::getAge),
                    Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties))
            .apply(instance, WeatheringHeatPipeBlock::new));

    public WeatheringHeatPipeBlock(WeatherState weatherState, Properties settings) {
        super(weatherState, settings);
    }

    @Override
    public MapCodec<? extends WeatheringHeatPipeBlock> codec() {
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

    @Override
    public String[] getWords() {
        var result = new ArrayList<String>(3);
        if (getAge() != UNAFFECTED) {
            result.add(getAge().name().toLowerCase());
        }
        result.add("heat");
        result.add("pipe");
        return result.toArray(String[]::new);
    }
}
