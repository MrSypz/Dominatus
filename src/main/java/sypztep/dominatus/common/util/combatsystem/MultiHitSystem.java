package sypztep.dominatus.common.util.combatsystem;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.client.init.ModParticle;
import sypztep.dominatus.client.payload.AddTextParticlesPayloadS2C;
import sypztep.dominatus.common.api.combat.CriticalOverhaul;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class MultiHitSystem {
    private static final Map<UUID, MultiHitData> activeHits = new HashMap<>();

    private static class MultiHitData {
        final Entity target;
        final int totalHits;
        int currentHit;
        float nextHitTick;
        float baseAmount; // Store the base damage amount

        MultiHitData(Entity target, int totalHits, float startTick, float baseAmount) {
            this.target = target;
            this.totalHits = totalHits;
            this.currentHit = 1;
            this.nextHitTick = startTick;
            this.baseAmount = baseAmount;
        }
    }

    public static void scheduleMultiHit(PlayerEntity player, Entity target, int hitCount) {
        if (!player.getWorld().isClient) {
            if (!(player.getMainHandStack().getItem() instanceof ToolItem)) return; // Exit if not holding a tool

            hitCount = Math.min(hitCount, 2);
            float startTick = player.getWorld().getServer().getTicks() + 2;
            float baseAmount = (float) player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 0.5f;
            activeHits.put(player.getUuid(), new MultiHitData(target, hitCount, startTick, baseAmount));
        }
    }

    public static void tick(MinecraftServer server) {
        long currentTick = server.getTicks();
        Iterator<Map.Entry<UUID, MultiHitData>> iterator = activeHits.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, MultiHitData> entry = iterator.next();
            MultiHitData data = entry.getValue();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());

            if (player == null || !data.target.isAlive() || data.currentHit >= data.totalHits) {
                iterator.remove();
                continue;
            }

            if (currentTick >= data.nextHitTick) {
                applyMultiHitDamage(player, data);

                data.currentHit++;
                data.nextHitTick = currentTick + ModConfig.hitDelay;

                if (data.currentHit >= data.totalHits) {
                    iterator.remove();
                }
            }
        }
    }

    private static void applyMultiHitDamage(ServerPlayerEntity player, MultiHitData data) {
        if (player instanceof CriticalOverhaul criticalAttacker && data.target instanceof LivingEntity) {
            // Calculate crit damage
            float critDamage = criticalAttacker.calCritDamage(data.baseAmount);
            boolean isCrit = critDamage > data.baseAmount;

            // Apply damage with potential crit
            if (isCrit) {
                criticalAttacker.setCritical(true);
                if (data.target.damage(player.getDamageSources().playerAttack(player), critDamage)) {
                    // Play crit effects
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);

                    if (data.target instanceof LivingEntity livingTarget) {
                        PlayerLookup.tracking((ServerWorld) livingTarget.getWorld(), livingTarget.getChunkPos()).forEach(foundPlayer -> AddTextParticlesPayloadS2C.send(foundPlayer, livingTarget.getId(), ModParticle.CRITICAL));
                    }
                }
            } else {
                // Normal hit without crit
                if (data.target.damage(player.getDamageSources().playerAttack(player), data.baseAmount)) {
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
                }
            }

            // Reset crit state
            criticalAttacker.setCritical(false);
        } else {
            // Fallback for non-critical entities
            if (data.target.damage(player.getDamageSources().playerAttack(player), data.baseAmount)) {
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
            }
        }
    }
}