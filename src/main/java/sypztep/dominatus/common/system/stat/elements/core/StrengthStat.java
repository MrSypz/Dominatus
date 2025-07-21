package sypztep.dominatus.common.system.stat.elements.core;

import net.minecraft.entity.LivingEntity;
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

    protected static final double MELEE_DAMAGE_SCALING = 0.05; // 5% per point
    protected static final double BLOCK_BREAK_SPEED_SCALING = 0.025; // 0.1% per point
    protected static final double ATTACK_SPEED_SCALING = 0.002; // 0.2% per point

    public StrengthStat() {
        super(1); // Base STR of 1
    }

    @Override
    public void applyPrimaryEffect(LivingEntity entity) {
        applyEffect(entity,
                ModEntityAttributes.MELEE_ATTACK_DAMAGE,
                PRIMARY_MODIFIER_ID,
                baseValue -> (currentValue - this.baseValue) * MELEE_DAMAGE_SCALING
        );
    }

    @Override
    public void applySecondaryEffect(LivingEntity entity) {
        List<AttributeModification> modifications = List.of(
                AttributeModification.addValue(
                        EntityAttributes.PLAYER_BLOCK_BREAK_SPEED,
                        SECONDARY_MODIFIER_ID,
                        baseValue -> (currentValue - this.baseValue) * BLOCK_BREAK_SPEED_SCALING
                ),
                AttributeModification.addValue(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        SECONDARY_MODIFIER_ID,
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
    public static double calculateMeleeDamageBonus(int currentValue, int baseValue) {
        return (currentValue - baseValue) * MELEE_DAMAGE_SCALING;
    }

    public static double calculateBlockBreakSpeedBonus(int currentValue, int baseValue) {
        return (currentValue - baseValue) * BLOCK_BREAK_SPEED_SCALING;
    }

    public static double calculateAttackSpeedBonus(int currentValue, int baseValue) {
        return (currentValue - baseValue) * ATTACK_SPEED_SCALING;
    }
}