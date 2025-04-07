package sypztep.dominatus.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.util.refinesystem.RefinementManager;

public class RefineSetCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("refine")
                .then(CommandManager.literal("set").requires(source -> source.hasPermissionLevel(3))
                        .then(CommandManager.argument("refinelevel", IntegerArgumentType.integer())
                                .executes(context -> execute(context, IntegerArgumentType.getInteger(context, "refinelevel"))))));
    }

    private static int execute(CommandContext<ServerCommandSource> context, int refinelevel) {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (player != null) {
            ItemStack handStack = player.getMainHandStack();

            if (handStack.isEmpty()) {
                context.getSource().sendError(Text.literal("⚠ Please hold an item in your main hand!")
                        .formatted(Formatting.RED));
                return 0;
            }

            DominatusItemEntry itemData = RefinementManager.getDominatusEntry(handStack);
            if (itemData == null) {
                MutableText errorMessage = Text.literal("")
                        .append(Text.literal("✖ This item cannot be refined!\n").formatted(Formatting.RED))
                        .append(Text.literal("Item: ").formatted(Formatting.GRAY))
                        .append(Text.literal(handStack.getItem().getName().getString()).formatted(Formatting.YELLOW))
                        .append(Text.literal("\nℹ Required: ").formatted(Formatting.GRAY))
                        .append(Text.literal("Add item data in datapack:\n").formatted(Formatting.YELLOW))
                        .append(Text.literal("Path: ").formatted(Formatting.GRAY))
                        .append(Text.literal("data/<your_datapack_name>/refine/" + getDatapackPath(handStack)).formatted(Formatting.AQUA));

                context.getSource().sendError(errorMessage);
                return 0;
            }

            RefinementManager.initializeRefinement(handStack);
            int maxLvl = itemData.maxLvl();

            if (refinelevel < 0 || refinelevel > maxLvl) {
                MutableText errorMessage = Text.literal("")
                        .append(Text.literal("✖ Invalid refine level!\n").formatted(Formatting.RED))
                        .append(Text.literal("Item: ").formatted(Formatting.GRAY))
                        .append(Text.literal(handStack.getItem().getName().getString()).formatted(Formatting.YELLOW))
                        .append(Text.literal("\nValid Range: ").formatted(Formatting.GRAY))
                        .append(Text.literal("0 - " + maxLvl).formatted(Formatting.GREEN));

                context.getSource().sendError(errorMessage);
                return 0;
            }

            RefinementManager.applyRefinement(handStack, itemData, refinelevel);

            MutableText successMessage = Text.literal("")
                    .append(Text.literal("✔ Refinement Success!\n").formatted(Formatting.GREEN))
                    .append(Text.literal("Item: ").formatted(Formatting.GRAY))
                    .append(Text.literal(handStack.getItem().getName().getString()).formatted(Formatting.YELLOW))
                    .append(Text.literal("\nNew Level: ").formatted(Formatting.GRAY))
                    .append(Text.literal("+" + refinelevel).formatted(Formatting.GREEN))
                    .append(Text.literal(" / +" + maxLvl).formatted(Formatting.GRAY));

            context.getSource().sendFeedback(() -> successMessage, false);
        }
        return 1;
    }

    private static String getDatapackPath(ItemStack stack) {
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return id.getNamespace() + "/" + id.getPath() + ".json";
    }
}
