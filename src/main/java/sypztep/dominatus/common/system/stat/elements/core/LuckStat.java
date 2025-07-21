package sypztep.dominatus.common.system.stat.elements.core;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.stat.Stat;
import sypztep.dominatus.common.util.AttributeModification;

import java.util.List;

public class LuckStat extends Stat {

    protected static final Identifier PRIMARY_MODIFIER_ID = Dominatus.id("luck_primary");
    protected static final Identifier SECONDARY_MODIFIER_ID = Dominatus.id("luck_secondary");

    protected static final double CRIT_CHANCE_SCALING = 0.003; // 0.3% per point (every 4 points = 1%)
    protected static final double MAGIC_DAMAGE_SCALING = 0.002; // 0.2% per point
    protected static final double ATTACK_SPEED_SCALING = 0.002; // 0.2% per point

    public LuckStat() {
        super(1); // Base LUK of 1
    }

    @Override
    public void applyPrimaryEffect(LivingEntity entity) {
        applyEffect(entity,
                ModEntityAttributes.CRIT_CHANCE,
                PRIMARY_MODIFIER_ID,
                baseValue -> (currentValue - this.baseValue) * CRIT_CHANCE_SCALING
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity entity) {
        List<AttributeModification> modifications = List.of(
                AttributeModification.addValue(
                        ModEntityAttributes.MAGIC_ATTACK_DAMAGE,
                        SECONDARY_MODIFIER_ID,
                        baseValue -> (currentValue - this.baseValue) * MAGIC_DAMAGE_SCALING
                ),
                AttributeModification.addValue(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        SECONDARY_MODIFIER_ID,
                        baseValue -> (currentValue - this.baseValue) * ATTACK_SPEED_SCALING
                )
        );
        applyEffects(entity, modifications);

        // Special luck bonuses every 3rd and 5th point
        int statPoints = currentValue - baseValue;
        if (statPoints > 0) {
            // Every 3 LUK = +1 Accuracy
            int accuracyBonus = statPoints / 3;
            if (accuracyBonus > 0) {
                applyEffect(entity,
                        ModEntityAttributes.ACCURACY,
                        SECONDARY_MODIFIER_ID,
                        baseValue -> (double) accuracyBonus
                );
            }

            // Every 5 LUK = +1 Evasion
            int evasionBonus = statPoints / 5;
            if (evasionBonus > 0) {
                applyEffect(entity,
                        ModEntityAttributes.EVASION,
                        SECONDARY_MODIFIER_ID,
                        baseValue -> (double) evasionBonus
                );
            }
        }
    }

    @Override
    protected Identifier getPrimaryModifierId() {
        return PRIMARY_MODIFIER_ID;
    }

    @Override
    protected Identifier getSecondaryModifierId() {
        return SECONDARY_MODIFIER_ID;
    }

    // Helper methods
    public static double calculateCritChanceBonus(int currentValue, int baseValue) {
        return (currentValue - baseValue) * CRIT_CHANCE_SCALING;
    }

    public static double calculateMagicDamageBonus(int currentValue, int baseValue) {
        return (currentValue - baseValue) * MAGIC_DAMAGE_SCALING;
    }

    public static double calculateAttackSpeedBonus(int currentValue, int baseValue) {
        return (currentValue - baseValue) * ATTACK_SPEED_SCALING;
    }

    public static int calculateAccuracyBonus(int currentValue, int baseValue) {
        return Math.max(0, (currentValue - baseValue) / 3);
    }

    public static int calculateEvasionBonus(int currentValue, int baseValue) {
        return Math.max(0, (currentValue - baseValue) / 5);
    }
}