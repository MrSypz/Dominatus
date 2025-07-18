package sypztep.dominatus.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.client.input.ModKeyBindings;
import sypztep.dominatus.client.payload.*;
import sypztep.dominatus.client.toast.ToastHudRenderer;

public class DominatusClient implements ClientModInitializer {
    public static ModConfig config = new ModConfig();

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        ClientPlayNetworking.registerGlobalReceiver(AddTextParticlesPayloadS2C.ID, new AddTextParticlesPayloadS2C.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(AddEmitterParticlePayloadS2C.ID, new AddEmitterParticlePayloadS2C.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(SendToastPayloadS2C.ID, new SendToastPayloadS2C.Receiver());

        ModKeyBindings.register();

        LevelHudRenderer.register();
        ToastHudRenderer.register();
    }
}
