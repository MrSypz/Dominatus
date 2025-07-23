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
import sypztep.dominatus.common.component.living.PhantomWalkerComponent;

public record AddAirhikeParticlesPayloadS2C(int entityId) implements CustomPayload {
    public static final Id<AddAirhikeParticlesPayloadS2C> ID = new Id<>(Dominatus.id("add_airhike_particles"));
    public static final PacketCodec<PacketByteBuf, AddAirhikeParticlesPayloadS2C> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            AddAirhikeParticlesPayloadS2C::entityId,
            AddAirhikeParticlesPayloadS2C::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static void send(ServerPlayerEntity player, int id) {
        ServerPlayNetworking.send(player, new AddAirhikeParticlesPayloadS2C(id));
    }

    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<AddAirhikeParticlesPayloadS2C> {
        @Override
        public void receive(AddAirhikeParticlesPayloadS2C payload, ClientPlayNetworking.Context context) {
            Entity entity = context.player().getWorld().getEntityById(payload.entityId());
            if (entity != null) {
                PhantomWalkerComponent.addAirhikeParticles(entity);
            }
        }
    }
}
