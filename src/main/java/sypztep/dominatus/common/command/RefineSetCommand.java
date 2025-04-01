package sypztep.dominatus.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.util.refinesystem.RefinementManager;

public class RefineSetCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("refine")
                .then(CommandManager.literal("set").requires(source -> source.hasPermissionLevel(3))  // Requires permission level 2
                        .then(CommandManager.argument("refinelevel", IntegerArgumentType.integer())
                                .executes(context -> execute(context, IntegerArgumentType.getInteger(context, "refinelevel"))))));
    }

    private static int execute(CommandContext<ServerCommandSource> context, int refinelevel) {
        ServerPlayerEntity player = context.getSource().getPlayer();

        if (player != null) {
            ItemStack handStack = player.getMainHandStack();
            DominatusItemEntry itemData = RefinementManager.getDominatusEntry(handStack);
            if (itemData == null) {
                player.sendMessage(Text.literal("Invalid Item Data").formatted(Formatting.RED), false);
                return 0;
            }

            RefinementManager.initializeRefinement(handStack); // Ensure item data is initialized

            int maxLvl = itemData.maxLvl();

            if (refinelevel < 0 || refinelevel > maxLvl) {
                player.sendMessage(Text.literal("Invalid refine level. Must be between 0 and " + maxLvl).formatted(Formatting.RED), false);
                return 0;
            }
            RefinementManager.applyRefinement(handStack, itemData, refinelevel);

            player.sendMessage(Text.literal("Refine level set to " + refinelevel), false);
        }

        return 1;
    }
}
