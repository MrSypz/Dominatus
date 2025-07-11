package sypztep.dominatus.client.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import sypztep.dominatus.Dominatus;

public record AddEmitterParticlePayloadS2C(int entityId, ParticleType<?> particleType) implements CustomPayload {
    public static final Id<AddEmitterParticlePayloadS2C> ID = new Id<>(Dominatus.id("add_emitter_particle"));
    public static final PacketCodec<RegistryByteBuf, AddEmitterParticlePayloadS2C> CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT,
            AddEmitterParticlePayloadS2C::entityId,
            PacketCodecs.registryCodec(Registries.PARTICLE_TYPE.getCodec()),
            AddEmitterParticlePayloadS2C::particleType,
            AddEmitterParticlePayloadS2C::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send(ServerPlayerEntity receiver, int entityId, ParticleType<?> particleType) {
        ServerPlayNetworking.send(receiver, new AddEmitterParticlePayloadS2C(entityId, particleType));
    }

    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<AddEmitterParticlePayloadS2C> {
        @Override
        public void receive(AddEmitterParticlePayloadS2C payload, ClientPlayNetworking.Context context) {
            Entity entity = context.player().getWorld().getEntityById(payload.entityId());
            if (entity != null) context.client().particleManager.addEmitter(entity, (ParticleEffect) payload.particleType);
        }
    }
}