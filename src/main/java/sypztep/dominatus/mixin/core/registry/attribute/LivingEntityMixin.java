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
                .add(ModEntityAttributes.ACCURACY)
                .add(ModEntityAttributes.EVASION)
                .add(ModEntityAttributes.CRIT_CHANCE)
                .add(ModEntityAttributes.CRIT_DAMAGE)
                .add(ModEntityAttributes.BACK_ATTACK)
                .add(ModEntityAttributes.AIR_ATTACK)
                .add(ModEntityAttributes.DOWN_ATTACK)
                .add(ModEntityAttributes.PLAYER_VERS_ENTITY_DAMAGE)
                .add(ModEntityAttributes.PLAYER_VERS_PLAYER_DAMAGE);
        cir.setReturnValue(builder);
    }
}