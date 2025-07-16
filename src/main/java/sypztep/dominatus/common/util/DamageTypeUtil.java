package sypztep.dominatus.common.util;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.tag.DamageTypeTags;

public class DamageTypeUtil {

    public static boolean isMagicDamage(DamageSource damageSource) {
        return damageSource.isIn(DamageTypeTags.WITCH_RESISTANT_TO) || // Includes magic, indirect_magic
                damageSource.isOf(DamageTypes.MAGIC) ||
                damageSource.isOf(DamageTypes.INDIRECT_MAGIC) ||
                damageSource.isOf(DamageTypes.DRAGON_BREATH) ||
                damageSource.isOf(DamageTypes.WITHER) ||
                damageSource.isOf(DamageTypes.WITHER_SKULL) ||
                damageSource.getName().contains("spell");
    }
}