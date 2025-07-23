package sypztep.dominatus.common.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.payload.AddAirhikeParticlesPayloadS2C;
import sypztep.dominatus.common.component.living.PhantomWalkerComponent;
import sypztep.dominatus.common.init.ModEntityComponents;

public record AirHikePayloadC2S() implements CustomPayload {
    public static final Id<AirHikePayloadC2S> ID =  new Id<>(Dominatus.id("airhike"));
    public static final PacketCodec<PacketByteBuf, AirHikePayloadC2S> CODEC = PacketCodec.unit(new AirHikePayloadC2S());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send() {
        ClientPlayNetworking.send(new AirHikePayloadC2S());
    }

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<AirHikePayloadC2S> {
        @Override
        public void receive(AirHikePayloadC2S payload, ServerPlayNetworking.Context context) { // handle on server side
            PhantomWalkerComponent airComponent = ModEntityComponents.PHANTOMWALKER.get(context.player());
            if (airComponent.canUseAirJump()) {
                airComponent.performAirJump();
                PlayerLookup.tracking(context.player()).forEach(foundPlayer -> AddAirhikeParticlesPayloadS2C.send(foundPlayer, context.player().getId()));
            }
        }
    }
}