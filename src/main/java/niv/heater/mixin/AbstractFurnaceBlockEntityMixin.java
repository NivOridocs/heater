package niv.heater.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import niv.heater.api.Furnace;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin implements Furnace {

    @Override
    public int getBurnTime() {
        return ((AbstractFurnaceBlockEntity)(Object)this).litTime;
    }

    @Override
    public void setBurnTime(int value) {
        ((AbstractFurnaceBlockEntity)(Object)this).litTime = value;
    }

    @Override
    public int getFuelTime() {
        return ((AbstractFurnaceBlockEntity)(Object)this).litDuration;
    }

    @Override
    public void setFuelTime(int value) {
        ((AbstractFurnaceBlockEntity)(Object)this).litDuration = value;
    }
}
