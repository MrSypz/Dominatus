package sypztep.dominatus.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.LevelData;

import java.util.Collection;

public class DebugCommand {

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("debug")
                .requires(source -> source.hasPermissionLevel(3))
                // /dominatus debug info [player]
                .then(CommandManager.literal("info")
                        .executes(DebugCommand::debugSelf)
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                .executes(DebugCommand::debugPlayer)
                        )
                );
    }

    private static int debugSelf(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return showDebugInfo(context.getSource(), player);
    }

    private static int debugPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");

        for (ServerPlayerEntity player : players) {
            showDebugInfo(context.getSource(), player);
        }

        return players.size();
    }

    private static int showDebugInfo(ServerCommandSource source, ServerPlayerEntity player) {
        LivingLevelComponent component = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = component.getLevelData();

        Text message = Text.literal(String.format(
                "§e=== DEBUG INFO: %s ===\n" +
                        "§7Type: §f%s\n" +
                        "§7Is Player: §f%s\n" +
                        "§7Level: §f%d§7/§f%d\n" +
                        "§7Experience: §f%d\n" +
                        "§7XP to Next: §f%d\n" +
                        "§7XP Percentage: §f%.2f%%\n" +
                        "§7Is Max Level: §f%s\n" +
                        "§7Benefits: §f%d\n" +
                        "§7Component Class: §f%s\n" +
                        "§7LevelData Class: §f%s",
                player.getName().getString(),
                levelData.isPlayer() ? "Player" : "Entity",
                levelData.isPlayer(),
                levelData.getLevel(),
                levelData.getMaxLevel(),
                levelData.getExperience(),
                levelData.getExperienceToNextLevel(),
                levelData.getExperiencePercentage(),
                levelData.isMaxLevel(),
                levelData.getAvailableBenefits(),
                component.getClass().getSimpleName(),
                levelData.getClass().getSimpleName()
        ));

        source.sendFeedback(() -> message, false);
        return 1;
    }
}