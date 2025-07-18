package sypztep.dominatus.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.client.payload.SendToastPayloadS2C;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModEntityComponents;

public final class LivingEntityUtil {
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


    public static boolean isKilledByMonster(DamageSource damageSource) {
        if (damageSource.getAttacker() instanceof LivingEntity attacker) return !(attacker instanceof PlayerEntity);

        // Indirect damage from a living entity (projectiles, etc.)
        if (damageSource.getSource() instanceof LivingEntity source) return !(source instanceof PlayerEntity);

        return false;
    }

    public static void applyDeathPenalty(ServerPlayerEntity player, DamageSource damageSource) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);

        // No penalty if player is at max level
        if (levelComponent.isMaxLevel()) return;

        // Calculate penalty: 10% of experience needed for next level
        long expToNextLevel = levelComponent.getExperienceToNextLevel();
        long penaltyAmount = Math.round(expToNextLevel * ModConfig.deathPenaltyPercentage);

        // No penalty if next level exp is 0 or calculation resulted in 0
        if (penaltyAmount <= 0) return;


        // Apply the penalty by subtracting experience
        long currentExp = levelComponent.getExperience();
        long newExp = Math.max(0, currentExp - penaltyAmount);

        levelComponent.setExperience(newExp);

        // Log the penalty
        String killerName = getKillerName(damageSource);
        // Notify the player
        SendToastPayloadS2C.sendDeathPenalty(player, penaltyAmount, killerName);

    }

    /**
     * Gets a readable name for what killed the player
     */
    private static String getKillerName(DamageSource damageSource) {
        if (damageSource.getAttacker() instanceof LivingEntity attacker) {
            if (attacker.hasCustomName()) return attacker.getCustomName().getString();
            return attacker.getType().getName().getString();
        }

        if (damageSource.getSource() instanceof LivingEntity source) {
            if (source.hasCustomName()) return source.getCustomName().getString();
            return source.getType().getName().getString();
        }

        // Fallback to damage type name
        return damageSource.getName();
    }
}
