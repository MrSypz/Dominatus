package sypztep.dominatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sypztep.dominatus.common.event.critevasionandexp.LivingEntityEvent;
import sypztep.dominatus.common.event.critevasionandexp.PlayerEntityEvent;
import sypztep.dominatus.common.init.*;
import sypztep.dominatus.common.reloadlistener.DominatusMobExpReloadListener;

public class Dominatus implements ModInitializer {
    public static final String MODID = "dominatus";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }

    @Override
    public void onInitialize() {
        ModPayloads.init();

        LivingEntityEvent.register();
        PlayerEntityEvent.register();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DominatusMobExpReloadListener());
    }
}
