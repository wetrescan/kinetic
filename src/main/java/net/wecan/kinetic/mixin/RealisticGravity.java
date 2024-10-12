package net.wecan.kinetic.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.wecan.kinetic.config.GravityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class RealisticGravity {

    private static final double CONVERTED_GRAVITY = GravityConfig.GRAVITY * (32.0 / 9.807); // Real-life gravity converted for Minecraft
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
        double mass = 80; // Replace with method to get entity's mass if available
        double dragAcceleration = dragForce / mass; // Drag effect
        double netAcceleration = (CONVERTED_GRAVITY - dragAcceleration) * 0.05;

        // Update the velocity using net acceleration for each axis
        double deltaTime = 0.05; // Time step
        double vanillaGravityCompensation = 0.078;

        double newVelocityX = velocity.x;
        double newVelocityY = velocity.y + vanillaGravityCompensation;
        double newVelocityZ = velocity.z;

        newVelocityX += vanillaGravityCompensation;
        newVelocityZ += vanillaGravityCompensation;

        newVelocityX += vanillaGravityCompensation;
        newVelocityZ += vanillaGravityCompensation;

        switch (GravityConfig.DIRECTION.toLowerCase()) {
            case "down":
                newVelocityY = velocity.y - netAcceleration * deltaTime + vanillaGravityCompensation;
                break;
            case "up":
                newVelocityY = -velocity.y - netAcceleration * deltaTime + vanillaGravityCompensation;
                break;
            case "east":
                newVelocityX = velocity.x + netAcceleration * deltaTime;
                break;
            case "west":
                newVelocityX = velocity.x - netAcceleration * deltaTime;
                break;
            case "north":
                newVelocityZ = velocity.z - netAcceleration * deltaTime;
                break;
            case "south":
                newVelocityZ = velocity.z + netAcceleration * deltaTime;
                break;
            default:
                // No gravity modification if direction is unknown
                break;
        }

        // Set the new velocity
        entity.setVelocity(newVelocityX, newVelocityY, newVelocityZ);
    }
}