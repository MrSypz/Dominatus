package sypztep.dominatus.mixin.core.item.refinement;

import net.minecraft.component.ComponentHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.util.RefinementUtil;


@Mixin(Item.class)
public abstract class ItemMixin implements ComponentHolder {
    @Inject(method = "onCraftByPlayer", at = @At("HEAD"))
    public void onCraft(ItemStack stack, World world, PlayerEntity player, CallbackInfo ci) {
        System.out.println("is Present"+DominatusItemEntry.getDominatusItemData(stack).isPresent());
        if (!stack.isEmpty() && DominatusItemEntry.getDominatusItemData(stack).isPresent()) {
            RefinementUtil.setRefinement(stack, new Refinement(0, 0, 0, 100, 0.0f, 0));
        }
    }
}


