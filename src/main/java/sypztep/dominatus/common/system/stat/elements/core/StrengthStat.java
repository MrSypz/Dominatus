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

public class StrengthStat extends Stat {

    protected static final Identifier PRIMARY_MODIFIER_ID = Dominatus.id("strength_primary");
    protected static final Identifier SECONDARY_MODIFIER_ID = Dominatus.id("strength_secondary");

    protected static final double MELEE_DAMAGE_SCALING = 0.02; // 2% per point
    protected static final double CRIT_CHANCE_SCALING = 0.005; // 0.5% per point
    protected static final double ATTACK_SPEED_SCALING = 0.002; // 0.2% per point

    public StrengthStat() {
        super(1); // Base STR of 1
    }

    @Override
    public void applyPrimaryEffect(LivingEntity entity) {
        applyEffect(entity,
                ModEntityAttributes.MELEE_ATTACK_DAMAGE,
                PRIMARY_MODIFIER_ID,
                EntityAttributeModifier.Operation.ADD_VALUE,
                baseValue -> (currentValue - this.baseValue) * MELEE_DAMAGE_SCALING
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity entity) {
        List<AttributeModification> modifications = List.of(
                new AttributeModification(
                        ModEntityAttributes.CRIT_CHANCE,
                        SECONDARY_MODIFIER_ID,
                        EntityAttributeModifier.Operation.ADD_VALUE,
                        baseValue -> (currentValue - this.baseValue) * CRIT_CHANCE_SCALING
                ),
                new AttributeModification(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        SECONDARY_MODIFIER_ID,
                        EntityAttributeModifier.Operation.ADD_VALUE,
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

    // Helper methods for subclasses
    protected double calculateMeleeDamageBonus() {
        return (currentValue - baseValue) * MELEE_DAMAGE_SCALING;
    }

    protected double calculateCritChanceBonus() {
        return (currentValue - baseValue) * CRIT_CHANCE_SCALING;
    }

    protected double calculateAttackSpeedBonus() {
        return (currentValue - baseValue) * ATTACK_SPEED_SCALING;
    }
}