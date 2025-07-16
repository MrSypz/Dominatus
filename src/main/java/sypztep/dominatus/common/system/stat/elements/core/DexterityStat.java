package sypztep.dominatus.common.system.stat.elements.core;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.stat.Stat;

public class DexterityStat extends Stat {

    protected static final Identifier PRIMARY_MODIFIER_ID = Dominatus.id("dexterity_primary");
    protected static final Identifier SECONDARY_MODIFIER_ID = Dominatus.id("dexterity_secondary");

    protected static final double ATTACK_SPEED_SCALING = 0.01; // 1% attack speed per point

    public DexterityStat() {
        super(1); // Base DEX of 1
    }

    @Override
    public void applyPrimaryEffect(LivingEntity entity) {
        // Primary effect: +1 Accuracy per DEX point
        applyEffect(entity,
                ModEntityAttributes.ACCURACY,
                PRIMARY_MODIFIER_ID,
                EntityAttributeModifier.Operation.ADD_VALUE,
                baseValue -> (double)(currentValue - this.baseValue) // +1 per point above base
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity entity) {
        // Secondary effect: Attack speed bonus
        applyEffect(entity,
                EntityAttributes.GENERIC_ATTACK_SPEED,
                SECONDARY_MODIFIER_ID,
                EntityAttributeModifier.Operation.ADD_VALUE,
                baseValue -> (currentValue - this.baseValue) * ATTACK_SPEED_SCALING
        );
    }

    @Override
    protected Identifier getPrimaryModifierId() {
        return PRIMARY_MODIFIER_ID;
    }

    @Override
    protected Identifier getSecondaryModifierId() {
        return SECONDARY_MODIFIER_ID;
    }

    // ====================
    // HELPER METHODS FOR SUBCLASSES
    // ====================

    protected int calculateAccuracyBonus() {
        return currentValue - baseValue; // +1 accuracy per point
    }

    protected double calculateAttackSpeedBonus() {
        return (currentValue - baseValue) * ATTACK_SPEED_SCALING;
    }
}