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
     * Calculate hit chance against target's evasion
     * @param targetEvasion The target's evasion value
     * @return Final hit chance (0.0 to 1.0)
     */
    public double calculateHitChance(Evasion targetEvasion) {
        double accuracyEffect = calculateEffect();
        double evasionEffect = targetEvasion.calculateEffect();
        double hitChance = accuracyEffect * (1.0 - evasionEffect);
        return MathHelper.clamp(hitChance, 0.05f, 1.0f); // between 0.05 (5%) and 1.0 (100%)
    }
}