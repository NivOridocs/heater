package niv.heater.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import niv.heater.api.Furnace;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin implements Furnace {

    @Shadow
    private int litTime;

    @Shadow
    private int litDuration;

    @Override
    public boolean isBurning() {
        return this.litTime > 0;
    }

    @Override
    public void addBurnTime(int value) {
        this.litTime += value;
    }

    @Override
    public void setFuelTime(int value) {
        this.litDuration = value;
    }

    @Override
    public int compareFuelTime(int value) {
        return Integer.compare(this.litDuration, value);
    }

    @Override
    public int compareDeltaTime(int value) {
        return Integer.compare(this.litDuration - this.litTime, value);
    }

    @Override
    public Number getComparable() {
        return this.litTime;
    }
}
