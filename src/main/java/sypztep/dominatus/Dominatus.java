package sypztep.dominatus;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sypztep.dominatus.common.api.entity.ServerLivingEntityEvents;
import sypztep.dominatus.common.event.GeepGoop;
import sypztep.dominatus.common.init.*;

public class Dominatus implements ModInitializer {
    public static final String MODID = "dominatus";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }

    @Override
    public void onInitialize() {
        ModPayloads.init();
        ServerLivingEntityEvents.POST_ARMOR_DAMAGE.register(new GeepGoop());
    }
}
