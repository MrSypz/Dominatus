package sypztep.dominatus.common.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import sypztep.dominatus.common.util.combatsystem.MultiHitSystem;

public record MultiHitPayloadC2S(int entityId,int hitcount) implements CustomPayload {
    public static final Id<MultiHitPayloadC2S> ID = CustomPayload.id("multihit");
    public static final PacketCodec<PacketByteBuf, MultiHitPayloadC2S> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            MultiHitPayloadC2S::entityId,
            PacketCodecs.VAR_INT,
            MultiHitPayloadC2S::hitcount,
            MultiHitPayloadC2S::new
    );
    public static void send(PlayerEntity player, Entity target, int hitCount) {
        if (player.getWorld().isClient) {
            ClientPlayNetworking.send(new MultiHitPayloadC2S(target.getId(),hitCount));
        }
    }
    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<MultiHitPayloadC2S> {
        @Override
        public void receive(MultiHitPayloadC2S payload, ServerPlayNetworking.Context context) {
            Entity target = context.player().getWorld().getEntityById(payload.entityId());
            if (target != null && payload.hitcount <= 2) {
                MultiHitSystem.scheduleMultiHit(context.player(), target, payload.hitcount);
            }
        }
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
