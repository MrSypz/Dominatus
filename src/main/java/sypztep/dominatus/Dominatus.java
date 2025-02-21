package sypztep.dominatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sypztep.dominatus.client.payload.AddRefineSoundPayloadS2C;
import sypztep.dominatus.client.payload.AddTextParticlesPayload;
import sypztep.dominatus.client.payload.RefinePayloadS2C;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModItems;
import sypztep.dominatus.common.init.ModScreenHandler;
import sypztep.dominatus.common.payload.RefineButtonPayloadC2S;
import sypztep.dominatus.common.payload.RefinePayloadC2S;
import sypztep.dominatus.common.reloadlistener.DominatusItemReloadListener;

public class Dominatus implements ModInitializer {
    public static final String MODID = "dominatus";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }

    @Override
    public void onInitialize() {
        ModDataComponents.init();
        ModScreenHandler.init();

        ModItems.init();
        PayloadTypeRegistry.playS2C().register(AddTextParticlesPayload.ID, AddTextParticlesPayload.CODEC); // Server to Client
        PayloadTypeRegistry.playS2C().register(AddRefineSoundPayloadS2C.ID, AddRefineSoundPayloadS2C.CODEC); // Server to Client
        PayloadTypeRegistry.playS2C().register(RefinePayloadS2C.ID, RefinePayloadS2C.CODEC); // Server to Client

        PayloadTypeRegistry.playC2S().register(RefinePayloadC2S.ID, RefinePayloadC2S.CODEC); // Client to Server
        PayloadTypeRegistry.playC2S().register(RefineButtonPayloadC2S.ID, RefineButtonPayloadC2S.CODEC); // Client to Server

        ServerPlayNetworking.registerGlobalReceiver(RefinePayloadC2S.ID, new RefinePayloadC2S.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(RefineButtonPayloadC2S.ID, new RefineButtonPayloadC2S.Receiver());

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DominatusItemReloadListener());
    }
}
