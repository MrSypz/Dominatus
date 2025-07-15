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
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.LevelData;

import java.util.Collection;

public class BenefitsCommand {

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("benefits")
                // /dominatus benefits get [player]
                .then(CommandManager.literal("get")
                        .executes(BenefitsCommand::getSelfBenefits)
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(BenefitsCommand::getPlayerBenefits)
                        )
                )
                // /dominatus benefits add <amount> [player]
                .then(CommandManager.literal("add")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                .executes(BenefitsCommand::addSelfBenefits)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(BenefitsCommand::addPlayerBenefits)
                                )
                        )
                )
                // /dominatus benefits set <amount> [player]
                .then(CommandManager.literal("set")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                .executes(BenefitsCommand::setSelfBenefits)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(BenefitsCommand::setPlayerBenefits)
                                )
                        )
                )
                // /dominatus benefits remove <amount> [player]
                .then(CommandManager.literal("remove")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                .executes(BenefitsCommand::removeSelfBenefits)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(BenefitsCommand::removePlayerBenefits)
                                )
                        )
                );
    }

    private static int getSelfBenefits(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return showBenefits(context.getSource(), player);
    }

    private static int getPlayerBenefits(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");

        for (ServerPlayerEntity player : players) {
            showBenefits(context.getSource(), player);
        }

        return players.size();
    }

    private static int addSelfBenefits(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        return addBenefits(context.getSource(), player, amount);
    }

    private static int addPlayerBenefits(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        for (ServerPlayerEntity player : players) {
            addBenefits(context.getSource(), player, amount);
        }

        return players.size();
    }

    private static int setSelfBenefits(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        return setBenefits(context.getSource(), player, amount);
    }

    private static int setPlayerBenefits(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        for (ServerPlayerEntity player : players) {
            setBenefits(context.getSource(), player, amount);
        }

        return players.size();
    }

    private static int removeSelfBenefits(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        return removeBenefits(context.getSource(), player, amount);
    }

    private static int removePlayerBenefits(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        for (ServerPlayerEntity player : players) {
            removeBenefits(context.getSource(), player, amount);
        }

        return players.size();
    }

    // Helper methods
    private static int showBenefits(ServerCommandSource source, ServerPlayerEntity player) {
        LivingLevelComponent component = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = component.getLevelData();

        if (!levelData.isPlayer()) {
            source.sendError(Text.literal("§cOnly players have benefits!"));
            return 0;
        }

        Text message = Text.literal(String.format(
                "§6%s has §f%d §6available benefits",
                player.getName().getString(), levelData.getAvailableBenefits()
        ));

        source.sendFeedback(() -> message, false);
        return 1;
    }

    private static int addBenefits(ServerCommandSource source, ServerPlayerEntity player, int amount) {
        LivingLevelComponent component = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = component.getLevelData();

        if (!levelData.isPlayer()) {
            source.sendError(Text.literal("§cOnly players have benefits!"));
            return 0;
        }

        int oldBenefits = levelData.getAvailableBenefits();
        component.addBenefits(amount);
        int newBenefits = levelData.getAvailableBenefits();

        Text message = Text.literal(String.format(
                "§6Added §f%d §6benefits to %s (§f%d §6→ §f%d§6)",
                amount, player.getName().getString(), oldBenefits, newBenefits
        ));

        source.sendFeedback(() -> message, true);

        player.sendMessage(Text.literal(String.format(
                "§6You have been granted §f%d §6benefits by an administrator!",
                amount
        )).formatted(Formatting.GOLD), false);

        return 1;
    }

    private static int setBenefits(ServerCommandSource source, ServerPlayerEntity player, int amount) {
        LivingLevelComponent component = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = component.getLevelData();

        if (!levelData.isPlayer()) {
            source.sendError(Text.literal("§cOnly players have benefits!"));
            return 0;
        }

        int oldBenefits = levelData.getAvailableBenefits();
        component.setBenefits(amount);

        Text message = Text.literal(String.format(
                "§6Set %s's benefits from §f%d §6to §f%d",
                player.getName().getString(), oldBenefits, amount
        ));

        source.sendFeedback(() -> message, true);

        player.sendMessage(Text.literal(String.format(
                "§6Your benefits have been set to §f%d §6by an administrator",
                amount
        )).formatted(Formatting.GOLD), false);

        return 1;
    }

    private static int removeBenefits(ServerCommandSource source, ServerPlayerEntity player, int amount) {
        LivingLevelComponent component = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = component.getLevelData();

        if (!levelData.isPlayer()) {
            source.sendError(Text.literal("§cOnly players have benefits!"));
            return 0;
        }

        int oldBenefits = levelData.getAvailableBenefits();
        boolean success = levelData.spendBenefits(Math.min(amount, oldBenefits));

        if (!success) {
            source.sendError(Text.literal(String.format(
                    "§c%s only has %d benefits available!",
                    player.getName().getString(), oldBenefits
            )));
            return 0;
        }

        Text message = Text.literal(String.format(
                "§6Removed §f%d §6benefits from %s (§f%d §6→ §f%d§6)",
                Math.min(amount, oldBenefits), player.getName().getString(),
                oldBenefits, levelData.getAvailableBenefits()
        ));

        source.sendFeedback(() -> message, true);

        player.sendMessage(Text.literal(String.format(
                "§c%d benefits have been removed by an administrator",
                Math.min(amount, oldBenefits)
        )).formatted(Formatting.RED), false);

        return 1;
    }
}