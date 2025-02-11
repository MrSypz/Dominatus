package sypztep.dominatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sypztep.dominatus.client.payload.AddTextParticlesPayload;
import sypztep.dominatus.common.init.ModDataComponents;

public class Dominatus implements ModInitializer {
    public static final String MODID = "dominatus";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }

    @Override
    public void onInitialize() {
        ModDataComponents.init();

        PayloadTypeRegistry.playS2C().register(AddTextParticlesPayload.ID, AddTextParticlesPayload.CODEC); // Server to Client

    }
}
