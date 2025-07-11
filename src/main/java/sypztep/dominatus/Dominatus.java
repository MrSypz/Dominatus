package sypztep.dominatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sypztep.dominatus.common.api.entity.DominatusLivingEntityEvents;
import sypztep.dominatus.common.api.entity.DominatusPlayerEntityEvents;
import sypztep.dominatus.common.event.critevasion.LivingEntityEvent;
import sypztep.dominatus.common.event.critevasion.PlayerEntityEvent;
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

        ServerLivingEntityEvents.ALLOW_DAMAGE.register(new LivingEntityEvent());
        DominatusLivingEntityEvents.POST_ARMOR_DAMAGE.register(new LivingEntityEvent());
        DominatusPlayerEntityEvents.MODIFY_ATTACK_CONDITION.register(new PlayerEntityEvent());
        DominatusPlayerEntityEvents.MODIFY_ATTACK_DAMAGE.register(new PlayerEntityEvent());
        DominatusPlayerEntityEvents.ALLOW_ATTACK.register(new PlayerEntityEvent());
    }
}
