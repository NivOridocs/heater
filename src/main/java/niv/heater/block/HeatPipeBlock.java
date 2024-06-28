package niv.heater.block;

import static net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings.copyOf;
import static net.minecraft.world.level.block.Blocks.COPPER_BLOCK;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.EXPOSED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.WEATHERED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import niv.heater.util.HeatSink;
import niv.heater.util.HeatSource;

public class HeatPipeBlock extends PipeBlock implements HeatSource, SimpleWaterloggedBlock {

    @SuppressWarnings("java:S1845")
    public static final MapCodec<HeatPipeBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeatherState.CODEC.fieldOf("weathering_state").forGetter(HeatPipeBlock::getWeatherState),
            Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties))
            .apply(instance, HeatPipeBlock::new));

    public static final HeatPipeBlock UNAFFECTED_BLOCK = new HeatPipeBlock(UNAFFECTED, copyOf(COPPER_BLOCK));
    public static final HeatPipeBlock EXPOSED_BLOCK = new HeatPipeBlock(EXPOSED, copyOf(COPPER_BLOCK));
    public static final HeatPipeBlock WEATHERED_BLOCK = new HeatPipeBlock(WEATHERED, copyOf(COPPER_BLOCK));
    public static final HeatPipeBlock OXIDIZED_BLOCK = new HeatPipeBlock(OXIDIZED, copyOf(COPPER_BLOCK));

    public static final BlockItem UNAFFECTED_ITEM = new BlockItem(UNAFFECTED_BLOCK, new FabricItemSettings());
    public static final BlockItem EXPOSED_ITEM = new BlockItem(EXPOSED_BLOCK, new FabricItemSettings());
    public static final BlockItem WEATHERED_ITEM = new BlockItem(WEATHERED_BLOCK, new FabricItemSettings());
    public static final BlockItem OXIDIZED_ITEM = new BlockItem(OXIDIZED_BLOCK, new FabricItemSettings());

    public static final Supplier<ImmutableMap<WeatherState, HeatPipeBlock>> BLOCKS = Suppliers
            .memoize(() -> ImmutableMap.<WeatherState, HeatPipeBlock>builder()
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

    private final WeatherState weatherState;

    public HeatPipeBlock(WeatherState weatherState, Properties settings) {
        super(.1875F, settings);
        this.weatherState = weatherState;
        this.registerDefaultState(stateDefinition.any()
                .setValue(DOWN, false)
                .setValue(UP, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(WATERLOGGED, false));
    }

    public WeatherState getWeatherState() {
        return weatherState;
    }

    @Override
    public MapCodec<? extends HeatPipeBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
        return !state.getValue(WATERLOGGED).booleanValue();
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return state.getFluidState();
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter getter, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var state = super.getStateForPlacement(context);
        if (level.getFluidState(pos).is(Fluids.WATER)) {
            state = state.setValue(WATERLOGGED, true);
        }
        for (var direction : Direction.values()) {
            if (canConnect(level, pos.relative(direction))) {
                state = state.setValue(getProperty(direction), true);
            }
        }
        return state;
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED).booleanValue()) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state.setValue(getProperty(direction), canConnect(level, neighborPos));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST, WATERLOGGED);
    }

    private boolean canConnect(LevelAccessor level, BlockPos pos) {
        var block = level.getBlockState(pos).getBlock();
        return block instanceof HeatSource || HeatSink.is(level, level.getBlockEntity(pos));
    }

    public static boolean isConnected(BlockState state, Direction direction) {
        return state.getValue(getProperty(direction)).booleanValue();
    }

    public static BooleanProperty getProperty(Direction direction) {
        return PROPERTY_BY_DIRECTION.get(direction);
    }

    @Override
    public Direction[] getConnected(BlockState state) {
        var directions = new ArrayList<>(6);
        for (var direction : Direction.values()) {
            if (isConnected(state, direction)) {
                directions.add(direction);
            }
        }
        return directions.toArray(Direction[]::new);
    }

    @Override
    public int reducedHeat(int heat) {
        return HeatSource.reduceHeat(weatherState, heat);
    }

}
