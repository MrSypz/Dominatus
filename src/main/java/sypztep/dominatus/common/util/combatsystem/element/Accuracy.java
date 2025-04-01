package sypztep.dominatus.common.util.combatsystem.element;

import net.minecraft.util.math.MathHelper;
import sypztep.dominatus.common.util.combatsystem.CombatAttribute;

/**
 * Handles accuracy calculations and modifiers
 */
public class Accuracy extends CombatAttribute {
    private static final double BASE_HIT_CHANCE = 0.95; // 95% base hit chance
    private static final double ACCURACY_SCALING_FACTOR = 0.00125; // 0.125% per point

    public Accuracy(double baseValue) {
        super(baseValue);
    }

    @Override
    public double calculateEffect() {
        return BASE_HIT_CHANCE + (getTotalValue() * ACCURACY_SCALING_FACTOR);
    }

    /**
     * Calculate hit chance against target's evasion and armor
     * @param targetEvasion The target's evasion value
     * @param targetArmor The target's armor points (can exceed vanilla cap of 20)
     * @return Final hit chance (0.0 to 1.0)
     */
    public double calculateHitChance(Evasion targetEvasion, float targetArmor) {
        double accuracyEffect = calculateEffect();
        double evasionEffect = targetEvasion.calculateEffect();

        /* Apply armor scaling to evasion effect
        This uses a different scaling curve for armor > 20 */
        double armorBonus = 0;
        if (targetArmor > 0) {
            if (targetArmor <= 20.0f) {
                armorBonus = targetArmor * 0.005; // Each point gives 0.5% evasion boost
            } else {
                armorBonus = 20 * 0.005 + (targetArmor - 20.0f) * 0.0015; // Points above 20 give 0.05% evasion
            }
        }

        // Enhanced evasion effect with armor bonus
        double enhancedEvasionEffect = evasionEffect + armorBonus;

        // Apply hit chance formula
        double hitChance = accuracyEffect * (1.0 - enhancedEvasionEffect);

        // Additional balancing for high accuracy vs high armor
        if (getTotalValue() > 200 && targetArmor > 20) {
            double highAccuracyBonus = (getTotalValue() - 200) / 500.0;
            hitChance += highAccuracyBonus * (enhancedEvasionEffect - evasionEffect);
        }

        return MathHelper.clamp(hitChance, 0.05, 0.95);
    }
}