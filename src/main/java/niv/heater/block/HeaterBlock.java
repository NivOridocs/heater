package niv.heater.block;

import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;

import java.util.ArrayList;
import java.util.Random;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import niv.heater.Tags;
import niv.heater.api.Connector;
import niv.heater.api.Worded;
import niv.heater.block.entity.HeaterBlockEntity;
import niv.heater.registry.HeaterBlockEntityTypes;

public class HeaterBlock extends AbstractFurnaceBlock implements Connector, Worded, WeatheringCopper {

    @SuppressWarnings("java:S1845")
    public static final MapCodec<HeaterBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeatherState.CODEC.fieldOf("weathering_state").forGetter(HeaterBlock::getAge),
            Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties))
            .apply(instance, HeaterBlock::new));

    private final WeatherState weatherState;

    public HeaterBlock(WeatherState weatherState, Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    @Override
    public MapCodec<? extends HeaterBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HeaterBlockEntity(pos, state);
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof HeaterBlockEntity heater) {
            player.openMenu(heater);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, HeaterBlockEntityTypes.HEATER, HeaterBlockEntity::tick);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
            Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        level.getBlockEntity(pos, HeaterBlockEntityTypes.HEATER).ifPresent(HeaterBlockEntity::makeDirty);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() instanceof HeaterBlock && newState.getBlock() instanceof HeaterBlock) {
            return;
        }
        var entity = level.getBlockEntity(pos);
        if (entity instanceof HeaterBlockEntity heater) {
            if (level instanceof ServerLevel) {
                Containers.dropContents(level, pos, heater.getContainer());
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            level.removeBlockEntity(pos);
        }
    }

    public void randomDisplayTick(BlockState state, Level level, BlockPos pos, Random random) {
        if (state.getValue(LIT).booleanValue()) {
            var x = pos.getX() + .5d;
            var y = pos.getY();
            var z = pos.getZ() + .5d;
            if (random.nextDouble() < .1d) {
                level.playLocalSound(x, y, z,
                        SoundEvents.BLASTFURNACE_FIRE_CRACKLE,
                        SoundSource.BLOCKS, 1f, 1f, false);
            }
            var direction = state.getValue(FACING);
            var axis = direction.getAxis();
            var c = .52d;
            var r = random.nextDouble() * .6d - .3d;
            var dx = axis == Axis.X ? direction.getStepX() * c : r;
            var dy = random.nextDouble() * 9d / 16d;
            var dz = axis == Axis.Z ? direction.getStepY() * c : r;
            level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, .0, .0, .0);
        }
    }

    @Override
    public boolean canPropagate(LevelAccessor level, BlockPos pos, BlockState state, Direction direction) {
        return level.getBlockState(pos.relative(direction)).is(Tags.Propagable.HEATERS);
    }

    @Override
    public WeatherState getAge() {
        return weatherState;
    }

    @Override
    public String[] getWords() {
        var result = new ArrayList<String>(3);
        result.add("waxed");
        if (getAge() != UNAFFECTED) {
            result.add(getAge().name().toLowerCase());
        }
        result.add("heater");
        return result.toArray(String[]::new);
    }
}
