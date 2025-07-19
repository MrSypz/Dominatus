package sypztep.dominatus.common.init;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import sypztep.dominatus.common.command.*;

public final class ModCommands {
    public ModCommands() {}
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("dominatus")
                    .then(LevelCommand.register())
                    .then(ExpCommand.register())
                    .then(BenefitsCommand.register())
                    .then(DebugCommand.register())
                    .then(MobStatsCommand.register())
                    .then(MobDynamicScalingCommand.register())
                    .then(StatsPlayerCommand.register())
            );
        });
    }
}
