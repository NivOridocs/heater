package niv.heater.block.entity;

import static niv.heater.util.WeatherStateExtra.burningReduction;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import niv.burning.api.Burning;
import niv.burning.api.BurningStorage;
import niv.burning.api.base.SimpleBurningStorage;
import niv.heater.block.HeaterBlock;
import niv.heater.registry.HeaterBlockEntityTypes;
import niv.heater.screen.HeaterMenu;
import niv.heater.util.Explorer;
import niv.heater.util.HeaterContainer;
import niv.heater.util.SingletonBurningContext;

public class HeaterBlockEntity extends BlockEntity implements MenuProvider, Nameable {

    public static final String CONTAINER_NAME = "container.heater";

    public static final int BURN_TIME_PROPERTY_INDEX = 0;
    public static final int FUEL_TIME_PROPERTY_INDEX = 1;

    private static final String CUSTOM_NAME_TAG = "CustomName";
    private static final String ITEM_TAG = "Item";

    private static final int MAX_HOPS = 64;

    private final HeaterContainer container;

    private final SimpleBurningStorage burningStorage;

    private final ContainerData burningData;

    private final EnumMap<Direction, InventoryStorage> wrappers;

    private final Set<BlockPos> cache;

    private final AtomicBoolean dirty;

    private LockCode lock;

    @Nullable
    private Component name;

    public HeaterBlockEntity(BlockPos pos, BlockState state) {
        super(HeaterBlockEntityTypes.HEATER, pos, state);
        this.container = HeaterContainer.getForBlockEntity(this);
        this.burningStorage = SimpleBurningStorage.getForBlockEntity(this, i -> i);
        this.burningData = SimpleBurningStorage.getDefaultContainerData(this.burningStorage);
        this.wrappers = new EnumMap<>(Direction.class);
        this.cache = new HashSet<>();
        this.dirty = new AtomicBoolean(true);
        this.lock = LockCode.NO_LOCK;
        this.name = null;
    }

    public HeaterContainer getContainer() {
        return container;
    }

    public boolean isBurning() {
        return this.burningStorage.getCurrentBurning() > 0;
    }

    public void makeDirty() {
        this.dirty.set(true);
    }

    // For {@link BlockEntity}

    @Override
    protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
        this.lock = LockCode.fromTag(compoundTag);
        if (compoundTag.contains(CUSTOM_NAME_TAG, 8)) {
            this.name = Component.Serializer.fromJson(compoundTag.getString(CUSTOM_NAME_TAG), provider);
        }
        this.container.setItem(0, ItemStack.parseOptional(provider, compoundTag.getCompound(ITEM_TAG)));
        this.burningStorage.load(compoundTag, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
        this.lock.addToTag(compoundTag);
        if (this.name != null) {
            compoundTag.putString(CUSTOM_NAME_TAG, Component.Serializer.toJson(this.name, provider));
        }
        if (!this.container.getItem(0).isEmpty()) {
            compoundTag.put(ITEM_TAG, this.container.getItem(0).save(provider, new CompoundTag()));
        }
        this.burningStorage.save(compoundTag, provider);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.name = input.get(DataComponents.CUSTOM_NAME);
        this.lock = input.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
        input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.container.getItems());
    }

    @Override
    protected void collectImplicitComponents(Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CUSTOM_NAME, this.name);
        if (!this.lock.equals(LockCode.NO_LOCK)) {
            builder.set(DataComponents.LOCK, this.lock);
        }
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.container.getItems()));
    }

    // For {@link MenuProvider}

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        if (BaseContainerBlockEntity.canUnlock(player, this.lock, this.getDisplayName())) {
            return new HeaterMenu(syncId, inventory, container, burningData);
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

    // Static

    public static void tick(Level level, BlockPos pos, BlockState state, HeaterBlockEntity heater) {
        var context = SingletonBurningContext.getInstance();
        try (var transaction = Transaction.openOuter()) {

            if (heater.isBurning()) {
                if (heater.dirty.compareAndSet(true, false)) {
                    heater.cache.clear();
                    new Explorer(level, pos, level.getBlockState(pos), MAX_HOPS)
                            .onBurningStorageCallback((s, p) -> heater.cache.add(p))
                            .run();
                }
                propagateBurnTime(level, heater, transaction);
            }

            if (heater.isBurning() && state.getBlock() instanceof HeaterBlock block) {
                heater.burningStorage.extract(
                        burningReduction(heater.burningStorage.getBurning(context), block.getAge()),
                        context,
                        transaction);
            }

            consumeFuel(heater, transaction);

            transaction.commit();
        }
    }

    private static void propagateBurnTime(Level level, HeaterBlockEntity heater, Transaction transaction) {
        var context = SingletonBurningContext.getInstance();
        var storages = heater.cache.stream()
                .map(pos -> BurningStorage.SIDED.find(level, pos, null))
                .filter(BurningStorage::supportsInsertion)
                .sorted((a, b) -> Double.compare(
                        a.getBurning(context).getReverseValue(context),
                        b.getBurning(context).getReverseValue(context)))
                .limit(heater.burningStorage.getCurrentBurning())
                .toArray(BurningStorage[]::new);

        if (storages.length > 0) {
            var deltaBurning = heater.burningStorage.getBurning(context)
                    .withValue(Math.round(heater.burningStorage.getCurrentBurning() * 1f / storages.length), context);

            for (var storage : storages) {
                BurningStorage.transfer(heater.burningStorage, storage, deltaBurning, context, transaction);
                if (!heater.isBurning()) {
                    break;
                }
            }
        }
    }

    private static void consumeFuel(HeaterBlockEntity heater, Transaction transaction) {
        var context = SingletonBurningContext.getInstance();
        var fuelStack = heater.container.getItem(0);
        if (!heater.isBurning() && !fuelStack.isEmpty()) {
            var fuelItem = fuelStack.getItem();
            var burning = Burning.of(fuelItem, context);
            if (burning != null) {
                fuelStack.shrink(1);
                if (fuelStack.isEmpty()) {
                    var bucketItem = fuelItem.getCraftingRemainingItem();
                    heater.container.setItem(0, bucketItem == null ? ItemStack.EMPTY : new ItemStack(bucketItem));
                }
                heater.burningStorage.insert(burning.one(), context, transaction);
            }
        }
    }

    public static InventoryStorage getInventoryStorage(
            HeaterBlockEntity entity, Direction direction) {
        return entity.wrappers.computeIfAbsent(direction, key -> InventoryStorage.of(entity.container, key));
    }

    public static BurningStorage getBurningStorage(
            HeaterBlockEntity entity, @SuppressWarnings("java:S1172") Direction direction) {
        return entity.burningStorage;
    }

    public static final void updateConnectedHeaters(Level level, BlockPos pos, BlockState state) {
        new Explorer(level, pos, state, MAX_HOPS)
                .onHeaterFound((h, p) -> level.getBlockEntity(p, HeaterBlockEntityTypes.HEATER)
                        .ifPresent(HeaterBlockEntity::makeDirty))
                .run();
    }
}
