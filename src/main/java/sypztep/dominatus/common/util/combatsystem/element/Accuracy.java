package sypztep.dominatus.common.util.combatsystem.element;

import net.minecraft.util.math.MathHelper;
import sypztep.dominatus.common.util.combatsystem.CombatAttribute;

public final class Accuracy extends CombatAttribute {
    private static final double BASE_HIT_CHANCE = 0.65;
    private static final double ACCURACY_SCALING = 0.005;

    public Accuracy(double baseValue) {
        super(baseValue);
    }

    @Override
    public double calculateEffect() {
        return BASE_HIT_CHANCE + (getTotalValue() * ACCURACY_SCALING);
    }

    /**
     * Calculate hit chance against target's evasion and armor
     * @param targetEvasion The target's evasion value
     * @param targetArmor The target's armor points
     * @return Final hit chance (0.0 to 1.0)
     */
    public double calculateHitChance(Evasion targetEvasion, float targetArmor) {
        double hitChance = calculateEffect() - targetEvasion.calculateEffect();
        double armorEffect = targetArmor * 0.003;
        hitChance -= armorEffect;
        return MathHelper.clamp(hitChance, 0.05, 1);
    }
}