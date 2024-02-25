package niv.heater.block;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.Oxidizable.OxidationLevel;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import niv.heater.block.entity.HeatSink;

public class ThermostatBlock extends FacingBlock implements HeatSource {

    public static final BooleanProperty POWERED = Properties.POWERED;

    private static final Direction[] EMPTY_DIRECTIONS = new Direction[0];

    private final OxidationLevel oxidationLevel;

    public ThermostatBlock(OxidationLevel oxidationLevel, Settings settings) {
        super(settings);
        this.oxidationLevel = oxidationLevel;
    }

    public OxidationLevel getOxidationLevel() {
        return oxidationLevel;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getPlayerLookDirection().getOpposite())
                .with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
            boolean notify) {
        if (world.isClient) {
            return;
        }
        boolean powered = state.get(POWERED);
        if (powered != world.isReceivingRedstonePower(pos)) {
            if (powered) {
                world.scheduleBlockTick(pos, this, 4);
            } else {
                world.setBlockState(pos, state.cycle(POWERED), Block.NOTIFY_LISTENERS);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED).booleanValue() && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, state.cycle(POWERED), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public Direction[] getConnected(BlockState state) {
        if (state.getOrEmpty(POWERED).orElse(false)) {
            return state.getOrEmpty(FACING).stream().toArray(Direction[]::new);
        } else {
            return EMPTY_DIRECTIONS;
        }
    }

    @Override
    public Optional<HeatSink> getNeighborAsSink(WorldAccess world, BlockPos pos, Direction direction) {
        var targetPos = pos.offset(direction);
        if (world.getBlockState(targetPos).getBlock() instanceof HeaterBlock) {
            return HeatSink.getHeatSink(world.getBlockEntity(targetPos));
        } else {
            return HeatSource.super.getNeighborAsSink(world, pos, direction);
        }
    }

    @Override
    public int reducedHeat(int heat) {
        return HeatSource.reduceHeat(oxidationLevel, heat);
    }

}
