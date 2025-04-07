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

public record AddTextParticlesPayloadS2C(int entityId, int selector) implements CustomPayload {
    public static final Id<AddTextParticlesPayloadS2C> ID = new Id<>(Dominatus.id("add_text_particle"));
    public static final PacketCodec<PacketByteBuf, AddTextParticlesPayloadS2C> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            AddTextParticlesPayloadS2C::entityId,
            PacketCodecs.UNSIGNED_SHORT,
            AddTextParticlesPayloadS2C::selector,
            AddTextParticlesPayloadS2C::new
    );

    public static void send(ServerPlayerEntity player, int entityId, TextParticleProvider selector) {
        ServerPlayNetworking.send(player, new AddTextParticlesPayloadS2C(entityId, selector.getFlag()));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<AddTextParticlesPayloadS2C> {
        @Override
        public void receive(AddTextParticlesPayloadS2C payload, ClientPlayNetworking.Context context) {
            Entity entity = context.player().getWorld().getEntityById(payload.entityId());
            if (entity != null) {
                TextParticleProvider.handleParticle(entity, payload.selector());
            }
        }
    }
}

