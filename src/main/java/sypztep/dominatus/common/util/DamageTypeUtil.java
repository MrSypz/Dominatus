package sypztep.dominatus.common.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import sypztep.dominatus.common.init.ModDamageTags;
import sypztep.dominatus.common.init.ModEntityAttributes;

public class DamageTypeUtil {
    public static boolean isMagicDamage(DamageSource damageSource) {
        return damageSource.isIn(ModDamageTags.MAGIC_DAMAGE);
    }

    public static boolean isPhysicalDamage(DamageSource damageSource) {
        return damageSource.isIn(ModDamageTags.PHYSICAL_DAMAGE);
    }

    public static boolean isMeleeDamage(DamageSource damageSource) {
        return damageSource.isIn(ModDamageTags.MELEE_DAMAGE);
    }

    public static boolean isFireDamage(DamageSource damageSource) {
        return damageSource.isIn(ModDamageTags.FIRE_DAMAGE);
    }

    public static boolean isProjectileDamage(DamageSource damageSource) {
        return damageSource.isIn(ModDamageTags.PROJECTILE_DAMAGE);
    }
    // NEW: Damage bonus calculation
    public static float calculateDamageBonus(LivingEntity attacker, DamageSource source) {
        float totalBonus = 0.0f;

        if (isMagicDamage(source)) {
            totalBonus += (float) attacker.getAttributeValue(ModEntityAttributes.MAGIC_ATTACK_DAMAGE);
        }

        if (isMeleeDamage(source)) {
            totalBonus += (float) attacker.getAttributeValue(ModEntityAttributes.MELEE_ATTACK_DAMAGE);
        }

        if (isProjectileDamage(source)) {
            totalBonus += (float) attacker.getAttributeValue(ModEntityAttributes.PROJECTILE_ATTACK_DAMAGE);
        }

        return totalBonus;
    }

    // NEW: Resistance calculation
    public static float calculateResistance(LivingEntity defender, DamageSource source) {
        float totalResistance = 0.0f;

        if (isMagicDamage(source)) {
            float magicResistance = (float) defender.getAttributeValue(ModEntityAttributes.MAGIC_RESISTANCE);
            totalResistance += magicResistance;
        }

        if (isPhysicalDamage(source)) {
            float physicalResistance = (float) defender.getAttributeValue(ModEntityAttributes.PHYSICAL_RESISTANCE);
            totalResistance += physicalResistance;
        }

        // Cap total resistance at 75% (minimum 25% damage goes through)
        return Math.min(totalResistance, 0.75f);
    }
}