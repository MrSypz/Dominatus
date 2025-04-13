package sypztep.dominatus.common.util.combatsystem;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import static sypztep.dominatus.Dominatus.LOGGER;

public final class NewDamage {
    // Maximum damage reduction cap (90%)
    private static final float MAX_REDUCTION = 0.9F;

    /**
     * Calculate damage after applying armor-based reduction using PoE-inspired formula.
     * <br><br>
     * Formula: damage = damage - defense
     * With a maximum damage reduction of 90%.
     *
     * @param armorWearer The entity wearing armor
     * @param damageAmount The incoming damage amount
     * @param damageSource The source of the damage
     * @param armor The armor value
     * @param armorToughness The armor toughness value
     * @return The damage after reduction
     */
    public static float getDamageLeft(LivingEntity armorWearer, float damageAmount, DamageSource damageSource, float armor, float armorToughness) {
//        LOGGER.info("==================== DAMAGE CALCULATION START ====================");
//        LOGGER.info("Entity: {}, Initial Damage: {}", armorWearer.getName().getString(), damageAmount);
//        LOGGER.info("Armor: {}, Toughness: {}", armor, armorToughness);

        // Calculate base defense
        float baseDefense = armor + armorToughness * 1.2F;
//        LOGGER.info("Base Defense (armor + toughness*1.2): {}", baseDefense);

        float reducedDamage = Math.max(0.0F, damageAmount - baseDefense);
        float initialDamageReduction = damageAmount > 0.0F
                ? 1.0F - (reducedDamage / damageAmount)
                : 0.0F;
//        LOGGER.info("Initial Damage Reduction (before penetration): {}%", initialDamageReduction * 100);

        // Apply armor penetration (directly reduces damage reduction)
        float penetrationPercentage = 0.0F;
        ItemStack itemStack = damageSource.getWeaponStack();
//        LOGGER.info("Weapon: {}", itemStack != null ? itemStack.getName().getString() : "None");

        if (itemStack != null && armorWearer.getWorld() instanceof ServerWorld serverWorld) {
            // Get modified armor effectiveness with penetration applied
            float modifiedArmorEffectiveness = EnchantmentHelper.getArmorEffectiveness(
                    serverWorld,
                    itemStack,
                    armorWearer,
                    damageSource,
                    initialDamageReduction
            );

            // Calculate penetration as the absolute difference in effectiveness
            penetrationPercentage = initialDamageReduction - modifiedArmorEffectiveness;

//            LOGGER.info("Base DR: {}%, Modified DR: {}%, Penetration: {}%",
//                    initialDamageReduction * 100,
//                    modifiedArmorEffectiveness * 100,
//                    penetrationPercentage * 100);
        }

        // Apply penetration directly to damage reduction percentage
        float damageReduction = Math.max(0.0F, initialDamageReduction - penetrationPercentage);
//        LOGGER.info("Final Damage Reduction (after penetration): {}%", damageReduction * 100);

        // Apply the 90% cap to damage reduction
        float cappedReduction = Math.min(damageReduction, MAX_REDUCTION);
//        if (damageReduction > MAX_REDUCTION) {
//            LOGGER.info("Damage Reduction capped from {}% to {}%",
//                    damageReduction * 100, MAX_REDUCTION * 100);
//        }

        // Calculate the final damage after capped reduction
        //        LOGGER.info("Final Damage: {} ({}% of original)",
//                finalDamage, ((finalDamage / damageAmount) * 100));
//        LOGGER.info("==================== DAMAGE CALCULATION END ====================");

        return damageAmount * (1.0F - cappedReduction);
    }
}