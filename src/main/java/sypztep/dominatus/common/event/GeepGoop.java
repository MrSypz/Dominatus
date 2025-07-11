package sypztep.dominatus.common.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import sypztep.dominatus.common.api.entity.DominatusLivingEntityEvents;
import sypztep.dominatus.common.api.entity.DominatusPlayerEntityEvents;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModParticles;
import sypztep.dominatus.common.util.ParticleHandler;

public class GeepGoop implements DominatusLivingEntityEvents.PostArmorDamage, DominatusPlayerEntityEvents.ModifyAttackCondition, DominatusPlayerEntityEvents.ModifyAttackDamage, ServerLivingEntityEvents.AllowDamage {
    @Override
    public boolean allowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            if (!isHitable(entity, source)) return false;

            if (!hitCheck(attacker, entity)) {
                ParticleHandler.sendToAll(entity, attacker, ModParticles.MISSING);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean modifyCondition(PlayerEntity player, boolean isCritical) {
        return roll(player) < getCritChance(player);
    }

    @Override
    public float modifyDamage(PlayerEntity player, float damage) {
        return 1 + (float) player.getAttributeValue(ModEntityAttributes.CRIT_DAMAGE);
    }

    @Override
    public float modifyDamage(LivingEntity entity, DamageSource source, float amount) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            float finalAmount = amount; // Start with base damage

            if (roll(attacker) < getCritChance(attacker) && !(attacker instanceof PlayerEntity)) {
                ParticleHandler.sendToAll(entity, attacker, ModParticles.CRITICAL);
                // this block I mimic ClientPlayerEntity when crit
                ParticleHandler.sendToAll(entity, attacker, ParticleTypes.CRIT);
                playCriticalSound(entity);
                //
                finalAmount *= (1.0f + (float) attacker.getAttributeValue(ModEntityAttributes.CRIT_DAMAGE));
            }

            float angleDifference = Math.abs(MathHelper.subtractAngles(entity.getHeadYaw(), attacker.getYaw()));
            if (angleDifference <= 75) {
                ParticleHandler.sendToAll(entity, attacker, ModParticles.BACKATTACK);
                finalAmount *= (1.0f + (float) attacker.getAttributeValue(ModEntityAttributes.BACK_ATTACK));
            }

            return finalAmount;
        }
        return amount;
    }

    private void playCriticalSound(Entity target) {
        target.getWorld().playSound(null, target.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.HOSTILE, 1, 1);
    }

    private boolean isHitable(LivingEntity entity, DamageSource source) {
        return !entity.isInvulnerable() && !entity.isInvulnerableTo(source) && entity.hurtTime == 0;
    }

    private boolean hitCheck(LivingEntity attacker, LivingEntity defender) {
        int attackerAccuracy = getAccuracy(attacker);
        int defenderEvasion = getEvasion(defender);

        int hitRate = attackerAccuracy - defenderEvasion;

        if (!isPlayer(defender)) hitRate += 25;

        float hitChance = hitRate / 100.0f;
        return roll(attacker) < hitChance;
    }

    public float roll(LivingEntity attacker) {
        return attacker.getRandom().nextFloat();
    }

    private boolean isPlayer(LivingEntity entity) {
        return entity instanceof PlayerEntity;
    }

    public int getAccuracy(LivingEntity entity) {
        return (int) entity.getAttributeValue(ModEntityAttributes.ACCURACY);
    }

    public int getEvasion(LivingEntity entity) {
        return (int) entity.getAttributeValue(ModEntityAttributes.EVASION);
    }

    public float getCritChance(LivingEntity entity) {
        return (float) entity.getAttributeValue(ModEntityAttributes.CRIT_CHANCE);
    }
}