package niv.heater.util;

import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents.TagsLoaded;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import niv.heater.Heater;
import niv.heater.Tags;
import niv.heater.adapter.FurnaceAdapter;

public class FurnacesBinder implements TagsLoaded {

    @Override
    public void onTagsLoaded(RegistryAccess registries, boolean client) {
        if (client) {
            return;
        }

        var blocks = registries.registryOrThrow(Registries.BLOCK);
        var adapters = registries.registryOrThrow(FurnaceAdapter.REGISTRY);

        var furnaces = getAllFurnaces(blocks, adapters);

        if (furnaces.isEmpty()) {
            return;
        }

        if (Heater.LOGGER.isDebugEnabled()) {
            Heater.LOGGER.debug("Found {} untagged furnace candidates, proceeding to bind them: {}",
                    furnaces.size(), furnaces.keySet());
        } else {
            Heater.LOGGER.info("Found {} untagged furnace candidates, proceeding to bind them",
                    furnaces.size());
        }

        var tags = blocks.getTags().collect(groupingBy(Pair::getFirst,
                flatMapping(pair -> pair.getSecond().stream(), toList())));

        for (var tag : Tags.Connectable.ALL.get()) {
            var map = toHolderMap(blocks, tags.getOrDefault(tag, List.of()));
            map.putAll(furnaces);
            tags.put(tag, map.values().stream().toList());
        }

        blocks.bindTags(tags);
    }

    private Map<ResourceLocation, Holder<Block>> getAllFurnaces(
            Registry<Block> blocks, Registry<FurnaceAdapter> adapters) {
        var result = new HashMap<ResourceLocation, Holder<Block>>();
        result.putAll(getNaturalFurnaces(blocks));
        result.putAll(getAdaptedFurnaces(blocks, adapters));
        return result;
    }

    private Map<ResourceLocation, Holder<Block>> getNaturalFurnaces(
            Registry<Block> blocks) {
        return blocks.stream()
                .filter(this::byTags)
                .flatMap(this::toEntity)
                .filter(AbstractFurnaceBlockEntity.class::isInstance)
                .map(BlockEntity::getType)
                .flatMap(this::toBlocks)
                .collect(toMap(blocks::getKey, blocks::wrapAsHolder, (a, b) -> a));
    }

    private Map<ResourceLocation, Holder<Block>> getAdaptedFurnaces(
            Registry<Block> blocks, Registry<FurnaceAdapter> adapters) {
        return adapters.stream()
                .map(FurnaceAdapter::getType)
                .flatMap(this::toBlocks)
                .collect(toMap(blocks::getKey, blocks::wrapAsHolder, (a, b) -> a));
    }

    private boolean byTags(Block block) {
        var state = block.defaultBlockState();
        return !(state.is(Tags.HEATERS) || state.is(ConventionalBlockTags.PLAYER_WORKSTATIONS_FURNACES));
    }

    private Stream<BlockEntity> toEntity(Block block) {
        if (block instanceof EntityBlock entityBlock) {
            return Stream.of(entityBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState()));
        } else {
            return Stream.empty();
        }
    }

    private Stream<Block> toBlocks(BlockEntityType<?> type) {
        return ((BlockEntityTypeAccessor) type).getBlocks().stream();
    }

    private Map<ResourceLocation, Holder<Block>> toHolderMap(
            Registry<Block> blocks, List<Holder<Block>> list) {
        return list == null ? Map.of()
                : list.stream().map(Holder::value)
                        .collect(toMap(blocks::getKey, blocks::wrapAsHolder,
                                (a, b) -> a, HashMap::new));
    }
}
