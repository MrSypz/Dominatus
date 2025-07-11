package sypztep.dominatus.mixin.api.event;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import sypztep.dominatus.common.api.entity.DominatusPlayerEntityEvents;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 2)
    private boolean modifyAttackCondition(boolean bl3, Entity target) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return DominatusPlayerEntityEvents.MODIFY_ATTACK_CONDITION.invoker().modifyCondition(player,target, bl3);
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "CONSTANT", args = "floatValue=1.5"))
    private float modifyAttackDamage(float f) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return DominatusPlayerEntityEvents.MODIFY_ATTACK_DAMAGE.invoker().modifyDamage(player, f);
    }
    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isAttackable()Z"))
    private boolean modifyAllowAttack(boolean original, @Local(argsOnly = true) Entity target) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return DominatusPlayerEntityEvents.ALLOW_ATTACK.invoker().allowAttack(player, target);
    }
}

