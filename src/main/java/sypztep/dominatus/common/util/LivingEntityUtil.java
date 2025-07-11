package sypztep.dominatus.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import sypztep.dominatus.common.init.ModEntityAttributes;

public class LivingEntityUtil {
    public static void playCriticalSound(Entity target) {
        target.getWorld().playSound(null, target.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.HOSTILE, 1, 1);
    }

    public static boolean isHitable(LivingEntity entity, DamageSource source) {
        return !entity.isInvulnerable() && !entity.isInvulnerableTo(source) && entity.hurtTime == 0;
    }

    public static boolean hitCheck(LivingEntity attacker, LivingEntity defender) {
        int attackerAccuracy = getAccuracy(attacker);
        int defenderEvasion = getEvasion(defender);
        int hitRate = attackerAccuracy - defenderEvasion;

        if (!isPlayer(defender)) hitRate += 25;

        float hitChance = hitRate / 100.0f;
        return roll(attacker) < hitChance;
    }
    public static boolean critCheck(LivingEntity attacker) {
        return roll(attacker) < getCritChance(attacker);
    }

    public static float roll(LivingEntity attacker) {
        return attacker.getRandom().nextFloat();
    }

    public static boolean isPlayer(LivingEntity entity) {
        return entity instanceof PlayerEntity;
    }

    private static int getAccuracy(LivingEntity entity) {
        return (int) entity.getAttributeValue(ModEntityAttributes.ACCURACY);
    }

    private static int getEvasion(LivingEntity entity) {
        return (int) entity.getAttributeValue(ModEntityAttributes.EVASION);
    }

    public static float getCritChance(LivingEntity entity) {
        return (float) entity.getAttributeValue(ModEntityAttributes.CRIT_CHANCE);
    }
}
