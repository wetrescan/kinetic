package net.wecan.kinetic.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class NoVanillaGravity {

    // Injecting after velocity is applied to modify gravity effects
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void removeGravity(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Get the entity's current velocity
        Vec3d velocity = entity.getVelocity();

        // Set Y velocity to 0 to cancel gravity's downward force
        entity.setVelocity(velocity.x, 0, velocity.z);
    }
}

