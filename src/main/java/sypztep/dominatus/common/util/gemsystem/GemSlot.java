package sypztep.dominatus.common.util.gemsystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import sypztep.dominatus.common.component.GemInventoryComponent;
import sypztep.dominatus.common.item.GemItem;

public class GemSlot extends Slot {
    public GemSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public void setStack(ItemStack stack, ItemStack previousStack) {
        if (this.inventory instanceof GemInventory gemInventory) {
            PlayerEntity player = gemInventory.getPlayer();
            if (player != null) {
                GemInventoryComponent.setGem(player,this.getIndex(), stack);
                return;
            }
        }
        super.setStack(stack, previousStack);
    }
    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof GemItem;
    }
}