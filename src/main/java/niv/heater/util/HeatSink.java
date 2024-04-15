package niv.heater.util;

import java.util.Optional;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import niv.heater.adapter.HeatSinkAdapter;

public interface HeatSink extends Comparable<HeatSink> {

    int getBurnTime();

    void setBurnTime(int value);

    int getFuelTime();

    void setFuelTime(int value);

    @Override
    default int compareTo(HeatSink that) {
        return Integer.compare(that.getBurnTime(), this.getBurnTime());
    }

    static boolean is(LevelAccessor level, BlockEntity entity) {
        return entity != null
                && (entity instanceof HeatSink
                        || entity instanceof AbstractFurnaceBlockEntity
                        || HeatSinkAdapter.of(level, entity.getType()).isPresent());
    }

    static Optional<HeatSink> of(LevelAccessor level, BlockEntity entity) {
        if (entity == null) {
            return Optional.empty();
        } else if (entity instanceof HeatSink sink) {
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
            return HeatSinkAdapter.of(level, entity.getType()).flatMap(adapter -> adapter.apply(entity));
        }
    }
}
