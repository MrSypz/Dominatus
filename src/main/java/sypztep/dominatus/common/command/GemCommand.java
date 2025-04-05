package sypztep.dominatus.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModItems;
import sypztep.dominatus.common.reloadlistener.GemItemDataReloadListener;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GemCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("givegem")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("type", IdentifierArgumentType.identifier())
                                        .suggests((context, builder) -> suggestGemTypes(builder))
                                        .executes(context -> giveGem(
                                                context.getSource(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                IdentifierArgumentType.getIdentifier(context, "type")
                                        ))))
        );
    }

    private static CompletableFuture<Suggestions> suggestGemTypes(SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();

        // Get all available gem types from the resource loader
        GemItemDataReloadListener.getGemTypes().forEach(gemType -> {
            String suggestion = gemType.toString();
            if (suggestion.toLowerCase().contains(remaining)) {
                // Add tooltip description for each gem type
                Optional<GemComponent> gemComponent = GemItemDataReloadListener.getGemType(gemType);
                if (gemComponent.isPresent()) {
                    StringBuilder tooltip = new StringBuilder();
                    gemComponent.get().attributeModifiers().forEach((attribute, modifier) -> {
                        String attrName = attribute.getPath().substring(attribute.getPath().lastIndexOf('.') + 1);
                        String operation = switch (modifier.operation()) {
                            case ADD_VALUE -> "+";
                            case ADD_MULTIPLIED_BASE -> "×";
                            case ADD_MULTIPLIED_TOTAL -> "%";
                        };
                        tooltip.append(String.format("%s%s%.1f ", operation, attrName, modifier.value()));
                    });
                    builder.suggest(suggestion);
                } else {
                    builder.suggest(suggestion);
                }
            }
        });

        return builder.buildFuture();
    }

    private static int giveGem(ServerCommandSource source, ServerPlayerEntity player, Identifier type) {
        ItemStack gemStack = ModItems.createGem(type);

        if (!gemStack.contains(ModDataComponents.GEM)) {
            source.sendError(Text.literal("Unknown gem type: " + type));
            return 0;
        }

        player.getInventory().insertStack(gemStack);

        GemComponent.fromStack(gemStack).ifPresent(gem -> {
            source.sendFeedback(() -> {
                MutableText feedback = Text.literal("Gave ")
                        .append(Text.literal(type.getPath()).formatted(Formatting.GOLD))
                        .append(" gem to ")
                        .append(player.getName())
                        .append(".\nAttributes:");

                gem.attributeModifiers().forEach((attribute, modifier) -> {
                    String operation = switch (modifier.operation()) {
                        case ADD_VALUE -> "+";
                        case ADD_MULTIPLIED_BASE -> "×";
                        case ADD_MULTIPLIED_TOTAL -> "%";
                    };

                    feedback.append("\n")
                            .append(Text.literal(String.format("%s%.1f ", operation, modifier.value()))
                                    .formatted(Formatting.AQUA))
                            .append(Text.translatable(Registries.ATTRIBUTE.get(attribute)
                                            .getTranslationKey())
                                    .formatted(Formatting.GRAY));
                });

                return feedback;
            }, true);
        });

        return 1;
    }
}