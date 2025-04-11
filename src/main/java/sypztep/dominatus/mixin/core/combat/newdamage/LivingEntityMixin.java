package sypztep.dominatus.mixin.core.combat.newdamage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.common.util.combatsystem.NewDamage;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract void damageArmor(DamageSource source, float amount);

    @Inject(
            method = "applyArmorToDamage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void applyImprovedArmorToDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
            cir.setReturnValue(amount);
            return;
        }

        damageArmor(source, amount);

        float damageAfterArmor = NewDamage.applyArmorToDamage(self, amount);
        cir.setReturnValue(damageAfterArmor);
    }
}