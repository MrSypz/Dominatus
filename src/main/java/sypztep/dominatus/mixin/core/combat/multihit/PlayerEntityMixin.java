package sypztep.dominatus.mixin.core.combat.multihit;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.common.payload.MultiHitPayloadC2S;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void injectMultiHit(Entity target, CallbackInfo ci) {
        if (!ModConfig.multihit) return;
        PlayerEntity player = (PlayerEntity) (Object) this;
        MultiHitPayloadC2S.send(player, target, 2);
    }
}
