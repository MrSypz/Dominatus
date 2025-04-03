package sypztep.dominatus.common.util.combatsystem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import sypztep.dominatus.ModConfig;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MultiHitSystem {
    private static final Map<UUID, MultiHitData> activeHits = new HashMap<>();

    private static class MultiHitData {
        final Entity target;
        final int totalHits;
        int currentHit;
        float nextHitTick;

        MultiHitData(Entity target, int totalHits, float startTick) {
            this.target = target;
            this.totalHits = totalHits;
            this.currentHit = 1;
            this.nextHitTick = startTick;
        }
    }

    public static void scheduleMultiHit(PlayerEntity player, Entity target, int hitCount) {
        if (!player.getWorld().isClient) {
            hitCount = Math.min(hitCount, 2);
            float startTick = player.getWorld().getServer().getTicks() + 2;
            activeHits.put(player.getUuid(), new MultiHitData(target, hitCount, startTick));
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
                float damage = (float) player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 0.5f;
                data.target.damage(player.getDamageSources().playerAttack(player), damage);

                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);

                data.currentHit++;
                data.nextHitTick = currentTick + ModConfig.hitdelay;

                if (data.currentHit >= data.totalHits) iterator.remove();
            }
        }
    }
}