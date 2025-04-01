package sypztep.dominatus.mixin.core.item.applyrefinestats;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.common.util.refinesystem.RefinementItemManager;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "getEquipmentChanges",
            at = @At("RETURN"))
    private void onEquipmentChange(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        if (cir.getReturnValue() != null) {
            LivingEntity target = (LivingEntity) (Object) this;
            RefinementItemManager.updateEntityStats(target);
        }
    }
}
