package sypztep.dominatus.client.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import sypztep.dominatus.client.screen.RefineScreen;

public record RefinePayloadS2C(boolean flag) implements CustomPayload {
    public static final Id<RefinePayloadS2C> ID = CustomPayload.id("can_refine");
    public static final PacketCodec<PacketByteBuf, RefinePayloadS2C> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL,
            RefinePayloadS2C::flag,
            RefinePayloadS2C::new
    );

    public static void send(ServerPlayerEntity player, boolean flag) {
        ServerPlayNetworking.send(player, new RefinePayloadS2C(flag));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<RefinePayloadS2C> {
        @Override
        public void receive(RefinePayloadS2C payload, ClientPlayNetworking.Context context) {
            if (context.client().currentScreen instanceof RefineScreen)
                ((RefineScreen) context.client().currentScreen).refineButton.setDisabled(payload.flag());
        }
    }
}
