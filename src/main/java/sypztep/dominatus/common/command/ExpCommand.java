package sypztep.dominatus.common.command;

import com.mojang.brigadier.arguments.LongArgumentType;
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
import sypztep.dominatus.common.util.level.ExpUtil;

import java.util.Collection;

public class ExpCommand {

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("exp")
                // /dominatus exp add <amount> [player]
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("amount", LongArgumentType.longArg(1))
                                .executes(ExpCommand::addSelfExp)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .executes(ExpCommand::addPlayerExp)
                                )
                        )
                )
                // /dominatus exp set <amount> [player]
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("amount", LongArgumentType.longArg(0))
                                .executes(ExpCommand::setSelfExp)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .executes(ExpCommand::setPlayerExp)
                                )
                        )
                )
                // /dominatus exp remove <amount> [player]
                .then(CommandManager.literal("remove")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("amount", LongArgumentType.longArg(1))
                                .executes(ExpCommand::removeSelfExp)
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(ExpCommand::removePlayerExp)
                                )
                        )
                );
    }

    private static int addSelfExp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        long amount = LongArgumentType.getLong(context, "amount");
        return addExperience(context.getSource(), player, amount);
    }

    private static int addPlayerExp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        long amount = LongArgumentType.getLong(context, "amount");

        for (ServerPlayerEntity player : players) {
            addExperience(context.getSource(), player, amount);
        }

        return players.size();
    }

    private static int setSelfExp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        long amount = LongArgumentType.getLong(context, "amount");
        return setExperience(context.getSource(), player, amount);
    }

    private static int setPlayerExp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        long amount = LongArgumentType.getLong(context, "amount");

        for (ServerPlayerEntity player : players) {
            setExperience(context.getSource(), player, amount);
        }

        return players.size();
    }

    private static int removeSelfExp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        long amount = LongArgumentType.getLong(context, "amount");
        return removeExperience(context.getSource(), player, amount);
    }

    private static int removePlayerExp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
        long amount = LongArgumentType.getLong(context, "amount");

        for (ServerPlayerEntity player : players) {
            removeExperience(context.getSource(), player, amount);
        }

        return players.size();
    }

    // Helper methods
    private static int addExperience(ServerCommandSource source, ServerPlayerEntity player, long amount) {
        ExpUtil.awardExperience(player, amount, "Command", true);

        Text message = Text.literal(String.format(
                "§6Added §f%s §6experience to %s",
                formatNumber(amount), player.getName().getString()
        ));

        source.sendFeedback(() -> message, true);
        return 1;
    }

    private static int setExperience(ServerCommandSource source, ServerPlayerEntity player, long amount) {
        LivingLevelComponent component = ModEntityComponents.LIVINGLEVEL.get(player);
        long oldExp = component.getExperience();

        component.setExperience(amount);

        Text message = Text.literal(String.format(
                "§6Set %s's experience from §f%s §6to §f%s",
                player.getName().getString(), formatNumber(oldExp), formatNumber(amount)
        ));

        source.sendFeedback(() -> message, true);

        player.sendMessage(Text.literal(String.format(
                "§6Your experience has been set to §f%s §6by an administrator",
                formatNumber(amount)
        )).formatted(Formatting.GOLD), false);

        return 1;
    }

    private static int removeExperience(ServerCommandSource source, ServerPlayerEntity player, long amount) {
        LivingLevelComponent component = ModEntityComponents.LIVINGLEVEL.get(player);

        long oldExp = component.getExperience();
        long newExp = Math.max(0, oldExp - amount);
        component.setExperience(newExp);

        Text message = Text.literal(String.format(
                "§6Removed §f%s §6experience from %s (§f%s §6→ §f%s§6)",
                formatNumber(amount), player.getName().getString(),
                formatNumber(oldExp), formatNumber(newExp)
        ));

        source.sendFeedback(() -> message, true);

        player.sendMessage(Text.literal(String.format(
                "§c%s experience has been removed by an administrator",
                formatNumber(Math.min(amount, oldExp))
        )).formatted(Formatting.RED), false);

        return 1;
    }

    private static String formatNumber(long number) {
        if (number >= 1_000_000_000L) {
            return String.format("%.1fB", number / 1_000_000_000.0);
        } else if (number >= 1_000_000L) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000L) {
            return String.format("%.1fK", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }
}