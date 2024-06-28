package niv.heater.block;

import static net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings.copyOf;
import static net.minecraft.world.level.block.Blocks.COPPER_BLOCK;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.EXPOSED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.WEATHERED;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringHeatPipeBlock extends HeatPipeBlock implements WeatheringCopper {

    @SuppressWarnings("java:S1845")
    public static final MapCodec<WeatheringHeatPipeBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    WeatherState.CODEC.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge),
                    Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties))
            .apply(instance, WeatheringHeatPipeBlock::new));

    public static final WeatheringHeatPipeBlock UNAFFECTED_BLOCK = new WeatheringHeatPipeBlock(UNAFFECTED, copyOf(COPPER_BLOCK));
    public static final WeatheringHeatPipeBlock EXPOSED_BLOCK = new WeatheringHeatPipeBlock(EXPOSED, copyOf(COPPER_BLOCK));
    public static final WeatheringHeatPipeBlock WEATHERED_BLOCK = new WeatheringHeatPipeBlock(WEATHERED, copyOf(COPPER_BLOCK));
    public static final WeatheringHeatPipeBlock OXIDIZED_BLOCK = new WeatheringHeatPipeBlock(OXIDIZED, copyOf(COPPER_BLOCK));

    public static final BlockItem UNAFFECTED_ITEM = new BlockItem(UNAFFECTED_BLOCK, new FabricItemSettings());
    public static final BlockItem EXPOSED_ITEM = new BlockItem(EXPOSED_BLOCK, new FabricItemSettings());
    public static final BlockItem WEATHERED_ITEM = new BlockItem(WEATHERED_BLOCK, new FabricItemSettings());
    public static final BlockItem OXIDIZED_ITEM = new BlockItem(OXIDIZED_BLOCK, new FabricItemSettings());

    public static final Supplier<ImmutableMap<WeatherState, WeatheringHeatPipeBlock>> BLOCKS = Suppliers
            .memoize(() -> ImmutableMap.<WeatherState, WeatheringHeatPipeBlock>builder()
                    .put(UNAFFECTED, UNAFFECTED_BLOCK)
                    .put(EXPOSED, EXPOSED_BLOCK)
                    .put(WEATHERED, WEATHERED_BLOCK)
                    .put(OXIDIZED, OXIDIZED_BLOCK)
                    .build());

    public static final Supplier<ImmutableMap<WeatherState, BlockItem>> ITEMS = Suppliers
            .memoize(() -> ImmutableMap.<WeatherState, BlockItem>builder()
                    .put(UNAFFECTED, UNAFFECTED_ITEM)
                    .put(EXPOSED, EXPOSED_ITEM)
                    .put(WEATHERED, WEATHERED_ITEM)
                    .put(OXIDIZED, OXIDIZED_ITEM)
                    .build());

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
    public WeatherState getAge() {
        return getWeatherState();
    }

}
