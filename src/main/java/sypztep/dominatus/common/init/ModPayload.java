package sypztep.dominatus.common.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import sypztep.dominatus.client.payload.*;
import sypztep.dominatus.common.payload.GemActionPayloadC2S;
import sypztep.dominatus.common.payload.MultiHitPayloadC2S;
import sypztep.dominatus.common.payload.RefineButtonPayloadC2S;
import sypztep.dominatus.common.payload.RefinePayloadC2S;

public final class ModPayload {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(AddTextParticlesPayloadS2C.ID, AddTextParticlesPayloadS2C.CODEC); // Server to Client
        PayloadTypeRegistry.playS2C().register(AddRefineSoundPayloadS2C.ID, AddRefineSoundPayloadS2C.CODEC); // Server to Client
        PayloadTypeRegistry.playS2C().register(RefinePayloadS2C.ID, RefinePayloadS2C.CODEC); // Server to Client
        PayloadTypeRegistry.playS2C().register(GemBreakPayloadS2C.ID, GemBreakPayloadS2C.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncAttackDamagePayloadS2C.ID, SyncAttackDamagePayloadS2C.CODEC);

        PayloadTypeRegistry.playC2S().register(RefinePayloadC2S.ID, RefinePayloadC2S.CODEC); // Client to Server
        PayloadTypeRegistry.playC2S().register(RefineButtonPayloadC2S.ID, RefineButtonPayloadC2S.CODEC); // Client to Server
        PayloadTypeRegistry.playC2S().register(MultiHitPayloadC2S.ID, MultiHitPayloadC2S.CODEC);
        PayloadTypeRegistry.playC2S().register(GemActionPayloadC2S.ID, GemActionPayloadC2S.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(RefinePayloadC2S.ID, new RefinePayloadC2S.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(RefineButtonPayloadC2S.ID, new RefineButtonPayloadC2S.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(MultiHitPayloadC2S.ID, new MultiHitPayloadC2S.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(GemActionPayloadC2S.ID, new GemActionPayloadC2S.Receiver());
    }
}
