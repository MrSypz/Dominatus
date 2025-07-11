package sypztep.dominatus.common.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import sypztep.dominatus.client.payload.AddEmitterParticlePayloadS2C;
import sypztep.dominatus.client.payload.AddTextParticlesPayloadS2C;
import sypztep.dominatus.client.util.TextParticleProvider;

public class ParticleHandler {

    private static void send(Entity target, Entity attacker, ParticleType<?> particle, boolean self, boolean others) {
        if (target == null) return;

        if (self && attacker instanceof ServerPlayerEntity player)
            AddEmitterParticlePayloadS2C.send(player, target.getId(), particle);

        if (others && !attacker.getWorld().isClient())
            PlayerLookup.tracking(attacker).forEach(p -> AddEmitterParticlePayloadS2C.send(p, target.getId(), particle));

    }

    private static void send(Entity target, Entity attacker, TextParticleProvider particle, boolean self, boolean others) {
        if (target == null) return;

        if (self && attacker instanceof ServerPlayerEntity player)
            AddTextParticlesPayloadS2C.send(player, target.getId(), particle);

        if (others && !attacker.getWorld().isClient())
            PlayerLookup.tracking(attacker).forEach(p -> AddTextParticlesPayloadS2C.send(p, target.getId(), particle));

    }

    // ParticleType overloads
    public static void sendToSelf(Entity target, Entity attacker, ParticleType<?> particle) {
        send(target, attacker, particle, true, false);
    }

    public static void sendToOthers(Entity target, Entity attacker, ParticleType<?> particle) {
        send(target, attacker, particle, false, true);
    }

    public static void sendToAll(Entity target, Entity attacker, ParticleType<?> particle) {
        send(target, attacker, particle, true, true);
    }

    // TextParticleProvider overloads

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