package sypztep.dominatus.common.system.stat.elements.core;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.stat.Stat;

public class DexterityStat extends Stat {

    protected static final Identifier PRIMARY_MODIFIER_ID = Dominatus.id("dexterity_main");
    protected static final Identifier SECONDARY_MODIFIER_ID = Dominatus.id("dexterity_secondary");
    protected static final double EVASION_SCALING = 0.6;

    public DexterityStat() {
        super(1); // Base DEX of 1
    }

    @Override
    public void applyPrimaryEffect(LivingEntity entity) {
        applyEffect(entity,
                ModEntityAttributes.ACCURACY,
                PRIMARY_MODIFIER_ID,
                EntityAttributeModifier.Operation.ADD_VALUE,
                baseValue -> baseValue + 1
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity entity) {
        applyEffect(entity,
                ModEntityAttributes.EVASION,
                SECONDARY_MODIFIER_ID,
                EntityAttributeModifier.Operation.ADD_VALUE,
                baseValue -> (currentValue - this.baseValue) * EVASION_SCALING
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

    protected double calculateAccuracyBonus() {
        return currentValue;
    }

    protected double calculateEvasionBonus() {
        return currentValue;
    }
}