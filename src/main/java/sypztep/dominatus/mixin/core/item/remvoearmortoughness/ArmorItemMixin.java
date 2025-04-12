package sypztep.dominatus.mixin.core.item.remvoearmortoughness;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.common.init.ModEntityAttributes;

import java.util.ArrayList;
import java.util.List;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true)
    private void modifyAttributeModifiers(CallbackInfoReturnable<AttributeModifiersComponent> cir) {
        ArmorItem self = (ArmorItem) (Object) this;
        AttributeModifiersComponent originalModifiers = cir.getReturnValue();
        List<AttributeModifiersComponent.Entry> entries = new ArrayList<>();

        boolean damageReductionAdded = false;

        for (AttributeModifiersComponent.Entry entry : originalModifiers.modifiers()) {
            if (entry.attribute() == EntityAttributes.GENERIC_ARMOR_TOUGHNESS) {
                continue;
            }

            // If we encounter knockback resistance and haven't added damage reduction yet, add it first
            if (entry.attribute() == EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE && !damageReductionAdded) {
                addDamageReduction(entries, self);
                damageReductionAdded = true;
            }

            // Add the current entry
            entries.add(entry);
        }

        // If we haven't added damage reduction yet (no knockback resistance was present), add it now
        if (!damageReductionAdded) {
            addDamageReduction(entries, self);
        }

        // Build new AttributeModifiersComponent
        AttributeModifiersComponent newModifiers = new AttributeModifiersComponent(entries, originalModifiers.showInTooltip());
        cir.setReturnValue(newModifiers);
    }

    @Unique
    private void addDamageReduction(List<AttributeModifiersComponent.Entry> entries, ArmorItem self) {
        float toughness = self.getMaterial().value().toughness();
        if (toughness > 0) {
            float drValue = toughness * 2.0F; // Convert toughness to DR (e.g., 4 -> 8)
            Identifier id = Identifier.ofVanilla("armor." + self.getType().getName());
            entries.add(new AttributeModifiersComponent.Entry(
                    ModEntityAttributes.DAMAGE_REDUCTION,
                    new EntityAttributeModifier(id, drValue, EntityAttributeModifier.Operation.ADD_VALUE),
                    AttributeModifierSlot.forEquipmentSlot(self.getType().getEquipmentSlot())
            ));
        }
    }
}
