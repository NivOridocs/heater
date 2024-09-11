package niv.heater.block;

import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;

import java.util.ArrayList;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT).booleanValue()) {
            double x = pos.getX() + .5d;
            double y = pos.getY() + .0d;
            double z = pos.getZ() + .5d;
            if (random.nextDouble() < 0.1) {
                level.playLocalSound(x, y, z,
                        SoundEvents.BLASTFURNACE_FIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        1.0F, 1.0F, false);
            }
            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double r = random.nextDouble() * .6d - .3d;
            double dx = axis == Direction.Axis.X ? direction.getStepX() * .52d : r;
            double dy = random.nextDouble() * 9.0d / 16.0d;
            double dz = axis == Direction.Axis.Z ? direction.getStepZ() * .52d : r;
            level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, .0d, .0d, .0d);
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
