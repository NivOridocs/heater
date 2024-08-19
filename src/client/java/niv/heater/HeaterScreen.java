package niv.heater;

import static niv.heater.Heater.MOD_ID;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import niv.heater.screen.HeaterMenu;

@Environment(EnvType.CLIENT)
public class HeaterScreen extends AbstractContainerScreen<HeaterMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.tryBuild(MOD_ID, "textures/gui/container/heater.png");

    public HeaterScreen(HeaterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (menu.isBurning()) {
            int k = menu.getFuelProgress();
            guiGraphics.blit(BACKGROUND, i + 80, j + 39 - k, 176, 12 - k, 14, k + 1);
        }
    }

}
