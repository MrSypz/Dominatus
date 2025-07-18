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
import net.minecraft.util.math.Vec3d;
import sypztep.dominatus.client.util.TextParticleProvider;
import sypztep.dominatus.common.api.entity.DominatusLivingEntityEvents;
import sypztep.dominatus.common.api.entity.DominatusPlayerEntityEvents;
import sypztep.dominatus.common.component.living.DamageTrackerComponent;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.init.ModParticles;
import sypztep.dominatus.common.util.DamageTypeUtil;
import sypztep.dominatus.common.util.LivingEntityUtil;
import sypztep.dominatus.common.util.ParticleHandler;
import sypztep.dominatus.common.util.level.ExpUtil;

import java.util.Map;
import java.util.UUID;

public final class LivingEntityEvent implements DominatusLivingEntityEvents.PostArmorDamage, ServerLivingEntityEvents.AllowDamage, ServerLivingEntityEvents.AfterDeath, ServerEntityEvents.Load {
    private static final LivingEntityEvent INSTANCE = new LivingEntityEvent();

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(INSTANCE);
        DominatusLivingEntityEvents.POST_ARMOR_DAMAGE.register(INSTANCE);
        ServerEntityEvents.ENTITY_LOAD.register(INSTANCE);
        ServerLivingEntityEvents.AFTER_DEATH.register(INSTANCE);
    }

    @Override
    public boolean allowDamage(LivingEntity target, DamageSource source, float amount) {
        if (!(source.getAttacker() instanceof LivingEntity attacker)) return true;
        if (!LivingEntityUtil.isHitable(target, source)) return false;
        if (LivingEntityUtil.hitCheck(attacker, target)) return true;

        TextParticleProvider missParticle = LivingEntityUtil.isPlayer(attacker) ? ModParticles.MISSING : ModParticles.MISSING_MONSTER;

        ParticleHandler.sendToAll(target, attacker, missParticle);
        return false;
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

            Vec3d entityPos = entity.getPos();
            Vec3d attackerPos = attacker.getPos();
            Vec3d damageVector = attackerPos.subtract(entityPos).normalize();

            float damageDirection = (float) Math.toDegrees(Math.atan2(-damageVector.x, damageVector.z));

            float angleDifference = Math.abs(MathHelper.subtractAngles(entity.getHeadYaw(), damageDirection));

            if (angleDifference >= 75) {
                ParticleHandler.sendToAll(entity, attacker, ModParticles.BACKATTACK);
                totalMultiplier += (float) attacker.getAttributeValue(ModEntityAttributes.BACK_ATTACK);
            }

            if (DamageTypeUtil.isMagicDamage(source))
                totalMultiplier += (float) attacker.getAttributeValue(ModEntityAttributes.MAGIC_ATTACK_DAMAGE);

            float finalDamage = amount * totalMultiplier;

            DominatusPlayerEntityEvents.DAMAGE_DEALT.invoker().onDamageDealt(entity, source, finalDamage);

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

        damageMap.entrySet().parallelStream().forEach(entry -> {
            UUID playerId = entry.getKey();

            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(playerId);

            LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
            if (levelComponent.isMaxLevel()) return;

            float damagePercentage = tracker.getDamagePercentage(player);
            if (damagePercentage <= 0) return;

            int expReward = ExpUtil.calculateExpReward(player, entity, damagePercentage);

            if (expReward > 0) {
                float percentage = damagePercentage * 100f;
                String source = String.format("%.1f%% damage to %s", percentage, entityName);

                ExpUtil.awardExperience(player, expReward, source);
            }
        });
//        for (Map.Entry<UUID, Float> entry : damageMap.entrySet()) {
//
//        }
        tracker.clearDamage();
    }

    @Override
    public void onLoad(Entity entity, ServerWorld serverWorld) {
        if (!(entity instanceof LivingEntity livingEntity) || livingEntity instanceof PlayerEntity) return;

        DamageTrackerComponent tracker = ModEntityComponents.DAMAGETRACKER.getNullable(livingEntity);
        if (tracker != null) tracker.setMaxHealth(livingEntity.getMaxHealth());
    }
}
