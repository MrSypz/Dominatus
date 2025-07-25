package sypztep.dominatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sypztep.dominatus.common.event.applylivingstats.MobSpawnStatsEvent;
import sypztep.dominatus.common.event.corecombat.LivingEntityEvent;
import sypztep.dominatus.common.event.corecombat.PlayerEntityEvent;
import sypztep.dominatus.common.init.*;
import sypztep.dominatus.common.reloadlistener.DominatusMobExpReloadListener;
import sypztep.dominatus.common.system.skill.active.ActiveSkillRegistry;
import sypztep.dominatus.common.util.DelayedDamageScheduler;

public class Dominatus implements ModInitializer {
    public static final String MODID = "dominatus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }

    @Override
    public void onInitialize() {
        ModPayloads.init();
        ModCommands.init();
        ModParticles.init();

        LivingEntityEvent.register();
        PlayerEntityEvent.register();
        MobSpawnStatsEvent.register();
        ActiveSkillRegistry.register();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            DelayedDamageScheduler.getInstance().tick();
        });

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DominatusMobExpReloadListener());
    }
}
