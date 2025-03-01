package sypztep.dominatus.mixin.core.item.refinement.newdurability;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.util.RefineSystem.RefinementManager;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "setDamage", at = @At("HEAD"), cancellable = true)
    private void onSetDamage(int damage, CallbackInfo ci) {
        ItemStack self = (ItemStack)(Object)this;

        if (DominatusItemEntry.getDominatusItemData(self).isPresent()) {
            int minimumDamage = RefinementManager.getMaxAllowedVanillaRepair(self);

            // If trying to repair beyond the allowed amount, cancel and set to minimum damage
            if (damage < minimumDamage) {
                self.setDamage(minimumDamage);
                ci.cancel();
            }
        }
    }
}
