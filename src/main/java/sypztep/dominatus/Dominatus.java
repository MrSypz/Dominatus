package sypztep.dominatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sypztep.dominatus.common.command.GemCommand;
import sypztep.dominatus.common.command.RefineSetCommand;
import sypztep.dominatus.common.event.PreventItemUsed;
import sypztep.dominatus.common.init.*;
import sypztep.dominatus.common.reloadlistener.DominatusEntityStatsReloadListener;
import sypztep.dominatus.common.reloadlistener.DominatusItemReloadListener;
import sypztep.dominatus.common.reloadlistener.GemItemDataReloadListener;
import sypztep.dominatus.common.util.combatsystem.MultiHitSystem;

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

        CommandRegistrationCallback.EVENT.register(new RefineSetCommand());
        CommandRegistrationCallback.EVENT.register(new GemCommand());

        ModItems.init();
        ModPayload.init();
        ModLootableModify.init();
        PreventItemUsed.register();

        ServerTickEvents.START_SERVER_TICK.register(MultiHitSystem::tick);

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DominatusItemReloadListener());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DominatusEntityStatsReloadListener());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GemItemDataReloadListener());
    }
}
