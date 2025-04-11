package sypztep.dominatus.common.util.gemsystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.init.ModItems;
import sypztep.dominatus.common.reloadlistener.GemItemDataReloadListener;

public final class GemManager {
    public static ItemStack createGem(Identifier type) {
        ItemStack stack = new ItemStack(ModItems.GEM);
        GemItemDataReloadListener.getGemType(type).ifPresent(component -> GemComponent.apply(stack, component));
        return stack;
    }

    public static boolean consumeGem(PlayerEntity player, ItemStack stack) {
        if (player.getWorld().isClient()) return false;

        return GemComponent.fromStack(stack).map(gem -> {
            if (!GemDataComponent.isInventoryFull(player)) {
                if (GemDataComponent.addToInventory(player, gem)) {
                    stack.decrement(1);
                    GemDataComponent gemData = GemDataComponent.get(player);
                    int currentCount = gemData.getGemInventory().size();
                    player.sendMessage(
                            Text.literal("✦ ")
                                    .formatted(Formatting.GOLD)
                                    .append(Text.translatable("item.dominatus.gem." + gem.type().getPath())
                                            .formatted(Formatting.YELLOW))
                                    .append(Text.literal(" added to inventory! ")
                                            .formatted(Formatting.GREEN))
                                    .append(Text.literal("[" + currentCount + "/" + GemDataComponent.getMaxInventorySize(player) + "]")
                                            .formatted(Formatting.AQUA)),
                            true
                    );
                    return true;
                }
            } else {
                player.sendMessage(
                        Text.literal("✖ ")
                                .formatted(Formatting.RED)
                                .append(Text.literal("Cannot add ")
                                        .formatted(Formatting.GRAY))
                                .append(Text.translatable("item.dominatus.gem." + gem.type().getPath())
                                        .formatted(Formatting.YELLOW))
                                .append(Text.literal(" - Inventory is full!")
                                        .formatted(Formatting.RED)),
                        true
                );
            }
            return false;
        }).orElse(false);
    }
}