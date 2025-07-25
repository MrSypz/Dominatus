package sypztep.dominatus.common.command;

import com.mojang.brigadier.CommandDispatcher;
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
import sypztep.dominatus.common.system.playerclass.EvolutionRequirement;
import sypztep.dominatus.common.system.playerclass.PlayerClass;
import sypztep.dominatus.common.system.skill.active.ActiveSkill;
import sypztep.dominatus.common.system.skill.active.ActiveSkillRegistry;
import sypztep.dominatus.common.system.skill.active.UpgradeableSkill;

import java.util.Map;

public class ClassCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(CommandManager.literal("class")
                // Basic class management
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("className", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    for (PlayerClass playerClass : PlayerClass.values()) {
                                        builder.suggest(playerClass.name().toLowerCase());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ClassCommand::setClass)))
                .then(CommandManager.literal("info")
                        .executes(ClassCommand::showFullInfo))

                // Class evolution
                .then(CommandManager.literal("evolve")
                        .then(CommandManager.argument("targetClass", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    if (player != null) {
                                        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
                                        for (PlayerClass option : classComp.getCurrentClass().getEvolutionOptions()) {
                                            builder.suggest(option.name().toLowerCase());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ClassCommand::evolveClass)))
                .then(CommandManager.literal("evolution")
                        .executes(ClassCommand::showEvolutionOptions))

                // Skill management with levels
                .then(CommandManager.literal("skill")
                        .then(CommandManager.literal("learn")
                                .then(CommandManager.argument("skillId", StringArgumentType.string())
                                        .suggests((context, builder) -> {
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
                        .then(CommandManager.literal("upgrade")
                                .then(CommandManager.argument("skillId", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            if (player != null) {
                                                PlayerSkillComponent skillComp = ModEntityComponents.PLAYERSKILL.get(player);
                                                for (Map.Entry<ActiveSkill, Integer> entry : skillComp.getLearnedSkillsWithLevels().entrySet()) {
                                                    if (entry.getKey() instanceof UpgradeableSkill) {
                                                        builder.suggest(entry.getKey().getId().toString());
                                                    }
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ClassCommand::upgradeSkill)))
                        .then(CommandManager.literal("list")
                                .executes(ClassCommand::listSkillsWithLevels)))

                // Experience and points
                .then(CommandManager.literal("exp")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("amount", LongArgumentType.longArg(1))
                                        .executes(ClassCommand::addClassExp))))
                .then(CommandManager.literal("points")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ClassCommand::addClassPoints))))

                // Resource management
                .then(CommandManager.literal("resource")
                        .then(CommandManager.literal("restore")
                                .executes(ClassCommand::restoreResource))));
    }

    private static int setClass(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String className = StringArgumentType.getString(context, "className").toUpperCase();

        try {
            PlayerClass targetClass = PlayerClass.valueOf(className);
            PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

            classComp.setClass(targetClass);
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

    private static int evolveClass(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String className = StringArgumentType.getString(context, "targetClass").toUpperCase();

        try {
            PlayerClass targetClass = PlayerClass.valueOf(className);
            PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
            PlayerClass currentClass = classComp.getCurrentClass();

            if (currentClass.canEvolveTo(player, targetClass)) {
                // Don't reset levels on evolution - this is progression!
                PlayerClass oldClass = classComp.getCurrentClass();

                // Remove old class modifiers
                oldClass.removeAttributeModifiers(player);

                // Set new class without resetting levels
                classComp.setClass(targetClass); // Would need to implement this method

                // Apply new class modifiers
                targetClass.applyAttributeModifiers(player);

                context.getSource().sendFeedback(() ->
                        Text.literal("Successfully evolved to ")
                                .append(targetClass.getFormattedName())
                                .append("!"), true);
                return 1;
            } else {
                EvolutionRequirement requirement = currentClass.getEvolutionRequirement(targetClass);
                String reqText = requirement != null ? requirement.getRequirementsText() : "Unknown requirements";

                context.getSource().sendError(
                        Text.literal("Cannot evolve to " + targetClass.getDisplayName() +
                                ". Requirements: " + reqText));
                return 0;
            }
        } catch (IllegalArgumentException e) {
            context.getSource().sendError(Text.literal("Unknown class: " + className));
            return 0;
        }
    }

    private static int showEvolutionOptions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
        PlayerClass currentClass = classComp.getCurrentClass();

        player.sendMessage(Text.literal("=== Evolution Options ===").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("Current Class: ").append(currentClass.getFormattedName()), false);

        PlayerClass[] options = currentClass.getEvolutionOptions();
        if (options.length == 0) {
            player.sendMessage(Text.literal("No evolution options available.").formatted(Formatting.GRAY), false);
            return 1;
        }

        for (PlayerClass option : options) {
            boolean canEvolve = currentClass.canEvolveTo(player, option);
            EvolutionRequirement requirement = currentClass.getEvolutionRequirement(option);

            Text status = canEvolve ?
                    Text.literal("[AVAILABLE]").formatted(Formatting.GREEN) :
                    Text.literal("[LOCKED]").formatted(Formatting.RED);

            player.sendMessage(status.copy().append(" ").append(option.getFormattedName()), false);
            if (requirement != null) {
                player.sendMessage(Text.literal("  Requirements: " + requirement.getRequirementsText())
                        .formatted(Formatting.GRAY), false);
            }
        }

        return 1;
    }

    private static int learnSkill(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String skillIdStr = StringArgumentType.getString(context, "skillId");
        Dominatus.LOGGER.info("Skill name: " + skillIdStr);
        try {
            Identifier skillId = Dominatus.id(skillIdStr);
            Dominatus.LOGGER.info("Call Skill name: " + skillId);
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

    private static int upgradeSkill(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String skillIdStr = StringArgumentType.getString(context, "skillId");
        try {
            Identifier skillId = Dominatus.id(skillIdStr);
            PlayerSkillComponent skillComp = ModEntityComponents.PLAYERSKILL.get(player);

            if (skillComp.upgradeSkill(skillId)) {
                ActiveSkill skill = ActiveSkillRegistry.getSkill(skillId);
                int newLevel = skillComp.getSkillLevel(skillId);

                if (skill instanceof UpgradeableSkill upgradeableSkill) {
                    context.getSource().sendFeedback(() ->
                            Text.literal("Upgraded skill: ")
                                    .append(upgradeableSkill.getFormattedNameWithLevel(newLevel)), true);
                } else {
                    context.getSource().sendFeedback(() ->
                            Text.literal("Upgraded skill: ").append(skill.getFormattedName()), true);
                }
                return 1;
            } else {
                context.getSource().sendError(Text.literal("Cannot upgrade skill"));
                return 0;
            }
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Invalid skill ID"));
            return 0;
        }
    }

    private static int listSkillsWithLevels(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
        PlayerSkillComponent skillComp = ModEntityComponents.PLAYERSKILL.get(player);

        player.sendMessage(Text.literal("=== Skills for " + classComp.getCurrentClass().getDisplayName() + " ===")
                .formatted(Formatting.GOLD), false);

        // Show available skills for current class
        for (ActiveSkill skill : ActiveSkillRegistry.getSkillsForClass(classComp.getCurrentClass())) {
            int skillLevel = skillComp.getSkillLevel(skill.getId());
            boolean learned = skillLevel > 0;
            boolean canLearn = skill.canLearn(player);

            Text status;
            if (learned) {
                if (skill instanceof UpgradeableSkill upgradeableSkill) {
                    boolean canUpgrade = upgradeableSkill.canUpgrade(player, skillLevel);
                    int upgradeCost = upgradeableSkill.getUpgradeCost(skillLevel);

                    status = Text.literal("[LEVEL " + skillLevel + "]").formatted(Formatting.GREEN);
                    if (canUpgrade && upgradeCost > 0) {
                        status = status.copy().append(Text.literal(" (Upgrade: " + upgradeCost + " pts)")
                                .formatted(Formatting.YELLOW));
                    } else if (skillLevel >= upgradeableSkill.getMaxLevel()) {
                        status = status.copy().append(Text.literal(" (MAX)").formatted(Formatting.GOLD));
                    }
                } else {
                    status = Text.literal("[LEARNED]").formatted(Formatting.GREEN);
                }
            } else if (canLearn) {
                status = Text.literal("[CAN LEARN - " + skill.getLearnCost() + " pts]").formatted(Formatting.YELLOW);
            } else {
                status = Text.literal("[LOCKED]").formatted(Formatting.RED);
            }

            Text skillName = skill instanceof UpgradeableSkill upgradeableSkill && learned ?
                    upgradeableSkill.getFormattedNameWithLevel(skillLevel) :
                    skill.getFormattedName();

            player.sendMessage(status.copy().append(" ").append(skillName), false);
            player.sendMessage(Text.literal("  ").append(skill.getFormattedDescription()), false);
        }

        return 1;
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

        // Class info with tier
        String tierText = currentClass.isTier2() ? " (Tier 2)" : currentClass.getTier() == 1 ? " (Tier 1)" : "";
        player.sendMessage(Text.literal("Current Class: ")
                .append(currentClass.getFormattedName())
                .append(Text.literal(tierText).formatted(Formatting.GRAY)), false);

        player.sendMessage(Text.literal("Class Level: ")
                .formatted(Formatting.GRAY)
                .append(Text.literal(String.valueOf(classComp.getClassLevel())).formatted(Formatting.WHITE))
                .append(Text.literal(" / ").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(String.valueOf(classComp.getMaxClassLevel())).formatted(Formatting.GRAY))
                .append(Text.literal(" (").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(classComp.getAvailableClassPoints() + " class points").formatted(Formatting.AQUA))
                .append(Text.literal(")").formatted(Formatting.DARK_GRAY)), false);

        // Resource info
        player.sendMessage(Text.literal("Resource: ")
                .append(classComp.getResourceType().getFormattedName())
                .append(Text.literal(String.format(" %.1f/%.1f",
                        classComp.getCurrentResource(), classComp.getMaxResource()))), false);

        return 1;
    }

    // Helper methods (same as before)
    private static int addClassExp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        long amount = LongArgumentType.getLong(context, "amount");
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

        classComp.awardClassExperience(amount, "Command");
        context.getSource().sendFeedback(() ->
                Text.literal("Added " + amount + " class experience"), true);
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

    private static int restoreResource(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

        classComp.restoreResource();
        context.getSource().sendFeedback(() ->
                Text.literal("Restored resource to full"), true);
        return 1;
    }
}