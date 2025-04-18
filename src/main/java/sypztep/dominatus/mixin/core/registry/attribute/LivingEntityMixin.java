package sypztep.dominatus.mixin.core.registry.attribute;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.common.init.ModEntityAttributes;

@Mixin(LivingEntity.class)
public class LivingEntityMixin { // #4
    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void registryExtraStats(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        for (RegistryEntry<EntityAttribute> entry : ModEntityAttributes.ENTRIES) {
            cir.getReturnValue().add(entry);
        }
    }
}
