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

public class VitalityStat extends Stat {

    protected static final Identifier PRIMARY_MODIFIER_ID = Dominatus.id("vitality_primary");
    protected static final Identifier SECONDARY_MODIFIER_ID = Dominatus.id("vitality_secondary");

    protected static final double MAX_HEALTH_SCALING = 0.05; // 5% per point
    protected static final double HEALTH_REGEN_SCALING = 0.02; // 2% per point
    protected static final double PHYSICAL_RESISTANCE_SCALING = 0.005; // 0.5% per point

    public VitalityStat() {
        super(1); // Base VIT of 1
    }

    @Override
    public void applyPrimaryEffect(LivingEntity entity) {
        applyEffect(entity,
                EntityAttributes.GENERIC_MAX_HEALTH,
                PRIMARY_MODIFIER_ID,
                EntityAttributeModifier.Operation.ADD_VALUE,
                baseValue -> baseValue * (currentValue - this.baseValue) * MAX_HEALTH_SCALING
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity entity) {
        List<AttributeModification> modifications = List.of(
                new AttributeModification(
                        ModEntityAttributes.HEALTH_REGEN,
                        SECONDARY_MODIFIER_ID,
                        EntityAttributeModifier.Operation.ADD_VALUE,
                        baseValue -> (currentValue - this.baseValue) * HEALTH_REGEN_SCALING
                ),
                new AttributeModification(
                        ModEntityAttributes.PHYSICAL_RESISTANCE,
                        SECONDARY_MODIFIER_ID,
                        EntityAttributeModifier.Operation.ADD_VALUE,
                        baseValue -> (currentValue - this.baseValue) * PHYSICAL_RESISTANCE_SCALING
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

    // Helper methods
    protected double calculateMaxHealthBonus(double baseHealth) {
        return baseHealth * (currentValue - this.baseValue) * MAX_HEALTH_SCALING;
    }

    protected double calculateHealthRegenBonus() {
        return (currentValue - baseValue) * HEALTH_REGEN_SCALING;
    }

    protected double calculatePhysicalResistanceBonus() {
        return (currentValue - baseValue) * PHYSICAL_RESISTANCE_SCALING;
    }
}