package niv.heater;

import static net.minecraft.data.client.VariantSettings.Rotation.R0;
import static net.minecraft.data.client.VariantSettings.Rotation.R180;
import static net.minecraft.data.client.VariantSettings.Rotation.R270;
import static net.minecraft.data.client.VariantSettings.Rotation.R90;
import static niv.heater.Heater.HEATER_BLOCK;
import static niv.heater.Heater.HEATER_ITEM;
import static niv.heater.Heater.HEAT_PIPE_BLOCK;
import static niv.heater.Heater.HEAT_PIPE_ITEM;
import static niv.heater.Heater.MOD_ID;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.client.TexturedModel;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.data.client.VariantSettings.Rotation;
import net.minecraft.data.client.When;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class HeaterDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();

        pack.addProvider(BlockModelProvider::new);
        pack.addProvider(EnglishLanguageProvider::new);
        pack.addProvider(LootTableProvider::new);
        pack.addProvider(RecipeProvider::new);
        pack.addProvider(TagProvider::new);
    }

    private static class BlockModelProvider extends FabricModelProvider {

        private static final Rotation[] ROTATIONS = new Rotation[] { R90, R270, R0, R180, R270, R90 };

        private BlockModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            blockStateModelGenerator.registerCooker(HEATER_BLOCK, TexturedModel.ORIENTABLE);

            var supplier = MultipartBlockStateSupplier.create(HEAT_PIPE_BLOCK)
                    .with(BlockStateVariant.create()
                            .put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(HEAT_PIPE_BLOCK, "_core")));

            for (var direction : Direction.values()) {
                supplier.with(
                        When.create()
                                .set(HeatPipeBlock.getProperty(direction), true),
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(HEAT_PIPE_BLOCK, "_arm"))
                                .put(direction.getAxis().isHorizontal() ? VariantSettings.Y : VariantSettings.X,
                                        getRotation(direction)));
            }

            blockStateModelGenerator.blockStateCollector.accept(supplier);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            itemModelGenerator.register(HEATER_ITEM, new Model(
                    Optional.of(new Identifier(MOD_ID, "block/heater")),
                    Optional.empty()));

            itemModelGenerator.register(HEAT_PIPE_ITEM, new Model(
                    Optional.of(new Identifier(MOD_ID, "block/heat_pipe")),
                    Optional.empty()));
        }

        private static Rotation getRotation(Direction direction) {
            return ROTATIONS[direction.getId()];
        }

    }

    private static class EnglishLanguageProvider extends FabricLanguageProvider {

        private EnglishLanguageProvider(FabricDataOutput dataOutput) {
            super(dataOutput, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            translationBuilder.add(HEATER_BLOCK, "Heater");
            translationBuilder.add(HEAT_PIPE_BLOCK, "Heat Pipe");
            translationBuilder.add("container.heater", "Heater");
        }

    }

    private static class LootTableProvider extends FabricBlockLootTableProvider {

        private LootTableProvider(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            addDrop(HEATER_BLOCK, HEATER_ITEM);
            addDrop(HEAT_PIPE_BLOCK, HEAT_PIPE_ITEM);
        }

    }

    private static class RecipeProvider extends FabricRecipeProvider {

        private RecipeProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, HEATER_BLOCK)
                    .pattern("ccc")
                    .pattern("cfc")
                    .pattern("ccc")
                    .input('c', Items.COPPER_INGOT)
                    .input('f', Blocks.FURNACE)
                    .criterion(
                            FabricRecipeProvider.hasItem(Items.COPPER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(Items.COPPER_INGOT))
                    .criterion(
                            FabricRecipeProvider.hasItem(Blocks.FURNACE),
                            FabricRecipeProvider.conditionsFromItem(Blocks.FURNACE))
                    .offerTo(exporter);
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, HEAT_PIPE_BLOCK)
                    .pattern("ccc")
                    .input('c', Items.COPPER_INGOT)
                    .criterion(
                            FabricRecipeProvider.hasItem(Items.COPPER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(Items.COPPER_INGOT))
                    .offerTo(exporter);
        }
    }

    private static class TagProvider extends BlockTagProvider {

        public TagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(WrapperLookup arg) {
            getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                    .setReplace(false)
                    .add(HEATER_BLOCK)
                    .add(HEAT_PIPE_BLOCK);
        }

    }

}
