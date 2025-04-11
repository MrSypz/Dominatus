package sypztep.dominatus.common.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModLootableModify;
import sypztep.dominatus.common.reloadlistener.GemItemDataReloadListener;

import java.util.Collection;
import java.util.Random;

public class RandomGemComponentLootFunction implements LootFunction {
    public static final MapCodec<RandomGemComponentLootFunction> CODEC =
            MapCodec.unit(RandomGemComponentLootFunction::new);

    private RandomGemComponentLootFunction() {}

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        Collection<Identifier> gemTypes = GemItemDataReloadListener.getGemTypes();
        if (!gemTypes.isEmpty()) {
            Identifier randomGemType = gemTypes.stream()
                    .skip(new Random().nextInt(gemTypes.size()))
                    .findFirst()
                    .orElse(Dominatus.id("accuracy")); // Fallback to accuracy
            GemItemDataReloadListener.getGemType(randomGemType).ifPresent(gemComponent -> stack.set(ModDataComponents.GEM, gemComponent));
        }
        return stack;
    }

    @Override
    public LootFunctionType<? extends LootFunction> getType() {
        return ModLootableModify.RANDOM_GEM_FUNCTION_TYPE; // Return the registered type
    }

    public static class Builder implements LootFunction.Builder {
        @Override
        public LootFunction build() {
            return new RandomGemComponentLootFunction();
        }
    }
}