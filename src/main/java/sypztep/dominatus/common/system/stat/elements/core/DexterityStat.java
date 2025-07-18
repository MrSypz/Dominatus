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

public class DexterityStat extends Stat {

    protected static final Identifier PRIMARY_MODIFIER_ID = Dominatus.id("dexterity_primary");
    protected static final Identifier SECONDARY_MODIFIER_ID = Dominatus.id("dexterity_secondary");

    protected static final double ATTACK_SPEED_SCALING = 0.005; // 0.5% attack speed per point
    protected static final double PROJECTILE_DAMAGE_SCALING = 0.02; // 2% attack speed per point

    public DexterityStat() {
        super(1); // Base DEX of 1
    }

    @Override
    public void applyPrimaryEffect(LivingEntity entity) {
        // Primary effect: +1 Accuracy per DEX point
        applyEffect(entity,
                ModEntityAttributes.ACCURACY,
                getPrimaryModifierId(),
                baseValue -> (double)(currentValue - this.baseValue) // +1 per point above base
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity entity) {
        // Secondary effect: Attack speed bonus
        List<AttributeModification> modifications = List.of(
                AttributeModification.addValue(
                        ModEntityAttributes.PROJECTILE_ATTACK_DAMAGE,
                        getSecondaryModifierId(),
                        baseValue -> (currentValue - this.baseValue) * PROJECTILE_DAMAGE_SCALING
                ),
                AttributeModification.addValue(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        getSecondaryModifierId(),
                        baseValue -> (currentValue - this.baseValue) * ATTACK_SPEED_SCALING
                )
        );
        applyEffects(entity, modifications);

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