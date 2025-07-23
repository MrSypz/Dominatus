package sypztep.dominatus.common.command;

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
import sypztep.dominatus.common.system.skill.PassiveAbility;
import sypztep.dominatus.common.system.skill.PassiveAbilityRegistry;
import sypztep.dominatus.common.system.skill.PassiveSkillManager;

import java.util.Collection;
import java.util.List;

public class PassiveSkillsCommand {

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("passives")
                .requires(source -> source.hasPermissionLevel(2))
                // /dominatus passives list [player]
                .then(CommandManager.literal("list")
                        .executes(PassiveSkillsCommand::listSelfPassives)
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                .executes(PassiveSkillsCommand::listPlayerPassives)
                        )
                )
                // /dominatus passives unlock <passive_id> [player]
                .then(CommandManager.literal("unlock")
                        .then(CommandManager.argument("passive_id", StringArgumentType.string())
                                .executes(PassiveSkillsCommand::unlockSelfPassive)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(PassiveSkillsCommand::unlockPlayerPassive)
                                )
                        )
                )
                // /dominatus passives reset [player]
                .then(CommandManager.literal("reset")
                        .executes(PassiveSkillsCommand::resetSelfPassives)
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                .executes(PassiveSkillsCommand::resetPlayerPassives)
                        )
                )
                // /dominatus passives registry
                .then(CommandManager.literal("registry")
                        .executes(PassiveSkillsCommand::showRegistry)
                )
                // /dominatus passives check <stat> <value> [player]
                .then(CommandManager.literal("check")
                        .then(CommandManager.argument("stat", StringArgumentType.string())
                                .then(CommandManager.argument("value", StringArgumentType.string())
                                        .executes(PassiveSkillsCommand::checkSelfPassiveUnlocks)
                                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                                .executes(PassiveSkillsCommand::checkPlayerPassiveUnlocks)
                                        )
                                )
                        )
                );
    }

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

    private static int resetSelfPassives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return resetPlayerPassives(context.getSource(), player);
    }

    private static int resetPlayerPassives(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");

        for (ServerPlayerEntity player : players) {
            resetPlayerPassives(context.getSource(), player);
        }

        return players.size();
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

    private static int showRegistry(CommandContext<ServerCommandSource> context) {
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

    // Helper methods
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

        boolean success = passiveManager.unlockPassive(player, passive);
        if (success) {
            levelComponent.sync();

            Text message = Text.literal(String.format("§6Unlocked passive '%s' for %s",
                    passive.getName().getString(), player.getName().getString()));
            source.sendFeedback(() -> message, true);
        } else {
            source.sendError(Text.literal("Passive already unlocked: " + passive.getName().getString()));
        }

        return success ? 1 : 0;
    }

    private static int resetPlayerPassives(ServerCommandSource source, ServerPlayerEntity player) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        PassiveSkillManager passiveManager = levelComponent.getPassiveSkillManager();

        if (passiveManager == null) {
            source.sendError(Text.literal("Player does not have passive skill system!"));
            return 0;
        }

        int removedCount = passiveManager.getTotalUnlockedCount();

        levelComponent.removeAllStatEffects(); // Fix
        passiveManager.getUnlockedPassives().clear();

        levelComponent.sync();

        Text message = Text.literal(String.format("§6Reset all passive abilities for %s (removed %d passives)",
                player.getName().getString(), removedCount));
        source.sendFeedback(() -> message, true);

        return 1;
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

            passiveManager.checkForNewUnlocks(player, stat.toLowerCase(), statValue);
            levelComponent.sync();

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