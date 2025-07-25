package sypztep.dominatus.common.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import sypztep.dominatus.client.payload.*;
import sypztep.dominatus.common.payload.AirHikePayloadC2S;
import sypztep.dominatus.common.payload.IncreaseStatPayloadC2S;

public final class ModPayloads {
    public ModPayloads() {
    }

    public static void init() {
        PayloadTypeRegistry.playS2C().register(AddTextParticlesPayloadS2C.ID, AddTextParticlesPayloadS2C.CODEC); // Server to Client
        PayloadTypeRegistry.playS2C().register(AddEmitterParticlePayloadS2C.ID, AddEmitterParticlePayloadS2C.CODEC); // Server to Client
        PayloadTypeRegistry.playS2C().register(SendToastPayloadS2C.ID, SendToastPayloadS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(AddAirhikeParticlesPayloadS2C.ID, AddAirhikeParticlesPayloadS2C.CODEC);

        PayloadTypeRegistry.playC2S().register(IncreaseStatPayloadC2S.ID, IncreaseStatPayloadC2S.CODEC); // Client to Server
        PayloadTypeRegistry.playC2S().register(AirHikePayloadC2S.ID, AirHikePayloadC2S.CODEC); // Client to Server

        registerPayloads();
    }
    private static void registerPayloads() {
        ServerPlayNetworking.registerGlobalReceiver(IncreaseStatPayloadC2S.ID, new IncreaseStatPayloadC2S.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(AirHikePayloadC2S.ID, new AirHikePayloadC2S.Receiver());
    }
}
