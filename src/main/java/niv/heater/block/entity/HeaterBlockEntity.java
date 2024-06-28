package niv.heater.block.entity;

import static net.minecraft.world.level.block.AbstractFurnaceBlock.LIT;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import niv.heater.block.HeaterBlock;
import niv.heater.block.WeatheringHeaterBlock;
import niv.heater.screen.HeaterMenu;
import niv.heater.util.HeatSink;
import niv.heater.util.Propagator;

public class HeaterBlockEntity extends BaseContainerBlockEntity implements HeatSink {

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

    private static final int MAX_HEAT = 63;

    private int burnTime;

    private int fuelTime;

    private NonNullList<ItemStack> items;

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
        items = NonNullList.withSize(1, ItemStack.EMPTY);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.items, slot, amount);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) {
            return AbstractFurnaceBlockEntity.isFuel(stack)
                    || stack.is(Items.BUCKET) && !items.get(0).is(Items.BUCKET);
        }
        return true;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory inventory) {
        return new HeaterMenu(syncId, inventory, this, containerData);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(CONTAINER_NAME);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, items);
        burnTime = compoundTag.getShort("BurnTime");
        fuelTime = this.getFuelTime(this.items.get(0));
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.putShort("BurnTime", (short) burnTime);
        ContainerHelper.saveAllItems(compoundTag, items);
    }

    @Override
    public int getBurnTime() {
        return burnTime;
    }

    @Override
    public void setBurnTime(int value) {
        burnTime = value;
    }

    @Override
    public int getFuelTime() {
        return fuelTime;
    }

    @Override
    public void setFuelTime(int value) {
        fuelTime = value;
    }

    private boolean isBurning() {
        return burnTime > 0;
    }

    private int getFuelTime(ItemStack fuel) {
        return fuel.isEmpty() ? 0
                : AbstractFurnaceBlockEntity.getFuel()
                        .getOrDefault(fuel.getItem(), 0);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HeaterBlockEntity heater) {
        var wasBurning = heater.isBurning();

        if (heater.isBurning()) {
            propagateBurnTime(level, pos, heater);
        }

        if (heater.isBurning() && level.getBlockState(pos).getBlock() instanceof HeaterBlock block) {
            heater.burnTime = block.reducedHeat(heater.burnTime);
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

    private static void propagateBurnTime(Level level, BlockPos pos, HeaterBlockEntity heater) {
        var propagator = new Propagator(level, pos, MAX_HEAT);
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
            var wasBurning = target.entity().getBurnTime() > 0;

            if (deltaBurn > heater.burnTime) {
                deltaBurn = heater.burnTime;
            }

            if (target.entity().getFuelTime() < heater.fuelTime) {
                target.entity().setFuelTime(heater.fuelTime);
            }

            if (target.entity().getBurnTime() + deltaBurn <= target.entity().getFuelTime()) {
                heater.burnTime -= deltaBurn;
                target.entity().setBurnTime(target.entity().getBurnTime() + deltaBurn);

                var isBurning = target.entity().getBurnTime() > 0;
                if (wasBurning != isBurning) {
                    var state = target.state().setValue(LIT, isBurning);
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
        var fuelStack = heater.items.get(0);
        var hasFuel = !fuelStack.isEmpty();
        if (!heater.isBurning() && hasFuel) {
            var fuelTime = heater.getFuelTime(fuelStack);
            if (fuelTime > 0) {
                var fuelItem = fuelStack.getItem();
                fuelStack.shrink(1);
                if (fuelStack.isEmpty()) {
                    var bucketItem = fuelItem.getCraftingRemainingItem();
                    heater.items.set(0, bucketItem == null ? ItemStack.EMPTY : new ItemStack(bucketItem));
                }
                heater.fuelTime = heater.burnTime = fuelTime;
            }
        }
    }

}
