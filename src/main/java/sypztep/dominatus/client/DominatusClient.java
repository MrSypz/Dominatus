package sypztep.dominatus.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.client.input.ModKeyBindings;
import sypztep.dominatus.client.input.SkillKeyBindings;
import sypztep.dominatus.client.particle.ShockwaveParticle;
import sypztep.dominatus.client.payload.*;
import sypztep.dominatus.client.screen.ResourceHudRenderer;
import sypztep.dominatus.client.screen.SkillHotbarHudRenderer;
import sypztep.dominatus.client.toast.ToastHudRenderer;
import sypztep.dominatus.common.init.ModParticles;

public class DominatusClient implements ClientModInitializer {
    public static ModConfig config = new ModConfig();

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        ResourceHudRenderer.register();

        ClientPlayNetworking.registerGlobalReceiver(AddTextParticlesPayloadS2C.ID, new AddTextParticlesPayloadS2C.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(AddEmitterParticlePayloadS2C.ID, new AddEmitterParticlePayloadS2C.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(SendToastPayloadS2C.ID, new SendToastPayloadS2C.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(AddAirhikeParticlesPayloadS2C.ID, new AddAirhikeParticlesPayloadS2C.Receiver());

        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(ModParticles.AIRHIKE, ShockwaveParticle.Factory::new);

        ModKeyBindings.register();

        LevelHudRenderer.register();
        ToastHudRenderer.register();
        SkillKeyBindings.register();
        SkillHotbarHudRenderer.register();
    }
}
