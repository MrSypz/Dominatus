package sypztep.dominatus.common.system.stat.elements.core;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.stat.Stat;

public class IntelligenceStat extends Stat {

    protected static final Identifier PRIMARY_MODIFIER_ID = Dominatus.id("intelligence_primary");
    protected static final Identifier SECONDARY_MODIFIER_ID = Dominatus.id("intelligence_secondary");

    protected static final double MAGIC_DAMAGE_SCALING = 0.02; // 2% per point
    protected static final double MAGIC_RESISTANCE_SCALING = 0.005; // 0.5% per point

    public IntelligenceStat() {
        super(1); // Base INT of 1
    }

    @Override
    public void applyPrimaryEffect(LivingEntity entity) {
        applyEffect(entity,
                ModEntityAttributes.MAGIC_ATTACK_DAMAGE,
                PRIMARY_MODIFIER_ID,
                EntityAttributeModifier.Operation.ADD_VALUE,
                baseValue -> (currentValue - this.baseValue) * MAGIC_DAMAGE_SCALING
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity entity) {
        applyEffect(entity,
                ModEntityAttributes.MAGIC_RESISTANCE,
                SECONDARY_MODIFIER_ID,
                EntityAttributeModifier.Operation.ADD_VALUE,
                baseValue -> (currentValue - this.baseValue) * MAGIC_RESISTANCE_SCALING
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
    protected double calculateMagicDamageBonus() {
        return (currentValue - baseValue) * MAGIC_DAMAGE_SCALING;
    }

    protected double calculateMagicResistanceBonus() {
        return (currentValue - baseValue) * MAGIC_RESISTANCE_SCALING;
    }
}