package niv.heater;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import niv.heater.screen.HeaterMenu;

public class HeaterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(HeaterMenu.TYPE, HeaterScreen::new);
    }

}
