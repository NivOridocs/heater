package niv.heater.block.entity;

import static niv.heater.util.WeatherStateExtra.burningReduction;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import niv.burning.api.Burning;
import niv.burning.api.BurningContext;
import niv.burning.api.BurningStorage;
import niv.burning.api.BurningStorageHelper;
import niv.burning.api.BurningStorageListener;
import niv.burning.api.base.SimpleBurningStorage;
import niv.burning.impl.FuelValuesBurningContext;
import niv.heater.block.HeaterBlock;
import niv.heater.registry.HeaterBlockEntityTypes;
import niv.heater.screen.HeaterMenu;
import niv.heater.util.Explorer;

public class HeaterBlockEntity extends BlockEntity
        implements BurningStorageListener, ContainerListener, MenuProvider, Nameable, WorldlyContainer {

    public static final String CONTAINER_NAME = "container.heater";

    public static final int BURN_TIME_PROPERTY_INDEX = 0;
    public static final int FUEL_TIME_PROPERTY_INDEX = 1;

    private static final String CUSTOM_NAME_TAG = "CustomName";
    private static final String ITEM_TAG = "Item";
    private static final String BURNING_TAG = "Burning";

    private static final int[] SLOTS = new int[] { 0 };

    private static final int MAX_HOPS = 64;

    private final ContainerData burningData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return HeaterBlockEntity.this.burningStorage.getCurrentBurning();
                case 1:
                    return HeaterBlockEntity.this.burningStorage.getMaxBurning();
                default:
                    throw new IndexOutOfBoundsException(index);
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    HeaterBlockEntity.this.burningStorage.setCurrentBurning(value);
                    break;
                case 1:
                    HeaterBlockEntity.this.burningStorage.setMaxBurning(value);
                    break;
                default:
                    throw new IndexOutOfBoundsException(index);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    private final SimpleContainer container;

    private final SimpleBurningStorage burningStorage;

    private final EnumMap<Direction, InventoryStorage> wrappers;

    private final Set<BlockPos> cache;

    private final AtomicBoolean dirty;

    private LockCode lock;

    @Nullable
    private Component name;

    public HeaterBlockEntity(BlockPos pos, BlockState state) {
        super(HeaterBlockEntityTypes.HEATER, pos, state);
        this.container = new SimpleContainer(1) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                if (slot == 0) {
                    return HeaterBlockEntity.this.getLevel().fuelValues().isFuel(stack)
                            || stack.is(Items.BUCKET) && !this.items.get(0).is(Items.BUCKET);
                }
                return true;
            }
        };
        this.burningStorage = new SimpleBurningStorage();
        this.wrappers = new EnumMap<>(Direction.class);
        this.cache = new HashSet<>();
        this.dirty = new AtomicBoolean(true);
        this.lock = LockCode.NO_LOCK;
        this.name = null;

        this.burningStorage.addListener(this);
        this.container.addListener(this);
    }

    public boolean isBurning() {
        return this.burningStorage.getCurrentBurning() > 0;
    }

    public void makeDirty() {
        this.dirty.set(true);
    }

    // For {@link BlockEntity}

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.lock = LockCode.fromTag(input);
        this.name = input.read(CUSTOM_NAME_TAG, ComponentSerialization.CODEC).orElse(null);
        this.container.setItem(0, input.read(ITEM_TAG, ItemStack.CODEC).orElse(ItemStack.EMPTY));
        input.read(BURNING_TAG, SimpleBurningStorage.SNAPSHOT_CODEC).ifPresent(this.burningStorage::readSnapshot);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.lock.addToTag(output);
        output.storeNullable(CUSTOM_NAME_TAG, ComponentSerialization.CODEC, this.name);
        var fuel = this.container.getItem(0);
        if (!fuel.isEmpty()) {
            output.store(ITEM_TAG, ItemStack.CODEC, fuel);
        }
        output.store(BURNING_TAG, SimpleBurningStorage.SNAPSHOT_CODEC, this.burningStorage.createSnapshot());
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter getter) {
        super.applyImplicitComponents(getter);
        this.name = getter.get(DataComponents.CUSTOM_NAME);
        this.lock = getter.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
        getter.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.container.getItems());
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

    // For {@link BurningStorageListener}

    @Override
    public void burningStorageChanged(BurningStorage storage) {
        BurningStorageHelper.tryUpdateLitProperty(this, storage);
        this.setChanged();
    }

    // For {@link ContainerListener}

    @Override
    public void containerChanged(Container container) {
        this.setChanged();
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

    // For {@link WorldlyContainer}

    @Override
    public int getContainerSize() {
        return this.container.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return this.container.isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return this.container.getItem(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return this.container.removeItem(i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return this.container.removeItemNoUpdate(i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        this.container.setItem(i, itemStack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.container.clearContent();
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return this.container.canPlaceItem(i, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return direction != Direction.DOWN || itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.BUCKET);
    }

    // Static

    public static void tick(Level level, BlockPos pos, BlockState state, HeaterBlockEntity heater) {
        var context = new FuelValuesBurningContext(level.fuelValues());
        try (var transaction = Transaction.openOuter()) {

            if (heater.isBurning()) {
                if (heater.dirty.compareAndSet(true, false)) {
                    heater.cache.clear();
                    new Explorer(level, pos, level.getBlockState(pos), MAX_HOPS)
                            .onBurningStorageCallback((s, p) -> heater.cache.add(p))
                            .run();
                }
                propagateBurnTime(level, heater, context, transaction);
            }

            if (heater.isBurning() && state.getBlock() instanceof HeaterBlock block) {
                heater.burningStorage.extract(
                        burningReduction(heater.burningStorage.getBurning(context), block.getAge(), context),
                        context,
                        transaction);
            }

            consumeFuel(heater, context, transaction);

            transaction.commit();
        }
    }

    private static void propagateBurnTime(Level level, HeaterBlockEntity heater, BurningContext context,
            Transaction transaction) {
        var set = new TreeSet<BurningStorage>((a, b) -> Double.compare(
                a.getBurning(context).getReverseValue(context),
                b.getBurning(context).getReverseValue(context)));
        for (var pos : heater.cache) {
            var storage = BurningStorage.SIDED.find(level, pos, null);
            if (storage != null && storage.supportsInsertion()) {
                set.add(storage);
            }
        }
        var storages = set.toArray(BurningStorage[]::new);
        storages = Arrays.copyOf(storages, Math.min(storages.length, heater.burningStorage.getCurrentBurning()));

        if (storages.length > 0) {
            var deltaBurning = heater.burningStorage.getBurning(context)
                    .withValue(Math.round(heater.burningStorage.getCurrentBurning() * 1f / storages.length), context);

            for (var storage : storages) {
                if (storage != null) {
                    BurningStorage.transfer(heater.burningStorage, storage, deltaBurning, context, transaction);
                    if (!heater.isBurning()) {
                        break;
                    }
                }
            }
        }
    }

    private static void consumeFuel(HeaterBlockEntity heater, BurningContext context, Transaction transaction) {
        var fuelStack = heater.container.getItem(0);
        if (!heater.isBurning() && !fuelStack.isEmpty()) {
            var fuelItem = fuelStack.getItem();
            var burning = Burning.of(fuelItem, context);
            if (burning != null) {
                fuelStack.shrink(1);
                if (fuelStack.isEmpty()) {
                    var bucketItem = fuelItem.getCraftingRemainder();
                    heater.container.setItem(0, bucketItem == null ? ItemStack.EMPTY : bucketItem);
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
