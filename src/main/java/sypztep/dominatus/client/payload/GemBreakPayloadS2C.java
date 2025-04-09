package sypztep.dominatus.client.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;

public record GemBreakPayloadS2C(List<Text> brokenGemMessages) implements CustomPayload {
    public static final CustomPayload.Id<GemBreakPayloadS2C> ID = CustomPayload.id("gem_break");

    public static final PacketCodec<RegistryByteBuf, GemBreakPayloadS2C> CODEC = PacketCodec.tuple(
            TextCodecs.PACKET_CODEC.collect(PacketCodecs.toList()), GemBreakPayloadS2C::brokenGemMessages,
            GemBreakPayloadS2C::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send(ServerPlayerEntity player, List<Text> breakMessages) {
        ServerPlayNetworking.send(player, new GemBreakPayloadS2C(breakMessages));
    }

    // Client-side receiver to store the broken gem messages
    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<GemBreakPayloadS2C> {
        private static List<Text> lastBrokenGemMessages = null;

        @Override
        public void receive(GemBreakPayloadS2C payload, ClientPlayNetworking.Context context) {
            lastBrokenGemMessages = payload.brokenGemMessages();
        }

        public static List<Text> getLastBrokenGemMessages() {
            return lastBrokenGemMessages;
        }

        public static void clearLastBrokenGemMessages() {
            lastBrokenGemMessages = null;
        }
    }
}