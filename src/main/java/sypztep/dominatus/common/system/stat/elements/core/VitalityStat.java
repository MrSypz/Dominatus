package sypztep.dominatus.common.system.stat.elements.core;

import net.minecraft.entity.LivingEntity;
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
    protected static final double PHYSICAL_RESISTANCE_SCALING = 0.01; // 1% per point

    public VitalityStat() {
        super(1); // Base VIT of 1
    }

    @Override
    public void applyPrimaryEffect(LivingEntity entity) {
        applyEffect(entity,
                EntityAttributes.GENERIC_MAX_HEALTH,
                PRIMARY_MODIFIER_ID,
                baseValue -> baseValue * (currentValue - this.baseValue) * MAX_HEALTH_SCALING
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity entity) {
        List<AttributeModification> modifications = List.of(
                AttributeModification.addValue(
                        ModEntityAttributes.HEALTH_REGEN,
                        SECONDARY_MODIFIER_ID,
                        baseValue -> (currentValue - this.baseValue) * HEALTH_REGEN_SCALING
                ),
                AttributeModification.addValue(
                        ModEntityAttributes.PHYSICAL_RESISTANCE,
                        getSecondaryModifierId(),
                        baseValue -> (currentValue - this.baseValue) * MAX_HEALTH_SCALING
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

    public static double calculateMaxHealthBonus(int currentValue, int baseValue) {
        return 20 * (currentValue - baseValue) * MAX_HEALTH_SCALING;
    }

    public static double calculateHealthRegenBonus(int currentValue, int baseValue) {
        return (currentValue - baseValue) * HEALTH_REGEN_SCALING;
    }
    public static double calculatePhysicalResistanceBonus(int currentValue, int baseValue) {
        return (currentValue - baseValue) * PHYSICAL_RESISTANCE_SCALING;
    }
}