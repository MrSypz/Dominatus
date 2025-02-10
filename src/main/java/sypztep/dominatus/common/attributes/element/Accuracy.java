package sypztep.dominatus.common.attributes.element;

import sypztep.dominatus.common.attributes.CombatAttribute;

/**
 * Handles accuracy calculations and modifiers
 */
public class Accuracy extends CombatAttribute {
    private static final double BASE_HIT_CHANCE = 0.95; // 95% base hit chance
    private static final double ACCURACY_SCALING_FACTOR = 0.001; // 0.1% per point

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

        // Ensure hit chance stays between 0.05 (5%) and 0.95 (95%)
        return Math.min(0.95, Math.max(0.05, hitChance));
    }
}