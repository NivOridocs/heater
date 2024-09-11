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

@SuppressWarnings("java:S110")
public class WeatheringHeaterBlock extends HeaterBlock {

    @SuppressWarnings("java:S1845")
    public static final MapCodec<WeatheringHeaterBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringHeaterBlock::getAge),
            Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties))
            .apply(instance, WeatheringHeaterBlock::new));

    public WeatheringHeaterBlock(WeatherState weatherState, Properties settings) {
        super(weatherState, settings);
    }

    @Override
    public MapCodec<? extends WeatheringHeaterBlock> codec() {
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
        var result = new ArrayList<String>(2);
        if (getAge() != UNAFFECTED) {
            result.add(getAge().name().toLowerCase());
        }
        result.add("heater");
        return result.toArray(String[]::new);
    }
}
