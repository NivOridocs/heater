package niv.heater.block;

import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import niv.heater.api.Connector;
import niv.heater.api.Worded;
import niv.heater.block.entity.HeaterBlockEntity;

public class HeatPipeBlock extends PipeBlock implements Connector, Worded, WeatheringCopper, SimpleWaterloggedBlock {

    @SuppressWarnings("java:S1845")
    public static final MapCodec<HeatPipeBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeatherState.CODEC.fieldOf("weathering_state").forGetter(HeatPipeBlock::getAge),
            Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties))
            .apply(instance, HeatPipeBlock::new));

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
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        return this.defaultBlockState()
                .trySetValue(DOWN, canConnect(level, pos, Direction.DOWN))
                .trySetValue(UP, canConnect(level, pos, Direction.UP))
                .trySetValue(NORTH, canConnect(level, pos, Direction.NORTH))
                .trySetValue(SOUTH, canConnect(level, pos, Direction.SOUTH))
                .trySetValue(WEST, canConnect(level, pos, Direction.WEST))
                .trySetValue(EAST, canConnect(level, pos, Direction.EAST))
                .trySetValue(WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED).booleanValue()) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (level instanceof Level world) {
            return state.trySetValue(PROPERTY_BY_DIRECTION.get(direction), canConnect(world, pos, direction));
        } else {
            return state;
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
            Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        HeaterBlockEntity.updateConnectedHeaters(level, pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        HeaterBlockEntity.updateConnectedHeaters(level, pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        HeaterBlockEntity.updateConnectedHeaters(level, pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST, WATERLOGGED);
    }

    @Override
    public Set<Direction> getConnected(BlockState state) {
        var directions = new HashSet<Direction>(6);
        for (var direction : Direction.values()) {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(direction)).booleanValue()) {
                directions.add(direction);
            }
        }
        return directions;
    }

    @Override
    public WeatherState getAge() {
        return weatherState;
    }

    @Override
    public String[] getWords() {
        var result = new ArrayList<String>(4);
        result.add("waxed");
        if (getAge() != UNAFFECTED) {
            result.add(getAge().name().toLowerCase());
        }
        result.add("heat");
        result.add("pipe");
        return result.toArray(String[]::new);
    }

    private boolean canConnect(Level level, BlockPos pos, Direction direction) {
        return Connector.isConnector(level, pos, direction) || Connector.isBurningStorage(level, pos, direction);
    }
}
