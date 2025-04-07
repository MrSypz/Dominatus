package sypztep.dominatus.common.util.gemsystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
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

    public static void consumeGem(PlayerEntity player, ItemStack stack) {
        if (player.getWorld().isClient()) return;

        GemComponent.fromStack(stack).ifPresent(gem -> {
            if (!GemDataComponent.isInventoryFull(player)) {
                if (GemDataComponent.addToInventory(player, gem)) {
                    stack.decrement(1);
                }
            } else {
                player.sendMessage(Text.literal("Gem inventory is full!"), true);
            }
        });
    }
}