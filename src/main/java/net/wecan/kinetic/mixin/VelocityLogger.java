package net.wecan.kinetic.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class VelocityLogger {

    @Inject(method = "travel", at = @At("HEAD"))
    public void applyTerminalVelocity(Vec3d movementInput, CallbackInfo ci) {

        final String MOD_ID = "kinetic";
        final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.isPlayer()) {
            // Get current velocity
            Vec3d velocity = entity.getVelocity();
            double vel_y = velocity.y;
            if (vel_y < -0.08 || vel_y > 0) {
                LOGGER.info("Cayendo a velocidad {}", vel_y);
            }
        }
    }
}

