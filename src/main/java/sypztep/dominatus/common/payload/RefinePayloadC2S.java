package sypztep.dominatus.common.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record RefinePayloadC2S() implements CustomPayload {
    public static final Id<RefinePayloadC2S> ID = CustomPayload.id("refined");
    public static final PacketCodec<PacketByteBuf, RefinePayloadC2S> CODEC = PacketCodec.unit(new RefinePayloadC2S());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send() {
        ClientPlayNetworking.send(new RefinePayloadC2S());
    }
    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<RefinePayloadC2S> {
        @Override
        public void receive(RefinePayloadC2S payload, ServerPlayNetworking.Context context) {
            if (context.player().currentScreenHandler instanceof RefineScreenHandler)
                ((RefineScreenHandler) context.player().currentScreenHandler).refine();
        }
    }
}
