package sypztep.dominatus.client.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import sypztep.dominatus.common.util.gemsystem.GemManagerHelper;

public record SyncAttackDamagePayloadS2C() implements CustomPayload {
    public static final Id<SyncAttackDamagePayloadS2C> ID = CustomPayload.id("sync_damage");
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final PacketCodec<PacketByteBuf, SyncAttackDamagePayloadS2C> CODEC = PacketCodec.unit(new SyncAttackDamagePayloadS2C());

    public static void send(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new SyncAttackDamagePayloadS2C());
    }

    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<SyncAttackDamagePayloadS2C> {
        @Override
        public void receive(SyncAttackDamagePayloadS2C payload, ClientPlayNetworking.Context context) {
            if (context.player() != null) {
                GemManagerHelper.updateEntityStats(context.player());
            }
        }
    }
}
