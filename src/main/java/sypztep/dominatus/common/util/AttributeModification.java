package sypztep.dominatus.common.util;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.function.ToDoubleFunction;

public record AttributeModification(RegistryEntry<EntityAttribute> attribute, Identifier modifierId,
                                    EntityAttributeModifier.Operation operation,
                                    ToDoubleFunction<Double> effectFunction) {

    public static AttributeModification addValue(RegistryEntry<EntityAttribute> attribute,
                                                 Identifier modifierId,
                                                 ToDoubleFunction<Double> effectFunction) {
        return new AttributeModification(attribute, modifierId, EntityAttributeModifier.Operation.ADD_VALUE, effectFunction);
    }

    public static AttributeModification multiplyBase(RegistryEntry<EntityAttribute> attribute,
                                                     Identifier modifierId,
                                                     ToDoubleFunction<Double> effectFunction) {
        return new AttributeModification(attribute, modifierId, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE, effectFunction);
    }

    public static AttributeModification multiplyTotal(RegistryEntry<EntityAttribute> attribute,
                                                      Identifier modifierId,
                                                      ToDoubleFunction<Double> effectFunction) {
        return new AttributeModification(attribute, modifierId, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, effectFunction);
    }
}