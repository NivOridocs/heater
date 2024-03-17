package niv.heater.block;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import niv.heater.block.entity.HeatSink;

public class ThermostatBlock extends DirectionalBlock implements HeatSource {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

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
            return HeatSink.getHeatSink(level.getBlockEntity(targetPos));
        } else {
            return HeatSource.super.getNeighborAsSink(level, pos, direction);
        }
    }

    @Override
    public int reducedHeat(int heat) {
        return HeatSource.reduceHeat(weatherState, heat);
    }

}
