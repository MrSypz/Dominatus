package sypztep.dominatus.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.LevelData;
import sypztep.dominatus.common.util.NumberUtil;

import java.util.Collection;

public class LevelCommand {

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("level")
                // /dominatus level get [player]
                .then(CommandManager.literal("get")
                        .executes(LevelCommand::getSelfLevel)
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(LevelCommand::getPlayerLevel)
                        )
                )
                // /dominatus level set <level> [player]
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(1))
                                .executes(LevelCommand::setSelfLevel)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .executes(LevelCommand::setPlayerLevel)
                                )
                        )
                )
                // /dominatus level reset [player]
                .then(CommandManager.literal("reset")
                        .executes(LevelCommand::resetSelfLevel)
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(LevelCommand::resetPlayerLevel)
                        )
                )
                // /dominatus level max [player]
                .then(CommandManager.literal("max")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(LevelCommand::maxSelfLevel)
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                .executes(LevelCommand::maxPlayerLevel)
                        )
                );
    }

    private static int getSelfLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return showLevelInfo(context.getSource(), player);
    }

    private static int getPlayerLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");

        for (ServerPlayerEntity player : players) {
            showLevelInfo(context.getSource(), player);
        }

        return players.size();
    }

    private static int setSelfLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int level = IntegerArgumentType.getInteger(context, "level");
        return setLevel(context.getSource(), player, level);
    }

    private static int setPlayerLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        int level = IntegerArgumentType.getInteger(context, "level");

        for (ServerPlayerEntity player : players) {
            setLevel(context.getSource(), player, level);
        }

        return players.size();
    }

    private static int resetSelfLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return resetLevel(context.getSource(), player);
    }

    private static int resetPlayerLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");

        for (ServerPlayerEntity player : players) {
            resetLevel(context.getSource(), player);
        }

        return players.size();
    }

    private static int maxSelfLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return maxLevel(context.getSource(), player);
    }

    private static int maxPlayerLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");

        for (ServerPlayerEntity player : players) {
            maxLevel(context.getSource(), player);
        }

        return players.size();
    }

    // Helper methods
    private static int showLevelInfo(ServerCommandSource source, ServerPlayerEntity player) {
        LivingLevelComponent component = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = component.getLevelData();

        Text message = Text.literal(String.format(
                "§6%s's Level Info:\n" +
                        "§7Level: §f%d§7/§f%d\n" +
                        "§7Experience: §f%s§7/§f%s §8(§f%.1f%%§8)\n" +
                        "§7Benefits: §f%d",
                player.getName().getString(),
                levelData.getLevel(),
                levelData.getMaxLevel(),
                NumberUtil.formatNumber(levelData.getExperience()),
                NumberUtil.formatNumber(levelData.getExperienceToNextLevel()),
                levelData.getExperiencePercentage(),
                levelData.getAvailableBenefits()
        ));

        source.sendFeedback(() -> message, false);
        return 1;
    }

    private static int setLevel(ServerCommandSource source, ServerPlayerEntity player, int level) {
        LivingLevelComponent component = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = component.getLevelData();

        int maxLevel = levelData.getMaxLevel();
        if (level > maxLevel) {
            source.sendError(Text.literal(String.format("§cLevel %d exceeds maximum level %d", level, maxLevel)));
            return 0;
        }

        int oldLevel = levelData.getLevel();

        // Use the level up method that refreshes effects automatically
        component.levelUpAndRefresh(level);

        Text message = Text.literal(String.format(
                "§6Set %s's level from §f%d §6to §f%d",
                player.getName().getString(), oldLevel, level
        ));

        source.sendFeedback(() -> message, true);

        // Notify the player
        player.sendMessage(Text.literal(String.format(
                "§6Your level has been set to §f%d §6by an administrator",
                level
        )).formatted(Formatting.GOLD), false);

        return 1;
    }

    private static int resetLevel(ServerCommandSource source, ServerPlayerEntity player) {
        LivingLevelComponent component = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = component.getLevelData();

        int oldLevel = levelData.getLevel();
        long oldExp = levelData.getExperience();
        int oldBenefits = levelData.getAvailableBenefits();

        // Use batch operation to reset everything with single sync
        component.performBatchUpdate(() -> {
            levelData.setLevel(levelData.getStartingLevel());
            levelData.setExperience(0);

            if (levelData.isPlayer()) levelData.setBenefits(ModConfig.startStatpoints);

            component.refreshAllStatEffectsInternal();
        });

        Text message = Text.literal(String.format(
                "§6Reset %s's progress:\n" +
                        "§7Level: §f%d §7→ §f%d\n" +
                        "§7Experience: §f%s §7→ §f0\n" +
                        "§7Benefits: §f%d §7→ §f48",
                player.getName().getString(),
                oldLevel, levelData.getStartingLevel(),
                NumberUtil.formatNumber(oldExp),
                oldBenefits
        ));

        source.sendFeedback(() -> message, true);

        player.sendMessage(Text.literal(
                "§6Your level progress has been reset by an administrator"
        ).formatted(Formatting.GOLD), false);

        return 1;
    }

    private static int maxLevel(ServerCommandSource source, ServerPlayerEntity player) {
        LivingLevelComponent component = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = component.getLevelData();

        int oldLevel = levelData.getLevel();
        int maxLevel = levelData.getMaxLevel();

        // Use the level up method that refreshes effects automatically
        component.levelUpAndRefresh(maxLevel);

        Text message = Text.literal(String.format(
                "§6Set %s to maximum level (§f%d §6→ §f%d§6)",
                player.getName().getString(), oldLevel, maxLevel
        ));

        source.sendFeedback(() -> message, true);

        player.sendMessage(Text.literal(
                "§6You have been set to maximum level by an administrator!"
        ).formatted(Formatting.GOLD), false);

        return 1;
    }
}