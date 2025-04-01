package sypztep.dominatus.common.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import sypztep.dominatus.client.init.ModParticle;
import sypztep.dominatus.client.payload.AddTextParticlesPayload;
import sypztep.dominatus.common.init.ModEntityAttributes;

import java.util.HashSet;
import java.util.Set;

public class CombatUtil {
    public static float damageModifier(LivingEntity target, float amount, DamageSource source) {
        Entity attackerEntity = source.getAttacker();
        if (attackerEntity instanceof LivingEntity attacker) {
            amount = calculatePlayerVersPlayer(attacker, amount);
            amount = calculatePlayerVersEntity(attacker, amount);
            amount = applyBackAttackModifier(target, attacker, amount);
        }
        return amount;
    }
    private static float calculatePlayerVersPlayer(LivingEntity attacker, float value) {
        float pvpAttackMultiplier = (float) attacker.getAttributeValue(ModEntityAttributes.PLAYER_VERS_PLAYER_DAMAGE);
        return Math.max(value * (1 + pvpAttackMultiplier/100), 0);
    }

    private static float calculatePlayerVersEntity(LivingEntity attacker, float value) {
        float pveAttackMultiplier = (float) attacker.getAttributeValue(ModEntityAttributes.PLAYER_VERS_ENTITY_DAMAGE);
        return Math.max(value * (1 + pveAttackMultiplier/100), 0);
    }
    private static float applyBackAttackModifier(LivingEntity target, LivingEntity attacker, float value) {
        float angleDifference = Math.abs(MathHelper.subtractAngles(target.getHeadYaw(), attacker.getYaw()));
        if (angleDifference <= 75) {
            float backAttackMultiplier = (float) attacker.getAttributeValue(ModEntityAttributes.BACK_ATTACK) + 1;
            if (attacker instanceof PlayerEntity || target instanceof PlayerEntity) {
                sendBackAttackParticles(target, attacker);
            }
            return value * backAttackMultiplier;
        }
        return value;
    }

    private static void sendBackAttackParticles(LivingEntity target, LivingEntity attacker) {
        ServerWorld targetWorld = (ServerWorld) target.getWorld();
        ServerWorld attackerWorld = (ServerWorld) attacker.getWorld();

        Set<ServerPlayerEntity> trackingPlayers = new HashSet<>();
        trackingPlayers.addAll(PlayerLookup.tracking(targetWorld, target.getChunkPos()));
        trackingPlayers.addAll(PlayerLookup.tracking(attackerWorld, attacker.getChunkPos()));

        for (ServerPlayerEntity player : trackingPlayers) {
            AddTextParticlesPayload.send(player, target.getId(), ModParticle.BACKATTACK);
        }
    }
}
