package sypztep.dominatus.common.event.critevasionandexp;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import sypztep.dominatus.common.api.entity.DominatusLivingEntityEvents;
import sypztep.dominatus.common.component.living.DamageTrackerComponent;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.init.ModParticles;
import sypztep.dominatus.common.util.DamageTypeUtil;
import sypztep.dominatus.common.util.LivingEntityUtil;
import sypztep.dominatus.common.util.ParticleHandler;
import sypztep.dominatus.common.util.level.ExpUtil;

import java.util.Map;
import java.util.UUID;

public final class LivingEntityEvent implements DominatusLivingEntityEvents.PostArmorDamage,
        ServerLivingEntityEvents.AllowDamage,
        ServerLivingEntityEvents.AfterDeath,
        ServerEntityEvents.Load {
    private static final LivingEntityEvent INSTANCE = new LivingEntityEvent();
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(INSTANCE);
        DominatusLivingEntityEvents.POST_ARMOR_DAMAGE.register(INSTANCE);
        ServerEntityEvents.ENTITY_LOAD.register(INSTANCE);
        ServerLivingEntityEvents.AFTER_DEATH.register(INSTANCE);
    }
    @Override
    public boolean allowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            if (!LivingEntityUtil.isHitable(entity, source)) return false;

            if (!(attacker instanceof PlayerEntity)) {
                if (!LivingEntityUtil.hitCheck(attacker, entity)) {
                    ParticleHandler.sendToAll(entity, attacker, ModParticles.MISSING);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public float modifyDamage(LivingEntity entity, DamageSource source, float amount) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            float totalMultiplier = 1.0f;

            // Crit check for NON-PLAYER attackers only
            if (!(LivingEntityUtil.isPlayer(attacker))) {
                if (LivingEntityUtil.critCheck(attacker)) {
                    ParticleHandler.sendToAll(entity, attacker, ModParticles.CRITICAL);
                    ParticleHandler.sendToAll(entity, attacker, ParticleTypes.CRIT);
                    LivingEntityUtil.playCriticalSound(entity);
                    totalMultiplier += (float) attacker.getAttributeValue(ModEntityAttributes.CRIT_DAMAGE);
                }
            }

            float angleDifference = Math.abs(MathHelper.subtractAngles(entity.getHeadYaw(), attacker.getYaw()));
            if (angleDifference <= 75) {
                ParticleHandler.sendToAll(entity, attacker, ModParticles.BACKATTACK);
                totalMultiplier += (float) attacker.getAttributeValue(ModEntityAttributes.BACK_ATTACK);
            }

            if (DamageTypeUtil.isMagicDamage(source)) totalMultiplier += (float) attacker.getAttributeValue(ModEntityAttributes.MAGIC_ATTACK_DAMAGE);

            float finalDamage = amount * totalMultiplier;

            DominatusLivingEntityEvents.DAMAGE_DEALT.invoker().onDamageDealt(entity, source, finalDamage);

            return finalDamage;
        }
        return amount;
    }
    @Override
    public void afterDeath(LivingEntity entity, DamageSource damageSource) {
        if (entity instanceof PlayerEntity || entity.getWorld().isClient()) return;

        DamageTrackerComponent tracker = ModEntityComponents.DAMAGETRACKER.getNullable(entity);
        if (tracker == null) return;

        Map<UUID, Float> damageMap = tracker.getAllDamage();
        if (damageMap.isEmpty()) return;

        ServerWorld world = (ServerWorld) entity.getWorld();
        String entityName = entity.getType().getName().getString();

        for (Map.Entry<UUID, Float> entry : damageMap.entrySet()) {
            UUID playerId = entry.getKey();

            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(playerId);
            if (player == null) continue; // Player is offline

            float damagePercentage = tracker.getDamagePercentage(player);
            if (damagePercentage <= 0) continue;


            int expReward = ExpUtil.calculateExpReward(player, entity, damagePercentage);

            if (expReward > 0) {
                float percentage = damagePercentage * 100f;
                String source = String.format("%.1f%% damage to %s", percentage, entityName);

                ExpUtil.awardExperience(player, expReward, source);
            }
        }
        tracker.clearDamage();
    }

    @Override
    public void onLoad(Entity entity, ServerWorld serverWorld) {
        if (!(entity instanceof LivingEntity livingEntity) || livingEntity instanceof PlayerEntity) return;

        DamageTrackerComponent tracker = ModEntityComponents.DAMAGETRACKER.getNullable(livingEntity);
        if (tracker != null) tracker.setMaxHealth(livingEntity.getMaxHealth());
    }
}
