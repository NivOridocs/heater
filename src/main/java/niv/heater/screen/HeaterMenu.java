package niv.heater.screen;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceFuelSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import niv.heater.block.HeatPipeBlock;
import niv.heater.block.HeaterBlock;
import niv.heater.block.ThermostatBlock;
import niv.heater.block.WeatheringHeatPipeBlock;
import niv.heater.block.WeatheringHeaterBlock;
import niv.heater.block.WeatheringThermostatBlock;

public class HeaterMenu extends AbstractContainerMenu {

    public static final String TAB_NAME = "creative.heater.tab";

    public static final MenuType<HeaterMenu> TYPE = new MenuType<>(HeaterMenu::new, FeatureFlags.VANILLA_SET);

    public static final CreativeModeTab TAB = FabricItemGroup.builder()
            .icon(HeaterBlock.UNAFFECTED_ITEM::getDefaultInstance)
            .title(Component.translatable(TAB_NAME))
            .displayItems((parameters, output) -> {
                WeatheringHeaterBlock.ITEMS.get().values().forEach(output::accept);
                HeaterBlock.ITEMS.get().values().forEach(output::accept);
                WeatheringThermostatBlock.ITEMS.get().values().forEach(output::accept);
                ThermostatBlock.ITEMS.get().values().forEach(output::accept);
                WeatheringHeatPipeBlock.ITEMS.get().values().forEach(output::accept);
                HeatPipeBlock.ITEMS.get().values().forEach(output::accept);
            }).build();

    public static final int SLOT_COUNT = 1;

    private final Container container;
    private final ContainerData containerData;

    public HeaterMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, new SimpleContainer(1), new SimpleContainerData(2));
    }

    public HeaterMenu(int syncId, Inventory inventory,
            Container container, ContainerData containerData) {
        super(TYPE, syncId);
        checkContainerSize(container, 1);
        checkContainerDataCount(containerData, 2);
        this.container = container;
        this.containerData = containerData;

        addSlot(new HeaterFuelSlot(container, 0, 80, 44));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }

        addDataSlots(containerData);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        var itemStack = ItemStack.EMPTY;
        var slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            var slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (slotIndex == 0 ? !moveItemStackTo(slotStack, container.getContainerSize(), slots.size(), true)
                    : !moveItemStackTo(slotStack, 0, container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public boolean isBurning() {
        return this.containerData.get(0) > 0;
    }

    public int getFuelProgress() {
        int i = this.containerData.get(1);
        if (i == 0) {
            i = 200;
        }

        return this.containerData.get(0) * 13 / i;
    }

    private static final class HeaterFuelSlot extends Slot {

        public HeaterFuelSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return AbstractFurnaceBlockEntity.isFuel(stack) || FurnaceFuelSlot.isBucket(stack);
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return FurnaceFuelSlot.isBucket(stack) ? 1 : super.getMaxStackSize(stack);
        }

    }

}
