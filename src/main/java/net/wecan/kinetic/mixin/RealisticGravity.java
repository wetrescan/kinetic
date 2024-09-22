package net.wecan.kinetic.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class RealisticGravity {

    private static final double GRAVITY = 9.807; // m/s^2
    private static final double CONVERTED_GRAVITY = GRAVITY * (32/9.807); //so that the real life gravity (9.807) is considered to 32m/s^2 on minecraft
    private static final double SEA_LEVEL_Y = 63; // Sea level in Minecraft
    private static final double DRAG_COEFFICIENT = 1.05; // Approximate value for a player-like entity
    private static final double CROSS_SECTIONAL_AREA = 0.5; // Approximate cross-sectional area (in m^2)
    private static final double AIR_DENSITY_SEA_LEVEL = 1.225; // kg/m^3

    private double getAirDensity(double yLevel) {
        return AIR_DENSITY_SEA_LEVEL * Math.max(0, 1 - (yLevel - SEA_LEVEL_Y) / 10000.0);
    }


    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void applyCustomGravityAndDrag(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Get the entity's current velocity and Y-level
        Vec3d velocity = entity.getVelocity();
        double yLevel = entity.getY();

        // Calculate air density based on Y-level
        double airDensity = getAirDensity(yLevel);

        // Calculate the drag force
        double dragForce = 0.5 * airDensity * DRAG_COEFFICIENT * CROSS_SECTIONAL_AREA * Math.pow(velocity.length(), 2);

        // Calculate the net acceleration
        double mass = 80; // Replace with method to get entity's mass
        double dragAcceleration = dragForce / mass; // Drag effect
        double netAcceleration = (CONVERTED_GRAVITY - dragAcceleration) * 0.05;

        // Update the Y-velocity using net acceleration
        double deltaTime = 0.05; // Time step
        double newVelocityY = velocity.y - netAcceleration * deltaTime + 0.078; // 0.08 is (in theory) the vanilla gravity

        // Set the new velocity while preserving X and Z components
        entity.setVelocity(velocity.x, newVelocityY, velocity.z);

        // If you want to ensure no base gravity is applied, also set the Y velocity to the computed value directly
        // This may vary based on how you want to handle interactions with other game mechanics.
    }
}
