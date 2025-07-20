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
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                                .executes(ctx -> setMobStat(ctx, "strength"))
                                        )
                                )
                                .then(CommandManager.literal("agility")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                                .executes(ctx -> setMobStat(ctx, "agility"))
                                        )
                                )
                                .then(CommandManager.literal("vitality")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                                .executes(ctx -> setMobStat(ctx, "vitality"))
                                        )
                                )
                                .then(CommandManager.literal("intelligence")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                                .executes(ctx -> setMobStat(ctx, "intelligence"))
                                        )
                                )
                                .then(CommandManager.literal("dexterity")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                                .executes(ctx -> setMobStat(ctx, "dexterity"))
                                        )
                                )
                                .then(CommandManager.literal("luck")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                                .executes(ctx -> setMobStat(ctx, "luck"))
                                        )
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

        boolean success = setStatValue(statManager, statName, value);
        if (success) {
            levelComponent.applyAllStatEffects();
            levelComponent.sync();

            Text message = Text.literal(String.format(
                    "§6Set %s's %s to §f%d",
                    livingEntity.getName().getString(), statName, value
            ));
            context.getSource().sendFeedback(() -> message, true);
        } else {
            context.getSource().sendError(Text.literal("Failed to set stat: " + statName));
        }

        return success ? 1 : 0;
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
                            "§7Stats:\n" +
                            "  §7STR: §f%d §7| AGI: §f%d §7| VIT: §f%d\n" +
                            "  §7INT: §f%d §7| DEX: §f%d §7| LUK: §f%d",
                    entity.getName().getString(),
                    levelComponent.getLevel(),
                    levelComponent.getExperience(),
                    levelComponent.getAvailableBenefits(),
                    statManager.getStrength() != null ? statManager.getStrength().getValue() : 0,
                    statManager.getAgility() != null ? statManager.getAgility().getValue() : 0,
                    statManager.getVitality() != null ? statManager.getVitality().getValue() : 0,
                    statManager.getIntelligence() != null ? statManager.getIntelligence().getValue() : 0,
                    statManager.getDexterity() != null ? statManager.getDexterity().getValue() : 0,
                    statManager.getLuck() != null ? statManager.getLuck().getValue() : 0
            ));
        } else {
            if (statManager != null) {
                message = Text.literal(String.format(
                        "§6=== MOB STATS: %s ===\n" +
                                "§7Level: §f%d\n" +
                                "§7Experience: §f%d\n" +
                                "§7Stats:\n" +
                                "  §7STR: §f%d §7| AGI: §f%d §7| VIT: §f%d\n" +
                                "  §7INT: §f%d §7| DEX: §f%d §7| LUK: §f%d",
                        entity.getName().getString(),
                        levelComponent.getLevel(),
                        levelComponent.getExperience(),
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