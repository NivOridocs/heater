package niv.heater;

import static niv.heater.Heater.HEATER_SCREEN_HANDLER;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class HeaterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(HEATER_SCREEN_HANDLER, HeaterScreen::new);
    }

}
