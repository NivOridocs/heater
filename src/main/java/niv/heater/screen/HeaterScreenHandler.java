package niv.heater.screen;

import static niv.heater.Heater.HEATER_SCREEN_HANDLER;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.FurnaceFuelSlot;
import net.minecraft.screen.slot.Slot;

public class HeaterScreenHandler extends ScreenHandler {

    public static final int SLOT_COUNT = 1;

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public HeaterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1), new ArrayPropertyDelegate(2));
    }

    public HeaterScreenHandler(int syncId, PlayerInventory playerInventory,
            Inventory inventory, PropertyDelegate propertyDelegate) {
        super(HEATER_SCREEN_HANDLER, syncId);
        checkSize(inventory, 1);
        checkDataCount(propertyDelegate, 2);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;

        inventory.onOpen(playerInventory.player);

        addSlot(new HeaterFuelSlot(inventory, 0, 80, 44));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        addProperties(propertyDelegate);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        var itemStack = ItemStack.EMPTY;
        var slot = this.slots.get(slotIndex);
        if (slot.hasStack()) {
            var slotStack = slot.getStack();
            itemStack = slotStack.copy();

            if (slotIndex == 0 ? !insertItem(slotStack, inventory.size(), slots.size(), true)
                    : !insertItem(slotStack, 0, inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, slotStack);
        }
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    public boolean isBurning() {
        return this.propertyDelegate.get(0) > 0;
    }

    public int getFuelProgress() {
        int i = this.propertyDelegate.get(1);
        if (i == 0) {
            i = 200;
        }

        return this.propertyDelegate.get(0) * 13 / i;
    }

    private static final class HeaterFuelSlot extends Slot {

        public HeaterFuelSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return AbstractFurnaceBlockEntity.canUseAsFuel(stack) || FurnaceFuelSlot.isBucket(stack);
        }

        @Override
        public int getMaxItemCount(ItemStack stack) {
            return FurnaceFuelSlot.isBucket(stack) ? 1 : super.getMaxItemCount(stack);
        }

    }

}
