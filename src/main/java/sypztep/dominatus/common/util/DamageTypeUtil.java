package sypztep.dominatus.common.util;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.tag.DamageTypeTags;

public class DamageTypeUtil {

    /**
     * Determines if a damage source is considered "magic" damage
     */
    public static boolean isMagicDamage(DamageSource damageSource) {
        // Magic damage types
        return damageSource.isIn(DamageTypeTags.WITCH_RESISTANT_TO) || // Includes magic, indirect_magic
                damageSource.isOf(DamageTypes.MAGIC) ||
                damageSource.isOf(DamageTypes.INDIRECT_MAGIC) ||
                damageSource.isOf(DamageTypes.DRAGON_BREATH) ||
                damageSource.isOf(DamageTypes.WITHER) ||
                damageSource.isOf(DamageTypes.WITHER_SKULL) ||
                damageSource.getName().contains("spell");
    }

    /**
     * Determines if a damage source is considered "physical" damage
     */
    public static boolean isPhysicalDamage(DamageSource damageSource) {
        // Physical damage types
        return damageSource.isOf(DamageTypes.MOB_ATTACK) ||
                damageSource.isOf(DamageTypes.PLAYER_ATTACK) ||
                damageSource.isOf(DamageTypes.MOB_PROJECTILE) ||
                damageSource.isOf(DamageTypes.ARROW) ||
                damageSource.isOf(DamageTypes.TRIDENT) ||
                damageSource.isOf(DamageTypes.THROWN) ||
                damageSource.isOf(DamageTypes.CACTUS) ||
                damageSource.isOf(DamageTypes.SWEET_BERRY_BUSH) ||
                damageSource.isOf(DamageTypes.FALLING_BLOCK) ||
                damageSource.isOf(DamageTypes.FALLING_ANVIL) ||
                damageSource.isOf(DamageTypes.FALLING_STALACTITE);
    }

    /**
     * Determines if a damage source is considered "melee" damage (subset of physical)
     */
    public static boolean isMeleeDamage(DamageSource damageSource) {
        return damageSource.isOf(DamageTypes.MOB_ATTACK) ||
                damageSource.isOf(DamageTypes.PLAYER_ATTACK) ||
                (damageSource.getAttacker() != null && damageSource.getSource() == damageSource.getAttacker());
    }

    /**
     * Determines if a damage source is ranged physical damage
     */
    public static boolean isRangedPhysicalDamage(DamageSource damageSource) {
        return isPhysicalDamage(damageSource) && !isMeleeDamage(damageSource) || damageSource.isOf(DamageTypes.MOB_PROJECTILE);
    }
}