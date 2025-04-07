package sypztep.dominatus.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.init.ModDataComponents;

import java.util.Map;
import java.util.Optional;

public record GemComponent(
        Identifier type,
        Map<Identifier, EntityAttributeModifier> attributeModifiers,
        int maxPresets
) {
    public static final MapCodec<GemComponent> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(GemComponent::type),
                    Codec.unboundedMap(
                            Identifier.CODEC,
                            EntityAttributeModifier.CODEC
                    ).fieldOf("attributes").forGetter(GemComponent::attributeModifiers),
                    Codec.INT.optionalFieldOf("max_presets", 8).forGetter(GemComponent::maxPresets)
            ).apply(instance, GemComponent::new)
    );

    public static final Codec<GemComponent> CODEC = MAP_CODEC.codec();

    public static Optional<GemComponent> fromStack(ItemStack stack) {
        return Optional.ofNullable(stack.get(ModDataComponents.GEM));
    }

    public static void apply(ItemStack stack, GemComponent component) {
        stack.set(ModDataComponents.GEM, component);
    }
}