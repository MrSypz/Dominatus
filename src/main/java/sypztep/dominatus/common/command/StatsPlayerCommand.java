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
import sypztep.dominatus.common.system.stat.PlayerStatManager;

import java.util.Collection;

public class StatsPlayerCommand {

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("stats")
                .requires(source -> source.hasPermissionLevel(2))
                // /dominatus stats reset <stat> [player]
                .then(CommandManager.literal("reset")
                        .then(CommandManager.literal("strength")
                                .executes(StatsPlayerCommand::resetSelfStrength)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(StatsPlayerCommand::resetPlayerStrength)
                                )
                        )
                        .then(CommandManager.literal("agility")
                                .executes(StatsPlayerCommand::resetSelfAgility)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(StatsPlayerCommand::resetPlayerAgility)
                                )
                        )
                        .then(CommandManager.literal("vitality")
                                .executes(StatsPlayerCommand::resetSelfVitality)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(StatsPlayerCommand::resetPlayerVitality)
                                )
                        )
                        .then(CommandManager.literal("intelligence")
                                .executes(StatsPlayerCommand::resetSelfIntelligence)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(StatsPlayerCommand::resetPlayerIntelligence)
                                )
                        )
                        .then(CommandManager.literal("dexterity")
                                .executes(StatsPlayerCommand::resetSelfDexterity)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(StatsPlayerCommand::resetPlayerDexterity)
                                )
                        )
                        .then(CommandManager.literal("luck")
                                .executes(StatsPlayerCommand::resetSelfLuck)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(StatsPlayerCommand::resetPlayerLuck)
                                )
                        )
                        .then(CommandManager.literal("all")
                                .executes(StatsPlayerCommand::resetSelfAllStats)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(StatsPlayerCommand::resetPlayerAllStats)
                                )
                        )
                );
    }

    // Self reset commands
    private static int resetSelfStrength(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return resetPlayerStat(context.getSource(), player, "strength");
    }

    private static int resetSelfAgility(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return resetPlayerStat(context.getSource(), player, "agility");
    }

    private static int resetSelfVitality(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return resetPlayerStat(context.getSource(), player, "vitality");
    }

    private static int resetSelfIntelligence(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return resetPlayerStat(context.getSource(), player, "intelligence");
    }

    private static int resetSelfDexterity(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return resetPlayerStat(context.getSource(), player, "dexterity");
    }

    private static int resetSelfLuck(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return resetPlayerStat(context.getSource(), player, "luck");
    }

    private static int resetSelfAllStats(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return resetAllPlayerStats(context.getSource(), player);
    }

    // Player reset commands
    private static int resetPlayerStrength(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        for (ServerPlayerEntity player : players) {
            resetPlayerStat(context.getSource(), player, "strength");
        }
        return players.size();
    }

    private static int resetPlayerAgility(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        for (ServerPlayerEntity player : players) {
            resetPlayerStat(context.getSource(), player, "agility");
        }
        return players.size();
    }

    private static int resetPlayerVitality(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        for (ServerPlayerEntity player : players) {
            resetPlayerStat(context.getSource(), player, "vitality");
        }
        return players.size();
    }

    private static int resetPlayerIntelligence(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        for (ServerPlayerEntity player : players) {
            resetPlayerStat(context.getSource(), player, "intelligence");
        }
        return players.size();
    }

    private static int resetPlayerDexterity(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        for (ServerPlayerEntity player : players) {
            resetPlayerStat(context.getSource(), player, "dexterity");
        }
        return players.size();
    }

    private static int resetPlayerLuck(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        for (ServerPlayerEntity player : players) {
            resetPlayerStat(context.getSource(), player, "luck");
        }
        return players.size();
    }

    private static int resetPlayerAllStats(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        for (ServerPlayerEntity player : players) {
            resetAllPlayerStats(context.getSource(), player);
        }
        return players.size();
    }

    // Helper methods
    private static int resetPlayerStat(ServerCommandSource source, ServerPlayerEntity player, String statName) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = levelComponent.getLevelData();

        if (!levelData.isPlayer()) {
            source.sendError(Text.literal("Target is not a player!"));
            return 0;
        }

        PlayerStatManager statManager = levelComponent.getPlayerStatManager();
        if (statManager == null) {
            source.sendError(Text.literal("No player stat manager found!"));
            return 0;
        }

        // Get points spent before reset
        int pointsRefunded = 0;
        boolean success = false;

        switch (statName.toLowerCase()) {
            case "strength" -> {
                if (statManager.getStrength() != null) {
                    pointsRefunded = statManager.getStrength().getTotalPointsSpent();
                    statManager.getStrength().resetWithRefund(player);
                    success = true;
                }
            }
            case "agility" -> {
                if (statManager.getAgility() != null) {
                    pointsRefunded = statManager.getAgility().getTotalPointsSpent();
                    statManager.getAgility().resetWithRefund(player);
                    success = true;
                }
            }
            case "vitality" -> {
                if (statManager.getVitality() != null) {
                    pointsRefunded = statManager.getVitality().getTotalPointsSpent();
                    statManager.getVitality().resetWithRefund(player);
                    success = true;
                }
            }
            case "intelligence" -> {
                if (statManager.getIntelligence() != null) {
                    pointsRefunded = statManager.getIntelligence().getTotalPointsSpent();
                    statManager.getIntelligence().resetWithRefund(player);
                    success = true;
                }
            }
            case "dexterity" -> {
                if (statManager.getDexterity() != null) {
                    pointsRefunded = statManager.getDexterity().getTotalPointsSpent();
                    statManager.getDexterity().resetWithRefund(player);
                    success = true;
                }
            }
            case "luck" -> {
                if (statManager.getLuck() != null) {
                    pointsRefunded = statManager.getLuck().getTotalPointsSpent();
                    statManager.getLuck().resetWithRefund(player);
                    success = true;
                }
            }
        }

        if (success) {
            // Apply all effects and sync
            levelComponent.applyAllStatEffects();
            levelComponent.sync();

            Text message = Text.literal(String.format(
                    "§6Reset %s's %s stat. Refunded §f%d §6benefit points.",
                    player.getName().getString(), statName, pointsRefunded
            ));
            source.sendFeedback(() -> message, true);

            // Notify the player
            player.sendMessage(Text.literal(String.format(
                    "§6Your %s stat has been reset. Refunded §f%d §6benefit points.",
                    statName, pointsRefunded
            )), false);
        } else {
            source.sendError(Text.literal("Failed to reset stat: " + statName));
        }

        return success ? 1 : 0;
    }

    private static int resetAllPlayerStats(ServerCommandSource source, ServerPlayerEntity player) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = levelComponent.getLevelData();

        if (!levelData.isPlayer()) {
            source.sendError(Text.literal("Target is not a player!"));
            return 0;
        }

        PlayerStatManager statManager = levelComponent.getPlayerStatManager();
        if (statManager == null) {
            source.sendError(Text.literal("No player stat manager found!"));
            return 0;
        }

        // Calculate total points that will be refunded
        int totalPointsRefunded = statManager.getTotalPointsSpent();

        // Reset all stats
        statManager.resetAllStats(player);

        // Apply all effects and sync
        levelComponent.applyAllStatEffects();
        levelComponent.sync();

        Text message = Text.literal(String.format(
                "§6Reset all stats for %s. Refunded §f%d §6benefit points total.",
                player.getName().getString(), totalPointsRefunded
        ));
        source.sendFeedback(() -> message, true);

        // Notify the player
        player.sendMessage(Text.literal(String.format(
                "§6All your stats have been reset! Refunded §f%d §6benefit points total.",
                totalPointsRefunded
        )), false);

        return 1;
    }
}