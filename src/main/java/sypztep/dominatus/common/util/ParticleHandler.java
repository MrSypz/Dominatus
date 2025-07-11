package sypztep.dominatus.common.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import sypztep.dominatus.client.payload.AddTextParticlesPayloadS2C;
import sypztep.dominatus.client.util.TextParticleProvider;

public class ParticleHandler {
    public ParticleHandler() {
    }

    private static void send(Entity target, Entity attacker, TextParticleProvider particle, boolean self, boolean others) {
        if (target == null) return;
        ///  player attack target send to player it self
        if (self && attacker instanceof ServerPlayerEntity player) AddTextParticlesPayloadS2C.send(player, target.getId(), particle);
        /// Attacker Attack player send to player
        if (others && !attacker.getWorld().isClient()) PlayerLookup.tracking(attacker).forEach(p ->
                AddTextParticlesPayloadS2C.send(p, target.getId(), particle));
    }
    /**
     * Send only to the attacker
     */
    public static void sendToSelf(Entity target, Entity attacker, TextParticleProvider particle) {
        send(target, attacker, particle, true, false);
    }

    /**
     * Send only to other players, not the attacker
     */
    public static void sendToOthers(Entity target, Entity attacker, TextParticleProvider particle) {
        send(target, attacker, particle, false, true);
    }

    /**
     * Send to everyone including the attacker
     */
    public static void sendToAll(Entity target, Entity attacker, TextParticleProvider particle) {
        send(target, attacker, particle, true, true);
    }
}