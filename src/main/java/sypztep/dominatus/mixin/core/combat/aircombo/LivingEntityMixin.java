package sypztep.dominatus.mixin.core.combat.aircombo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.dominatus.common.util.combatsystem.CombatUtil;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
    private void modifyAirKnockback(double strength, double x, double z, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (CombatUtil.isAirBorne(entity)) {
            Vec3d velocity = entity.getVelocity();

            double horizontalMultiplier = -0.01;
            double verticalMultiplier = 0.6;

            entity.setVelocity(
                    velocity.x + x * strength * horizontalMultiplier,
                    Math.max(velocity.y, 0.4) + strength * verticalMultiplier,
                    velocity.z + z * strength * horizontalMultiplier
            );

            Vec3d newVel = entity.getVelocity();
            double maxHorizontalVel = 0.2;
            double maxVerticalVel = 0.16;

            entity.setVelocity(
                    MathHelper.clamp(newVel.x, -maxHorizontalVel, maxHorizontalVel),
                    MathHelper.clamp(newVel.y, -maxVerticalVel, maxVerticalVel),
                    MathHelper.clamp(newVel.z, -maxHorizontalVel, maxHorizontalVel)
            );
            ci.cancel();
        }
    }
}