package sypztep.dominatus.common.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import sypztep.dominatus.client.payload.*;

public final class ModPayloads {
    public ModPayloads() {
    }

    public static void init() {
        PayloadTypeRegistry.playS2C().register(AddTextParticlesPayloadS2C.ID, AddTextParticlesPayloadS2C.CODEC); // Server to Client
        PayloadTypeRegistry.playS2C().register(AddEmitterParticlePayloadS2C.ID, AddEmitterParticlePayloadS2C.CODEC); // Server to Client
    }
}
