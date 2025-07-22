package sypztep.dominatus.mixin.vanilla.projectileclip;

import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.dominatus.common.api.entity.DominatusProjectileEvents;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {
    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    private void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        TridentEntity trident = (TridentEntity) (Object) this;
        if (!DominatusProjectileEvents.ALLOW_PROJECTILE_HIT.invoker()
                .allowHit(trident, entityHitResult.getEntity(), entityHitResult)) {
            ci.cancel(); // Trident clips through!
        }
    }
}
