package sypztep.dominatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sypztep.dominatus.common.api.entity.DominatusLivingEntityEvents;
import sypztep.dominatus.common.api.entity.DominatusPlayerEntityEvents;
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
        DominatusLivingEntityEvents.POST_ARMOR_DAMAGE.register(new GeepGoop());
        DominatusPlayerEntityEvents.MODIFY_ATTACK_CONDITION.register(new GeepGoop());
        DominatusPlayerEntityEvents.MODIFY_ATTACK_DAMAGE.register(new GeepGoop());
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(new GeepGoop());

    }
}
