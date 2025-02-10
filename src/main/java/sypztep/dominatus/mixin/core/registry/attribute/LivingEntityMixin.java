package sypztep.dominatus.mixin.core.registry.attribute;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.common.init.ModEntityAttributes;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "createLivingAttributes", at = @At("RETURN"), cancellable = true)
    private static void registryExtraStats(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        DefaultAttributeContainer.Builder builder = cir.getReturnValue();
        builder
                .add(ModEntityAttributes.GENERIC_ACCURACY)
                .add(ModEntityAttributes.GENERIC_EVASION)
                .add(ModEntityAttributes.GENERIC_CRIT_CHANCE)
                .add(ModEntityAttributes.GENERIC_CRIT_DAMAGE);
        cir.setReturnValue(builder);
    }
}