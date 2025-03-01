package sypztep.dominatus.common.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import sypztep.dominatus.client.payload.AddRefineSoundPayloadS2C;
import sypztep.dominatus.client.payload.AddTextParticlesPayload;
import sypztep.dominatus.client.payload.RefinePayloadS2C;
import sypztep.dominatus.common.payload.RefineButtonPayloadC2S;
import sypztep.dominatus.common.payload.RefinePayloadC2S;
import sypztep.dominatus.common.payload.ReformButtonPayloadC2S;
import sypztep.dominatus.common.payload.ReformPayloadC2S;

public class ModPayload {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(AddTextParticlesPayload.ID, AddTextParticlesPayload.CODEC); // Server to Client
        PayloadTypeRegistry.playS2C().register(AddRefineSoundPayloadS2C.ID, AddRefineSoundPayloadS2C.CODEC); // Server to Client
        PayloadTypeRegistry.playS2C().register(RefinePayloadS2C.ID, RefinePayloadS2C.CODEC); // Server to Client

        PayloadTypeRegistry.playC2S().register(RefinePayloadC2S.ID, RefinePayloadC2S.CODEC); // Client to Server
        PayloadTypeRegistry.playC2S().register(ReformPayloadC2S.ID, ReformPayloadC2S.CODEC); // Client to Server
        PayloadTypeRegistry.playC2S().register(RefineButtonPayloadC2S.ID, RefineButtonPayloadC2S.CODEC); // Client to Server
        PayloadTypeRegistry.playC2S().register(ReformButtonPayloadC2S.ID, ReformButtonPayloadC2S.CODEC); // Client to Server

        ServerPlayNetworking.registerGlobalReceiver(RefinePayloadC2S.ID, new RefinePayloadC2S.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(RefineButtonPayloadC2S.ID, new RefineButtonPayloadC2S.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(ReformButtonPayloadC2S.ID, new ReformButtonPayloadC2S.Receiver());
    }
}
