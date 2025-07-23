package sypztep.dominatus.common.event.corecombat;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.common.api.entity.DominatusPlayerEntityEvents;
import sypztep.dominatus.common.component.living.DamageTrackerComponent;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.init.ModCustomParticles;
import sypztep.dominatus.common.util.LivingEntityUtil;
import sypztep.dominatus.common.util.ParticleHandler;

// NOTE : modifier damage are in LivingEntityPart in mixin is ApplyDamage method
public final class PlayerEntityEvent implements DominatusPlayerEntityEvents.ModifyAttackDamage,
        DominatusPlayerEntityEvents.ModifyAttackCondition,
        DominatusPlayerEntityEvents.AllowAttack,
        DominatusPlayerEntityEvents.DamageDealt,
        ServerLivingEntityEvents.AfterDeath,
        ServerPlayerEvents.AfterRespawn {
    private static final PlayerEntityEvent INSTANCE = new PlayerEntityEvent();

    public static void register() {
        DominatusPlayerEntityEvents.MODIFY_ATTACK_CONDITION.register(INSTANCE);
        DominatusPlayerEntityEvents.MODIFY_ATTACK_DAMAGE.register(INSTANCE);
        DominatusPlayerEntityEvents.ALLOW_ATTACK.register(INSTANCE);
        DominatusPlayerEntityEvents.DAMAGE_DEALT.register(INSTANCE);
        ServerLivingEntityEvents.AFTER_DEATH.register(INSTANCE);
        ServerPlayerEvents.AFTER_RESPAWN.register(INSTANCE);
    }

    @Override
    public boolean allowAttack(PlayerEntity player, Entity target) {
        if (!(target instanceof LivingEntity livingTarget)) return true;

        DamageSource damageSource = target.getDamageSources().playerAttack(player);

        if (!LivingEntityUtil.isHitable(livingTarget, damageSource)) return false;

        if (!LivingEntityUtil.hitCheck(player, livingTarget)) {
            ParticleHandler.sendToAll(target, player, ModCustomParticles.MISSING);
            target.getWorld().playSound(null, target.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, target.getSoundCategory());
            return false;
        }
        return true;
    }

    @Override
    public boolean modifyCondition(PlayerEntity player, Entity target, boolean vanillaCrit) {
        boolean isCrit = LivingEntityUtil.critCheck(player);
        if (isCrit) ParticleHandler.sendToAll(target, player, ModCustomParticles.CRITICAL);
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
        if (!(entity instanceof ServerPlayerEntity player) || entity.getWorld().isClient()) return;


        if (!ModConfig.enableDeathPenalty) return;

        if (!LivingEntityUtil.isKilledByMonster(damageSource)) return;


        LivingEntityUtil.applyDeathPenalty(player, damageSource);
    }

    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(newPlayer);

        levelComponent.handleRespawn();

        newPlayer.setHealth(newPlayer.getMaxHealth());
    }
}
