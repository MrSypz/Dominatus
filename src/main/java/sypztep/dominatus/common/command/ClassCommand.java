package sypztep.dominatus.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.component.living.PlayerClassComponent;
import sypztep.dominatus.common.component.living.PlayerSkillComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.playerclass.PlayerClass;
import sypztep.dominatus.common.system.skill.active.ActiveSkill;
import sypztep.dominatus.common.system.skill.active.ActiveSkillRegistry;
import sypztep.dominatus.common.system.skill.active.SkillResult;

public class ClassCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(CommandManager.literal("class")
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("className", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    for (PlayerClass playerClass : PlayerClass.values()) {
                                        builder.suggest(playerClass.name().toLowerCase());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ClassCommand::setClass)))
                .then(CommandManager.literal("skill")
                        .then(CommandManager.literal("learn")
                                .then(CommandManager.argument("skillId", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            // Suggest available skills for current class
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            if (player != null) {
                                                PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
                                                for (ActiveSkill skill : ActiveSkillRegistry.getSkillsForClass(classComp.getCurrentClass())) {
                                                    builder.suggest(skill.getId().toString());
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ClassCommand::learnSkill)))
                        .then(CommandManager.literal("use")
                                .then(CommandManager.argument("skillId", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            // Suggest learned skills
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            if (player != null) {
                                                PlayerSkillComponent skillComp = ModEntityComponents.PLAYERSKILL.get(player);
                                                for (ActiveSkill skill : skillComp.getLearnedSkills()) {
                                                    builder.suggest(skill.getId().toString());
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ClassCommand::useSkill)))
                        .then(CommandManager.literal("list")
                                .executes(ClassCommand::listSkills)))
                .then(CommandManager.literal("info")
                        .executes(ClassCommand::showFullInfo))
                .then(CommandManager.literal("exp")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("amount", LongArgumentType.longArg(1))
                                        .executes(ClassCommand::addClassExp)))
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("amount", LongArgumentType.longArg(0))
                                        .executes(ClassCommand::setClassExp))))
                .then(CommandManager.literal("points")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ClassCommand::addClassPoints)))
                        .then(CommandManager.literal("spend")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ClassCommand::spendClassPoints))))
                .then(CommandManager.literal("resource")
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("amount", FloatArgumentType.floatArg(0))
                                        .executes(ClassCommand::setResource)))
                        .then(CommandManager.literal("restore")
                                .executes(ClassCommand::restoreResource))));
    }

    private static int setClass(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String className = StringArgumentType.getString(context, "className").toUpperCase();

        try {
            PlayerClass targetClass = PlayerClass.valueOf(className);
            PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

            classComp.setClass(targetClass); // This resets levels automatically
            context.getSource().sendFeedback(() ->
                    Text.literal("Changed class to ")
                            .append(targetClass.getFormattedName())
                            .append(" (all levels reset!)"), true);
            return 1;
        } catch (IllegalArgumentException e) {
            context.getSource().sendError(Text.literal("Unknown class: " + className));
            return 0;
        }
    }

    private static int showFullInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
        LivingLevelComponent livingComp = ModEntityComponents.LIVINGLEVEL.get(player);
        PlayerClass currentClass = classComp.getCurrentClass();

        player.sendMessage(Text.literal("=== Character & Class Information ===")
                .formatted(Formatting.GOLD), false);

        // Character level info
        player.sendMessage(Text.literal("Character Level: ")
                .formatted(Formatting.GRAY)
                .append(Text.literal(String.valueOf(livingComp.getLevel())).formatted(Formatting.WHITE))
                .append(Text.literal(" (").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(livingComp.getAvailableBenefits() + " stat points").formatted(Formatting.YELLOW))
                .append(Text.literal(")").formatted(Formatting.DARK_GRAY)), false);

        // Class info
        player.sendMessage(Text.literal("Current Class: ")
                .append(currentClass.getFormattedName()), false);
        player.sendMessage(Text.literal("Class Level: ")
                .formatted(Formatting.GRAY)
                .append(Text.literal(String.valueOf(classComp.getClassLevel())).formatted(Formatting.WHITE))
                .append(Text.literal(" / ").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(String.valueOf(classComp.getMaxClassLevel())).formatted(Formatting.GRAY))
                .append(Text.literal(" (").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(classComp.getAvailableClassPoints() + " class points").formatted(Formatting.AQUA))
                .append(Text.literal(")").formatted(Formatting.DARK_GRAY)), false);

        // Experience info
        player.sendMessage(Text.literal(String.format("Class Exp: %d / %d (%.1f%%)",
                classComp.getClassExperience(),
                classComp.getClassExperienceToNextLevel() + classComp.getClassExperience(),
                classComp.getClassExperiencePercentage())), false);

        // Resource info
        player.sendMessage(Text.literal("Resource: ")
                .append(classComp.getResourceType().getFormattedName())
                .append(Text.literal(String.format(" %.1f/%.1f",
                        classComp.getCurrentResource(), classComp.getMaxResource()))), false);

        return 1;
    }

    private static int addClassExp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        long amount = LongArgumentType.getLong(context, "amount");
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

        classComp.awardClassExperience(amount, "Command");
        context.getSource().sendFeedback(() ->
                Text.literal("Added " + amount + " class experience"), true);
        return 1;
    }

    private static int setClassExp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        long amount = LongArgumentType.getLong(context, "amount");
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

        classComp.setClassExperience(amount);
        context.getSource().sendFeedback(() ->
                Text.literal("Set class experience to " + amount), true);
        return 1;
    }

    private static int addClassPoints(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

        classComp.addClassPoints(amount);
        context.getSource().sendFeedback(() ->
                Text.literal("Added " + amount + " class points"), true);
        return 1;
    }

    private static int spendClassPoints(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

        if (classComp.spendClassPoints(amount)) {
            context.getSource().sendFeedback(() ->
                    Text.literal("Spent " + amount + " class points"), true);
            return 1;
        } else {
            context.getSource().sendError(Text.literal("Not enough class points"));
            return 0;
        }
    }

    // Resource commands (same as before)
    private static int setResource(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        float amount = FloatArgumentType.getFloat(context, "amount");
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

        classComp.setResource(amount);
        context.getSource().sendFeedback(() ->
                Text.literal("Set resource to " + amount), true);
        return 1;
    }

    private static int restoreResource(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

        classComp.restoreResource();
        context.getSource().sendFeedback(() ->
                Text.literal("Restored resource to full"), true);
        return 1;
    }
    private static int learnSkill(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String skillIdStr = StringArgumentType.getString(context, "skillId");
        Dominatus.LOGGER.info("Learning Skill: " + skillIdStr);
        try {
            Identifier skillId = Dominatus.id(skillIdStr);
            Dominatus.LOGGER.info("Learning Skill: " + skillId);
            PlayerSkillComponent skillComp = ModEntityComponents.PLAYERSKILL.get(player);

            if (skillComp.learnSkill(skillId)) {
                ActiveSkill skill = ActiveSkillRegistry.getSkill(skillId);
                context.getSource().sendFeedback(() ->
                        Text.literal("Learned skill: ").append(skill.getFormattedName()), true);
                return 1;
            } else {
                context.getSource().sendError(Text.literal("Cannot learn skill"));
                return 0;
            }
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Invalid skill ID"));
            return 0;
        }
    }

    private static int useSkill(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String skillIdStr = StringArgumentType.getString(context, "skillId");

        try {
            Identifier skillId = Dominatus.id(skillIdStr);
            PlayerSkillComponent skillComp = ModEntityComponents.PLAYERSKILL.get(player);

            SkillResult result = skillComp.useSkill(skillId);
            if (result.success()) {
                context.getSource().sendFeedback(() ->
                        Text.literal("Used skill: " + result.message()), true);
                return 1;
            } else {
                context.getSource().sendError(Text.literal("Skill failed: " + result.message()));
                return 0;
            }
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Invalid skill ID"));
            return 0;
        }
    }

    private static int listSkills(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
        PlayerSkillComponent skillComp = ModEntityComponents.PLAYERSKILL.get(player);

        player.sendMessage(Text.literal("=== Available Skills ===").formatted(Formatting.GOLD), false);

        for (ActiveSkill skill : ActiveSkillRegistry.getSkillsForClass(classComp.getCurrentClass())) {
            boolean learned = skillComp.hasLearnedSkill(skill.getId());
            boolean canLearn = skill.canLearn(player);

            Text status;
            if (learned) {
                status = Text.literal("[LEARNED]").formatted(Formatting.GREEN);
            } else if (canLearn) {
                status = Text.literal("[CAN LEARN - " + skill.getLearnCost() + " pts]").formatted(Formatting.YELLOW);
            } else {
                status = Text.literal("[LOCKED]").formatted(Formatting.RED);
            }

            player.sendMessage(status.copy().append(" ").append(skill.getFormattedName()), false);
            player.sendMessage(Text.literal("  ").append(skill.getFormattedDescription()), false);
        }

        return 1;
    }
}