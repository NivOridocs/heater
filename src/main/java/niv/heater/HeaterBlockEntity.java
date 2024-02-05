package niv.heater;

import static net.minecraft.block.AbstractFurnaceBlock.LIT;
import static net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED;
import static net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED;
import static net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED;
import static net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED;

import java.util.Optional;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable.OxidationLevel;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HeaterBlockEntity extends LockableContainerBlockEntity {

    public static final int BURN_TIME_PROPERTY_INDEX = 0;
    public static final int FUEL_TIME_PROPERTY_INDEX = 1;

    private static final int[] OXIDATION_WASTE_MAP = new int[4];

    private static final int MAX_HEAT = 63;

    static {
        OXIDATION_WASTE_MAP[UNAFFECTED.ordinal()] = 1;
        OXIDATION_WASTE_MAP[EXPOSED.ordinal()] = 2;
        OXIDATION_WASTE_MAP[WEATHERED.ordinal()] = 3;
        OXIDATION_WASTE_MAP[OXIDIZED.ordinal()] = 4;
    }

    private static final int mapToWaste(OxidationLevel oxidationLevel) {
        return OXIDATION_WASTE_MAP[oxidationLevel.ordinal()];
    }

    private int burnTime;

    private int fuelTime;

    private DefaultedList<ItemStack> inventory;

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {

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
        public int size() {
            return 2;
        }

    };

    public HeaterBlockEntity(BlockPos pos, BlockState state) {
        super(Heater.HEATER_BLOCK_ENTITY, pos, state);
        burnTime = 0;
        inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(inventory, slot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0) {
            return AbstractFurnaceBlockEntity.canUseAsFuel(stack)
                    || stack.isOf(Items.BUCKET) && !inventory.get(0).isOf(Items.BUCKET);
        }
        return true;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new HeaterScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.heater");
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, inventory);
        burnTime = nbt.getShort("BurnTime");
        fuelTime = this.getFuelTime(this.inventory.get(0));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("BurnTime", (short) burnTime);
        Inventories.writeNbt(nbt, inventory);
    }

    private boolean isBurning() {
        return burnTime > 0;
    }

    private int getFuelTime(ItemStack fuel) {
        return fuel.isEmpty() ? 0
                : AbstractFurnaceBlockEntity.createFuelTimeMap()
                        .getOrDefault(fuel.getItem(), 0);
    }

    public static void tick(World world, BlockPos pos, BlockState state, HeaterBlockEntity heater) {
        var wasBurning = heater.isBurning();
        var dirty = false;

        if (heater.isBurning()) {
            propagateBurnTime(world, pos, heater);
        }

        if (heater.isBurning() && world.getBlockState(pos).getBlock() instanceof HeaterBlock block) {
            heater.burnTime -= mapToWaste(block.getOxidationLevel());
            if (heater.burnTime < 0) {
                heater.burnTime = 0;
            }
        }

        dirty = consumeFuel(heater, dirty);

        if (wasBurning != heater.isBurning()) {
            dirty = true;
            state = state.with(AbstractFurnaceBlock.LIT, heater.isBurning());
            world.setBlockState(pos, state);
        }

        if (dirty) {
            markDirty(world, pos, state);
        }
    }

    private static void propagateBurnTime(World world, BlockPos pos, HeaterBlockEntity heater) {

        var propagator = new Propagator<AbstractFurnaceBlockEntity>(world, pos, MAX_HEAT,
                HeaterBlockEntity::cast, HeaterBlockEntity::compare, HeaterBlockEntity::mapToWaste);
        propagator.run();
        var targets = propagator.get();

        if (targets.isEmpty()) {
            return;
        }

        var deltaBurn = heater.burnTime / targets.size();
        if (heater.burnTime % targets.size() > 0) {
            deltaBurn += 1;
        }

        for (var target : targets) {
            if (!propagateTo(heater, world, target.pos(), target.state(), target.entity(), deltaBurn)) {
                break;
            }
        }
    }

    private static Optional<AbstractFurnaceBlockEntity> cast(BlockEntity entity) {
        if (entity instanceof AbstractFurnaceBlockEntity furnace) {
            return Optional.of(furnace);
        } else {
            return Optional.empty();
        }
    }

    private static int compare(AbstractFurnaceBlockEntity a, AbstractFurnaceBlockEntity b) {
        return Integer.compare(b.burnTime, a.burnTime);
    }

    private static boolean propagateTo(HeaterBlockEntity heater, World world, BlockPos furnacePos,
            BlockState furnaceState, AbstractFurnaceBlockEntity furnace, int deltaBurn) {
        var wasBurning = furnace.burnTime > 0;

        if (deltaBurn > heater.burnTime) {
            deltaBurn = heater.burnTime;
        }

        if (furnace.fuelTime < heater.fuelTime) {
            furnace.fuelTime = heater.fuelTime;
        }

        if (furnace.burnTime + deltaBurn > furnace.fuelTime) {
            return true;
        }

        heater.burnTime -= deltaBurn;
        furnace.burnTime += deltaBurn;

        var isBurning = furnace.burnTime > 0;
        if (wasBurning != isBurning) {
            furnaceState = furnaceState.with(LIT, isBurning);
            world.setBlockState(furnacePos, furnaceState);
            markDirty(world, furnacePos, furnaceState);
        }

        if (heater.burnTime <= 0) {
            heater.burnTime = 0;
            return false;
        }

        return true;
    }

    private static boolean consumeFuel(HeaterBlockEntity heater, boolean dirty) {
        var fuelStack = heater.inventory.get(0);
        var hasFuel = !fuelStack.isEmpty();
        if (!heater.isBurning() && hasFuel) {
            heater.fuelTime = heater.burnTime = heater.getFuelTime(fuelStack);
            if (heater.isBurning()) {
                dirty = true;
                if (hasFuel) {
                    var fuelItem = fuelStack.getItem();
                    fuelStack.decrement(1);
                    if (fuelStack.isEmpty()) {
                        var bucketItem = fuelItem.getRecipeRemainder();
                        heater.inventory.set(0, bucketItem == null ? ItemStack.EMPTY : new ItemStack(bucketItem));
                    }
                }
            }
        }
        return dirty;
    }

}
