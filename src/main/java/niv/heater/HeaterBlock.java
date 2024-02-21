package niv.heater;

import java.util.Random;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable.OxidationLevel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;

public class HeaterBlock extends AbstractFurnaceBlock {

    private final OxidationLevel oxidationLevel;

    public HeaterBlock(OxidationLevel oxidationLevel, Settings settings) {
        super(settings);
        this.oxidationLevel = oxidationLevel;
    }

    public OxidationLevel getOxidationLevel() {
        return oxidationLevel;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HeaterBlockEntity(pos, state);
    }

    @Override
    protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof HeaterBlockEntity heater) {
            player.openHandledScreen(heater);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, Heater.HEATER_BLOCK_ENTITY, HeaterBlockEntity::tick);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            var entity = world.getBlockEntity(pos);
            if (entity instanceof HeaterBlockEntity heater) {
                heater.setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() instanceof HeaterBlock && newState.getBlock() instanceof HeaterBlock) {
            return;
        }
        var entity = world.getBlockEntity(pos);
        if (entity instanceof HeaterBlockEntity heater) {
            if (world instanceof ServerWorld) {
                ItemScatterer.spawn(world, pos, heater.getInventory());
            }
            world.updateComparators(pos, this);
        }
        if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            world.removeBlockEntity(pos);
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT).booleanValue()) {
            var x = pos.getX() + .5d;
            var y = pos.getY();
            var z = pos.getZ() + .5d;
            if (random.nextDouble() < .1d) {
                world.playSound(x, y, z,
                        SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE,
                        SoundCategory.BLOCKS, 1f, 1f, false);
            }
            var direction = state.get(FACING);
            var axis = direction.getAxis();
            var c = .52d;
            var r = random.nextDouble() * .6d - .3d;
            var dx = axis == Axis.X ? direction.getOffsetX() * c : r;
            var dy = random.nextDouble() * 9d / 16d;
            var dz = axis == Axis.Z ? direction.getOffsetZ() * c : r;
            world.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, .0, .0, .0);
        }
    }

}
