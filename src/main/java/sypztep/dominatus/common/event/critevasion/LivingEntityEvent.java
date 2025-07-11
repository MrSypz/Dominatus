package sypztep.dominatus.common.event.critevasion;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import sypztep.dominatus.common.api.entity.DominatusLivingEntityEvents;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModParticles;
import sypztep.dominatus.common.util.LivingEntityUtil;
import sypztep.dominatus.common.util.ParticleHandler;

public final class LivingEntityEvent implements DominatusLivingEntityEvents.PostArmorDamage,
        ServerLivingEntityEvents.AllowDamage {
    @Override
    public boolean allowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            if (!LivingEntityUtil.isHitable(entity, source)) return false;

            // Only check hit for non-player attackers (players already handled in PlayerEntityEvent.allowAttack)
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
            // Player Are start here
            // Back attack check for ALL attackers
            float angleDifference = Math.abs(MathHelper.subtractAngles(entity.getHeadYaw(), attacker.getYaw()));
            if (angleDifference <= 75) {
                ParticleHandler.sendToAll(entity, attacker, ModParticles.BACKATTACK);
                totalMultiplier += (float) attacker.getAttributeValue(ModEntityAttributes.BACK_ATTACK);
            }

            return amount * totalMultiplier;
        }
        return amount;
    }
}
