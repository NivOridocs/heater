package niv.heater.util;

import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class HeaterContainer extends SimpleContainer implements WorldlyContainer {

    private static final int[] SLOTS = new int[] { 0 };

    public HeaterContainer() {
        super(1);
    }

    // For {@link SimpleContainer}

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) {
            return AbstractFurnaceBlockEntity.isFuel(stack)
                    || stack.is(Items.BUCKET) && !items.get(0).is(Items.BUCKET);
        }
        return true;
    }

    // For {@link WorldlyContainer}

    @Override
    public int[] getSlotsForFace(Direction var1) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction face) {
        return this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction face) {
        return face != Direction.DOWN || stack.is(Items.WATER_BUCKET) || stack.is(Items.BUCKET);
    }
}
