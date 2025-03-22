package sypztep.dominatus.mixin.core.item.refinement.freerepair;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.util.RefineSystem.RefinementManager;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    @Final
    private Property levelCost;

    @Shadow private int repairItemUsage;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    private void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        ItemStack refinedItem = input.getStack(0);
        if (refinedItem.contains(ModDataComponents.REFINEMENT)) {
            int currentDamage = refinedItem.getDamage();
            int minAllowedDamage = RefinementManager.getMaxAllowedVanillaRepair(refinedItem);

            if (currentDamage > minAllowedDamage && !input.getStack(1).isEmpty()) cir.setReturnValue(true);
            else cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateResult", at = @At("TAIL"))
    private void zeroRepairCost(CallbackInfo ci) {
        if (input.getStack(0).contains(ModDataComponents.REFINEMENT)) {
            levelCost.set(0);
        }
    }
    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void calculateRepairAmount(CallbackInfo ci) {
        ItemStack itemToRepair = this.input.getStack(0);
        ItemStack repairMaterial = this.input.getStack(1);

        if (!itemToRepair.isEmpty() && itemToRepair.contains(ModDataComponents.REFINEMENT)) {
            if (!repairMaterial.isEmpty() && itemToRepair.canRepairWith(repairMaterial)) {
                int damage = itemToRepair.getDamage();
                if (damage <= 0) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    ci.cancel();
                    return;
                }

                // Calculate how much durability each repair material restores
                int repairPerItem = itemToRepair.getMaxDamage() / 4; // Each material repairs 25% of max durability

                // Calculate how many items needed for full repair
                int materialsNeeded = (int) Math.ceil((double) damage / repairPerItem);

                // Limit by available materials
                materialsNeeded = Math.min(materialsNeeded, repairMaterial.getCount());

                // Calculate actual repair amount
                int repairAmount = Math.min(damage, repairPerItem * materialsNeeded);

                if (repairAmount > 0) {
                    ItemStack result = itemToRepair.copy();
                    result.setDamage(Math.max(0, damage - repairAmount));

                    this.repairItemUsage = materialsNeeded;
                    this.output.setStack(0, result);
                    this.levelCost.set(0); // Keep repair free for refined items
                } else {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                }

                ci.cancel();
            }
        }
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    private void handleRefinedItemRepair(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (!stack.isEmpty() && stack.contains(ModDataComponents.REFINEMENT)) {
            ItemStack repairMaterial = this.input.getStack(1);

            if (this.repairItemUsage > 0) {
                if (!repairMaterial.isEmpty()) {
                    if (repairMaterial.getCount() > this.repairItemUsage) {
                        repairMaterial.decrement(this.repairItemUsage);
                    } else {
                        this.input.setStack(1, ItemStack.EMPTY);
                    }
                }
            }

            this.input.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);

            this.context.run((world, pos) -> {
                BlockState blockState = world.getBlockState(pos);
                if (!player.isInCreativeMode() && blockState.isIn(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
                    BlockState damagedState = AnvilBlock.getLandingState(blockState);
                    if (damagedState == null) {
                        world.removeBlock(pos, false);
                        world.syncWorldEvent(1029, pos, 0);
                    } else {
                        world.setBlockState(pos, damagedState, 2);
                        world.syncWorldEvent(1030, pos, 0);
                    }
                } else {
                    world.syncWorldEvent(1030, pos, 0);
                }
            });

            ci.cancel();
        }
    }

}
