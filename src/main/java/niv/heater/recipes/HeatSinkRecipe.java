package niv.heater.recipes;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.getIfBlank;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import niv.heater.Heater;
import niv.heater.util.ForwardingHeatSink;
import niv.heater.util.HeatSink;
import niv.heater.util.NullContainer;

public class HeatSinkRecipe implements Recipe<HeatSinkRecipe.Context>, Function<BlockEntity, Optional<HeatSink>> {

    static record Context(BlockEntityType<?> type) implements NullContainer {
    }

    private final BlockEntityType<?> type;

    private final String litTime;

    private final String litDuration;

    public HeatSinkRecipe(BlockEntityType<?> type, String litTime, String litDuration) {
        this.type = requireNonNull(type);
        this.litTime = requireNonNull(getIfBlank(litTime, () -> null));
        this.litDuration = requireNonNull(getIfBlank(litDuration, () -> null));
    }

    @Override
    public boolean matches(Context context, Level level) {
        return this.type == context.type;
    }

    @Override
    public ItemStack assemble(Context context, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Heater.HEAT_SINK_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return Heater.HEAT_SINK;
    }

    @Override
    public Optional<HeatSink> apply(BlockEntity entity) {
        return get(entity.getClass()).map(constructor -> constructor.apply(entity));
    }

    @SuppressWarnings("java:S3011")
    private Optional<Function<? super BlockEntity, HeatSink>> get(Class<?> clazz) {
        while (clazz != null
                && BlockEntity.class.isAssignableFrom(clazz)
                && !clazz.getName().startsWith("net.minecraft")) {
            try {
                var litTimeField = clazz.getDeclaredField(this.litTime);
                var litDurationField = clazz.getDeclaredField(this.litDuration);

                litTimeField.setAccessible(true);
                litDurationField.setAccessible(true);

                return Optional.of(entry -> new ForwardingHeatSink(entry, litTimeField, litDurationField));
            } catch (NoSuchFieldException ex) {
                clazz = clazz.getSuperclass();
            }
        }
        return Optional.empty();
    }

    public static boolean hasRecipeFor(LevelAccessor levelAccessor, BlockEntity entity) {
        return getRecipeFor(levelAccessor, entity).isPresent();
    }

    public static Optional<HeatSinkRecipe> getRecipeFor(LevelAccessor levelAccessor, BlockEntity entity) {
        if (levelAccessor instanceof Level level) {
            return level.getRecipeManager().getRecipeFor(Heater.HEAT_SINK, new Context(entity.getType()), level).map(RecipeHolder::value);
        } else {
            return Optional.empty();
        }
    }

    public static final class Serializer implements RecipeSerializer<HeatSinkRecipe> {

        static final Codec<HeatSinkRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(r -> r.type),
                Codec.STRING.fieldOf("lit_time").forGetter(r -> r.litTime),
                Codec.STRING.fieldOf("lit_duration").forGetter(r -> r.litDuration))
                .apply(instance, HeatSinkRecipe::new));

        @Override
        public Codec<HeatSinkRecipe> codec() {
            return CODEC;
        }

        @Override
        public HeatSinkRecipe fromNetwork(FriendlyByteBuf buf) {
            var type = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(buf.readResourceLocation());
            var litTime = buf.readUtf();
            var litDuration = buf.readUtf();
            return new HeatSinkRecipe(type, litTime, litDuration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, HeatSinkRecipe recipe) {
            buf.writeResourceLocation(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(recipe.type));
            buf.writeUtf(recipe.litTime);
            buf.writeUtf(recipe.litDuration);
        }
    }
}
