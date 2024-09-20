package net.wecan.kinetic.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class MaxFallSpeed {

    @ModifyVariable(
            method = "applyFluidMovingSpeed",
            at = @At("STORE"),
            ordinal = 0 // Ensures we're modifying the right variable
    )
    private double modifyFallVelocity(double velocity) {
        double maxFallSpeed = -100.0; // Set your custom max fall speed here
        if (velocity < maxFallSpeed) {
            return maxFallSpeed; // Cap the velocity
        }
        return velocity;
    }
}
