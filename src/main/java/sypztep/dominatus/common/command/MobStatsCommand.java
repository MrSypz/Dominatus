package sypztep.dominatus.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.stat.EntityStatManager;
import sypztep.dominatus.common.system.stat.Stat;

import java.util.Collection;

public class MobStatsCommand {

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("mobstats")
                .requires(source -> source.hasPermissionLevel(2))
                // /dominatus mobstats info <entity>
                .then(CommandManager.literal("info")
                        .then(CommandManager.argument("entity", EntityArgumentType.entities())
                                .executes(MobStatsCommand::showMobStats)
                        )
                )
                // /dominatus mobstats set <entity> <stat> <value>
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("entity", EntityArgumentType.entity())
                                .then(CommandManager.literal("strength")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setMobStat(ctx, "strength"))
                                        )
                                )
                                .then(CommandManager.literal("agility")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setMobStat(ctx, "agility"))
                                        )
                                )
                                .then(CommandManager.literal("vitality")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setMobStat(ctx, "vitality"))
                                        )
                                )
                                .then(CommandManager.literal("intelligence")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setMobStat(ctx, "intelligence"))
                                        )
                                )
                                .then(CommandManager.literal("dexterity")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setMobStat(ctx, "dexterity"))
                                        )
                                )
                                .then(CommandManager.literal("luck")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1, Stat.MAX_STAT_VALUE))
                                                .executes(ctx -> setMobStat(ctx, "luck"))
                                        )
                                )
                        )
                )
                // /dominatus mobstats reset <entity> [stat]
                .then(CommandManager.literal("reset")
                        .then(CommandManager.argument("entity", EntityArgumentType.entity())
                                .executes(MobStatsCommand::resetAllMobStats)
                                .then(CommandManager.literal("strength")
                                        .executes(ctx -> resetMobStat(ctx, "strength"))
                                )
                                .then(CommandManager.literal("agility")
                                        .executes(ctx -> resetMobStat(ctx, "agility"))
                                )
                                .then(CommandManager.literal("vitality")
                                        .executes(ctx -> resetMobStat(ctx, "vitality"))
                                )
                                .then(CommandManager.literal("intelligence")
                                        .executes(ctx -> resetMobStat(ctx, "intelligence"))
                                )
                                .then(CommandManager.literal("dexterity")
                                        .executes(ctx -> resetMobStat(ctx, "dexterity"))
                                )
                                .then(CommandManager.literal("luck")
                                        .executes(ctx -> resetMobStat(ctx, "luck"))
                                )
                        )
                );
    }

    private static int showMobStats(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<? extends net.minecraft.entity.Entity> entities = EntityArgumentType.getEntities(context, "entity");

        for (net.minecraft.entity.Entity entity : entities) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                context.getSource().sendError(Text.literal("Entity " + entity.getName().getString() + " is not a living entity"));
                continue;
            }

            showEntityStats(context.getSource(), livingEntity);
        }

        return entities.size();
    }

    private static int setMobStat(CommandContext<ServerCommandSource> context, String statName) throws CommandSyntaxException {
        net.minecraft.entity.Entity entity = EntityArgumentType.getEntity(context, "entity");
        int value = IntegerArgumentType.getInteger(context, "value");

        if (!(entity instanceof LivingEntity livingEntity)) {
            context.getSource().sendError(Text.literal("Target is not a living entity"));
            return 0;
        }

        // Validate stat value is within bounds
        if (value < 1 || value > Stat.MAX_STAT_VALUE) {
            context.getSource().sendError(Text.literal(String.format("Stat value must be between 1 and %d", Stat.MAX_STAT_VALUE)));
            return 0;
        }

        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(livingEntity);
        if (levelComponent == null) {
            context.getSource().sendError(Text.literal("No level component found for entity"));
            return 0;
        }

        if (levelComponent.isPlayer()) {
            context.getSource().sendError(Text.literal("Cannot modify player stats with this command. Use the player stat commands instead."));
            return 0;
        }

        EntityStatManager statManager = levelComponent.getEntityStatManager();
        if (statManager == null) {
            context.getSource().sendError(Text.literal("No stat manager found for entity"));
            return 0;
        }

        // Use batch operation to set stat and apply effects with single sync
        final boolean[] success = {false};
        levelComponent.performBatchUpdate(() -> {
            success[0] = setStatValue(statManager, statName, value);
            if (success[0]) {
                levelComponent.refreshAllStatEffectsInternal(); // Apply effects without sync
            }
        });

        if (success[0]) {
            Text message = Text.literal(String.format(
                    "§6Set %s's %s to §f%d §7(Max: %d)",
                    livingEntity.getName().getString(), statName, value, Stat.MAX_STAT_VALUE
            ));
            context.getSource().sendFeedback(() -> message, true);
        } else {
            context.getSource().sendError(Text.literal("Failed to set stat: " + statName));
        }

        return success[0] ? 1 : 0;
    }

    private static int resetMobStat(CommandContext<ServerCommandSource> context, String statName) throws CommandSyntaxException {
        net.minecraft.entity.Entity entity = EntityArgumentType.getEntity(context, "entity");

        if (!(entity instanceof LivingEntity livingEntity)) {
            context.getSource().sendError(Text.literal("Target is not a living entity"));
            return 0;
        }

        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(livingEntity);
        if (levelComponent == null) {
            context.getSource().sendError(Text.literal("No level component found for entity"));
            return 0;
        }

        if (levelComponent.isPlayer()) {
            context.getSource().sendError(Text.literal("Cannot modify player stats with this command. Use the player stat commands instead."));
            return 0;
        }

        EntityStatManager statManager = levelComponent.getEntityStatManager();
        if (statManager == null) {
            context.getSource().sendError(Text.literal("No stat manager found for entity"));
            return 0;
        }

        // Use batch operation to reset stat and apply effects with single sync
        final boolean[] success = {false};
        levelComponent.performBatchUpdate(() -> {
            success[0] = setStatValue(statManager, statName, 1); // Reset to base value of 1
            if (success[0]) {
                levelComponent.refreshAllStatEffectsInternal();
            }
        });

        if (success[0]) {
            Text message = Text.literal(String.format(
                    "§6Reset %s's %s to §f1",
                    livingEntity.getName().getString(), statName
            ));
            context.getSource().sendFeedback(() -> message, true);
        } else {
            context.getSource().sendError(Text.literal("Failed to reset stat: " + statName));
        }

        return success[0] ? 1 : 0;
    }

    private static int resetAllMobStats(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        net.minecraft.entity.Entity entity = EntityArgumentType.getEntity(context, "entity");

        if (!(entity instanceof LivingEntity livingEntity)) {
            context.getSource().sendError(Text.literal("Target is not a living entity"));
            return 0;
        }

        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(livingEntity);
        if (levelComponent == null) {
            context.getSource().sendError(Text.literal("No level component found for entity"));
            return 0;
        }

        if (levelComponent.isPlayer()) {
            context.getSource().sendError(Text.literal("Cannot modify player stats with this command. Use the player stat commands instead."));
            return 0;
        }

        EntityStatManager statManager = levelComponent.getEntityStatManager();
        if (statManager == null) {
            context.getSource().sendError(Text.literal("No stat manager found for entity"));
            return 0;
        }

        // Use batch operation to reset all stats and apply effects with single sync
        levelComponent.performBatchUpdate(() -> {
            // Reset all stats to base value of 1
            setStatValue(statManager, "strength", 1);
            setStatValue(statManager, "agility", 1);
            setStatValue(statManager, "vitality", 1);
            setStatValue(statManager, "intelligence", 1);
            setStatValue(statManager, "dexterity", 1);
            setStatValue(statManager, "luck", 1);

            // Apply effects after all stats are reset
            levelComponent.refreshAllStatEffectsInternal();
        });

        Text message = Text.literal(String.format(
                "§6Reset all stats for %s to base values",
                livingEntity.getName().getString()
        ));
        context.getSource().sendFeedback(() -> message, true);

        return 1;
    }

    private static boolean setStatValue(EntityStatManager statManager, String statName, int value) {
        return switch (statName.toLowerCase()) {
            case "strength" -> {
                if (statManager.getStrength() != null) {
                    statManager.getStrength().setValue(value);
                    yield true;
                }
                yield false;
            }
            case "agility" -> {
                if (statManager.getAgility() != null) {
                    statManager.getAgility().setValue(value);
                    yield true;
                }
                yield false;
            }
            case "vitality" -> {
                if (statManager.getVitality() != null) {
                    statManager.getVitality().setValue(value);
                    yield true;
                }
                yield false;
            }
            case "intelligence" -> {
                if (statManager.getIntelligence() != null) {
                    statManager.getIntelligence().setValue(value);
                    yield true;
                }
                yield false;
            }
            case "dexterity" -> {
                if (statManager.getDexterity() != null) {
                    statManager.getDexterity().setValue(value);
                    yield true;
                }
                yield false;
            }
            case "luck" -> {
                if (statManager.getLuck() != null) {
                    statManager.getLuck().setValue(value);
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    private static void showEntityStats(ServerCommandSource source, LivingEntity entity) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(entity);

        if (levelComponent == null) {
            source.sendError(Text.literal("No level component found for " + entity.getName().getString()));
            return;
        }

        EntityStatManager statManager = levelComponent.getEntityStatManager();
        Text message;

        if (levelComponent.isPlayer()) {
            message = Text.literal(String.format(
                    "§6=== PLAYER STATS: %s ===\n" +
                            "§7Level: §f%d\n" +
                            "§7Experience: §f%d\n" +
                            "§7Benefits: §f%d\n" +
                            "§7Stats (Max: %d):\n" +
                            "  §7STR: §f%d §7| AGI: §f%d §7| VIT: §f%d\n" +
                            "  §7INT: §f%d §7| DEX: §f%d §7| LUK: §f%d\n" +
                            "§c⚠ Use '/dominatus stats' commands for players!",
                    entity.getName().getString(),
                    levelComponent.getLevel(),
                    levelComponent.getExperience(),
                    levelComponent.getAvailableBenefits(),
                    Stat.MAX_STAT_VALUE,
                    statManager != null && statManager.getStrength() != null ? statManager.getStrength().getValue() : 0,
                    statManager != null && statManager.getAgility() != null ? statManager.getAgility().getValue() : 0,
                    statManager != null && statManager.getVitality() != null ? statManager.getVitality().getValue() : 0,
                    statManager != null && statManager.getIntelligence() != null ? statManager.getIntelligence().getValue() : 0,
                    statManager != null && statManager.getDexterity() != null ? statManager.getDexterity().getValue() : 0,
                    statManager != null && statManager.getLuck() != null ? statManager.getLuck().getValue() : 0
            ));
        } else {
            if (statManager != null) {
                message = Text.literal(String.format(
                        "§6=== MOB STATS: %s ===\n" +
                                "§7Level: §f%d\n" +
                                "§7Experience: §f%d\n" +
                                "§7Stats (Max: %d):\n" +
                                "  §7STR: §f%d §7| AGI: §f%d §7| VIT: §f%d\n" +
                                "  §7INT: §f%d §7| DEX: §f%d §7| LUK: §f%d",
                        entity.getName().getString(),
                        levelComponent.getLevel(),
                        levelComponent.getExperience(),
                        Stat.MAX_STAT_VALUE,
                        statManager.getStrength() != null ? statManager.getStrength().getValue() : 0,
                        statManager.getAgility() != null ? statManager.getAgility().getValue() : 0,
                        statManager.getVitality() != null ? statManager.getVitality().getValue() : 0,
                        statManager.getIntelligence() != null ? statManager.getIntelligence().getValue() : 0,
                        statManager.getDexterity() != null ? statManager.getDexterity().getValue() : 0,
                        statManager.getLuck() != null ? statManager.getLuck().getValue() : 0
                ));
            } else {
                message = Text.literal(String.format(
                        "§c=== NO STAT MANAGER: %s ===\n" +
                                "§7Level: §f%d\n" +
                                "§7Experience: §f%d\n" +
                                "§cNo stat manager found for this entity.",
                        entity.getName().getString(),
                        levelComponent.getLevel(),
                        levelComponent.getExperience()
                ));
            }
        }

        source.sendFeedback(() -> message, false);
    }
}