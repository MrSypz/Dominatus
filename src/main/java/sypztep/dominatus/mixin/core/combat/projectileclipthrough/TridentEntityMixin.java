package sypztep.dominatus.mixin.core.combat.projectileclipthrough;

import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.dominatus.common.api.combat.MissingAccessor;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin {

    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/TridentEntity;deflect(Lnet/minecraft/entity/ProjectileDeflection;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Z)Z"), cancellable = true)
    private void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (entityHitResult.getEntity() instanceof MissingAccessor accessor && accessor.isMissing()) {
            ci.cancel();
        }
    }
}