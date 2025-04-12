package sypztep.dominatus.mixin.core.item.remvoearmortoughness;

import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true)
    private void removeToughnessModifiers(CallbackInfoReturnable<AttributeModifiersComponent> cir) {
        AttributeModifiersComponent modifiers = cir.getReturnValue();
        List<AttributeModifiersComponent.Entry> filteredEntries = modifiers.modifiers()
                .stream()
                .filter(entry -> entry.attribute() != EntityAttributes.GENERIC_ARMOR_TOUGHNESS)
                .toList();
        AttributeModifiersComponent newModifiers = new AttributeModifiersComponent(filteredEntries, modifiers.showInTooltip());
        cir.setReturnValue(newModifiers);
    }
}
