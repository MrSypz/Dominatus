package sypztep.dominatus.common.event.critevasion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import sypztep.dominatus.common.api.entity.DominatusPlayerEntityEvents;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModParticles;
import sypztep.dominatus.common.util.LivingEntityUtil;
import sypztep.dominatus.common.util.ParticleHandler;

public final class PlayerEntityEvent implements DominatusPlayerEntityEvents.ModifyAttackDamage,
        DominatusPlayerEntityEvents.ModifyAttackCondition,
        DominatusPlayerEntityEvents.AllowAttack {
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
}
