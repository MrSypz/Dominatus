package sypztep.dominatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sypztep.dominatus.common.api.entity.DominatusLivingEntityEvents;
import sypztep.dominatus.common.api.entity.DominatusPlayerEntityEvents;
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

        ServerLivingEntityEvents.ALLOW_DAMAGE.register(new LivingEntityEvent());
        DominatusLivingEntityEvents.POST_ARMOR_DAMAGE.register(new LivingEntityEvent());
        DominatusPlayerEntityEvents.MODIFY_ATTACK_CONDITION.register(new PlayerEntityEvent());
        DominatusPlayerEntityEvents.MODIFY_ATTACK_DAMAGE.register(new PlayerEntityEvent());
        DominatusPlayerEntityEvents.ALLOW_ATTACK.register(new PlayerEntityEvent());
        DominatusLivingEntityEvents.DAMAGE_DEALT.register(new PlayerEntityEvent());
        ServerEntityEvents.ENTITY_LOAD.register(new LivingEntityEvent());
        ServerLivingEntityEvents.AFTER_DEATH.register(new LivingEntityEvent());

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DominatusMobExpReloadListener());
    }
}
