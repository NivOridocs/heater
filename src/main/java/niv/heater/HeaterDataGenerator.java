package niv.heater;

import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.R0;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.R180;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.R270;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.R90;
import static net.minecraft.world.item.Items.COBBLESTONE;
import static net.minecraft.world.item.Items.COPPER_INGOT;
import static net.minecraft.world.item.Items.FURNACE;
import static net.minecraft.world.item.Items.HONEYCOMB;
import static net.minecraft.world.item.Items.REDSTONE;
import static niv.heater.Heater.EXPOSED_HEATER_BLOCK;
import static niv.heater.Heater.EXPOSED_HEATER_ITEM;
import static niv.heater.Heater.EXPOSED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.EXPOSED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.EXPOSED_THERMOSTAT_BLOCK;
import static niv.heater.Heater.EXPOSED_THERMOSTAT_ITEM;
import static niv.heater.Heater.HEATER_BLOCK;
import static niv.heater.Heater.HEATER_ITEM;
import static niv.heater.Heater.HEAT_PIPE_BLOCK;
import static niv.heater.Heater.HEAT_PIPE_ITEM;
import static niv.heater.Heater.OXIDIZED_HEATER_BLOCK;
import static niv.heater.Heater.OXIDIZED_HEATER_ITEM;
import static niv.heater.Heater.OXIDIZED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.OXIDIZED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.OXIDIZED_THERMOSTAT_BLOCK;
import static niv.heater.Heater.OXIDIZED_THERMOSTAT_ITEM;
import static niv.heater.Heater.THERMOSTAT_BLOCK;
import static niv.heater.Heater.THERMOSTAT_ITEM;
import static niv.heater.Heater.WAXED_EXPOSED_HEATER_BLOCK;
import static niv.heater.Heater.WAXED_EXPOSED_HEATER_ITEM;
import static niv.heater.Heater.WAXED_EXPOSED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.WAXED_EXPOSED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.WAXED_EXPOSED_THERMOSTAT_BLOCK;
import static niv.heater.Heater.WAXED_EXPOSED_THERMOSTAT_ITEM;
import static niv.heater.Heater.WAXED_HEATER_BLOCK;
import static niv.heater.Heater.WAXED_HEATER_ITEM;
import static niv.heater.Heater.WAXED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.WAXED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.WAXED_OXIDIZED_HEATER_BLOCK;
import static niv.heater.Heater.WAXED_OXIDIZED_HEATER_ITEM;
import static niv.heater.Heater.WAXED_OXIDIZED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.WAXED_OXIDIZED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.WAXED_OXIDIZED_THERMOSTAT_BLOCK;
import static niv.heater.Heater.WAXED_OXIDIZED_THERMOSTAT_ITEM;
import static niv.heater.Heater.WAXED_THERMOSTAT_BLOCK;
import static niv.heater.Heater.WAXED_THERMOSTAT_ITEM;
import static niv.heater.Heater.WAXED_WEATHERED_HEATER_BLOCK;
import static niv.heater.Heater.WAXED_WEATHERED_HEATER_ITEM;
import static niv.heater.Heater.WAXED_WEATHERED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.WAXED_WEATHERED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.WAXED_WEATHERED_THERMOSTAT_BLOCK;
import static niv.heater.Heater.WAXED_WEATHERED_THERMOSTAT_ITEM;
import static niv.heater.Heater.WEATHERED_HEATER_BLOCK;
import static niv.heater.Heater.WEATHERED_HEATER_ITEM;
import static niv.heater.Heater.WEATHERED_HEAT_PIPE_BLOCK;
import static niv.heater.Heater.WEATHERED_HEAT_PIPE_ITEM;
import static niv.heater.Heater.WEATHERED_THERMOSTAT_BLOCK;
import static niv.heater.Heater.WEATHERED_THERMOSTAT_ITEM;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import niv.heater.block.HeatPipeBlock;

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
                    Optional.of(ModelLocationUtils.getModelLocation(HEAT_PIPE_BLOCK, "_base_core")),
                    Optional.empty(), TextureSlot.TEXTURE);

            armHeatPipeBlock = new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(HEAT_PIPE_BLOCK, "_base_arm")),
                    Optional.empty(), TextureSlot.TEXTURE);

            coreHeatPipeItem = new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(HEAT_PIPE_ITEM, "_core")),
                    Optional.empty(), TextureSlot.TEXTURE);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators generator) {
            generateHeaters(generator, HEATER_BLOCK, WAXED_HEATER_BLOCK);
            generateHeaters(generator, EXPOSED_HEATER_BLOCK, WAXED_EXPOSED_HEATER_BLOCK);
            generateHeaters(generator, WEATHERED_HEATER_BLOCK, WAXED_WEATHERED_HEATER_BLOCK);
            generateHeaters(generator, OXIDIZED_HEATER_BLOCK, WAXED_OXIDIZED_HEATER_BLOCK);

            generatePipes(generator, HEAT_PIPE_BLOCK, WAXED_HEAT_PIPE_BLOCK);
            generatePipes(generator, EXPOSED_HEAT_PIPE_BLOCK, WAXED_EXPOSED_HEAT_PIPE_BLOCK);
            generatePipes(generator, WEATHERED_HEAT_PIPE_BLOCK, WAXED_WEATHERED_HEAT_PIPE_BLOCK);
            generatePipes(generator, OXIDIZED_HEAT_PIPE_BLOCK, WAXED_OXIDIZED_HEAT_PIPE_BLOCK);

            generateThermostats(generator, THERMOSTAT_BLOCK, WAXED_THERMOSTAT_BLOCK);
            generateThermostats(generator, EXPOSED_THERMOSTAT_BLOCK, WAXED_EXPOSED_THERMOSTAT_BLOCK);
            generateThermostats(generator, WEATHERED_THERMOSTAT_BLOCK, WAXED_WEATHERED_THERMOSTAT_BLOCK);
            generateThermostats(generator, OXIDIZED_THERMOSTAT_BLOCK, WAXED_OXIDIZED_THERMOSTAT_BLOCK);
        }

        private void generateHeaters(BlockModelGenerators generator, Block heater, Block waxed) {
            var unlit = TexturedModel.ORIENTABLE_ONLY_TOP.create(heater, generator.modelOutput);

            var lit = TexturedModel.ORIENTABLE_ONLY_TOP.get(heater)
                    .updateTextures(
                            textures -> textures.put(TextureSlot.FRONT,
                                    TextureMapping.getBlockTexture(heater, "_front_on")))
                    .createWithSuffix(heater, "_on", generator.modelOutput);

            generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(heater)
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

        private void generatePipes(BlockModelGenerators generator, Block pipe, Block waxed) {
            var coreId = coreHeatPipeBlock.createWithSuffix(pipe, "_core",
                    TextureMapping.defaultTexture(pipe), generator.modelOutput);
            var armId = armHeatPipeBlock.createWithSuffix(pipe, "_arm",
                    TextureMapping.defaultTexture(pipe), generator.modelOutput);
            generatePipe(generator, pipe, coreId, armId);
            generatePipe(generator, waxed, coreId, armId);
        }

        private void generatePipe(BlockModelGenerators generator, Block pipe, ResourceLocation core,
                ResourceLocation arm) {
            var supplier = MultiPartGenerator.multiPart(pipe)
                    .with(Variant.variant().with(VariantProperties.MODEL, core));
            for (var direction : Direction.values()) {
                supplier.with(
                        Condition.condition().term(HeatPipeBlock.getProperty(direction), true),
                        Variant.variant()
                                .with(VariantProperties.MODEL, arm)
                                .with(
                                        direction.getAxis().isHorizontal() ? VariantProperties.Y_ROT
                                                : VariantProperties.X_ROT,
                                        getRotation(direction)));
            }
            generator.blockStateOutput.accept(supplier);
        }

        private void generateThermostats(BlockModelGenerators generator, Block thermostat, Block waxed) {
            var off = THERMOSTAT.create(thermostat, generator.modelOutput);

            THERMOSTAT_INVENTORY.createWithSuffix(thermostat, INVENTORY, generator.modelOutput);

            generator.blockStateOutput.accept(MultiVariantGenerator
                    .multiVariant(thermostat, Variant.variant().with(VariantProperties.MODEL, off))
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
            generator.generateFlatItem(HEATER_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(HEATER_BLOCK)), Optional.empty()));
            generator.generateFlatItem(EXPOSED_HEATER_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(EXPOSED_HEATER_BLOCK)), Optional.empty()));
            generator.generateFlatItem(WEATHERED_HEATER_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(WEATHERED_HEATER_BLOCK)), Optional.empty()));
            generator.generateFlatItem(OXIDIZED_HEATER_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(OXIDIZED_HEATER_BLOCK)), Optional.empty()));
            generator.generateFlatItem(WAXED_HEATER_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(HEATER_BLOCK)), Optional.empty()));
            generator.generateFlatItem(WAXED_EXPOSED_HEATER_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(EXPOSED_HEATER_BLOCK)), Optional.empty()));
            generator.generateFlatItem(WAXED_WEATHERED_HEATER_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(WEATHERED_HEATER_BLOCK)), Optional.empty()));
            generator.generateFlatItem(WAXED_OXIDIZED_HEATER_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(OXIDIZED_HEATER_BLOCK)), Optional.empty()));

            coreHeatPipeItem.create(ModelLocationUtils.getModelLocation(HEAT_PIPE_ITEM),
                    TextureMapping.defaultTexture(HEAT_PIPE_BLOCK), generator.output);
            coreHeatPipeItem.create(ModelLocationUtils.getModelLocation(EXPOSED_HEAT_PIPE_ITEM),
                    TextureMapping.defaultTexture(EXPOSED_HEAT_PIPE_BLOCK), generator.output);
            coreHeatPipeItem.create(ModelLocationUtils.getModelLocation(WEATHERED_HEAT_PIPE_ITEM),
                    TextureMapping.defaultTexture(WEATHERED_HEAT_PIPE_BLOCK), generator.output);
            coreHeatPipeItem.create(ModelLocationUtils.getModelLocation(OXIDIZED_HEAT_PIPE_ITEM),
                    TextureMapping.defaultTexture(OXIDIZED_HEAT_PIPE_BLOCK), generator.output);
            coreHeatPipeItem.create(ModelLocationUtils.getModelLocation(WAXED_HEAT_PIPE_ITEM),
                    TextureMapping.defaultTexture(HEAT_PIPE_BLOCK), generator.output);
            coreHeatPipeItem.create(ModelLocationUtils.getModelLocation(WAXED_EXPOSED_HEAT_PIPE_ITEM),
                    TextureMapping.defaultTexture(EXPOSED_HEAT_PIPE_BLOCK), generator.output);
            coreHeatPipeItem.create(ModelLocationUtils.getModelLocation(WAXED_WEATHERED_HEAT_PIPE_ITEM),
                    TextureMapping.defaultTexture(WEATHERED_HEAT_PIPE_BLOCK), generator.output);
            coreHeatPipeItem.create(ModelLocationUtils.getModelLocation(WAXED_OXIDIZED_HEAT_PIPE_ITEM),
                    TextureMapping.defaultTexture(OXIDIZED_HEAT_PIPE_BLOCK), generator.output);

            generator.generateFlatItem(THERMOSTAT_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(THERMOSTAT_BLOCK, INVENTORY)), Optional.empty()));
            generator.generateFlatItem(EXPOSED_THERMOSTAT_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(EXPOSED_THERMOSTAT_BLOCK, INVENTORY)),
                    Optional.empty()));
            generator.generateFlatItem(WEATHERED_THERMOSTAT_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(WEATHERED_THERMOSTAT_BLOCK, INVENTORY)),
                    Optional.empty()));
            generator.generateFlatItem(OXIDIZED_THERMOSTAT_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(OXIDIZED_THERMOSTAT_BLOCK, INVENTORY)),
                    Optional.empty()));
            generator.generateFlatItem(WAXED_THERMOSTAT_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(THERMOSTAT_BLOCK, INVENTORY)), Optional.empty()));
            generator.generateFlatItem(WAXED_EXPOSED_THERMOSTAT_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(EXPOSED_THERMOSTAT_BLOCK, INVENTORY)),
                    Optional.empty()));
            generator.generateFlatItem(WAXED_WEATHERED_THERMOSTAT_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(WEATHERED_THERMOSTAT_BLOCK, INVENTORY)),
                    Optional.empty()));
            generator.generateFlatItem(WAXED_OXIDIZED_THERMOSTAT_ITEM, new ModelTemplate(
                    Optional.of(ModelLocationUtils.getModelLocation(OXIDIZED_THERMOSTAT_BLOCK, INVENTORY)),
                    Optional.empty()));
        }

    }

    private static class EnglishLanguageProvider extends FabricLanguageProvider {

        private EnglishLanguageProvider(FabricDataOutput dataOutput) {
            super(dataOutput, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            var name = "";

            final var exposed = "Exposed ";
            final var weathered = "Weathered ";
            final var oxidized = "Oxidized ";

            final var waxed = "Waxed ";

            name = "Heater";

            translationBuilder.add(HEATER_BLOCK, name);
            translationBuilder.add(EXPOSED_HEATER_BLOCK, exposed + name);
            translationBuilder.add(WEATHERED_HEATER_BLOCK, weathered + name);
            translationBuilder.add(OXIDIZED_HEATER_BLOCK, oxidized + name);

            translationBuilder.add(WAXED_HEATER_BLOCK, waxed + name);
            translationBuilder.add(WAXED_EXPOSED_HEATER_BLOCK, waxed + exposed + name);
            translationBuilder.add(WAXED_WEATHERED_HEATER_BLOCK, waxed + weathered + name);
            translationBuilder.add(WAXED_OXIDIZED_HEATER_BLOCK, waxed + oxidized + name);

            translationBuilder.add("container.heater", name);
            translationBuilder.add("itemGroup.heater.tab", name);

            name = "Heat Pipe";

            translationBuilder.add(HEAT_PIPE_BLOCK, name);
            translationBuilder.add(EXPOSED_HEAT_PIPE_BLOCK, exposed + name);
            translationBuilder.add(WEATHERED_HEAT_PIPE_BLOCK, weathered + name);
            translationBuilder.add(OXIDIZED_HEAT_PIPE_BLOCK, oxidized + name);

            translationBuilder.add(WAXED_HEAT_PIPE_BLOCK, waxed + name);
            translationBuilder.add(WAXED_EXPOSED_HEAT_PIPE_BLOCK, waxed + exposed + name);
            translationBuilder.add(WAXED_WEATHERED_HEAT_PIPE_BLOCK, waxed + weathered + name);
            translationBuilder.add(WAXED_OXIDIZED_HEAT_PIPE_BLOCK, waxed + oxidized + name);

            name = "Thermostat";

            translationBuilder.add(THERMOSTAT_BLOCK, name);
            translationBuilder.add(EXPOSED_THERMOSTAT_BLOCK, exposed + name);
            translationBuilder.add(WEATHERED_THERMOSTAT_BLOCK, weathered + name);
            translationBuilder.add(OXIDIZED_THERMOSTAT_BLOCK, oxidized + name);

            translationBuilder.add(WAXED_THERMOSTAT_BLOCK, waxed + name);
            translationBuilder.add(WAXED_EXPOSED_THERMOSTAT_BLOCK, waxed + exposed + name);
            translationBuilder.add(WAXED_WEATHERED_THERMOSTAT_BLOCK, waxed + weathered + name);
            translationBuilder.add(WAXED_OXIDIZED_THERMOSTAT_BLOCK, waxed + oxidized + name);
        }

    }

    private static class LootTableProvider extends FabricBlockLootTableProvider {

        private LootTableProvider(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            dropSelf(HEATER_BLOCK);
            dropSelf(EXPOSED_HEATER_BLOCK);
            dropSelf(WEATHERED_HEATER_BLOCK);
            dropSelf(OXIDIZED_HEATER_BLOCK);

            dropSelf(WAXED_HEATER_BLOCK);
            dropSelf(WAXED_EXPOSED_HEATER_BLOCK);
            dropSelf(WAXED_WEATHERED_HEATER_BLOCK);
            dropSelf(WAXED_OXIDIZED_HEATER_BLOCK);

            dropSelf(HEAT_PIPE_BLOCK);
            dropSelf(EXPOSED_HEAT_PIPE_BLOCK);
            dropSelf(WEATHERED_HEAT_PIPE_BLOCK);
            dropSelf(OXIDIZED_HEAT_PIPE_BLOCK);

            dropSelf(WAXED_HEAT_PIPE_BLOCK);
            dropSelf(WAXED_EXPOSED_HEAT_PIPE_BLOCK);
            dropSelf(WAXED_WEATHERED_HEAT_PIPE_BLOCK);
            dropSelf(WAXED_OXIDIZED_HEAT_PIPE_BLOCK);

            dropSelf(THERMOSTAT_BLOCK);
            dropSelf(EXPOSED_THERMOSTAT_BLOCK);
            dropSelf(WEATHERED_THERMOSTAT_BLOCK);
            dropSelf(OXIDIZED_THERMOSTAT_BLOCK);

            dropSelf(WAXED_THERMOSTAT_BLOCK);
            dropSelf(WAXED_EXPOSED_THERMOSTAT_BLOCK);
            dropSelf(WAXED_WEATHERED_THERMOSTAT_BLOCK);
            dropSelf(WAXED_OXIDIZED_THERMOSTAT_BLOCK);
        }

    }

    private static class RecipeProvider extends FabricRecipeProvider {

        private RecipeProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void buildRecipes(RecipeOutput output) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HEATER_BLOCK)
                    .pattern("ccc")
                    .pattern("cfc")
                    .pattern("ccc")
                    .define('c', COPPER_INGOT)
                    .define('f', FURNACE)
                    .unlockedBy(getHasName(COPPER_INGOT), has(COPPER_INGOT))
                    .unlockedBy(getHasName(FURNACE), has(FURNACE))
                    .save(output);

            generateWaxingRecipe(output, HEATER_ITEM, WAXED_HEATER_ITEM);
            generateWaxingRecipe(output, EXPOSED_HEATER_ITEM, WAXED_EXPOSED_HEATER_ITEM);
            generateWaxingRecipe(output, WEATHERED_HEATER_ITEM, WAXED_WEATHERED_HEATER_ITEM);
            generateWaxingRecipe(output, OXIDIZED_HEATER_ITEM, WAXED_OXIDIZED_HEATER_ITEM);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, HEAT_PIPE_BLOCK)
                    .pattern("ccc")
                    .define('c', COPPER_INGOT)
                    .unlockedBy(getHasName(COPPER_INGOT), has(COPPER_INGOT))
                    .save(output);

            generateWaxingRecipe(output, HEAT_PIPE_ITEM, WAXED_HEAT_PIPE_ITEM);
            generateWaxingRecipe(output, EXPOSED_HEAT_PIPE_ITEM, WAXED_EXPOSED_HEAT_PIPE_ITEM);
            generateWaxingRecipe(output, WEATHERED_HEAT_PIPE_ITEM, WAXED_WEATHERED_HEAT_PIPE_ITEM);
            generateWaxingRecipe(output, OXIDIZED_HEAT_PIPE_ITEM, WAXED_OXIDIZED_HEAT_PIPE_ITEM);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, THERMOSTAT_ITEM)
                    .pattern("ccc")
                    .pattern("#c#")
                    .pattern("#r#")
                    .define('c', COPPER_INGOT)
                    .define('r', REDSTONE)
                    .define('#', COBBLESTONE)
                    .unlockedBy(getHasName(COBBLESTONE), has(COBBLESTONE))
                    .unlockedBy(getHasName(COPPER_INGOT), has(COPPER_INGOT))
                    .unlockedBy(getHasName(REDSTONE), has(REDSTONE))
                    .save(output);

            generateWaxingRecipe(output, THERMOSTAT_ITEM, WAXED_THERMOSTAT_ITEM);
            generateWaxingRecipe(output, EXPOSED_THERMOSTAT_ITEM, WAXED_EXPOSED_THERMOSTAT_ITEM);
            generateWaxingRecipe(output, WEATHERED_THERMOSTAT_ITEM, WAXED_WEATHERED_THERMOSTAT_ITEM);
            generateWaxingRecipe(output, OXIDIZED_THERMOSTAT_ITEM, WAXED_OXIDIZED_THERMOSTAT_ITEM);
        }

        private void generateWaxingRecipe(RecipeOutput output, Item unwaxed, Item waxed) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, waxed)
                    .requires(unwaxed).requires(HONEYCOMB)
                    .unlockedBy(getHasName(unwaxed), has(unwaxed))
                    .unlockedBy(getHasName(HONEYCOMB), has(HONEYCOMB))
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
                    .add(
                            HEATER_BLOCK, EXPOSED_HEATER_BLOCK,
                            WEATHERED_HEATER_BLOCK, OXIDIZED_HEATER_BLOCK,

                            WAXED_HEATER_BLOCK, WAXED_EXPOSED_HEATER_BLOCK,
                            WAXED_WEATHERED_HEATER_BLOCK, WAXED_OXIDIZED_HEATER_BLOCK,

                            HEAT_PIPE_BLOCK, EXPOSED_HEAT_PIPE_BLOCK,
                            WEATHERED_HEAT_PIPE_BLOCK, OXIDIZED_HEAT_PIPE_BLOCK,

                            WAXED_HEAT_PIPE_BLOCK, WAXED_EXPOSED_HEAT_PIPE_BLOCK,
                            WAXED_WEATHERED_HEAT_PIPE_BLOCK, WAXED_OXIDIZED_HEAT_PIPE_BLOCK,

                            THERMOSTAT_BLOCK, EXPOSED_THERMOSTAT_BLOCK,
                            WEATHERED_THERMOSTAT_BLOCK, OXIDIZED_THERMOSTAT_BLOCK,

                            WAXED_THERMOSTAT_BLOCK, WAXED_EXPOSED_THERMOSTAT_BLOCK,
                            WAXED_WEATHERED_THERMOSTAT_BLOCK, WAXED_OXIDIZED_THERMOSTAT_BLOCK);
        }
    }
}
