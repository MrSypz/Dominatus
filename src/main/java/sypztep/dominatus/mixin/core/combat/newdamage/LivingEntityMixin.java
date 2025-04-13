package sypztep.dominatus.mixin.core.combat.newdamage;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sypztep.dominatus.common.util.combatsystem.NewDamage;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @WrapOperation(
            method = "applyArmorToDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/DamageUtil;getDamageLeft(Lnet/minecraft/entity/LivingEntity;FLnet/minecraft/entity/damage/DamageSource;FF)F"
            )
    )
    private float useCustomDamageCalculation(
            LivingEntity armorWearer, float damageAmount, DamageSource damageSource, float armor, float armorToughness, Operation<Float> original) {
        return NewDamage.getDamageLeft(armorWearer, damageAmount, damageSource, armor, armorToughness);
    }
}