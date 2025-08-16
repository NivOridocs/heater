package niv.heater;

import static net.minecraft.client.data.models.blockstates.Condition.condition;
import static net.minecraft.client.data.models.blockstates.Variant.variant;
import static net.minecraft.client.data.models.blockstates.VariantProperties.MODEL;
import static net.minecraft.client.data.models.blockstates.VariantProperties.X_ROT;
import static net.minecraft.client.data.models.blockstates.VariantProperties.Y_ROT;
import static net.minecraft.client.data.models.blockstates.VariantProperties.Rotation.R0;
import static net.minecraft.client.data.models.blockstates.VariantProperties.Rotation.R180;
import static net.minecraft.client.data.models.blockstates.VariantProperties.Rotation.R270;
import static net.minecraft.client.data.models.blockstates.VariantProperties.Rotation.R90;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOWN;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.EAST;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.NORTH;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.SOUTH;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.UP;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WEST;
import static niv.heater.Heater.MOD_ID;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import niv.heater.block.HeatPipeBlock;
import niv.heater.block.HeaterBlock;
import niv.heater.block.ThermostatBlock;
import niv.heater.block.WeatheringHeatPipeBlock;
import niv.heater.block.WeatheringHeaterBlock;
import niv.heater.block.WeatheringThermostatBlock;
import niv.heater.block.entity.HeaterBlockEntity;
import niv.heater.registry.HeaterBlocks;
import niv.heater.registry.HeaterTabs;
import niv.heater.util.WeatherStateExtra;

public class HeaterDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        HeaterBlocks.initialize();
        HeaterTabs.initialize();

        var pack = fabricDataGenerator.createPack();

        pack.addProvider(HeaterModelProvider::new);
        pack.addProvider(HeaterEnglishLanguageProvider::new);
        pack.addProvider(HeaterLootTableProvider::new);
        pack.addProvider(HeaterRecipeProvider::new);
        pack.addProvider(HeaterTagProvider::new);
    }

    private static class HeaterModelTemplates {
        private HeaterModelTemplates() {
        }

        public static final ModelTemplate THERMOSTAT = create("template_thermostat", null,
                TextureSlot.TOP, TextureSlot.SIDE, TextureSlot.BOTTOM);
        public static final ModelTemplate PIPE_CORE = create("pipe_core", "_core",
                TextureSlot.TEXTURE);
        public static final ModelTemplate PIPE_ARM = create("pipe_arm", "_arm",
                TextureSlot.TEXTURE);

        private static ModelTemplate create(String template, String suffix, TextureSlot... textureSlots) {
            return new ModelTemplate(
                    Optional.of(ResourceLocation.fromNamespaceAndPath(MOD_ID, "block/" + template)),
                    Optional.ofNullable(suffix),
                    textureSlots);
        }
    }

    private static class HeaterModelProvider extends FabricModelProvider {

        private static final TexturedModel.Provider THERMOSTAT = TexturedModel
                .createDefault(HeaterModelProvider::orientableFullTilt, HeaterModelTemplates.THERMOSTAT);
        private static final TexturedModel.Provider PIPE_CORE = TexturedModel
                .createDefault(HeaterModelProvider::pipeCore, HeaterModelTemplates.PIPE_CORE);
        private static final TexturedModel.Provider PIPE_ARM = TexturedModel
                .createDefault(HeaterModelProvider::pipeArm, HeaterModelTemplates.PIPE_ARM);

        public HeaterModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public String getName() {
            return "Heater Block Provider";
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators generator) {
            for (var weathering : WeatherState.values()) {
                createWaxingFurnace(generator,
                        HeaterBlocks.HEATERS.get(weathering),
                        HeaterBlocks.WAXED_HEATERS.get(weathering));

                createWaxingOrientable(generator,
                        HeaterBlocks.THERMOSTATS.get(weathering),
                        HeaterBlocks.WAXED_THERMOSTATS.get(weathering));

                createWaxingPipe(generator,
                        HeaterBlocks.HEAT_PIPES.get(weathering),
                        HeaterBlocks.WAXED_HEAT_PIPES.get(weathering));
            }
        }

        @Override
        public void generateItemModels(ItemModelGenerators generator) {
            // no-op
        }

        private static final void createWaxingFurnace(BlockModelGenerators generator, Block block, Block waxed) {
            var provider = TexturedModel.ORIENTABLE_ONLY_TOP;

            var unlit = provider.create(block, generator.modelOutput);
            var lit = provider.get(block)
                    .updateTextures(mapping -> mapping.put(TextureSlot.FRONT,
                            TextureMapping.getBlockTexture(block, "_front_on")))
                    .createWithSuffix(block, "_on", generator.modelOutput);

            generator.blockStateOutput.accept(MultiVariantGenerator
                    .multiVariant(block)
                    .with(BlockModelGenerators.createBooleanModelDispatch(LIT, lit, unlit))
                    .with(BlockModelGenerators.createHorizontalFacingDispatch()));

            generator.blockStateOutput.accept(MultiVariantGenerator
                    .multiVariant(waxed)
                    .with(BlockModelGenerators.createBooleanModelDispatch(LIT, lit, unlit))
                    .with(BlockModelGenerators.createHorizontalFacingDispatch()));

            generator.itemModelOutput.copy(block.asItem(), waxed.asItem());
        }

        private static final void createWaxingOrientable(BlockModelGenerators generator, Block block, Block waxed) {
            var model = THERMOSTAT.create(block, generator.modelOutput);

            generator.blockStateOutput.accept(MultiVariantGenerator
                    .multiVariant(block, Variant.variant().with(MODEL, model))
                    .with(BlockModelGenerators.createFacingDispatch()));

            generator.blockStateOutput.accept(MultiVariantGenerator
                    .multiVariant(waxed, Variant.variant().with(MODEL, model))
                    .with(BlockModelGenerators.createFacingDispatch()));

            generator.registerSimpleItemModel(block, model);
            generator.registerSimpleItemModel(waxed, model);
        }

        private static final void createWaxingPipe(BlockModelGenerators generator, Block block, Block waxed) {
            var core = PIPE_CORE.create(block, generator.modelOutput);
            var arm = PIPE_ARM.create(block, generator.modelOutput);

            generator.blockStateOutput.accept(MultiPartGenerator.multiPart(block)
                    .with(variant().with(MODEL, core))
                    .with(condition().term(DOWN, true), variant().with(MODEL, arm).with(X_ROT, R90))
                    .with(condition().term(UP, true), variant().with(MODEL, arm).with(X_ROT, R270))
                    .with(condition().term(NORTH, true), variant().with(MODEL, arm).with(Y_ROT, R0))
                    .with(condition().term(SOUTH, true), variant().with(MODEL, arm).with(Y_ROT, R180))
                    .with(condition().term(EAST, true), variant().with(MODEL, arm).with(Y_ROT, R90))
                    .with(condition().term(WEST, true), variant().with(MODEL, arm).with(Y_ROT, R270)));

            generator.blockStateOutput.accept(MultiPartGenerator.multiPart(waxed)
                    .with(variant().with(MODEL, core))
                    .with(condition().term(DOWN, true), variant().with(MODEL, arm).with(X_ROT, R90))
                    .with(condition().term(UP, true), variant().with(MODEL, arm).with(X_ROT, R270))
                    .with(condition().term(NORTH, true), variant().with(MODEL, arm).with(Y_ROT, R0))
                    .with(condition().term(SOUTH, true), variant().with(MODEL, arm).with(Y_ROT, R180))
                    .with(condition().term(EAST, true), variant().with(MODEL, arm).with(Y_ROT, R90))
                    .with(condition().term(WEST, true), variant().with(MODEL, arm).with(Y_ROT, R270)));

            generator.registerSimpleItemModel(block, core);
            generator.registerSimpleItemModel(waxed, core);
        }

        private static TextureMapping orientableFullTilt(Block block) {
            return new TextureMapping()
                    .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
                    .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
                    .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_bottom"));
        }

        private static TextureMapping pipeCore(Block block) {
            return new TextureMapping()
                    .put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block));
        }

        private static TextureMapping pipeArm(Block block) {
            return new TextureMapping()
                    .put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block));
        }
    }

    private static class HeaterEnglishLanguageProvider extends FabricLanguageProvider {

        private HeaterEnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateTranslations(Provider registryLookup, TranslationBuilder builder) {
            addAll(builder, "Heater",
                    HeaterBlocks.WAXED_HEATERS::get, HeaterBlocks.HEATERS::get);

            addAll(builder, "Heat Pipe",
                    HeaterBlocks.WAXED_HEAT_PIPES::get, HeaterBlocks.HEAT_PIPES::get);

            addAll(builder, "Thermostat",
                    HeaterBlocks.WAXED_THERMOSTATS::get, HeaterBlocks.THERMOSTATS::get);

            builder.add(HeaterBlockEntity.CONTAINER_NAME, Heater.MOD_NAME);
            builder.add(HeaterTabs.TAB_NAME, Heater.MOD_NAME);
        }

        @SuppressWarnings("java:S1643")
        private void addAll(TranslationBuilder translationBuilder, String name,
                Function<WeatherState, Block> blocks, Function<WeatherState, Block> weatheringBlocks) {
            for (var state : WeatherState.values()) {
                var value = WeatherStateExtra.toName(state) + name;
                translationBuilder.add(weatheringBlocks.apply(state), value);
                translationBuilder.add(blocks.apply(state), "Waxed " + value);
            }
        }
    }

    private static class HeaterLootTableProvider extends FabricBlockLootTableProvider {

        private HeaterLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generate() {
            HeaterBlocks.HEATERS.values().forEach(this::dropSelf);
            HeaterBlocks.WAXED_HEATERS.values().forEach(this::dropSelf);
            HeaterBlocks.THERMOSTATS.values().forEach(this::dropSelf);
            HeaterBlocks.WAXED_THERMOSTATS.values().forEach(this::dropSelf);
            HeaterBlocks.HEAT_PIPES.values().forEach(this::dropSelf);
            HeaterBlocks.WAXED_HEAT_PIPES.values().forEach(this::dropSelf);
        }
    }

    private static class HeaterRecipeProvider extends FabricRecipeProvider {

        private HeaterRecipeProvider(FabricDataOutput output, CompletableFuture<Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public String getName() {
            return "HeaterRecipeProvider";
        }

        @Override
        protected RecipeProvider createRecipeProvider(Provider provider, RecipeOutput exporter) {
            return new RecipeProvider(provider, exporter) {
                @Override
                public void buildRecipes() {
                    shaped(RecipeCategory.MISC, HeaterBlocks.HEATER)
                            .pattern("ccc")
                            .pattern("cfc")
                            .pattern("ccc")
                            .define('c', Items.COPPER_INGOT)
                            .define('f', Items.FURNACE)
                            .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                            .unlockedBy(getHasName(Items.FURNACE), has(Items.FURNACE))
                            .save(output);

                    for (var state : WeatherState.values()) {
                        generateWaxingRecipe(output,
                                HeaterBlocks.HEATERS.get(state).asItem(),
                                HeaterBlocks.WAXED_HEATERS.get(state).asItem());
                    }

                    shaped(RecipeCategory.MISC, HeaterBlocks.HEAT_PIPE)
                            .pattern("ccc")
                            .define('c', Items.COPPER_INGOT)
                            .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                            .save(output);

                    for (var state : WeatherState.values()) {
                        generateWaxingRecipe(output,
                                HeaterBlocks.HEAT_PIPES.get(state).asItem(),
                                HeaterBlocks.WAXED_HEAT_PIPES.get(state).asItem());
                    }

                    shaped(RecipeCategory.MISC, HeaterBlocks.THERMOSTAT)
                            .pattern("ccc")
                            .pattern("#c#")
                            .pattern("#r#")
                            .define('c', Items.COPPER_INGOT)
                            .define('r', Items.REDSTONE)
                            .define('#', Items.COBBLESTONE)
                            .unlockedBy(getHasName(Items.COBBLESTONE), has(Items.COBBLESTONE))
                            .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                            .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                            .save(output);

                    for (var state : WeatherState.values()) {
                        generateWaxingRecipe(output,
                                HeaterBlocks.THERMOSTATS.get(state).asItem(),
                                HeaterBlocks.WAXED_THERMOSTATS.get(state).asItem());
                    }
                }

                private void generateWaxingRecipe(RecipeOutput output, Item unwaxed, Item waxed) {
                    shapeless(RecipeCategory.MISC, waxed)
                            .requires(unwaxed).requires(Items.HONEYCOMB)
                            .unlockedBy(getHasName(unwaxed), has(unwaxed))
                            .unlockedBy(getHasName(Items.HONEYCOMB), has(Items.HONEYCOMB))
                            .save(output);
                }
            };
        }
    }

    private static class HeaterTagProvider extends BlockTagProvider {

        public HeaterTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {

            getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                    .setReplace(false)
                    // Heaters
                    .add(HeaterBlocks.HEATERS.values().toArray(WeatheringHeaterBlock[]::new))
                    .add(HeaterBlocks.WAXED_HEATERS.values().toArray(HeaterBlock[]::new))
                    // Heat Pipes
                    .add(HeaterBlocks.HEAT_PIPES.values().toArray(WeatheringHeatPipeBlock[]::new))
                    .add(HeaterBlocks.WAXED_HEAT_PIPES.values().toArray(HeatPipeBlock[]::new))
                    // Thermostats
                    .add(HeaterBlocks.THERMOSTATS.values().toArray(WeatheringThermostatBlock[]::new))
                    .add(HeaterBlocks.WAXED_THERMOSTATS.values().toArray(ThermostatBlock[]::new));
        }
    }
}
