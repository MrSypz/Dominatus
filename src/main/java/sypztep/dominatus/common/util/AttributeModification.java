package sypztep.dominatus.common.util;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.function.ToDoubleFunction;

public record AttributeModification(RegistryEntry<EntityAttribute> attribute, Identifier modifierId,
                                    EntityAttributeModifier.Operation operation,
                                    ToDoubleFunction<Double> effectFunction) {
}