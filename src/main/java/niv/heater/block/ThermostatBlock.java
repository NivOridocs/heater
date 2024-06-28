package niv.heater.block;

import static net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings.copyOf;
import static net.minecraft.world.level.block.Blocks.COPPER_BLOCK;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.EXPOSED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.WEATHERED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import niv.heater.util.HeatSink;
import niv.heater.util.HeatSource;

public class ThermostatBlock extends DirectionalBlock implements HeatSource {

    @SuppressWarnings("java:S1845")
    public static final MapCodec<ThermostatBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeatherState.CODEC.fieldOf("weathering_state").forGetter(ThermostatBlock::getWeatherState),
            Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties))
            .apply(instance, ThermostatBlock::new));

    public static final ThermostatBlock UNAFFECTED_BLOCK = new ThermostatBlock(UNAFFECTED, copyOf(COPPER_BLOCK));
    public static final ThermostatBlock EXPOSED_BLOCK = new ThermostatBlock(EXPOSED, copyOf(COPPER_BLOCK));
    public static final ThermostatBlock WEATHERED_BLOCK = new ThermostatBlock(WEATHERED, copyOf(COPPER_BLOCK));
    public static final ThermostatBlock OXIDIZED_BLOCK = new ThermostatBlock(OXIDIZED, copyOf(COPPER_BLOCK));

    public static final BlockItem UNAFFECTED_ITEM = new BlockItem(UNAFFECTED_BLOCK, new FabricItemSettings());
    public static final BlockItem EXPOSED_ITEM = new BlockItem(EXPOSED_BLOCK, new FabricItemSettings());
    public static final BlockItem WEATHERED_ITEM = new BlockItem(WEATHERED_BLOCK, new FabricItemSettings());
    public static final BlockItem OXIDIZED_ITEM = new BlockItem(OXIDIZED_BLOCK, new FabricItemSettings());

    public static final Supplier<ImmutableMap<WeatherState, ThermostatBlock>> BLOCKS = Suppliers
            .memoize(() -> ImmutableMap.<WeatherState, ThermostatBlock>builder()
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

    private static final Direction[] EMPTY_DIRECTIONS = new Direction[0];

    private final WeatherState weatherState;

    public ThermostatBlock(WeatherState weatherState, Properties settings) {
        super(settings);
        this.weatherState = weatherState;
    }

    public WeatherState getWeatherState() {
        return weatherState;
    }

    @Override
    public MapCodec<? extends ThermostatBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
            boolean notify) {
        if (world.isClientSide) {
            return;
        }
        boolean powered = state.getValue(POWERED);
        if (powered != world.hasNeighborSignal(pos)) {
            if (powered) {
                world.scheduleTick(pos, this, 4);
            } else {
                world.setBlock(pos, state.cycle(POWERED), 2);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED).booleanValue() && !world.hasNeighborSignal(pos)) {
            world.setBlock(pos, state.cycle(POWERED), 2);
        }
    }

    @Override
    public Direction[] getConnected(BlockState state) {
        if (state.getOptionalValue(POWERED).orElse(false)) {
            return state.getOptionalValue(FACING).stream().toArray(Direction[]::new);
        } else {
            return EMPTY_DIRECTIONS;
        }
    }

    @Override
    public Optional<HeatSink> getNeighborAsSink(LevelAccessor level, BlockPos pos, Direction direction) {
        var targetPos = pos.relative(direction);
        if (level.getBlockState(targetPos).getBlock() instanceof HeaterBlock) {
            return HeatSink.of(level, level.getBlockEntity(targetPos));
        } else {
            return HeatSource.super.getNeighborAsSink(level, pos, direction);
        }
    }

    @Override
    public int reducedHeat(int heat) {
        return HeatSource.reduceHeat(weatherState, heat);
    }

}
