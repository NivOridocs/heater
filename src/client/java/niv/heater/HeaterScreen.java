package niv.heater;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static niv.heater.Heater.MOD_ID;

@Environment(EnvType.CLIENT)
public class HeaterScreen extends HandledScreen<HeaterScreenHandler> {
    private static final Identifier BACKGROUND = new Identifier(MOD_ID, "textures/gui/container/heater.png");

    public HeaterScreen(HeaterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = x;
        int j = y;
        context.drawTexture(BACKGROUND, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (handler.isBurning()) {
            int k = handler.getFuelProgress();
            context.drawTexture(BACKGROUND, i + 80, j + 39 - k, 176, 12 - k, 14, k + 1);
        }
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return mouseX < left || mouseY < top
                || mouseX >= (left + backgroundWidth)
                || mouseY >= (top + this.backgroundHeight);
    }

}
