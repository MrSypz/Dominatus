package sypztep.dominatus.common.util.combatsystem;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.init.ModParticle;
import sypztep.dominatus.client.payload.AddTextParticlesPayloadS2C;
import sypztep.dominatus.client.util.TextParticleProvider;
import sypztep.dominatus.common.init.ModEntityAttributes;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CombatUtil {
    private static final boolean DEBUG = false;

    private static void debugLog(String message, Object... args) {
        if (DEBUG) {
            Dominatus.LOGGER.info("[CombatUtil]{} {}%n", message, args);
        }
    }

    @FunctionalInterface
    private interface DamageModifier {
        float modify(LivingEntity attacker, LivingEntity target, float damage);
    }

    private enum CombatModifierType {
        PVP(CombatUtil::calculatePlayerVersPlayer),
        PVE(CombatUtil::calculatePlayerVersEntity),
        BACK_ATTACK((attacker, target, damage) -> {
            float angleDifference = Math.abs(MathHelper.subtractAngles(target.getHeadYaw(), attacker.getYaw()));
            if (angleDifference <= 75) {
                sendCombatParticles(target, attacker, ModParticle.BACKATTACK);
                return damage * ((float) attacker.getAttributeValue(ModEntityAttributes.BACK_ATTACK) + 1);
            }
            return damage;
        }),
        AIR_ATTACK((attacker, target, damage) -> {
            if (isAirBorne(target)) {
                sendCombatParticles(target, attacker, ModParticle.AIRATTACK);
                return damage * ((float) attacker.getAttributeValue(ModEntityAttributes.AIR_ATTACK) + 1);
            }
            return damage;
        });


        private final DamageModifier modifier;

        CombatModifierType(DamageModifier modifier) {
            this.modifier = modifier;
        }

        float apply(LivingEntity attacker, LivingEntity target, float damage) {
            return modifier.modify(attacker, target, damage);
        }
    }
    public static boolean isAirBorne(LivingEntity target) {
        return !target.isOnGround()
                && !target.isTouchingWater()
                && !target.isInLava()
                && !target.hasNoGravity()
                && !target.isClimbing()
                && target.fallDistance > 0;
    }

    public static float damageModifier(LivingEntity target, float amount, DamageSource source) {
        if (!(source.getAttacker() instanceof LivingEntity attacker)) {
            return amount;
        }

        return Arrays.stream(CombatModifierType.values())
                .reduce(amount,
                        (damage, modifier) -> modifier.apply(attacker, target, damage),
                        Float::sum);
    }

    private static float calculatePlayerVersPlayer(LivingEntity attacker, LivingEntity target, float damage) {
        float multiplier = (float) attacker.getAttributeValue(ModEntityAttributes.PLAYER_VERS_PLAYER_DAMAGE);
        return Math.max(damage * (1 + multiplier * 0.01f), 0);
    }

    private static float calculatePlayerVersEntity(LivingEntity attacker, LivingEntity target, float damage) {
        float multiplier = (float) attacker.getAttributeValue(ModEntityAttributes.PLAYER_VERS_ENTITY_DAMAGE);
        return Math.max(damage * (1 + multiplier * 0.01f), 0);
    }

    private static void sendCombatParticles(LivingEntity target, LivingEntity attacker, TextParticleProvider particleText) {
        if (!(attacker instanceof PlayerEntity || target instanceof PlayerEntity)) return;

        Set<ServerPlayerEntity> trackingPlayers = getTrackingPlayers(target, attacker);

        trackingPlayers.forEach(player ->
                AddTextParticlesPayloadS2C.send(player, target.getId(), particleText)
        );
    }

    private static Set<ServerPlayerEntity> getTrackingPlayers(LivingEntity... entities) {
        return Arrays.stream(entities)
                .filter(entity -> entity.getWorld() instanceof ServerWorld)
                .flatMap(entity -> PlayerLookup.tracking((ServerWorld) entity.getWorld(),
                        entity.getChunkPos()).stream())
                .collect(Collectors.toSet());
    }
}
