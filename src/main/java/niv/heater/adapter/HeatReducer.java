package niv.heater.adapter;

import java.util.function.IntUnaryOperator;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import niv.heater.util.HeatSource;

public interface HeatReducer extends IntUnaryOperator {

    public static final Codec<HeatReducer> CODEC = Codec.either(Fixed.CODEC, Weathering.CODEC)
            .flatComapMap(either -> either.map(HeatReducer.class::cast, HeatReducer.class::cast), HeatReducer::cast);

    private static DataResult<Either<Fixed, Weathering>> cast(HeatReducer reducer) {
        if (reducer instanceof Fixed fixed) {
            return DataResult.success(Either.left(fixed));
        } else if (reducer instanceof Weathering weathering) {
            return DataResult.success(Either.right(weathering));
        } else {
            return DataResult.error(() -> "Unkwown type");
        }
    }

    public static final class Fixed implements HeatReducer {

        static final Codec<Fixed> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("amount").forGetter(x -> x.amount))
                .apply(instance, Fixed::new));

        private final int amount;

        public Fixed(int amount) {
            this.amount = Math.max(0, amount);
        }

        @Override
        public int applyAsInt(int heat) {
            return Math.max(0, heat - amount);
        }
    }

    public static final class Weathering implements HeatReducer {

        static final Codec<Weathering> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                WeatherState.CODEC.fieldOf("weathering_state").forGetter(w -> w.weatherState))
                .apply(instance, Weathering::new));

        private final WeatherState weatherState;

        public Weathering(WeatherState weatherState) {
            this.weatherState = weatherState;
        }

        @Override
        public int applyAsInt(int heat) {
            return HeatSource.reduceHeat(weatherState, heat);
        }
    }
}
