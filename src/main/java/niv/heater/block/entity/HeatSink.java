package niv.heater.block.entity;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

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

    static boolean isHeatSink(WorldAccess world, BlockPos pos, Block block) {
        if (block instanceof BlockWithEntity) {
            var entity = world.getBlockEntity(pos);
            return entity instanceof HeatSink
                    || entity instanceof AbstractFurnaceBlockEntity
                    || ForwardingHeatSink.isForwardable(entity);
        }
        return false;
    }

    static Optional<HeatSink> getHeatSink(BlockEntity entity) {
        if (entity instanceof HeatSink sink) {
            return Optional.of(sink);
        } else if (entity instanceof AbstractFurnaceBlockEntity furnace) {
            return Optional.of(new HeatSink() {
                @Override
                public int getBurnTime() {
                    return furnace.burnTime;
                }

                @Override
                public void setBurnTime(int value) {
                    furnace.burnTime = value;
                }

                @Override
                public int getFuelTime() {
                    return furnace.fuelTime;
                }

                @Override
                public void setFuelTime(int value) {
                    furnace.fuelTime = value;
                }
            });
        } else {
            return ForwardingHeatSink.getHeatSink(entity);
        }
    }

}
