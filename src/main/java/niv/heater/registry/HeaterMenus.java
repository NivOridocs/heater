package niv.heater.registry;

import static niv.heater.Heater.MOD_ID;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import niv.heater.screen.HeaterMenu;

public class HeaterMenus {
    private HeaterMenus() {
    }

    public static final MenuType<HeaterMenu> HEATER;

    static {
        HEATER = Registry.register(BuiltInRegistries.MENU, ResourceLocation.tryBuild(MOD_ID, "heater"),
                new MenuType<>(HeaterMenu::new, FeatureFlags.VANILLA_SET));
    }

    public static final void initialize() {
        // Trigger static initialization
    }
}
