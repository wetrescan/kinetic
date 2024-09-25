package net.wecan.kinetic.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class RealisticSmashDamage {

    private Vec3d previousVelocity = Vec3d.ZERO; // Previous velocity
    private double previousEnergy = 0; // Track previous energy to calculate energy dissipated
    private static final Logger LOGGER = LogManager.getLogger("kineticmod");

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo info) {
        LivingEntity entity = (LivingEntity)(Object)this;

        if (entity.isRemoved()) {
            // Reset if the entity is removed or dead
            previousVelocity = Vec3d.ZERO;
            previousEnergy = 0;
            return;
        }

        // Get the current velocity and calculate energy
        Vec3d currentVelocity = entity.getVelocity();
        double energyX = calculateEnergyForAxis(currentVelocity.x);
        double energyY = calculateEnergyForAxis(currentVelocity.y);
        double energyZ = calculateEnergyForAxis(currentVelocity.z);
        double totalCurrentEnergy = energyX + energyY + energyZ;

        // Detect if a collision happened and energy was dissipated
        if (entity.isOnGround() || collidedWithBlockOrEntity(entity, entity.getBoundingBox(), entity.getWorld())) {
            double dissipatedEnergy = previousEnergy - totalCurrentEnergy;

            if (dissipatedEnergy > 1.1) {
                LOGGER.info("Energy dissipated due to collision: " + dissipatedEnergy);
            }

            // Log the current energy values for each axis
//            LOGGER.info("Current energy on X axis: " + energyX);
//            LOGGER.info("Current energy on Y axis: " + energyY);
//            LOGGER.info("Current energy on Z axis: " + energyZ);
            //LOGGER.info("Total current energy: " + totalCurrentEnergy);
        }

        // Update previous values for the next tick
        previousVelocity = currentVelocity;
        previousEnergy = totalCurrentEnergy;
    }

    // Calculate energy for a given axis based on velocity (E = 0.5 * v^2)
    private double calculateEnergyForAxis(double velocity) {
        return 5 * velocity * velocity; // E = m * v^2
    }

    // Detect if the player collided with a block or entity
    private boolean collidedWithBlockOrEntity(LivingEntity entity, Box boundingBox, World world) {
        // Use the correct method signature for shrink with three arguments (X, Y, Z)
        return !world.isSpaceEmpty(entity, boundingBox.shrink(0.01, 0.01, 0.01));
    }
}
