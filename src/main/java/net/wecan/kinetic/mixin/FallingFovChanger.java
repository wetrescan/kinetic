package net.wecan.kinetic.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class FallingFovChanger {

    @Inject(method = "getFovMultiplier", at = @At("RETURN"), cancellable = true)
    private void modifyFovMultiplier(CallbackInfoReturnable<Float> cir) {
        float originalFov = cir.getReturnValue();
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) (Object) this;

        // Get the player's velocities
        double yVelocity = player.getVelocity().y;
        double xzVelocity = Math.sqrt(player.getVelocity().x * player.getVelocity().x + player.getVelocity().z * player.getVelocity().z);

        // Define initial thresholds
        double fallThreshold = 0.5F;
        double upwardThreshold = 0.5F;
        double horizontalThreshold = 0.1F;

        // Falling FOV adjustment (downward)
        if (yVelocity < -fallThreshold) {
            float fallVelocityFactor = 1.0F + (float) Math.sqrt(Math.abs(yVelocity) - fallThreshold) * 0.1F;
            originalFov *= fallVelocityFactor;
        }

        // Rising FOV adjustment (upward)
        if (yVelocity > upwardThreshold) {
            float riseVelocityFactor = 1.0F + (float) Math.sqrt(yVelocity - upwardThreshold) * 0.05F;
            originalFov *= riseVelocityFactor;
        }

        // Horizontal movement FOV adjustment
        if (xzVelocity > horizontalThreshold) {
            float horizontalVelocityFactor = 1.0F + (float) Math.sqrt(xzVelocity - horizontalThreshold) * 0.03F;
            originalFov *= horizontalVelocityFactor;
        }

        // Set the new FOV value
        cir.setReturnValue(MathHelper.lerp(
                ((Double) MinecraftClient.getInstance().options.getFovEffectScale().getValue()).floatValue(),
                1.0F, originalFov
        ));
    }
}
