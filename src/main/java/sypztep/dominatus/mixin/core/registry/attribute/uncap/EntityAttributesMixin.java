package sypztep.dominatus.mixin.core.registry.attribute.uncap;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAttributes.class)
public class EntityAttributesMixin {
    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void maxRange(String id, EntityAttribute attribute, CallbackInfoReturnable<RegistryEntry<EntityAttribute>> info) {
        switch (id) {
            case "generic.max_health" -> info.setReturnValue(
                    Registry.registerReference(Registries.ATTRIBUTE, Identifier.ofVanilla(id), new ClampedEntityAttribute("attribute.name.generic.max_health", 20.0, 1.0, 10000000.0).setTracked(true)));
            case "generic.armor" -> info.setReturnValue(
                    Registry.registerReference(Registries.ATTRIBUTE, Identifier.ofVanilla(id), new ClampedEntityAttribute("attribute.name.generic.armor", 0.0, 0.0, 1000000.0).setTracked(true)));
            case "generic.attack_damage" -> info.setReturnValue(
                    Registry.registerReference(Registries.ATTRIBUTE, Identifier.ofVanilla(id), new ClampedEntityAttribute("attribute.name.generic.attack_damage", 2.0, 0.0, 1000000.0)));
        }
    }
}
