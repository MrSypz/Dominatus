package sypztep.dominatus.common.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;
import sypztep.dominatus.common.api.entity.ServerLivingEntityEvents;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModParticles;
import sypztep.dominatus.common.util.ParticleHandler;

public class GeepGoop implements ServerLivingEntityEvents.PostArmorDamage {
    @Override
    public float modifyDamage(LivingEntity entity, DamageSource source, float amount) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            float angleDifference = Math.abs(MathHelper.subtractAngles(entity.getHeadYaw(), attacker.getYaw()));
            if (angleDifference <= 75) {
                ParticleHandler.sendToAll(entity, attacker, ModParticles.BACKATTACK);
                return amount + (amount * (float) attacker.getAttributeValue(ModEntityAttributes.BACK_ATTACK));
            }
        }
        return amount;
    }
}
