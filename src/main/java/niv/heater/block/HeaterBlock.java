package niv.heater.block;

import static net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings.copyOf;
import static net.minecraft.world.level.block.Blocks.FURNACE;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.EXPOSED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.WEATHERED;

import java.util.Random;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import niv.heater.block.entity.HeaterBlockEntity;
import niv.heater.util.HeatSource;

public class HeaterBlock extends AbstractFurnaceBlock implements HeatSource {

    @SuppressWarnings("java:S1845")
    public static final MapCodec<HeaterBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeatherState.CODEC.fieldOf("weathering_state").forGetter(HeaterBlock::getWeatherState),
            Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties))
            .apply(instance, HeaterBlock::new));

    public static final HeaterBlock UNAFFECTED_BLOCK = new HeaterBlock(UNAFFECTED, copyOf(FURNACE));
    public static final HeaterBlock EXPOSED_BLOCK = new HeaterBlock(EXPOSED, copyOf(FURNACE));
    public static final HeaterBlock WEATHERED_BLOCK = new HeaterBlock(WEATHERED, copyOf(FURNACE));
    public static final HeaterBlock OXIDIZED_BLOCK = new HeaterBlock(OXIDIZED, copyOf(FURNACE));

    public static final BlockItem UNAFFECTED_ITEM = new BlockItem(UNAFFECTED_BLOCK, new FabricItemSettings());
    public static final BlockItem EXPOSED_ITEM = new BlockItem(EXPOSED_BLOCK, new FabricItemSettings());
    public static final BlockItem WEATHERED_ITEM = new BlockItem(WEATHERED_BLOCK, new FabricItemSettings());
    public static final BlockItem OXIDIZED_ITEM = new BlockItem(OXIDIZED_BLOCK, new FabricItemSettings());

    public static final Supplier<ImmutableMap<WeatherState, HeaterBlock>> BLOCKS = Suppliers
            .memoize(() -> ImmutableMap.<WeatherState, HeaterBlock>builder()
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

    public HeaterBlock(WeatherState weatherState, Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    public WeatherState getWeatherState() {
        return weatherState;
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
                : createTickerHelper(type, HeaterBlockEntity.TYPE, HeaterBlockEntity::tick);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomHoverName()) {
            var entity = world.getBlockEntity(pos);
            if (entity instanceof HeaterBlockEntity heater) {
                heater.setCustomName(itemStack.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() instanceof HeaterBlock && newState.getBlock() instanceof HeaterBlock) {
            return;
        }
        var entity = level.getBlockEntity(pos);
        if (entity instanceof HeaterBlockEntity heater) {
            if (level instanceof ServerLevel) {
                Containers.dropContents(level, pos, heater.getItems());
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
    public int reducedHeat(int heat) {
        return HeatSource.reduceHeat(weatherState, heat);
    }

}
