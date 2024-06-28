package niv.heater.util;

import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.EXPOSED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.WEATHERED;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableBiMap;

import net.minecraft.world.level.block.WeatheringCopper.WeatherState;

public class WeatherStateExtra {

    private static final String[] PATH = generatePath();

    private static final String[] NAME = generateName();

    private static final ImmutableBiMap<WeatherState, WeatherState> BI_MAP = ImmutableBiMap
            .<WeatherState, WeatherState>builder()
            .put(UNAFFECTED, EXPOSED)
            .put(EXPOSED, WEATHERED)
            .put(WEATHERED, OXIDIZED)
            .build();

    private WeatherStateExtra() {
    }

    private static final String[] generatePath() {
        var result = new String[4];
        for (var state : WeatherState.values()) {
            result[state.ordinal()] = UNAFFECTED.equals(state) ? "" : (state.name().toLowerCase() + "_");
        }
        return result;
    }

    public static final String toPath(WeatherState state) {
        return PATH[state.ordinal()];
    }

    private static final String[] generateName() {
        var result = new String[4];
        for (var state : WeatherState.values()) {
            result[state.ordinal()] = UNAFFECTED.equals(state) ? ""
                    : (StringUtils.capitalize(state.name().toLowerCase()) + " ");
        }
        return result;
    }

    public static final String toName(WeatherState state) {
        return NAME[state.ordinal()];
    }

    public static final Optional<WeatherState> getNext(WeatherState state) {
        return Optional.ofNullable(BI_MAP.getOrDefault(state, null));
    }

}
