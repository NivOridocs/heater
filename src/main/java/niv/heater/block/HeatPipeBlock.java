package niv.heater.block;

import java.util.ArrayList;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import niv.heater.util.HeatSink;
import niv.heater.util.HeatSource;

public class HeatPipeBlock extends Block implements HeatSource, SimpleWaterloggedBlock {

    @SuppressWarnings("java:S1845")
    public static final MapCodec<HeatPipeBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeatherState.CODEC.fieldOf("weathering_state").forGetter(HeatPipeBlock::getWeatherState),
            Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties))
            .apply(instance, HeatPipeBlock::new));

    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final BooleanProperty[] FACING_PROPERTIES = new BooleanProperty[] {
            DOWN, UP, NORTH, SOUTH, WEST, EAST };

    private static final VoxelShape CORE;
    private static final VoxelShape[] PIPE_ARM;

    static {
        CORE = Block.box(5, 5, 5, 11, 11, 11);
        PIPE_ARM = new VoxelShape[] {
                Block.box(5, 0, 5, 11, 5, 11),
                Block.box(5, 11, 5, 11, 16, 11),
                Block.box(5, 5, 0, 11, 11, 5),
                Block.box(5, 5, 11, 11, 11, 16),
                Block.box(0, 5, 5, 5, 11, 11),
                Block.box(11, 5, 5, 16, 11, 11),
        };
    }

    private final WeatherState weatherState;

    public HeatPipeBlock(WeatherState weatherState, Properties settings) {
        super(settings);
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
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        var shapes = new ArrayList<VoxelShape>(6);
        for (var direction : Direction.values()) {
            if (isConnected(state, direction)) {
                shapes.add(PIPE_ARM[direction.get3DDataValue()]);
            }
        }
        return Shapes.or(CORE, shapes.toArray(VoxelShape[]::new));
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
        return FACING_PROPERTIES[direction.get3DDataValue()];
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
