package sypztep.dominatus.mixin.core.item;

import net.minecraft.component.ComponentHolder;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder{

    @Inject(method = "appendAttributeModifierTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0), cancellable = true)
    private void modifyExtraDamage(Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, CallbackInfo ci) {
        if (player == null) return;

        double d = computeDamageValue(modifier, player);
//        int extraDamage = RefineUtil.getExtraDamage(stack);
        if (modifier.idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID)) {
            textConsumer.accept(createText("dominatus.attribute", d, 5));
            ci.cancel();
        }
    }

//    @Inject(method = "appendAttributeModifierTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 1), cancellable = true)
//    private void modifyExtraProtect(Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, CallbackInfo ci) {
//        if (player == null) return;
//
//        double d = computeDamageValue(modifier, player);
//        int extraProtect = RefineUtil.getExtraProtect(stack);
//        if (stack.isIn(ItemTags.ARMOR_ENCHANTABLE) && !RefineUtil.isBroken(stack) && attribute.matches(EntityAttributes.ARMOR) && RefineUtil.getRefineLvl(stack) > 0) {
//            textConsumer.accept(createText("penomior.attribute.modifier.plus.1",attribute, d, extraProtect));
//            ci.cancel();
//        }
//    }

    @Unique
    private double computeDamageValue(EntityAttributeModifier modifier, PlayerEntity player) {
        double baseValue = player.getAttributeBaseValue(EntityAttributes.ATTACK_DAMAGE) + modifier.value();
        return modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE ||
                modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ? baseValue * 100.0
                : baseValue;
    }

    @Unique
    private Text createText(String key, double value, int extra) {
        return ScreenTexts.space()
                .append(Text.translatable(
                        key,
                        AttributeModifiersComponent.DECIMAL_FORMAT.format(value),
                        "+" + AttributeModifiersComponent.DECIMAL_FORMAT.format(extra)
                ).formatted(Formatting.DARK_GREEN));
    }
    @Unique
    private Text createText(String key, RegistryEntry<EntityAttribute> attribute, double value, int extra) {
        return Text.translatable(
                key,
                AttributeModifiersComponent.DECIMAL_FORMAT.format(value),
                "+" + AttributeModifiersComponent.DECIMAL_FORMAT.format(extra)
        ).formatted(attribute.value().getFormatting(true));
    }
}
