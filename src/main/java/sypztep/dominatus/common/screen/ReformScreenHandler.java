package sypztep.dominatus.common.screen;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import sypztep.dominatus.common.init.ModScreenHandler;
import sypztep.dominatus.common.tag.ModItemTags;
import sypztep.dominatus.common.util.ReformSystem.ReformManager;

public class ReformScreenHandler extends ForgingScreenHandler {

    public ReformScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandler.REFORM_SCREEN_HANDLER_TYPE, syncId, playerInventory, context,
                new ForgingSlotsManager.Builder()
                        .input(0, 57, 39, stack -> stack.isIn(ModItemTags.REFORM_MATERIAL))
                        .input(1, 107, 39, stack -> ReformManager.getItemCategory(stack) != null)
                        .output(2, 165, 39)
                        .build()
        );
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        ItemStack reformMaterial = this.input.getStack(0);
        ItemStack targetItem = this.input.getStack(1);

        if (!reformMaterial.isEmpty() && !targetItem.isEmpty()) {
            boolean success = ReformManager.applyReform(stack, reformMaterial);

            if (success) {
                reformMaterial.decrement(1);
                // Clear the input item
                this.input.setStack(1, ItemStack.EMPTY);
                player.playSound(SoundEvents.BLOCK_ANVIL_USE, 1.0f, 1.0f);
            } else {
                player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0f, 0.8f);
            }
        }
    }

    @Override
    public void updateResult() {
        ItemStack reformMaterial = this.input.getStack(0);
        ItemStack targetItem = this.input.getStack(1);

        if (reformMaterial.isEmpty() || targetItem.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }

        if (ReformManager.canReform(targetItem, reformMaterial)) {
            ItemStack result = targetItem.copy();
            this.output.setStack(0, result);
        } else {
            this.output.setStack(0, ItemStack.EMPTY);
        }
    }

    @Override
    protected boolean canUse(BlockState state) {
        return true;  // Replace with your reform block tag
    }

    @Override
    protected boolean isValidIngredient(ItemStack stack) {
        return stack.isIn(ModItemTags.REFORM_MATERIAL) || ReformManager.getItemCategory(stack) != null;
    }
}