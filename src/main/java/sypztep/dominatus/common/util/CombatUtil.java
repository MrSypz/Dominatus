package sypztep.dominatus.common.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import sypztep.dominatus.client.init.ModParticle;
import sypztep.dominatus.client.payload.AddTextParticlesPayload;

public class CombatUtil {
    public static float applyBackAttackModifier(LivingEntity target,float value, DamageSource source) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof LivingEntity livingAttacker) {
            float angleDifference = Math.abs(MathHelper.subtractAngles(target.getHeadYaw(), source.getSource().getHeadYaw()));
            if (angleDifference <= 75) {
                if (livingAttacker instanceof PlayerEntity) {
                    PlayerLookup.tracking((ServerWorld) target.getWorld(), target.getChunkPos()).forEach(foundPlayer ->
                            AddTextParticlesPayload.send(foundPlayer, target.getId(), ModParticle.BACKATTACK)
                    );
                    PlayerLookup.tracking((ServerWorld) livingAttacker.getWorld(), livingAttacker.getChunkPos()).forEach(foundPlayer ->
                            AddTextParticlesPayload.send(foundPlayer, target.getId(), ModParticle.BACKATTACK)
                    );
                }
                return value * 1.5F;
            }
        }
        return value;
    }
}
