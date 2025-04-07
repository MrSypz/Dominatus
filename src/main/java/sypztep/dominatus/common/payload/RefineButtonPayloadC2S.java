package sypztep.dominatus.common.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import sypztep.dominatus.common.screen.RefineScreenHandler;

public record RefineButtonPayloadC2S() implements CustomPayload {
    public static final Id<RefineButtonPayloadC2S> ID = CustomPayload.id("refine_button");
    public static final PacketCodec<PacketByteBuf, RefineButtonPayloadC2S> CODEC = PacketCodec.unit(new RefineButtonPayloadC2S());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send() {
        ClientPlayNetworking.send(new RefineButtonPayloadC2S());
    }
    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<RefineButtonPayloadC2S> {
        @Override
        public void receive(RefineButtonPayloadC2S payload, ServerPlayNetworking.Context context) {
            if (context.player() != null) {
                ServerPlayerEntity player = context.player();
                player.openHandledScreen(createScreenHandlerFactory(player.getWorld(), player.getBlockPos()));
            }
        }
    }
    private static @NotNull NamedScreenHandlerFactory createScreenHandlerFactory(World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new RefineScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), Text.of("refiner"));
    }
}
