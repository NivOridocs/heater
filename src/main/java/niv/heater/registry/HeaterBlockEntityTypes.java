package niv.heater.registry;

import static niv.heater.Heater.MOD_ID;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import niv.burning.api.BurningStorage;
import niv.heater.block.entity.HeaterBlockEntity;

public class HeaterBlockEntityTypes {
    private HeaterBlockEntityTypes() {
    }

    public static final BlockEntityType<HeaterBlockEntity> HEATER;

    static {
        HEATER = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.tryBuild(MOD_ID, "heater"),
                FabricBlockEntityTypeBuilder.create(HeaterBlockEntity::new,
                        HeaterBlocks.WAXED_HEATER,
                        HeaterBlocks.WAXED_EXPOSED_HEATER,
                        HeaterBlocks.WAXED_WEATHERED_HEATER,
                        HeaterBlocks.WAXED_OXIDIZED_HEATER,
                        HeaterBlocks.HEATER,
                        HeaterBlocks.EXPOSED_HEATER,
                        HeaterBlocks.WEATHERED_HEATER,
                        HeaterBlocks.OXIDIZED_HEATER)
                        .build());

        ItemStorage.SIDED.registerForBlockEntity(HeaterBlockEntity::getInventoryStorage, HEATER);
        BurningStorage.SIDED.registerForBlockEntity(HeaterBlockEntity::getBurningStorage, HEATER);
    }

    public static final void initialize() {
        // Trigger static initialization
    }
}
