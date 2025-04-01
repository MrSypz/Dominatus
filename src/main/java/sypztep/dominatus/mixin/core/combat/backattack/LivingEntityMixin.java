package sypztep.dominatus.mixin.core.combat.backattack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import sypztep.dominatus.common.util.CombatUtil;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyVariable(method = "modifyAppliedDamage", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/damage/DamageSource;)F"), argsOnly = true)
    private float attackModifyDamage(float amount, DamageSource source) {
        LivingEntity target = (LivingEntity) (Object) this;
        return CombatUtil.damageModifier(target,amount,source);
    }
}