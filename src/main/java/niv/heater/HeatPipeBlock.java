package niv.heater;

import java.util.ArrayList;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class HeatPipeBlock extends Block implements Waterloggable {

    public static final IntProperty CONNECTED = IntProperty.of("connected", 0, 0x0FFF);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape CORE;
    private static final VoxelShape[] SHAPES;

    static {
        CORE = VoxelShapes.cuboid(5, 5, 5, 11, 11, 11);

        SHAPES = new VoxelShape[] {
                // DOWN
                VoxelShapes.cuboid(5, 0, 5, 11, 5, 11),
                VoxelShapes.cuboid(4, 0, 4, 12, 2, 12),
                // UP
                VoxelShapes.cuboid(5, 11, 5, 11, 16, 11),
                VoxelShapes.cuboid(4, 14, 4, 12, 16, 12),
                // NORTH
                VoxelShapes.cuboid(5, 5, 0, 11, 11, 5),
                VoxelShapes.cuboid(4, 4, 0, 12, 12, 2),
                // SOUTH
                VoxelShapes.cuboid(5, 5, 11, 11, 11, 16),
                VoxelShapes.cuboid(4, 4, 14, 12, 12, 16),
                // WEST
                VoxelShapes.cuboid(0, 5, 5, 5, 11, 11),
                VoxelShapes.cuboid(0, 4, 4, 2, 12, 12),
                // EAST
                VoxelShapes.cuboid(11, 5, 5, 16, 11, 11),
                VoxelShapes.cuboid(14, 4, 4, 2, 12, 12)
        };
    }

    public HeatPipeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(stateManager.getDefaultState()
                .with(CONNECTED, 0x50)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(CONNECTED, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int connected = state.get(CONNECTED).intValue();
        var shapes = new ArrayList<>(12);
        for (int i = 0; connected > 0; i++) {
            if ((connected & 1) != 0) {
                shapes.add(SHAPES[i * 2]);
            }
            connected = connected >>> 1;
            if ((connected & 1) != 0) {
                shapes.add(SHAPES[i * 2 + 1]);
            }
            connected = connected >>> 1;
        }
        return VoxelShapes.union(CORE, shapes.toArray(VoxelShape[]::new));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED).booleanValue()) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return state.with(CONNECTED, evalConnectedFragment(
                state.get(CONNECTED).intValue(),
                direction.getId() * 2,
                neighborState.getBlock()));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        var world = ctx.getWorld();
        var pos = ctx.getBlockPos();
        return super.getPlacementState(ctx)
                .with(CONNECTED, evalConnected(world, pos))
                .with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER);
    }

    public static int evalConnected(World world, BlockPos pos) {
        int connected = 0;
        for (var direction : Direction.values()) {
            connected = evalConnectedFragment(connected, direction.getId() * 2,
                    world.getBlockState(pos.offset(direction)).getBlock());
        }
        return connected;
    }

    public static Direction[] getConnectedDirections(BlockState state) {
        var result = new ArrayList<Direction>(6);
        var connected = state.get(CONNECTED).intValue();
        for (int i = 0; i < DIRECTIONS.length && connected > 0; i++) {
            if ((connected & 1) != 0) {
                result.add(DIRECTIONS[i]);
            }
            connected >>>= 2;
        }
        return result.toArray(Direction[]::new);
    }

    private static int evalConnectedFragment(int connected, int index, Block block) {
        int i = 0x1 << index;
        int j = 0x1 << (index + 1);
        if (block instanceof AbstractFurnaceBlock) {
            return connected | i | j;
        } else if (block instanceof HeatPipeBlock) {
            return connected | i & ~j;
        } else {
            return connected & ~i & ~j;
        }
    }

}
