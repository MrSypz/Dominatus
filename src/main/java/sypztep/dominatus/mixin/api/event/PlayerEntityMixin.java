package sypztep.dominatus.mixin.api.event;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import sypztep.dominatus.common.api.entity.DominatusPlayerEntityEvents;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.util.ItemStackHelper;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Unique
    private PlayerEntity player = (PlayerEntity) (Object) this;

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 2)
    private boolean modifyAttackCondition(boolean bl3, Entity target) {
        return DominatusPlayerEntityEvents.MODIFY_ATTACK_CONDITION.invoker().modifyCondition(player,target, bl3);
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "CONSTANT", args = "floatValue=1.5"))
    private float modifyAttackDamage(float f) {
        return DominatusPlayerEntityEvents.MODIFY_ATTACK_DAMAGE.invoker().modifyDamage(player, f);
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isAttackable()Z"))
    private boolean modifyAllowAttack(boolean original, @Local(argsOnly = true) Entity target) {
        return DominatusPlayerEntityEvents.ALLOW_ATTACK.invoker().allowAttack(player, target);
    }

    @ModifyExpressionValue(method = "attack",at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getBonusAttackDamage(Lnet/minecraft/entity/Entity;FLnet/minecraft/entity/damage/DamageSource;)F"))
    private float addWeaponPercentageDamage(float originalBonus, @Local(argsOnly = true) Entity target, @Local ItemStack itemStack) {
        float baseWeaponDamage = ItemStackHelper.getBaseWeaponAttackDamage(itemStack);

        if (baseWeaponDamage > 0) {
            double percentageMultiplier = player.getAttributeValue(ModEntityAttributes.MELEE_ATTACK_DAMAGE);
            float weaponBonus = baseWeaponDamage * (float) percentageMultiplier;

            return originalBonus + weaponBonus;
        }

        return originalBonus;
    }
}

