package net.wecan.kinetic.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class NoVanillaGravity {

    @Inject(
            method = "tickMovement",
            at = @At("HEAD")
    )
    private void applyCustomGravity(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Get the current motion/velocity
        Vec3d currentMotion = entity.getVelocity();

        // Apply your custom gravity logic here
        double customGravity = -0.001; // Your custom gravity constant
        Vec3d newMotion = new Vec3d(currentMotion.x, currentMotion.y + customGravity, currentMotion.z);

        // Set the new velocity with custom gravity applied
        entity.setVelocity(newMotion);
    }
}
