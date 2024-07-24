package niv.heater.block;

import static net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings.copyOf;
import static net.minecraft.world.level.block.Blocks.COPPER_BLOCK;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.EXPOSED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.WEATHERED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import niv.heater.Tags;
import niv.heater.api.Connector;

public class ThermostatBlock extends DirectionalBlock implements Connector, WeatheringCopper {

    @SuppressWarnings("java:S1845")
    public static final MapCodec<ThermostatBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeatherState.CODEC.fieldOf("weathering_state").forGetter(ThermostatBlock::getAge),
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

    private final WeatherState weatherState;

    public ThermostatBlock(WeatherState weatherState, Properties settings) {
        super(settings);
        this.weatherState = weatherState;
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
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
            Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        var powered = state.getOptionalValue(POWERED).orElse(Boolean.FALSE).booleanValue();
        if (powered != level.hasNeighborSignal(pos)) {
            if (powered) {
                level.scheduleTick(pos, this, 4);
            } else {
                level.setBlock(pos, state.cycle(POWERED), 2);
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
    public Set<Direction> getConnected(BlockState state) {
        if (state.getOptionalValue(POWERED).orElse(false)) {
            return state.getOptionalValue(FACING).stream().collect(Collectors.toSet());
        } else {
            return Set.of();
        }
    }

    @Override
    public boolean canPropagate(BlockState state) {
        return state.is(Tags.Propagable.THERMOSTATS);
    }

    @Override
    public WeatherState getAge() {
        return weatherState;
    }
}
