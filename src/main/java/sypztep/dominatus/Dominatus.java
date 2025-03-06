package sypztep.dominatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sypztep.dominatus.common.command.RefineSetCommand;
import sypztep.dominatus.common.event.PreventItemUsed;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModItems;
import sypztep.dominatus.common.init.ModPayload;
import sypztep.dominatus.common.init.ModScreenHandler;
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

        CommandRegistrationCallback.EVENT.register(new RefineSetCommand());

        ModItems.init();
        ModPayload.init();
// In your main mod class initialization method
        PreventItemUsed.register();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DominatusItemReloadListener());
    }
}
