package sypztep.dominatus.common.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import sypztep.dominatus.common.screen.ReformScreenHandler;

public record ReformPayloadC2S() implements CustomPayload {
    public static final Id<ReformPayloadC2S> ID = CustomPayload.id("reformed");
    public static final PacketCodec<PacketByteBuf, ReformPayloadC2S> CODEC = PacketCodec.unit(new ReformPayloadC2S());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send() {
        ClientPlayNetworking.send(new ReformPayloadC2S());
    }
    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<ReformPayloadC2S> {
        @Override
        public void receive(ReformPayloadC2S payload, ServerPlayNetworking.Context context) {
//            if (context.player().currentScreenHandler instanceof ReformScreenHandler)
//                ((ReformScreenHandler) context.player().currentScreenHandler).reform();
        }
    }
}
