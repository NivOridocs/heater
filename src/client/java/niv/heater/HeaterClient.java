package niv.heater;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import niv.heater.registry.HeaterMenus;

public class HeaterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(HeaterMenus.HEATER, HeaterScreen::new);
    }

}
