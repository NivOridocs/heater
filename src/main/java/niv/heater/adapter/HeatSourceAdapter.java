package niv.heater.adapter;

import static java.util.Objects.requireNonNull;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING_HOPPER;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.VERTICAL_DIRECTION;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import niv.heater.Heater;
import niv.heater.util.HeatSource;

public class HeatSourceAdapter implements Predicate<Block>, Supplier<HeatSource>, HeatSource {

    public static final Codec<HeatSourceAdapter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(r -> r.block),
            Codec.BOOL.fieldOf("useProperties").forGetter(r -> r.useProperties),
            Codec.optionalField("reducer", HeatReducer.CODEC).forGetter(r -> r.reducer))
            .apply(instance, HeatSourceAdapter::new));

    private static final Set<DirectionProperty> DIRECTION_PROPERTIES = Set.of(
            FACING, FACING_HOPPER, HORIZONTAL_FACING, VERTICAL_DIRECTION);

    private static final HeatReducer IDENTITY = x -> x;

    private final Block block;

    private final boolean useProperties;

    private final Optional<HeatReducer> reducer;

    private final Set<DirectionProperty> properties;

    private final Function<BlockState, Direction[]> mapper;

    private final IntUnaryOperator operator;

    public HeatSourceAdapter(Block block, boolean useProperties, Optional<HeatReducer> reducer) {
        this.block = requireNonNull(block);
        this.useProperties = useProperties;
        this.reducer = requireNonNull(reducer);

        if (this.useProperties) {
            var defaultState = this.block.defaultBlockState();
            this.properties = DIRECTION_PROPERTIES.stream()
                    .filter(property -> defaultState.getOptionalValue(property).isPresent())
                    .distinct().collect(ImmutableSet.toImmutableSet());
            this.mapper = state -> this.properties.stream()
                    .map(state::getOptionalValue)
                    .flatMap(Optional::stream)
                    .distinct().toArray(Direction[]::new);
        } else {
            this.properties = ImmutableSet.of();
            this.mapper = state -> Direction.values();
        }

        this.operator = reducer.orElse(IDENTITY);
    }

    @Override
    public boolean test(Block block) {
        return this.block == block;
    }

    @Override
    public HeatSource get() {
        return this;
    }

    @Override
    public Direction[] getConnected(BlockState state) {
        return mapper.apply(state);
    }

    @Override
    public int reducedHeat(int heat) {
        return operator.applyAsInt(heat);
    }

    public static Optional<HeatSourceAdapter> of(LevelAccessor levelAccessor, Block block) {
        if (levelAccessor instanceof Level level) {
            return level.registryAccess()
                    .registryOrThrow(Heater.HEAT_SOURCE_ADAPTER).stream()
                    .filter(adapter -> adapter.test(block)).findFirst();
        } else {
            return Optional.empty();
        }
    }
}
