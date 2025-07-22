package sypztep.dominatus.mixin.vanilla.projectileclip;

import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.dominatus.common.api.entity.DominatusProjectileEvents;

@Mixin(SnowballEntity.class)
public class SnowballEntityMixin {
    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    private void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        SnowballEntity snowballEntity = (SnowballEntity) (Object) this;
        if (!DominatusProjectileEvents.ALLOW_PROJECTILE_HIT.invoker()
                .allowHit(snowballEntity, entityHitResult.getEntity(), entityHitResult)) {
            ci.cancel();
        }
    }
}
