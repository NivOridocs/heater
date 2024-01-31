package niv.heater;

import static net.minecraft.block.Blocks.FURNACE;
import static net.minecraft.data.client.VariantSettings.Rotation.R0;
import static net.minecraft.data.client.VariantSettings.Rotation.R180;
import static net.minecraft.data.client.VariantSettings.Rotation.R270;
import static net.minecraft.data.client.VariantSettings.Rotation.R90;
import static net.minecraft.item.Items.COPPER_INGOT;
import static net.minecraft.item.Items.HONEYCOMB;
import static niv.heater.Heater.EXPOSED_HEATER_BLOCK;
import static niv.heater.Heater.EXPOSED_HEATER_ITEM;
import static niv.heater.Heater.EXPOSED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.EXPOSED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.HEATER_BLOCK;
import static niv.heater.Heater.HEATER_ITEM;
import static niv.heater.Heater.HEAT_PIPE_BLOCK;
import static niv.heater.Heater.HEAT_PIPE_ITEM;
import static niv.heater.Heater.OXIDIZED_HEATER_BLOCK;
import static niv.heater.Heater.OXIDIZED_HEATER_ITEM;
import static niv.heater.Heater.OXIDIZED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.OXIDIZED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.WAXED_EXPOSED_HEATER_BLOCK;
import static niv.heater.Heater.WAXED_EXPOSED_HEATER_ITEM;
import static niv.heater.Heater.WAXED_EXPOSED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.WAXED_EXPOSED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.WAXED_HEATER_BLOCK;
import static niv.heater.Heater.WAXED_HEATER_ITEM;
import static niv.heater.Heater.WAXED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.WAXED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.WAXED_OXIDIZED_HEATER_BLOCK;
import static niv.heater.Heater.WAXED_OXIDIZED_HEATER_ITEM;
import static niv.heater.Heater.WAXED_OXIDIZED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.WAXED_OXIDIZED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.WAXED_WEATHERED_HEATER_BLOCK;
import static niv.heater.Heater.WAXED_WEATHERED_HEATER_ITEM;
import static niv.heater.Heater.WAXED_WEATHERED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.WAXED_WEATHERED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.WEATHERED_HEATER_BLOCK;
import static niv.heater.Heater.WEATHERED_HEATER_ITEM;
import static niv.heater.Heater.WEATHERED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.WEATHERED_HEAT_PIPE_ITEM;

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
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.TexturedModel;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.data.client.VariantSettings.Rotation;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.data.client.When;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
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

        private final Model coreHeatPipeBlock;
        private final Model armHeatPipeBlock;
        private final Model coreHeatPipeItem;

        private BlockModelProvider(FabricDataOutput output) {
            super(output);
            coreHeatPipeBlock = new Model(
                    Optional.of(ModelIds.getBlockSubModelId(HEAT_PIPE_BLOCK, "_base_core")),
                    Optional.empty(), TextureKey.TEXTURE);

            armHeatPipeBlock = new Model(
                    Optional.of(ModelIds.getBlockSubModelId(HEAT_PIPE_BLOCK, "_base_arm")),
                    Optional.empty(), TextureKey.TEXTURE);

            coreHeatPipeItem = new Model(
                    Optional.of(ModelIds.getItemSubModelId(HEAT_PIPE_ITEM, "_core")),
                    Optional.empty(), TextureKey.TEXTURE);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator generator) {
            generateHeaters(generator, HEATER_BLOCK, WAXED_HEATER_BLOCK);
            generateHeaters(generator, EXPOSED_HEATER_BLOCK, WAXED_EXPOSED_HEATER_BLOCK);
            generateHeaters(generator, WEATHERED_HEATER_BLOCK, WAXED_WEATHERED_HEATER_BLOCK);
            generateHeaters(generator, OXIDIZED_HEATER_BLOCK, WAXED_OXIDIZED_HEATER_BLOCK);

            generatePipes(generator, HEAT_PIPE_BLOCK, WAXED_HEAT_PIPE_BLOCK);
            generatePipes(generator, EXPOSED_HEAT_PIPE_BLOCK, WAXED_EXPOSED_HEAT_PIPE_BLOCK);
            generatePipes(generator, WEATHERED_HEAT_PIPE_BLOCK, WAXED_WEATHERED_HEAT_PIPE_BLOCK);
            generatePipes(generator, OXIDIZED_HEAT_PIPE_BLOCK, WAXED_OXIDIZED_HEAT_PIPE_BLOCK);
        }

        private void generateHeaters(BlockStateModelGenerator generator, Block heater, Block waxed) {
            var unlit = TexturedModel.ORIENTABLE.upload(heater, generator.modelCollector);

            var lit = TexturedModel.ORIENTABLE.get(heater)
                    .textures(textures -> textures.put(TextureKey.FRONT, TextureMap.getSubId(heater, "_front_on")))
                    .upload(heater, "_on", generator.modelCollector);

            generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(heater)
                    .coordinate(BlockStateModelGenerator
                            .createBooleanModelMap(Properties.LIT, lit, unlit))
                    .coordinate(BlockStateModelGenerator
                            .createNorthDefaultHorizontalRotationStates()));

            generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(waxed)
                    .coordinate(BlockStateModelGenerator
                            .createBooleanModelMap(Properties.LIT, lit, unlit))
                    .coordinate(BlockStateModelGenerator
                            .createNorthDefaultHorizontalRotationStates()));
        }

        private void generatePipes(BlockStateModelGenerator generator, Block pipe, Block waxed) {
            var coreId = coreHeatPipeBlock.upload(pipe, "_core", TextureMap.texture(pipe), generator.modelCollector);
            var armId = armHeatPipeBlock.upload(pipe, "_arm", TextureMap.texture(pipe), generator.modelCollector);
            generatePipe(generator, pipe, coreId, armId);
            generatePipe(generator, waxed, coreId, armId);
        }

        private void generatePipe(BlockStateModelGenerator generator, Block pipe, Identifier core, Identifier arm) {
            var supplier = MultipartBlockStateSupplier.create(pipe)
                    .with(BlockStateVariant.create().put(VariantSettings.MODEL, core));
            for (var direction : Direction.values()) {
                supplier.with(
                        When.create().set(HeatPipeBlock.getProperty(direction), true),
                        BlockStateVariant.create()
                                .put(VariantSettings.MODEL, arm)
                                .put(
                                        direction.getAxis().isHorizontal() ? VariantSettings.Y : VariantSettings.X,
                                        getRotation(direction)));
            }
            generator.blockStateCollector.accept(supplier);
        }

        private static Rotation getRotation(Direction direction) {
            return ROTATIONS[direction.getId()];
        }

        @Override
        public void generateItemModels(ItemModelGenerator generator) {
            generator.register(HEATER_ITEM, new Model(
                    Optional.of(ModelIds.getBlockModelId(HEATER_BLOCK)), Optional.empty()));
            generator.register(EXPOSED_HEATER_ITEM, new Model(
                    Optional.of(ModelIds.getBlockModelId(EXPOSED_HEATER_BLOCK)), Optional.empty()));
            generator.register(WEATHERED_HEATER_ITEM, new Model(
                    Optional.of(ModelIds.getBlockModelId(WEATHERED_HEATER_BLOCK)), Optional.empty()));
            generator.register(OXIDIZED_HEATER_ITEM, new Model(
                    Optional.of(ModelIds.getBlockModelId(OXIDIZED_HEATER_BLOCK)), Optional.empty()));
            generator.register(WAXED_HEATER_ITEM, new Model(
                    Optional.of(ModelIds.getBlockModelId(HEATER_BLOCK)), Optional.empty()));
            generator.register(WAXED_EXPOSED_HEATER_ITEM, new Model(
                    Optional.of(ModelIds.getBlockModelId(EXPOSED_HEATER_BLOCK)), Optional.empty()));
            generator.register(WAXED_WEATHERED_HEATER_ITEM, new Model(
                    Optional.of(ModelIds.getBlockModelId(WEATHERED_HEATER_BLOCK)), Optional.empty()));
            generator.register(WAXED_OXIDIZED_HEATER_ITEM, new Model(
                    Optional.of(ModelIds.getBlockModelId(OXIDIZED_HEATER_BLOCK)), Optional.empty()));

            coreHeatPipeItem.upload(ModelIds.getItemModelId(HEAT_PIPE_ITEM),
                    TextureMap.texture(HEAT_PIPE_BLOCK), generator.writer);
            coreHeatPipeItem.upload(ModelIds.getItemModelId(EXPOSED_HEAT_PIPE_ITEM),
                    TextureMap.texture(EXPOSED_HEAT_PIPE_BLOCK), generator.writer);
            coreHeatPipeItem.upload(ModelIds.getItemModelId(WEATHERED_HEAT_PIPE_ITEM),
                    TextureMap.texture(WEATHERED_HEAT_PIPE_BLOCK), generator.writer);
            coreHeatPipeItem.upload(ModelIds.getItemModelId(OXIDIZED_HEAT_PIPE_ITEM),
                    TextureMap.texture(OXIDIZED_HEAT_PIPE_BLOCK), generator.writer);
            coreHeatPipeItem.upload(ModelIds.getItemModelId(WAXED_HEAT_PIPE_ITEM),
                    TextureMap.texture(HEAT_PIPE_BLOCK), generator.writer);
            coreHeatPipeItem.upload(ModelIds.getItemModelId(WAXED_EXPOSED_HEAT_PIPE_ITEM),
                    TextureMap.texture(EXPOSED_HEAT_PIPE_BLOCK), generator.writer);
            coreHeatPipeItem.upload(ModelIds.getItemModelId(WAXED_WEATHERED_HEAT_PIPE_ITEM),
                    TextureMap.texture(WEATHERED_HEAT_PIPE_BLOCK), generator.writer);
            coreHeatPipeItem.upload(ModelIds.getItemModelId(WAXED_OXIDIZED_HEAT_PIPE_ITEM),
                    TextureMap.texture(OXIDIZED_HEAT_PIPE_BLOCK), generator.writer);
        }

    }

    private static class EnglishLanguageProvider extends FabricLanguageProvider {

        private EnglishLanguageProvider(FabricDataOutput dataOutput) {
            super(dataOutput, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {

            translationBuilder.add(HEATER_BLOCK, "Heater");
            translationBuilder.add(EXPOSED_HEATER_BLOCK, "Exposed Heater");
            translationBuilder.add(WEATHERED_HEATER_BLOCK, "Weathered Heater");
            translationBuilder.add(OXIDIZED_HEATER_BLOCK, "Oxidized Heater");

            translationBuilder.add(WAXED_HEATER_BLOCK, "Waxed Heater");
            translationBuilder.add(WAXED_EXPOSED_HEATER_BLOCK, "Waxed Exposed Heater");
            translationBuilder.add(WAXED_WEATHERED_HEATER_BLOCK, "Waxed Weathered Heater");
            translationBuilder.add(WAXED_OXIDIZED_HEATER_BLOCK, "Waxed Oxidized Heater");

            translationBuilder.add(HEAT_PIPE_BLOCK, "Heat Pipe");
            translationBuilder.add(EXPOSED_HEAT_PIPE_BLOCK, "Exposed Heat Pipe");
            translationBuilder.add(WEATHERED_HEAT_PIPE_BLOCK, "Weathered Heat Pipe");
            translationBuilder.add(OXIDIZED_HEAT_PIPE_BLOCK, "Oxidized Heat Pipe");

            translationBuilder.add(WAXED_HEAT_PIPE_BLOCK, "Waxed Heat Pipe");
            translationBuilder.add(WAXED_EXPOSED_HEAT_PIPE_BLOCK, "Waxed Exposed Heat Pipe");
            translationBuilder.add(WAXED_WEATHERED_HEAT_PIPE_BLOCK, "Waxed Weathered Heat Pipe");
            translationBuilder.add(WAXED_OXIDIZED_HEAT_PIPE_BLOCK, "Waxed Oxidized Heat Pipe");

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
            addDrop(EXPOSED_HEATER_BLOCK, EXPOSED_HEATER_ITEM);
            addDrop(WEATHERED_HEATER_BLOCK, WEATHERED_HEATER_ITEM);
            addDrop(OXIDIZED_HEATER_BLOCK, OXIDIZED_HEATER_ITEM);

            addDrop(WAXED_HEATER_BLOCK, WAXED_HEATER_ITEM);
            addDrop(WAXED_EXPOSED_HEATER_BLOCK, WAXED_EXPOSED_HEATER_ITEM);
            addDrop(WAXED_WEATHERED_HEATER_BLOCK, WAXED_WEATHERED_HEATER_ITEM);
            addDrop(WAXED_OXIDIZED_HEATER_BLOCK, WAXED_OXIDIZED_HEATER_ITEM);

            addDrop(HEAT_PIPE_BLOCK, HEAT_PIPE_ITEM);
            addDrop(EXPOSED_HEAT_PIPE_BLOCK, EXPOSED_HEAT_PIPE_ITEM);
            addDrop(WEATHERED_HEAT_PIPE_BLOCK, WEATHERED_HEAT_PIPE_ITEM);
            addDrop(OXIDIZED_HEAT_PIPE_BLOCK, OXIDIZED_HEAT_PIPE_ITEM);

            addDrop(WAXED_HEAT_PIPE_BLOCK, WAXED_HEAT_PIPE_ITEM);
            addDrop(WAXED_EXPOSED_HEAT_PIPE_BLOCK, WAXED_EXPOSED_HEAT_PIPE_ITEM);
            addDrop(WAXED_WEATHERED_HEAT_PIPE_BLOCK, WAXED_WEATHERED_HEAT_PIPE_ITEM);
            addDrop(WAXED_OXIDIZED_HEAT_PIPE_BLOCK, WAXED_OXIDIZED_HEAT_PIPE_ITEM);
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
                    .input('c', COPPER_INGOT)
                    .input('f', FURNACE)
                    .criterion(hasItem(COPPER_INGOT), conditionsFromItem(COPPER_INGOT))
                    .criterion(hasItem(FURNACE), conditionsFromItem(FURNACE))
                    .offerTo(exporter);

            generateWaxingRecipe(exporter, HEATER_ITEM, WAXED_HEATER_ITEM);
            generateWaxingRecipe(exporter, EXPOSED_HEATER_ITEM, WAXED_EXPOSED_HEATER_ITEM);
            generateWaxingRecipe(exporter, WEATHERED_HEATER_ITEM, WAXED_WEATHERED_HEATER_ITEM);
            generateWaxingRecipe(exporter, OXIDIZED_HEATER_ITEM, WAXED_OXIDIZED_HEATER_ITEM);

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, HEAT_PIPE_BLOCK)
                    .pattern("ccc")
                    .input('c', COPPER_INGOT)
                    .criterion(hasItem(COPPER_INGOT), conditionsFromItem(COPPER_INGOT))
                    .offerTo(exporter);

            generateWaxingRecipe(exporter, HEAT_PIPE_ITEM, WAXED_HEAT_PIPE_ITEM);
            generateWaxingRecipe(exporter, EXPOSED_HEAT_PIPE_ITEM, WAXED_EXPOSED_HEAT_PIPE_ITEM);
            generateWaxingRecipe(exporter, WEATHERED_HEAT_PIPE_ITEM, WAXED_WEATHERED_HEAT_PIPE_ITEM);
            generateWaxingRecipe(exporter, OXIDIZED_HEAT_PIPE_ITEM, WAXED_OXIDIZED_HEAT_PIPE_ITEM);
        }

        private void generateWaxingRecipe(Consumer<RecipeJsonProvider> exporter, Item unwaxed, Item waxed) {
            ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, waxed)
                    .input(unwaxed).input(HONEYCOMB)
                    .criterion(hasItem(unwaxed), conditionsFromItem(unwaxed))
                    .criterion(hasItem(HONEYCOMB), conditionsFromItem(HONEYCOMB))
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
                    .add(
                            HEATER_BLOCK, EXPOSED_HEATER_BLOCK,
                            WEATHERED_HEATER_BLOCK, OXIDIZED_HEATER_BLOCK,
                            WAXED_HEATER_BLOCK, WAXED_EXPOSED_HEATER_BLOCK,
                            WAXED_WEATHERED_HEATER_BLOCK, WAXED_OXIDIZED_HEATER_BLOCK,
                            HEAT_PIPE_BLOCK, EXPOSED_HEAT_PIPE_BLOCK,
                            WEATHERED_HEAT_PIPE_BLOCK, OXIDIZED_HEAT_PIPE_BLOCK,
                            WAXED_HEAT_PIPE_BLOCK, WAXED_EXPOSED_HEAT_PIPE_BLOCK,
                            WAXED_WEATHERED_HEAT_PIPE_BLOCK, WAXED_OXIDIZED_HEAT_PIPE_BLOCK);
        }

    }

}
