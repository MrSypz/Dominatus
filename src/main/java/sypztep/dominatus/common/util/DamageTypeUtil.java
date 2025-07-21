package sypztep.dominatus.common.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import sypztep.dominatus.common.init.ModDamageTags;
import sypztep.dominatus.common.init.ModEntityAttributes;

public class DamageTypeUtil {
    public static boolean isMagicDamage(DamageSource damageSource) {
        return damageSource.isIn(ModDamageTags.MAGIC_DAMAGE);
    }

    public static boolean isProjectileDamage(DamageSource damageSource) {
        return damageSource.isIn(ModDamageTags.PROJECTILE_DAMAGE);
    }

    public static float calculateDamageBonus(LivingEntity attacker, DamageSource source) {
        float totalBonus = 0.0f;

        if (isMagicDamage(source)) totalBonus += (float) attacker.getAttributeValue(ModEntityAttributes.MAGIC_ATTACK_DAMAGE);

        if (isProjectileDamage(source)) totalBonus += (float) attacker.getAttributeValue(ModEntityAttributes.PROJECTILE_ATTACK_DAMAGE);

        return totalBonus;
    }

    public static float calculateResistance(LivingEntity defender, DamageSource source) {
        float totalResistance = 0.0f;

        if (isMagicDamage(source)) {
            float magicResistance = (float) defender.getAttributeValue(ModEntityAttributes.MAGIC_RESISTANCE);
            totalResistance += magicResistance;
        }

        return Math.min(totalResistance, 0.75f);
    }
}