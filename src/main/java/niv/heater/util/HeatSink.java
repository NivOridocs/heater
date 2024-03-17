package niv.heater.util;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import niv.heater.recipes.HeatSinkRecipe;

public interface HeatSink extends Comparable<HeatSink> {

    int getBurnTime();

    void setBurnTime(int value);

    int getFuelTime();

    void setFuelTime(int value);

    @Override
    default int compareTo(HeatSink that) {
        return HeatSink.compare(this, that);
    }

    static int compare(HeatSink a, HeatSink b) {
        return Integer.compare(b.getBurnTime(), a.getBurnTime());
    }

    static boolean isHeatSink(LevelAccessor level, BlockPos pos, Block block) {
        if (block instanceof BaseEntityBlock) {
            var entity = level.getBlockEntity(pos);
            return entity instanceof HeatSink
                    || entity instanceof AbstractFurnaceBlockEntity
                    || HeatSinkRecipe.hasRecipeFor(level, entity);
        }
        return false;
    }

    static Optional<HeatSink> getHeatSink(LevelAccessor level, BlockEntity entity) {
        if (entity instanceof HeatSink sink) {
            return Optional.of(sink);
        } else if (entity instanceof AbstractFurnaceBlockEntity furnace) {
            return Optional.of(new HeatSink() {
                @Override
                public int getBurnTime() {
                    return furnace.litTime;
                }

                @Override
                public void setBurnTime(int value) {
                    furnace.litTime = value;
                }

                @Override
                public int getFuelTime() {
                    return furnace.litDuration;
                }

                @Override
                public void setFuelTime(int value) {
                    furnace.litDuration = value;
                }
            });
        } else {
            return HeatSinkRecipe.getRecipeFor(level, entity).flatMap(r -> r.apply(entity));
        }
    }

}
