package sypztep.dominatus.mixin.core.registry.attribute;

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
            case "max_health" -> info.setReturnValue(
                    Registry.registerReference(Registries.ATTRIBUTE, Identifier.ofVanilla(id), new ClampedEntityAttribute("attribute.name.max_health", 20.0, 1.0, 10000000.0).setTracked(true)));
            case "armor" -> info.setReturnValue(
                    Registry.registerReference(Registries.ATTRIBUTE, Identifier.ofVanilla(id), new ClampedEntityAttribute("attribute.name.armor", 0.0, 0.0, 1000000.0).setTracked(true)));
            case "attack_damage" -> info.setReturnValue(
                    Registry.registerReference(Registries.ATTRIBUTE, Identifier.ofVanilla(id), new ClampedEntityAttribute("attribute.name.attack_damage", 2.0, 0.0, 1000000.0)));
        }
    }
}
