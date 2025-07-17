package sypztep.dominatus.common.event.critevasionandexp;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.common.api.entity.DominatusLivingEntityEvents;
import sypztep.dominatus.common.api.entity.DominatusPlayerEntityEvents;
import sypztep.dominatus.common.component.living.DamageTrackerComponent;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.init.ModParticles;
import sypztep.dominatus.common.system.level.core.LevelData;
import sypztep.dominatus.common.util.LivingEntityUtil;
import sypztep.dominatus.common.util.ParticleHandler;
// NOTE : modifier damage are in LivingEntityPart in mixin is ApplyDamage method
public final class PlayerEntityEvent implements DominatusPlayerEntityEvents.ModifyAttackDamage,
        DominatusPlayerEntityEvents.ModifyAttackCondition,
        DominatusPlayerEntityEvents.AllowAttack,
        DominatusLivingEntityEvents.DamageDealt,
        ServerLivingEntityEvents.AfterDeath {
    private static final PlayerEntityEvent INSTANCE = new PlayerEntityEvent();
    private static final float DEATH_PENALTY_PERCENTAGE = ModConfig.deathPenaltyPercentage * 0.01f;

    public static void register() {
        DominatusPlayerEntityEvents.MODIFY_ATTACK_CONDITION.register(INSTANCE);
        DominatusPlayerEntityEvents.MODIFY_ATTACK_DAMAGE.register(INSTANCE);
        DominatusPlayerEntityEvents.ALLOW_ATTACK.register(INSTANCE);
        DominatusLivingEntityEvents.DAMAGE_DEALT.register(INSTANCE);
        ServerLivingEntityEvents.AFTER_DEATH.register(INSTANCE);
    }
    @Override
    public boolean allowAttack(PlayerEntity player, Entity target) {
        LivingEntity livingTarget = (LivingEntity) target;
        DamageSource damageSource = target.getDamageSources().playerAttack(player);

        if (!LivingEntityUtil.isHitable(livingTarget, damageSource)) return false;

        if (!LivingEntityUtil.hitCheck(player, livingTarget)) {
            ParticleHandler.sendToAll(target, player, ModParticles.MISSING);
            target.getWorld().playSound(null, target.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, target.getSoundCategory());
            return false;
        }
        return true;
    }

    @Override
    public boolean modifyCondition(PlayerEntity player, Entity target, boolean vanillaCrit) {
        boolean isCrit = LivingEntityUtil.critCheck(player);
        if (isCrit) ParticleHandler.sendToAll(target, player, ModParticles.CRITICAL);
        return isCrit;
    }

    @Override
    public float modifyDamage(PlayerEntity player, float originalDamage) {
        return 1 + (float) player.getAttributeValue(ModEntityAttributes.CRIT_DAMAGE);
    }

    @Override
    public void onDamageDealt(LivingEntity entity, DamageSource source, float finalDamage) {
        if (entity.getWorld().isClient()) return; // Server Side Only

        if (entity instanceof PlayerEntity || !(source.getAttacker() instanceof PlayerEntity player)) return;

        DamageTrackerComponent tracker = ModEntityComponents.DAMAGETRACKER.getNullable(entity);

        if (tracker == null) return;

        if (finalDamage > 0) {
            float actualDamage = Math.min(finalDamage, entity.getHealth());
            tracker.addDamage(player, actualDamage);
        }
    }
    @Override
    public void afterDeath(LivingEntity entity, DamageSource damageSource) {
        if (!(entity instanceof ServerPlayerEntity player) || entity.getWorld().isClient()) {
            return;
        }

        if (!ModConfig.enableDeathPenalty) {
            return;
        }
        if (!isKilledByMonster(damageSource)) {
            return;
        }

        applyDeathPenalty(player, damageSource);
    }

    private boolean isKilledByMonster(DamageSource damageSource) {
        // Direct attack from a living entity (excluding players)
        if (damageSource.getAttacker() instanceof LivingEntity attacker) {
            return !(attacker instanceof PlayerEntity);
        }

        // Indirect damage from a living entity (projectiles, etc.)
        if (damageSource.getSource() instanceof LivingEntity source) {
            return !(source instanceof PlayerEntity);
        }

        return false;
    }

    private void applyDeathPenalty(ServerPlayerEntity player, DamageSource damageSource) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = levelComponent.getLevelData();

        // No penalty if player is at max level
        if (levelData.isMaxLevel()) {
            return;
        }

        // Calculate penalty: 10% of experience needed for next level
        long expToNextLevel = levelData.getExperienceToNextLevel();
        long penaltyAmount = Math.round(expToNextLevel * DEATH_PENALTY_PERCENTAGE);

        if (penaltyAmount <= 0) {
            return; // No penalty if next level exp is 0 or calculation resulted in 0
        }

        // Apply the penalty by subtracting experience
        long currentExp = levelData.getExperience();
        long newExp = Math.max(0, currentExp - penaltyAmount);

        levelComponent.setExperience(newExp);

        // Log the penalty
        String killerName = getKillerName(damageSource);
        Dominatus.LOGGER.debug("Player {} lost {} exp due to death penalty (killed by {})",
                player.getName().getString(), penaltyAmount, killerName);

        // Notify the player
        notifyPlayer(player, penaltyAmount, killerName);
    }

    /**
     * Gets a readable name for what killed the player
     */
    private String getKillerName(DamageSource damageSource) {
        if (damageSource.getAttacker() instanceof LivingEntity attacker) {
            if (attacker.hasCustomName()) {
                return attacker.getCustomName().getString();
            }
            return attacker.getType().getName().getString();
        }

        if (damageSource.getSource() instanceof LivingEntity source) {
            if (source.hasCustomName()) {
                return source.getCustomName().getString();
            }
            return source.getType().getName().getString();
        }

        // Fallback to damage type name
        return damageSource.getName();
    }

    /**
     * Sends a death penalty notification to the player
     */
    private void notifyPlayer(ServerPlayerEntity player, long penaltyAmount, String killerName) {
        // Format the penalty amount
        String formattedPenalty = formatExperienceAmount(penaltyAmount);

        // Create death penalty message
        Text penaltyMessage = Text.literal(String.format(
                "§c§lDEATH PENALTY! §r§cYou lost %s experience for being slain by %s",
                formattedPenalty, killerName
        )).formatted(Formatting.RED);

        // Send to player
        player.sendMessage(penaltyMessage, false);

        // Optional: Send a subtitle for more visibility
        Text subtitle = Text.literal(String.format("-%s EXP", formattedPenalty))
                .formatted(Formatting.RED, Formatting.BOLD);

        player.sendMessage(subtitle, true); // Send as action bar
    }
    private String formatExperienceAmount(long amount) {
        if (amount >= 1_000_000_000L) {
            return String.format("%.1fB", amount / 1_000_000_000.0);
        } else if (amount >= 1_000_000L) {
            return String.format("%.1fM", amount / 1_000_000.0);
        } else if (amount >= 1_000L) {
            return String.format("%.1fK", amount / 1_000.0);
        } else {
            return String.valueOf(amount);
        }
    }
    public static double getDeathPenaltyPercentage() {
        return DEATH_PENALTY_PERCENTAGE;
    }
}
