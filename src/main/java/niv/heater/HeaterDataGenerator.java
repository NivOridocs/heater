package niv.heater;

import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.R0;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.R180;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.R270;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.R90;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.blockstates.VariantProperties.Rotation;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import niv.heater.block.HeatPipeBlock;
import niv.heater.block.HeaterBlock;
import niv.heater.block.ThermostatBlock;
import niv.heater.block.WeatheringHeatPipeBlock;
import niv.heater.block.WeatheringHeaterBlock;
import niv.heater.block.WeatheringThermostatBlock;
import niv.heater.block.entity.HeaterBlockEntity;
import niv.heater.screen.HeaterMenu;
import niv.heater.util.WeatherStateExtra;

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

        private static final String INVENTORY = "_inventory";

        private static final Rotation[] ROTATIONS = new Rotation[] { R90, R270, R0, R180, R270, R90 };

        private static final TexturedModel.Provider THERMOSTAT = TexturedModel
                .createDefault(BlockModelProvider::thermostat, ModelTemplates.PISTON);
        private static final TexturedModel.Provider THERMOSTAT_INVENTORY = TexturedModel
                .createDefault(BlockModelProvider::thermostatInventory, ModelTemplates.CUBE_BOTTOM_TOP);

        private final ModelTemplate coreHeatPipeBlock;
        private final ModelTemplate armHeatPipeBlock;
        private final ModelTemplate coreHeatPipeItem;

        private BlockModelProvider(FabricDataOutput output) {
            super(output);
            coreHeatPipeBlock = new ModelTemplate(
                    Optional.of(ModelLocationUtils
                            .getModelLocation(WeatheringHeatPipeBlock.UNAFFECTED_BLOCK, "_base_core")),
                    Optional.empty(), TextureSlot.TEXTURE);

            armHeatPipeBlock = new ModelTemplate(
                    Optional.of(ModelLocationUtils
                            .getModelLocation(WeatheringHeatPipeBlock.UNAFFECTED_BLOCK, "_base_arm")),
                    Optional.empty(), TextureSlot.TEXTURE);

            coreHeatPipeItem = new ModelTemplate(
                    Optional.of(ModelLocationUtils
                            .getModelLocation(WeatheringHeatPipeBlock.UNAFFECTED_ITEM, "_core")),
                    Optional.empty(), TextureSlot.TEXTURE);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators generator) {

            for (var state : WeatherState.values()) {
                generateHeaters(generator,
                        WeatheringHeaterBlock.BLOCKS.get().get(state),
                        HeaterBlock.BLOCKS.get().get(state));

                generatePipes(generator,
                        WeatheringHeatPipeBlock.BLOCKS.get().get(state),
                        HeatPipeBlock.BLOCKS.get().get(state));

                generateThermostats(generator,
                        WeatheringThermostatBlock.BLOCKS.get().get(state),
                        ThermostatBlock.BLOCKS.get().get(state));
            }
        }

        private void generateHeaters(BlockModelGenerators generator, Block weathering, Block waxed) {
            var unlit = TexturedModel.ORIENTABLE_ONLY_TOP.create(weathering, generator.modelOutput);

            var lit = TexturedModel.ORIENTABLE_ONLY_TOP.get(weathering)
                    .updateTextures(
                            textures -> textures.put(TextureSlot.FRONT,
                                    TextureMapping.getBlockTexture(weathering, "_front_on")))
                    .createWithSuffix(weathering, "_on", generator.modelOutput);

            generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(weathering)
                    .with(BlockModelGenerators
                            .createBooleanModelDispatch(BlockStateProperties.LIT, lit, unlit))
                    .with(BlockModelGenerators
                            .createHorizontalFacingDispatch()));

            generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(waxed)
                    .with(BlockModelGenerators
                            .createBooleanModelDispatch(BlockStateProperties.LIT, lit, unlit))
                    .with(BlockModelGenerators
                            .createHorizontalFacingDispatch()));
        }

        private void generatePipes(BlockModelGenerators generator, Block weathering, Block waxed) {
            var coreId = coreHeatPipeBlock.createWithSuffix(weathering, "_core",
                    TextureMapping.defaultTexture(weathering), generator.modelOutput);
            var armId = armHeatPipeBlock.createWithSuffix(weathering, "_arm",
                    TextureMapping.defaultTexture(weathering), generator.modelOutput);
            generatePipe(generator, weathering, coreId, armId);
            generatePipe(generator, waxed, coreId, armId);
        }

        private void generatePipe(BlockModelGenerators generator, Block pipe, ResourceLocation core,
                ResourceLocation arm) {
            var supplier = MultiPartGenerator.multiPart(pipe)
                    .with(Variant.variant().with(VariantProperties.MODEL, core));
            for (var direction : Direction.values()) {
                supplier.with(
                        Condition.condition().term(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), true),
                        Variant.variant()
                                .with(VariantProperties.MODEL, arm)
                                .with(
                                        direction.getAxis().isHorizontal() ? VariantProperties.Y_ROT
                                                : VariantProperties.X_ROT,
                                        getRotation(direction)));
            }
            generator.blockStateOutput.accept(supplier);
        }

        private void generateThermostats(BlockModelGenerators generator, Block weathering, Block waxed) {
            var off = THERMOSTAT.create(weathering, generator.modelOutput);

            THERMOSTAT_INVENTORY.createWithSuffix(weathering, INVENTORY, generator.modelOutput);

            generator.blockStateOutput.accept(MultiVariantGenerator
                    .multiVariant(weathering, Variant.variant().with(VariantProperties.MODEL, off))
                    .with(BlockModelGenerators.createFacingDispatch()));

            generator.blockStateOutput.accept(MultiVariantGenerator
                    .multiVariant(waxed, Variant.variant().with(VariantProperties.MODEL, off))
                    .with(BlockModelGenerators.createFacingDispatch()));
        }

        private static Rotation getRotation(Direction direction) {
            return ROTATIONS[direction.get3DDataValue()];
        }

        private static TextureMapping thermostat(Block block) {
            return new TextureMapping()
                    .put(TextureSlot.PLATFORM, TextureMapping.getBlockTexture(block, "_front"))
                    .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
                    .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_bottom"));
        }

        private static TextureMapping thermostatInventory(Block block) {
            return new TextureMapping()
                    .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_front"))
                    .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
                    .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_bottom"));
        }

        @Override
        public void generateItemModels(ItemModelGenerators generator) {

            for (var state : WeatherState.values()) {
                var model = toModelTemplate(WeatheringHeaterBlock.BLOCKS.get().get(state));
                generator.generateFlatItem(WeatheringHeaterBlock.ITEMS.get().get(state), model);
                generator.generateFlatItem(HeaterBlock.ITEMS.get().get(state), model);
            }

            for (var state : WeatherState.values()) {
                var mapping = TextureMapping.defaultTexture(WeatheringHeatPipeBlock.BLOCKS.get().get(state));
                coreHeatPipeItem.create(
                        ModelLocationUtils.getModelLocation(WeatheringHeatPipeBlock.ITEMS.get().get(state)),
                        mapping, generator.output);
                coreHeatPipeItem.create(
                        ModelLocationUtils.getModelLocation(HeatPipeBlock.ITEMS.get().get(state)),
                        mapping, generator.output);
            }

            for (var state : WeatherState.values()) {
                var model = toModelTemplate(WeatheringThermostatBlock.BLOCKS.get().get(state), INVENTORY);
                generator.generateFlatItem(WeatheringThermostatBlock.ITEMS.get().get(state), model);
                generator.generateFlatItem(ThermostatBlock.ITEMS.get().get(state), model);
            }
        }

        private ModelTemplate toModelTemplate(Block block) {
            return new ModelTemplate(Optional.of(ModelLocationUtils.getModelLocation(block)), Optional.empty());
        }

        private ModelTemplate toModelTemplate(Block block, String string) {
            return new ModelTemplate(Optional.of(ModelLocationUtils.getModelLocation(block, string)), Optional.empty());
        }
    }

    private static class EnglishLanguageProvider extends FabricLanguageProvider {

        private EnglishLanguageProvider(FabricDataOutput dataOutput) {
            super(dataOutput, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder builder) {
            addAll(builder, "Heater",
                    HeaterBlock.BLOCKS.get()::get, WeatheringHeaterBlock.BLOCKS.get()::get);

            addAll(builder, "Heat Pipe",
                    HeatPipeBlock.BLOCKS.get()::get, WeatheringHeatPipeBlock.BLOCKS.get()::get);

            addAll(builder, "Thermostat",
                    ThermostatBlock.BLOCKS.get()::get, WeatheringThermostatBlock.BLOCKS.get()::get);

            builder.add(HeaterBlockEntity.CONTAINER_NAME, Heater.MOD_NAME);
            builder.add(HeaterMenu.TAB_NAME, Heater.MOD_NAME);
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

    private static class LootTableProvider extends FabricBlockLootTableProvider {

        private LootTableProvider(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            WeatheringHeaterBlock.BLOCKS.get().values().forEach(this::dropSelf);
            HeaterBlock.BLOCKS.get().values().forEach(this::dropSelf);
            WeatheringThermostatBlock.BLOCKS.get().values().forEach(this::dropSelf);
            ThermostatBlock.BLOCKS.get().values().forEach(this::dropSelf);
            WeatheringHeatPipeBlock.BLOCKS.get().values().forEach(this::dropSelf);
            HeatPipeBlock.BLOCKS.get().values().forEach(this::dropSelf);
        }
    }

    private static class RecipeProvider extends FabricRecipeProvider {

        private RecipeProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void buildRecipes(RecipeOutput output) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WeatheringHeaterBlock.UNAFFECTED_BLOCK)
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
                        WeatheringHeaterBlock.ITEMS.get().get(state),
                        HeaterBlock.ITEMS.get().get(state));
            }

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WeatheringHeatPipeBlock.UNAFFECTED_BLOCK)
                    .pattern("ccc")
                    .define('c', Items.COPPER_INGOT)
                    .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                    .save(output);

            for (var state : WeatherState.values()) {
                generateWaxingRecipe(output,
                        WeatheringHeatPipeBlock.ITEMS.get().get(state),
                        HeatPipeBlock.ITEMS.get().get(state));
            }

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WeatheringThermostatBlock.UNAFFECTED_BLOCK)
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
                        WeatheringThermostatBlock.ITEMS.get().get(state),
                        ThermostatBlock.ITEMS.get().get(state));
            }
        }

        private void generateWaxingRecipe(RecipeOutput output, Item unwaxed, Item waxed) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, waxed)
                    .requires(unwaxed).requires(Items.HONEYCOMB)
                    .unlockedBy(getHasName(unwaxed), has(unwaxed))
                    .unlockedBy(getHasName(Items.HONEYCOMB), has(Items.HONEYCOMB))
                    .save(output);
        }
    }

    private static class TagProvider extends BlockTagProvider {

        public TagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {

            getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                    .setReplace(false)
                    .addTag(Tags.HEATERS)
                    .addTag(Tags.PIPES)
                    .addTag(Tags.THERMOSTATS);

            getOrCreateTagBuilder(Tags.HEATERS)
                    .setReplace(false)
                    .add(WeatheringHeaterBlock.BLOCKS.get().values().toArray(WeatheringHeaterBlock[]::new))
                    .add(HeaterBlock.BLOCKS.get().values().toArray(HeaterBlock[]::new));

            getOrCreateTagBuilder(Tags.PIPES)
                    .setReplace(false)
                    .add(WeatheringHeatPipeBlock.BLOCKS.get().values().toArray(WeatheringHeatPipeBlock[]::new))
                    .add(HeatPipeBlock.BLOCKS.get().values().toArray(HeatPipeBlock[]::new));

            getOrCreateTagBuilder(Tags.THERMOSTATS)
                    .setReplace(false)
                    .add(WeatheringThermostatBlock.BLOCKS.get().values().toArray(WeatheringThermostatBlock[]::new))
                    .add(ThermostatBlock.BLOCKS.get().values().toArray(ThermostatBlock[]::new));

            getOrCreateTagBuilder(Tags.FURNACES)
                    .setReplace(false)
                    .add(Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER);

            getOrCreateTagBuilder(Tags.Connectable.PIPES)
                    .setReplace(false)
                    .addTag(Tags.HEATERS)
                    .addTag(Tags.PIPES)
                    .addTag(Tags.THERMOSTATS)
                    .addTag(Tags.FURNACES);

            getOrCreateTagBuilder(Tags.Propagable.HEATERS)
                    .setReplace(false)
                    .addTag(Tags.PIPES)
                    .addTag(Tags.THERMOSTATS)
                    .addTag(Tags.FURNACES);

            getOrCreateTagBuilder(Tags.Propagable.PIPES)
                    .setReplace(false)
                    .addTag(Tags.PIPES)
                    .addTag(Tags.THERMOSTATS)
                    .addTag(Tags.FURNACES);

            getOrCreateTagBuilder(Tags.Propagable.THERMOSTATS)
                    .setReplace(false)
                    .addTag(Tags.HEATERS)
                    .addTag(Tags.PIPES)
                    .addTag(Tags.THERMOSTATS)
                    .addTag(Tags.FURNACES);
        }
    }
}
