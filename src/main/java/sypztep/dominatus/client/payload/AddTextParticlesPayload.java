package sypztep.dominatus.client.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.util.TextParticleProvider;

public record AddTextParticlesPayload(int entityId,int selector) implements CustomPayload {
    public static final Id<AddTextParticlesPayload> ID = new Id<>(Dominatus.id("add_text_particle"));
    public static final PacketCodec<PacketByteBuf, AddTextParticlesPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            AddTextParticlesPayload::entityId,
            PacketCodecs.UNSIGNED_SHORT,
            AddTextParticlesPayload::selector,
            AddTextParticlesPayload::new
    );

    public static void send(ServerPlayerEntity player, int entityId, TextParticleProvider selector) {
        ServerPlayNetworking.send(player, new AddTextParticlesPayload(entityId, selector.getFlag()));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<AddTextParticlesPayload> {
        @Override
        public void receive(AddTextParticlesPayload payload, ClientPlayNetworking.Context context) {
            Entity entity = context.player().getWorld().getEntityById(payload.entityId());
            if (entity != null) {
                TextParticleProvider.handleParticle(entity, payload.selector());
            }
        }
    }
}

