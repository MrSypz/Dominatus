package sypztep.dominatus.common.init;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import sypztep.dominatus.common.command.BenefitsCommand;
import sypztep.dominatus.common.command.DebugCommand;
import sypztep.dominatus.common.command.ExpCommand;
import sypztep.dominatus.common.command.LevelCommand;

public final class ModCommands {
    public ModCommands() {}
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("dominatus")
                    .then(LevelCommand.register())
                    .then(ExpCommand.register())
                    .then(BenefitsCommand.register())
                    .then(DebugCommand.register())
            );
        });
    }
}
