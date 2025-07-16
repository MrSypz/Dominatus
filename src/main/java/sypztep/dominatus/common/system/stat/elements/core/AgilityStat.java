package sypztep.dominatus.common.system.stat.elements.core;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.stat.Stat;

public class AgilityStat extends Stat {

    protected static final Identifier PRIMARY_MODIFIER_ID = Dominatus.id("agility_primary");
    protected static final Identifier SECONDARY_MODIFIER_ID = Dominatus.id("agility_secondary");

    protected static final double ATTACK_SPEED_SCALING = 0.01; // 1% per point
    protected static final double BOW_DRAW_SPEED_SCALING = 0.005; // 0.5% per point

    public AgilityStat() {
        super(1); // Base AGI of 1
    }

    @Override
    public void applyPrimaryEffect(LivingEntity entity) {
        applyEffect(entity,
                EntityAttributes.GENERIC_ATTACK_SPEED,
                PRIMARY_MODIFIER_ID,
                EntityAttributeModifier.Operation.ADD_VALUE,
                baseValue -> (currentValue - this.baseValue) * ATTACK_SPEED_SCALING
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity entity) {
        // Add 1 evasion per AGI point (flat bonus)
        applyEffect(entity,
                ModEntityAttributes.EVASION,
                SECONDARY_MODIFIER_ID,
                EntityAttributeModifier.Operation.ADD_VALUE,
                baseValue -> (double)(currentValue - this.baseValue) // 1 per point
        );

        // Bow draw speed bonus
        applyEffect(entity,
                ModEntityAttributes.BOW_DRAWSPEED,
                SECONDARY_MODIFIER_ID,
                EntityAttributeModifier.Operation.ADD_VALUE,
                baseValue -> (currentValue - this.baseValue) * BOW_DRAW_SPEED_SCALING
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

    // Helper methods
    protected double calculateAttackSpeedBonus() {
        return (currentValue - baseValue) * ATTACK_SPEED_SCALING;
    }

    protected int calculateEvasionBonus() {
        return currentValue - baseValue; // 1 per point
    }

    protected double calculateBowDrawSpeedBonus() {
        return (currentValue - baseValue) * BOW_DRAW_SPEED_SCALING;
    }
}