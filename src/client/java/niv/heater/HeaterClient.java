package niv.heater;

import static niv.heater.Heater.HEATER_MENU_TYPE;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class HeaterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(HEATER_MENU_TYPE, HeaterScreen::new);
    }

}
