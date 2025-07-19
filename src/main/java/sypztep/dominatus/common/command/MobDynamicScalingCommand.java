package sypztep.dominatus.common.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.util.PlayerEntityUtils;

import java.util.List;

public class MobDynamicScalingCommand {

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("scaling")
                .requires(source -> source.hasPermissionLevel(2))
                // /dominatus scaling info [pos] - Show scaling info for area
                .then(CommandManager.literal("info")
                        .executes(MobDynamicScalingCommand::showScalingInfoAtPlayer)
                        .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                .executes(MobDynamicScalingCommand::showScalingInfoAtPos)
                        )
                )
                // /dominatus scaling config - Show current config
                .then(CommandManager.literal("config")
                        .executes(MobDynamicScalingCommand::showConfig)
                        .then(CommandManager.literal("enable")
                                .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                        .executes(MobDynamicScalingCommand::setEnabled)
                                )
                        )
                        .then(CommandManager.literal("radius")
                                .then(CommandManager.argument("chunks", IntegerArgumentType.integer(1, 10))
                                        .executes(MobDynamicScalingCommand::setChunkRadius)
                                )
                        )
                        .then(CommandManager.literal("factor")
                                .then(CommandManager.argument("percent", IntegerArgumentType.integer(0, 500))
                                        .executes(MobDynamicScalingCommand::setScalingFactor)
                                )
                        )
                        .then(CommandManager.literal("maxbonus")
                                .then(CommandManager.argument("levels", IntegerArgumentType.integer(1, 200))
                                        .executes(MobDynamicScalingCommand::setMaxBonus)
                                )
                        )
                );
    }

    private static int showScalingInfoAtPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        return showScalingInfo(context.getSource(), player.getBlockPos(), player.getServerWorld());
    }

    private static int showScalingInfoAtPos(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        ServerWorld world = context.getSource().getWorld();
        return showScalingInfo(context.getSource(), pos, world);
    }

    private static int showScalingInfo(ServerCommandSource source, BlockPos pos, ServerWorld world) {
        List<PlayerEntity> nearbyPlayers = PlayerEntityUtils.getPlayersInChunk(
                world, pos.getX() >> 4, pos.getZ() >> 4, ModConfig.mobScalingChunkRadius);

        int totalLevel = 0;
        int playerCount = 0;
        StringBuilder playerList = new StringBuilder();

        for (PlayerEntity player : nearbyPlayers) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(serverPlayer);
                if (levelComponent != null) {
                    int level = levelComponent.getLevel();
                    totalLevel += level;
                    playerCount++;
                    if (playerList.length() > 0) playerList.append(", ");
                    playerList.append(player.getName().getString()).append("(").append(level).append(")");
                }
            }
        }

        int meanLevel = playerCount > 0 ? totalLevel / playerCount : 0;
        int levelBonus = Math.round(meanLevel * ModConfig.getMobScalingFactor());
        levelBonus = Math.min(levelBonus, ModConfig.maxMobLevelBonus);

        Text message = Text.literal(String.format(
                "§6=== SCALING INFO FOR AREA ===\n" +
                        "§7Position: §f%d, %d, %d\n" +
                        "§7Chunk: §f%d, %d\n" +
                        "§7Search Radius: §f%d chunks\n" +
                        "§7Players Found: §f%d\n" +
                        "§7Player Details: §f%s\n" +
                        "§7Average Level: §f%d\n" +
                        "§7Scaling Factor: §f%.0f%%\n" +
                        "§7Level Bonus: §f%d §7(capped at %d)\n" +
                        "§7Dynamic Scaling: §f%s",
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() >> 4, pos.getZ() >> 4,
                ModConfig.mobScalingChunkRadius,
                playerCount,
                !playerList.isEmpty() ? playerList.toString() : "None",
                meanLevel,
                ModConfig.getMobScalingFactor() * 100,
                levelBonus, ModConfig.maxMobLevelBonus,
                ModConfig.enableDynamicMobScaling ? "ENABLED" : "DISABLED"
        ));

        source.sendFeedback(() -> message, false);
        return 1;
    }

    private static int showConfig(CommandContext<ServerCommandSource> context) {
        Text message = Text.literal(String.format(
                "§6=== DYNAMIC MOB SCALING CONFIG ===\n" +
                        "§7Enabled: §f%s\n" +
                        "§7Chunk Radius: §f%d chunks\n" +
                        "§7Scaling Factor: §f%d%% §7(%.2f multiplier)\n" +
                        "§7Min Mob Level: §f%d\n" +
                        "§7Max Level Bonus: §f%d\n" +
                        "§7Scale Boss Mobs: §f%s\n" +
                        "§7Scale Passive Mobs: §f%s\n" +
                        "§7Block Radius Mode: §f%s",
                ModConfig.enableDynamicMobScaling ? "YES" : "NO",
                ModConfig.mobScalingChunkRadius,
                ModConfig.mobScalingFactorPercent, ModConfig.getMobScalingFactor(),
                ModConfig.minMobLevel,
                ModConfig.maxMobLevelBonus,
                ModConfig.scaleBossMobs ? "YES" : "NO",
                ModConfig.scalePassiveMobs ? "YES" : "NO",
                ModConfig.mobScalingBlockRadius > 0 ? ModConfig.mobScalingBlockRadius + " blocks" : "Disabled"
        ));

        context.getSource().sendFeedback(() -> message, false);
        return 1;
    }

    private static int setEnabled(CommandContext<ServerCommandSource> context) {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        ModConfig.enableDynamicMobScaling = enabled;

        Text message = Text.literal("§6Dynamic mob scaling " + (enabled ? "§aENABLED" : "§cDISABLED"));
        context.getSource().sendFeedback(() -> message, true);
        return 1;
    }

    private static int setChunkRadius(CommandContext<ServerCommandSource> context) {
        int radius = IntegerArgumentType.getInteger(context, "chunks");
        ModConfig.mobScalingChunkRadius = radius;

        Text message = Text.literal("§6Set chunk search radius to §f" + radius + " chunks");
        context.getSource().sendFeedback(() -> message, true);
        return 1;
    }

    private static int setScalingFactor(CommandContext<ServerCommandSource> context) {
        int percent = IntegerArgumentType.getInteger(context, "percent");
        ModConfig.mobScalingFactorPercent = percent;

        Text message = Text.literal(String.format("§6Set scaling factor to §f%d%% §7(%.2f multiplier)",
                percent, percent / 100.0f));
        context.getSource().sendFeedback(() -> message, true);
        return 1;
    }

    private static int setMaxBonus(CommandContext<ServerCommandSource> context) {
        int levels = IntegerArgumentType.getInteger(context, "levels");
        ModConfig.maxMobLevelBonus = levels;

        Text message = Text.literal("§6Set max level bonus to §f" + levels + " levels");
        context.getSource().sendFeedback(() -> message, true);
        return 1;
    }
}