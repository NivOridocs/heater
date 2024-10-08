package niv.heater.block.entity;

import static net.minecraft.world.level.block.AbstractFurnaceBlock.LIT;
import static niv.heater.util.WeatherStateExtra.heatReduction;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import niv.heater.api.Furnace;
import niv.heater.block.HeaterBlock;
import niv.heater.block.WeatheringHeaterBlock;
import niv.heater.screen.HeaterMenu;
import niv.heater.util.Explorer;
import niv.heater.util.FurnaceExtra;
import niv.heater.util.HeaterContainer;

public class HeaterBlockEntity extends BlockEntity implements MenuProvider, Nameable, Furnace {

    public static final String CONTAINER_NAME = "container.heater";

    public static final BlockEntityType<HeaterBlockEntity> TYPE = FabricBlockEntityTypeBuilder
            .create(HeaterBlockEntity::new,
                    HeaterBlock.UNAFFECTED_BLOCK,
                    HeaterBlock.EXPOSED_BLOCK,
                    HeaterBlock.WEATHERED_BLOCK,
                    HeaterBlock.OXIDIZED_BLOCK,
                    WeatheringHeaterBlock.UNAFFECTED_BLOCK,
                    WeatheringHeaterBlock.EXPOSED_BLOCK,
                    WeatheringHeaterBlock.WEATHERED_BLOCK,
                    WeatheringHeaterBlock.OXIDIZED_BLOCK)
            .build();

    public static final int BURN_TIME_PROPERTY_INDEX = 0;
    public static final int FUEL_TIME_PROPERTY_INDEX = 1;

    private static final String BURN_TIME_TAG = "BurnTime";
    private static final String CUSTOM_NAME_TAG = "CustomName";
    private static final String ITEM_TAG = "Item";

    private static final int MAX_HOPS = 64;

    private final Set<BlockPos> cache;

    private final AtomicBoolean dirty;

    private LockCode lock = LockCode.NO_LOCK;

    private Component name;

    private int burnTime;

    private int fuelTime;

    private final HeaterContainer container = new HeaterContainer() {
        @Override
        public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(HeaterBlockEntity.this, player);
        }

        @Override
        public void setChanged() {
            HeaterBlockEntity.this.setChanged();
        }
    };

    private final EnumMap<Direction, InventoryStorage> wrappers = new EnumMap<>(Direction.class);

    private final ContainerData containerData = new ContainerData() {

        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return HeaterBlockEntity.this.burnTime;
                case 1:
                    return HeaterBlockEntity.this.fuelTime;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    HeaterBlockEntity.this.burnTime = value;
                    break;
                case 1:
                    HeaterBlockEntity.this.fuelTime = value;
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

    };

    public HeaterBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
        burnTime = 0;
        cache = new HashSet<>();
        dirty = new AtomicBoolean(true);
    }

    public HeaterContainer getContainer() {
        return container;
    }

    // For {@link BlockEntity}

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.lock = LockCode.fromTag(compoundTag);
        if (compoundTag.contains(CUSTOM_NAME_TAG, 8)) {
            this.name = Component.Serializer.fromJson(compoundTag.getString(CUSTOM_NAME_TAG));
        }
        this.container.setItem(0, ItemStack.of(compoundTag.getCompound(ITEM_TAG)));
        this.burnTime = compoundTag.getShort(BURN_TIME_TAG);
        this.fuelTime = this.getFuelTime(this.container.getItem(0));
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        this.lock.addToTag(compoundTag);
        if (this.name != null) {
            compoundTag.putString(CUSTOM_NAME_TAG, Component.Serializer.toJson(this.name));
        }
        compoundTag.put(ITEM_TAG, this.container.getItem(0).save(new CompoundTag()));
        compoundTag.putShort(BURN_TIME_TAG, (short) this.burnTime);
    }

    // For {@link MenuProvider}

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        if (BaseContainerBlockEntity.canUnlock(player, this.lock, this.getDisplayName())) {
            return new HeaterMenu(syncId, inventory, container, containerData);
        } else {
            return null;
        }
    }

    // For {@link Nameable}

    @Override
    public Component getName() {
        return name == null ? Component.translatable(CONTAINER_NAME) : name;
    }

    @Override
    public Component getCustomName() {
        return name;
    }

    public void setCustomName(Component name) {
        this.name = name;
    }

    // For {@link Furnace}

    @Override
    public boolean isBurning() {
        return burnTime > 0;
    }

    @Override
    public void addBurnTime(int value) {
        burnTime += value;
    }

    @Override
    public void setFuelTime(int value) {
        fuelTime = value;
    }

    @Override
    public int compareFuelTime(int value) {
        return Integer.compare(fuelTime, value);
    }

    @Override
    public int compareDeltaTime(int value) {
        return Integer.compare(fuelTime - burnTime, value);
    }

    @Override
    public Number getComparable() {
        return this.burnTime;
    }

    // Non-static

    public void makeDirty() {
        this.dirty.set(true);
    }

    private int getFuelTime(ItemStack fuel) {
        return fuel.isEmpty() ? 0
                : AbstractFurnaceBlockEntity.getFuel()
                        .getOrDefault(fuel.getItem(), 0);
    }

    private Map<Direction, InventoryStorage> getWrappers() {
        if (wrappers.isEmpty()) {
            for (var direction : Direction.values()) {
                wrappers.put(direction, InventoryStorage.of(container, direction));
            }
        }
        return wrappers;
    }

    // Static

    public static void tick(Level level, BlockPos pos, BlockState state, HeaterBlockEntity heater) {
        var wasBurning = heater.isBurning();

        if (heater.isBurning()) {
            if (heater.dirty.compareAndSet(true, false)) {
                heater.cache.clear();
                new Explorer(level, pos, level.getBlockState(pos), MAX_HOPS)
                        .onFurnaceFound((f, p) -> heater.cache.add(p))
                        .run();
            }
            propagateBurnTime(level, heater);
        }

        if (heater.isBurning() && level.getBlockState(pos).getBlock() instanceof HeaterBlock block) {
            heater.burnTime = Math.max(0, heater.burnTime - heatReduction(block.getAge()));
        }

        consumeFuel(heater);

        var changed = false;
        if (wasBurning != heater.isBurning()) {
            changed = true;
            state = state.setValue(AbstractFurnaceBlock.LIT, heater.isBurning());
            level.setBlockAndUpdate(pos, state);
        }

        if (changed) {
            setChanged(level, pos, state);
        }
    }

    private static record FurnaceHolder(Furnace furnace, BlockPos pos) implements Comparable<FurnaceHolder> {
        @Override
        public int compareTo(FurnaceHolder that) {
            int result = FurnaceExtra.compare(this.furnace(), that.furnace());
            if (result == 0) {
                result = this.pos().compareTo(that.pos());
            }
            return result;
        }
    }

    private static void propagateBurnTime(Level level, HeaterBlockEntity heater) {
        var targets = new TreeSet<FurnaceHolder>();
        heater.cache.stream()
                .map(pos -> Explorer.getOptionalFurnace(level, pos)
                        .map(furnace -> new FurnaceHolder(furnace, pos)))
                .flatMap(Optional::stream)
                .forEach(targets::add);

        if (targets.isEmpty()) {
            return;
        }

        var deltaBurn = heater.burnTime / targets.size();
        if (heater.burnTime % targets.size() > 0) {
            deltaBurn += 1;
        }

        for (var target : targets) {
            var wasBurning = target.furnace().isBurning();

            if (deltaBurn > heater.burnTime) {
                deltaBurn = heater.burnTime;
            }

            if (target.furnace().compareFuelTime(heater.fuelTime) < 0) {
                target.furnace().setFuelTime(heater.fuelTime);
            }

            if (target.furnace().compareDeltaTime(deltaBurn) >= 0) {
                heater.burnTime -= deltaBurn;
                target.furnace().addBurnTime(deltaBurn);

                var isBurning = target.furnace().isBurning();
                if (wasBurning != isBurning) {
                    var state = level.getBlockState(target.pos()).setValue(LIT, isBurning);
                    level.setBlockAndUpdate(target.pos(), state);
                    setChanged(level, target.pos(), state);
                }

                if (heater.burnTime <= 0) {
                    heater.burnTime = 0;
                    break;
                }
            }
        }
    }

    private static void consumeFuel(HeaterBlockEntity heater) {
        var fuelStack = heater.container.getItem(0);
        var hasFuel = !fuelStack.isEmpty();
        if (!heater.isBurning() && hasFuel) {
            var fuelTime = heater.getFuelTime(fuelStack);
            if (fuelTime > 0) {
                var fuelItem = fuelStack.getItem();
                fuelStack.shrink(1);
                if (fuelStack.isEmpty()) {
                    var bucketItem = fuelItem.getCraftingRemainingItem();
                    heater.container.setItem(0, bucketItem == null ? ItemStack.EMPTY : new ItemStack(bucketItem));
                }
                heater.fuelTime = heater.burnTime = fuelTime;
            }
        }
    }

    public static InventoryStorage getInventoryStorage(HeaterBlockEntity entity, Direction direction) {
        return entity.getWrappers().get(direction);
    }

    public static final void updateConnectedHeaters(LevelAccessor level, BlockPos pos, BlockState state) {
        new Explorer(level, pos, state, MAX_HOPS)
                .onHeaterFound((h, p) -> level.getBlockEntity(p, TYPE)
                        .ifPresent(HeaterBlockEntity::makeDirty))
                .run();
    }
}
