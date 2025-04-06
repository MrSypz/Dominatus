package sypztep.dominatus.common.util.gemsystem;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.init.ModItems;
import sypztep.dominatus.common.reloadlistener.GemItemDataReloadListener;

public final class GemManager {
    public static ItemStack createGem(Identifier type) {
        ItemStack stack = new ItemStack(ModItems.GEM);
        GemItemDataReloadListener.getGemType(type).ifPresent(component -> GemComponent.apply(stack, component));
        return stack;
    }
}
