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

    //private static double GRAVITY = 9.807; // m/s^2
    private static final double CONVERTED_GRAVITY = GravityConfig.GRAVITY * (32 / 9.807); // Convert to Minecraft gravity
    private static final double SEA_LEVEL_Y = 63; // Sea level in Minecraft
    private static final double DRAG_COEFFICIENT = 1.05; // Approximate drag coefficient for a player
    private static final double CROSS_SECTIONAL_AREA = 0.5; // Approximate cross-sectional area in m^2
    private static final double AIR_DENSITY_SEA_LEVEL = 1.225; // kg/m^3

    // Add a new field to represent the direction
    /// private static String DIRECTION = "down"; // Default gravity direction

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

        // Apply gravity based on the current direction
        double deltaTime = 0.05; // Time step
        double newVelocityX = velocity.x;
        double newVelocityY = velocity.y;
        double newVelocityZ = velocity.z;

        switch (GravityConfig.DIRECTION.toLowerCase()) {
            case "down":
                // Default behavior: apply gravity in the Y-axis downwards
                newVelocityY -= netAcceleration * deltaTime + 0.078; // 0.078 is the vanilla gravity compensation
                break;
            case "up":
                // Invert gravity for upward movement
                newVelocityY += netAcceleration * deltaTime;
                break;
            case "south":
                // Apply gravity on the Z-axis downwards
                newVelocityZ -= netAcceleration * deltaTime;
                break;
            case "north":
                // Apply gravity on the Z-axis upwards
                newVelocityZ += netAcceleration * deltaTime;
                break;
            case "east":
                // Apply gravity on the X-axis downwards
                newVelocityX -= netAcceleration * deltaTime;
                break;
            case "west":
                // Apply gravity on the X-axis upwards
                newVelocityX += netAcceleration * deltaTime;
                break;
            default:
                // Default to downward gravity if direction is unrecognized
                newVelocityY -= netAcceleration * deltaTime + 0.078;
                break;
        }

        // Set the new velocity with updated components
        entity.setVelocity(newVelocityX, newVelocityY, newVelocityZ);
    }
}

