package net.wecan.kinetic.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.wecan.kinetic.config.GravityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class RealisticSmashDamage {

    @Shadow protected abstract void attackLivingEntity(LivingEntity target);

    private Vec3d previousVelocity = Vec3d.ZERO; // Previous velocity
    private double previousEnergy = 0; // Track previous energy to calculate energy dissipated
    private long lastCollisionTime = 0; // Track the last collision time
    private static final Logger LOGGER = LogManager.getLogger("kineticmod");
    private static final long COLLISION_COOLDOWN_MS = 400; // Cooldown period of 0.4 seconds

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo info) {
        LivingEntity entity = (LivingEntity)(Object)this;

        if (entity.isRemoved() || entity.isDead()) {
            // Reset if the entity is removed or dead
            previousVelocity = Vec3d.ZERO;
            previousEnergy = 0;
            lastCollisionTime = 0;
            return;
        }

        // Get the current velocity and calculate energy
        Vec3d currentVelocity = entity.getVelocity();
        double totalCurrentEnergy = calculateEnergy(currentVelocity);

        // Detect if a collision happened and energy was dissipated
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastCollisionTime) >= COLLISION_COOLDOWN_MS && collidedWithBlockOrEntity(entity, entity.getBoundingBox(), entity.getWorld())) {
            double dissipatedEnergy = previousEnergy - totalCurrentEnergy;

            if (dissipatedEnergy > 1.1) {
                LOGGER.info("Energy dissipated due to collision: " + dissipatedEnergy);

                // Apply damage based on the dissipated energy, considering Feather Falling and Protection
                float damageAmount = calculateDamageBasedOnEnergy(dissipatedEnergy, entity);
                entity.damage(entity.getDamageSources().fall(), damageAmount);
            }

            lastCollisionTime = currentTime; // Update last collision time
        }

        // Update previous values for the next tick
        previousVelocity = currentVelocity;
        previousEnergy = totalCurrentEnergy;
    }

    // Calculate total kinetic energy (E = 0.5 * v^2 for each axis)
    private double calculateEnergy(Vec3d velocity) {
        return 5 * (velocity.x * velocity.x + velocity.y * velocity.y + velocity.z * velocity.z);
    }

    // Detect if the player collided with a block or entity
    private boolean collidedWithBlockOrEntity(LivingEntity entity, Box boundingBox, World world) {
        // Use the correct method signature for shrink with three arguments (X, Y, Z)
        return !world.isSpaceEmpty(entity, boundingBox.shrink(0.01));
    }

    // Custom method to calculate damage based on dissipated energy and enchantments
    private float calculateDamageBasedOnEnergy(double dissipatedEnergy, LivingEntity entity) {
        float damageAmount = (float)(dissipatedEnergy * GravityConfig.SMASH_MULTIPLIER); // Base damage

        // Apply Feather Falling reduction
        int featherFallingLevel = EnchantmentHelper.getEquipmentLevel(Enchantments.FEATHER_FALLING, entity);
        if (featherFallingLevel > 0) {
            float reduction = 1.0f - (featherFallingLevel * 0.12f); // 12% reduction per level
            damageAmount *= Math.max(reduction, 0.0f);
        }

        // Apply Protection reduction
        int protectionLevel = EnchantmentHelper.getEquipmentLevel(Enchantments.PROTECTION, entity);
        if (protectionLevel > 0) {
            float reduction = 1.0f - (protectionLevel * 0.04f); // 4% reduction per level
            damageAmount *= Math.max(reduction, 0.0f);
        }

        return damageAmount;
    }
}