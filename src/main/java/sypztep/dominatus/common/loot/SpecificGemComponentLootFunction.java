package sypztep.dominatus.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.init.ModLootableModify;
import sypztep.dominatus.common.reloadlistener.GemItemDataReloadListener;

public class SpecificGemComponentLootFunction implements LootFunction {
    public static final MapCodec<SpecificGemComponentLootFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Identifier.CODEC.fieldOf("gem_type").forGetter(func -> func.gemType)
            ).apply(instance, SpecificGemComponentLootFunction::new)
    );

    private final Identifier gemType;

    public SpecificGemComponentLootFunction(Identifier gemType) {
        this.gemType = gemType;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        GemComponent gemComponent = GemItemDataReloadListener.getGemType(gemType).orElse(null);
        if (gemComponent != null) {
            GemComponent.apply(stack, gemComponent);
        } else {
            Dominatus.LOGGER.warn("No GemComponent found for type: {}", gemType);
        }
        return stack;
    }

    @Override
    public LootFunctionType<? extends LootFunction> getType() {
        return ModLootableModify.SPECIFIC_GEM_FUNCTION_TYPE;
    }

    public static class Builder implements LootFunction.Builder {
        private final Identifier gemType;

        public Builder(Identifier gemType) {
            this.gemType = gemType;
        }

        @Override
        public LootFunction build() {
            return new SpecificGemComponentLootFunction(gemType);
        }
    }
}