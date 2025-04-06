package sypztep.dominatus.common.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import sypztep.dominatus.common.screen.GemScreenHandler;

public record GemInvButtonPayloadC2S() implements CustomPayload {
    public static final Id<GemInvButtonPayloadC2S> ID = CustomPayload.id("geminv");
    public static final PacketCodec<PacketByteBuf, GemInvButtonPayloadC2S> CODEC = PacketCodec.unit(new GemInvButtonPayloadC2S());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send() {
        ClientPlayNetworking.send(new GemInvButtonPayloadC2S());
    }
    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<GemInvButtonPayloadC2S> {
        @Override
        public void receive(GemInvButtonPayloadC2S payload, ServerPlayNetworking.Context context) {
            if (context.player() != null) {
                ServerPlayerEntity player = context.player();
                player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inventory, obj) -> new GemScreenHandler(syncId, inventory), Text.of("geminv")));
            }
        }
    }
}
