package sypztep.dominatus.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.LevelData;
import sypztep.dominatus.common.system.skill.PassiveAbility;
import sypztep.dominatus.common.system.skill.PassiveAbilityRegistry;
import sypztep.dominatus.common.system.skill.PassiveSkillManager;
import sypztep.dominatus.common.system.stat.PlayerStat;
import sypztep.dominatus.common.system.stat.PlayerStatBehavior;
import sypztep.dominatus.common.system.stat.PlayerStatManager;
import sypztep.dominatus.common.system.stat.Stat;

import java.util.Collection;
import java.util.List;

public class PlayerStatsCommand {

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("stats")
                .requires(source -> source.hasPermissionLevel(2))
                // /dominatus stats info [player]
                .then(CommandManager.literal("info")
                        .executes(PlayerStatsCommand::showSelfStats)
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(PlayerStatsCommand::showPlayerStats)
                        )
                )
                // /dominatus stats set <player> <stat> <value>
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.literal("strength")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setPlayerStat(ctx, "strength"))
                                        )
                                )
                                .then(CommandManager.literal("agility")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setPlayerStat(ctx, "agility"))
                                        )
                                )
                                .then(CommandManager.literal("vitality")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setPlayerStat(ctx, "vitality"))
                                        )
                                )
                                .then(CommandManager.literal("intelligence")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setPlayerStat(ctx, "intelligence"))
                                        )
                                )
                                .then(CommandManager.literal("dexterity")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setPlayerStat(ctx, "dexterity"))
                                        )
                                )
                                .then(CommandManager.literal("luck")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setPlayerStat(ctx, "luck"))
                                        )
                                )
                        )
                )
                // /dominatus stats reset <stat> [player]
                .then(CommandManager.literal("reset")
                        .then(CommandManager.literal("strength")
                                .executes(PlayerStatsCommand::resetSelfStrength)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(PlayerStatsCommand::resetPlayerStrength)
                                )
                        )
                        .then(CommandManager.literal("agility")
                                .executes(PlayerStatsCommand::resetSelfAgility)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(PlayerStatsCommand::resetPlayerAgility)
                                )
                        )
                        .then(CommandManager.literal("vitality")
                                .executes(PlayerStatsCommand::resetSelfVitality)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(PlayerStatsCommand::resetPlayerVitality)
                                )
                        )
                        .then(CommandManager.literal("intelligence")
                                .executes(PlayerStatsCommand::resetSelfIntelligence)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(PlayerStatsCommand::resetPlayerIntelligence)
                                )
                        )
                        .then(CommandManager.literal("dexterity")
                                .executes(PlayerStatsCommand::resetSelfDexterity)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(PlayerStatsCommand::resetPlayerDexterity)
                                )
                        )
                        .then(CommandManager.literal("luck")
                                .executes(PlayerStatsCommand::resetSelfLuck)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(PlayerStatsCommand::resetPlayerLuck)
                                )
                        )
                        .then(CommandManager.literal("all")
                                .executes(PlayerStatsCommand::resetSelfAllStats)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(PlayerStatsCommand::resetPlayerAllStats)
                                )
                        )
                        .then(CommandManager.literal("passives")
                                .executes(PlayerStatsCommand::resetSelfPassives)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(PlayerStatsCommand::resetPlayerPassives)
                                )
                        )
                )
                // PASSIVE SKILLS SECTION
                // /dominatus stats passives list [player]
                .then(CommandManager.literal("passives")
                        .then(CommandManager.literal("list")
                                .executes(PlayerStatsCommand::listSelfPassives)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(PlayerStatsCommand::listPlayerPassives)
                                )
                        )
                        // /dominatus stats passives unlock <passive_id> [player]
                        .then(CommandManager.literal("unlock")
                                .then(CommandManager.argument("passive_id", StringArgumentType.string())
                                        .executes(PlayerStatsCommand::unlockSelfPassive)
                                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                                .executes(PlayerStatsCommand::unlockPlayerPassive)
                                        )
                                )
                        )
                        // /dominatus stats passives registry
                        .then(CommandManager.literal("registry")
                                .executes(PlayerStatsCommand::showPassiveRegistry)
                        )
                        // /dominatus stats passives check <stat> <value> [player]
                        .then(CommandManager.literal("check")
                                .then(CommandManager.argument("stat", StringArgumentType.string())
                                        .then(CommandManager.argument("value", StringArgumentType.string())
                                                .executes(PlayerStatsCommand::checkSelfPassiveUnlocks)
                                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                                        .executes(PlayerStatsCommand::checkPlayerPassiveUnlocks)
                                                )
                                        )
                                )
                        )
                );
    }

    // ===== INFO COMMANDS =====

    private static int showSelfStats(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return showPlayerStats(context.getSource(), player);
    }

    private static int showPlayerStats(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        return showPlayerStats(context.getSource(), player);
    }

    private static int showPlayerStats(ServerCommandSource source, ServerPlayerEntity player) {
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

        source.sendFeedback(() -> Text.literal("§6=== " + player.getName().getString() + "'s Stats ==="), false);

        String[] statNames = {"strength", "agility", "vitality", "intelligence", "dexterity", "luck"};
        for (String statName : statNames) {
            PlayerStatBehavior stat = statManager.getStat(statName);
            if (stat instanceof PlayerStat<?> playerStat) {
                int current = playerStat.getValue();
                int spent = stat.getTotalPointsSpent();
                source.sendFeedback(() -> Text.literal(String.format(
                        "§f%s: §a%d §7(Points spent: %d)",
                        statName.substring(0, 1).toUpperCase() + statName.substring(1),
                        current, spent
                )), false);
            }
        }

        int totalPointsSpent = statManager.getTotalPointsSpent();
        int availableBenefits = levelComponent.getAvailableBenefits();

        source.sendFeedback(() -> Text.literal(String.format(
                "§7Total points spent: §f%d §7| Available: §a%d",
                totalPointsSpent, availableBenefits
        )), false);

        // Show passive skills info if available
        PassiveSkillManager passiveManager = levelComponent.getPassiveSkillManager();
        if (passiveManager != null) {
            int unlockedCount = passiveManager.getTotalUnlockedCount();
            source.sendFeedback(() -> Text.literal(String.format(
                    "§7Passive skills unlocked: §b%d", unlockedCount
            )), false);
        }

        return 1;
    }

    // ===== SET COMMANDS =====

    private static int setPlayerStat(CommandContext<ServerCommandSource> context, String statName) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        int targetValue = IntegerArgumentType.getInteger(context, "value");

        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = levelComponent.getLevelData();

        if (!levelData.isPlayer()) {
            context.getSource().sendError(Text.literal("Target is not a player!"));
            return 0;
        }

        PlayerStatManager statManager = levelComponent.getPlayerStatManager();
        if (statManager == null) {
            context.getSource().sendError(Text.literal("No player stat manager found!"));
            return 0;
        }

        PlayerStatBehavior stat = statManager.getStat(statName);
        if (stat == null) {
            context.getSource().sendError(Text.literal("Unknown stat: " + statName));
            return 0;
        }

        // Cast to PlayerStat to access getValue()
        if (!(stat instanceof PlayerStat<?> playerStat)) {
            context.getSource().sendError(Text.literal("Invalid stat type: " + statName));
            return 0;
        }

        int currentValue = playerStat.getValue();

        if (targetValue == currentValue) {
            context.getSource().sendError(Text.literal(String.format(
                    "%s's %s is already %d",
                    player.getName().getString(), statName, targetValue
            )));
            return 0;
        }

        if (targetValue < 1 || targetValue > Stat.MAX_STAT_VALUE) {
            context.getSource().sendError(Text.literal(String.format(
                    "Target value must be between 1 and %d", Stat.MAX_STAT_VALUE
            )));
            return 0;
        }

        // Use batch operation to set stat and refresh effects with single sync
        levelComponent.performBatchUpdate(() -> {
            // Reset the stat first, then set it to the new value
            stat.resetWithRefund(player);

            // Calculate points needed for the new value (base value is 1, so points needed = target - 1)
            int pointsNeeded = targetValue - 1;

            if (pointsNeeded > 0) {
                // Use increaseWithPoints to reach target value
                boolean success = stat.increaseWithPoints(player, pointsNeeded);
                if (!success) {
                    context.getSource().sendError(Text.literal(String.format(
                            "Failed to set %s to %d (not enough benefit points or stat limit reached)",
                            statName, targetValue
                    )));
                    return;
                }
            }

            // Apply effects and check for passive unlocks
            levelComponent.refreshAllStatEffectsInternal();
            levelComponent.checkPassiveUnlocks(statName, targetValue);
        });

        Text message = Text.literal(String.format(
                "§6Set %s's %s to §f%d §7(was %d)",
                player.getName().getString(), statName, targetValue, currentValue
        ));
        context.getSource().sendFeedback(() -> message, true);

        // Notify the player
        player.sendMessage(Text.literal(String.format(
                "§6Your %s has been set to §f%d",
                statName, targetValue
        )), false);

        return 1;
    }

    // ===== SELF RESET COMMANDS =====

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

    private static int resetSelfPassives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return resetPlayerPassives(context.getSource(), player);
    }

    // ===== PLAYER RESET COMMANDS =====

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

    private static int resetPlayerPassives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        for (ServerPlayerEntity player : players) {
            resetPlayerPassives(context.getSource(), player);
        }
        return players.size();
    }

    // ===== PASSIVE SKILL COMMANDS =====

    private static int listSelfPassives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return showPlayerPassives(context.getSource(), player);
    }

    private static int listPlayerPassives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");

        for (ServerPlayerEntity player : players) {
            showPlayerPassives(context.getSource(), player);
        }

        return players.size();
    }

    private static int unlockSelfPassive(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String passiveId = StringArgumentType.getString(context, "passive_id");
        return unlockPassiveForPlayer(context.getSource(), player, passiveId);
    }

    private static int unlockPlayerPassive(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        String passiveId = StringArgumentType.getString(context, "passive_id");

        for (ServerPlayerEntity player : players) {
            unlockPassiveForPlayer(context.getSource(), player, passiveId);
        }

        return players.size();
    }

    private static int showPassiveRegistry(CommandContext<ServerCommandSource> context) {
        Collection<PassiveAbility> allPassives = PassiveAbilityRegistry.getAllPassives();

        StringBuilder message = new StringBuilder("§6=== PASSIVE ABILITY REGISTRY ===\n");

        for (String statType : List.of("strength", "agility", "vitality", "intelligence", "dexterity", "luck")) {
            List<PassiveAbility> passivesForStat = PassiveAbilityRegistry.getPassivesForStat(statType);

            if (!passivesForStat.isEmpty()) {
                message.append(String.format("§7%s (%d passives):\n", statType.toUpperCase(), passivesForStat.size()));

                for (PassiveAbility passive : passivesForStat) {
                    message.append(String.format("  §f%s §7(Lv.%d) - %s\n",
                            passive.getName().getString(),
                            passive.getRequiredStatValue(),
                            passive.getDescription().getString()));
                }
                message.append("\n");
            }
        }

        message.append(String.format("§7Total: §f%d §7passive abilities registered", allPassives.size()));

        context.getSource().sendFeedback(() -> Text.literal(message.toString()), false);
        return 1;
    }

    private static int checkSelfPassiveUnlocks(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String stat = StringArgumentType.getString(context, "stat");
        String valueStr = StringArgumentType.getString(context, "value");
        return checkPassiveUnlocksForPlayer(context.getSource(), player, stat, valueStr);
    }

    private static int checkPlayerPassiveUnlocks(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        String stat = StringArgumentType.getString(context, "stat");
        String valueStr = StringArgumentType.getString(context, "value");

        for (ServerPlayerEntity player : players) {
            checkPassiveUnlocksForPlayer(context.getSource(), player, stat, valueStr);
        }

        return players.size();
    }

    // ===== HELPER METHODS =====

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

        PlayerStatBehavior stat = statManager.getStat(statName);
        if (stat == null) {
            source.sendError(Text.literal("Unknown stat: " + statName));
            return 0;
        }

        int pointsRefunded = stat.getTotalPointsSpent();

        // Use batch operation for reset and effect refresh
        levelComponent.performBatchUpdate(() -> {
            stat.resetWithRefund(player);
            levelComponent.refreshAllStatEffectsInternal();
        });

        Text message = Text.literal(String.format(
                "§6Reset %s for %s. Refunded §f%d §6benefit points.",
                statName, player.getName().getString(), pointsRefunded
        ));
        source.sendFeedback(() -> message, true);

        // Notify the player
        player.sendMessage(Text.literal(String.format(
                "§6Your %s stat has been reset. Refunded §f%d §6benefit points.",
                statName, pointsRefunded
        )), false);

        return 1;
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

        // Reset passive skills info
        int passivesRemoved = 0;
        PassiveSkillManager passiveManager = levelComponent.getPassiveSkillManager();
        if (passiveManager != null) {
            passivesRemoved = passiveManager.getTotalUnlockedCount();
        }

        // Use the built-in reset method that handles everything
        levelComponent.resetAllPassiveSkills();

        // Reset all stats with batch operation
        levelComponent.performBatchUpdate(() -> {
            statManager.resetAllStats(player);
            levelComponent.refreshAllStatEffectsInternal();
        });

        String passiveMessage = passivesRemoved > 0
                ? String.format(" Also reset §f%d §6passive skills.", passivesRemoved)
                : "";

        Text message = Text.literal(String.format(
                "§6Reset all stats for %s. Refunded §f%d §6benefit points total.%s",
                player.getName().getString(), totalPointsRefunded, passiveMessage
        ));
        source.sendFeedback(() -> message, true);

        // Notify the player
        String playerPassiveMessage = passivesRemoved > 0
                ? String.format(" Also reset %d passive skills.", passivesRemoved)
                : "";

        player.sendMessage(Text.literal(String.format(
                "§6All your stats have been reset! Refunded §f%d §6benefit points total.%s",
                totalPointsRefunded, playerPassiveMessage
        )), false);

        return 1;
    }

    private static int resetPlayerPassives(ServerCommandSource source, ServerPlayerEntity player) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        PassiveSkillManager passiveManager = levelComponent.getPassiveSkillManager();

        if (passiveManager == null) {
            source.sendError(Text.literal("Player does not have passive skill system!"));
            return 0;
        }

        int removedCount = passiveManager.getTotalUnlockedCount();

        // Use the built-in reset method that handles everything and syncs
        levelComponent.resetAllPassiveSkills();

        Text message = Text.literal(String.format("§6Reset all passive abilities for %s (removed %d passives)",
                player.getName().getString(), removedCount));
        source.sendFeedback(() -> message, true);

        // Notify the player
        player.sendMessage(Text.literal(String.format(
                "§6All your passive abilities have been reset! (removed %d passives)",
                removedCount
        )), false);

        return 1;
    }

    private static int showPlayerPassives(ServerCommandSource source, ServerPlayerEntity player) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        PassiveSkillManager passiveManager = levelComponent.getPassiveSkillManager();

        if (passiveManager == null) {
            source.sendError(Text.literal("Player does not have passive skill system!"));
            return 0;
        }

        Collection<PassiveAbility> unlockedPassives = passiveManager.getUnlockedPassives();

        if (unlockedPassives.isEmpty()) {
            Text message = Text.literal(String.format("§6%s has no unlocked passive abilities.",
                    player.getName().getString()));
            source.sendFeedback(() -> message, false);
            return 1;
        }

        StringBuilder message = new StringBuilder();
        message.append(String.format("§6=== %s's PASSIVE ABILITIES ===\n", player.getName().getString()));
        message.append(String.format("§7Total Unlocked: §f%d\n\n", unlockedPassives.size()));

        // Group by stat type
        for (String statType : List.of("strength", "agility", "vitality", "intelligence", "dexterity", "luck")) {
            List<PassiveAbility> passivesForStat = passiveManager.getUnlockedPassivesForStat(statType);

            if (!passivesForStat.isEmpty()) {
                message.append(String.format("§7%s (%d):\n", statType.toUpperCase(), passivesForStat.size()));

                for (PassiveAbility passive : passivesForStat) {
                    String status = passive.isActive() ? "§a✓" : "§c✗";
                    message.append(String.format("  %s §f%s §7(Lv.%d)\n",
                            status, passive.getName().getString(), passive.getRequiredStatValue()));
                }
                message.append("\n");
            }
        }

        source.sendFeedback(() -> Text.literal(message.toString()), false);
        return 1;
    }

    private static int unlockPassiveForPlayer(ServerCommandSource source, ServerPlayerEntity player, String passiveIdStr) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        PassiveSkillManager passiveManager = levelComponent.getPassiveSkillManager();

        if (passiveManager == null) {
            source.sendError(Text.literal("Player does not have passive skill system!"));
            return 0;
        }

        Identifier passiveId = Identifier.tryParse(passiveIdStr);
        if (passiveId == null) {
            source.sendError(Text.literal("Invalid passive ID: " + passiveIdStr));
            return 0;
        }

        PassiveAbility passive = PassiveAbilityRegistry.getPassive(passiveId);
        if (passive == null) {
            source.sendError(Text.literal("Passive ability not found: " + passiveIdStr));
            return 0;
        }

        // Use the new auto-sync method
        boolean success = levelComponent.unlockPassive(passive);
        if (success) {
            Text message = Text.literal(String.format("§6Unlocked passive '%s' for %s",
                    passive.getName().getString(), player.getName().getString()));
            source.sendFeedback(() -> message, true);
        } else {
            source.sendError(Text.literal("Passive already unlocked: " + passive.getName().getString()));
        }

        return success ? 1 : 0;
    }

    private static int checkPassiveUnlocksForPlayer(ServerCommandSource source, ServerPlayerEntity player, String stat, String valueStr) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        PassiveSkillManager passiveManager = levelComponent.getPassiveSkillManager();

        if (passiveManager == null) {
            source.sendError(Text.literal("Player does not have passive skill system!"));
            return 0;
        }

        try {
            int statValue = Integer.parseInt(valueStr);

            // Valid stat names
            List<String> validStats = List.of("strength", "agility", "vitality", "intelligence", "dexterity", "luck");
            if (!validStats.contains(stat.toLowerCase())) {
                source.sendError(Text.literal("Invalid stat name. Valid stats: " + String.join(", ", validStats)));
                return 0;
            }

            // Use the auto-sync method
            levelComponent.checkPassiveUnlocks(stat.toLowerCase(), statValue);

            Text message = Text.literal(String.format("§6Checked passive unlocks for %s with %s=%d",
                    player.getName().getString(), stat.toUpperCase(), statValue));
            source.sendFeedback(() -> message, true);

            return 1;
        } catch (NumberFormatException e) {
            source.sendError(Text.literal("Invalid stat value: " + valueStr + " (must be a number)"));
            return 0;
        }
    }
}