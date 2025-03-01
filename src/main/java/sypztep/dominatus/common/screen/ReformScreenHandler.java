package sypztep.dominatus.common.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import sypztep.dominatus.common.init.ModScreenHandler;
import sypztep.dominatus.common.tag.ModItemTags;
import sypztep.dominatus.common.util.ReformSystem.ReformManager;

public class ReformScreenHandler extends ScreenHandler {
    private final Inventory inventory = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            ReformScreenHandler.this.onContentChanged(this);
        }
    };
    private final ScreenHandlerContext context;
    private final PlayerEntity player;

    // Flag to track if reform is in progress
    private boolean reformInProgress = false;

    public ReformScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandler.REFORM_SCREEN_HANDLER_TYPE, syncId);

        this.context = context;
        this.player = playerInventory.player;

        // Slot 0: Reform material slot
        addSlot(new Slot(this.inventory, 0, 57, 39){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isIn(ModItemTags.REFORM_MATERIAL);
            }
        });

        // Slot 1: Item to reform slot
        addSlot(new Slot(this.inventory, 1, 107, 39){
            @Override
            public boolean canInsert(ItemStack stack) {
                return ReformManager.getItemCategory(stack) != null;
            }

            @Override
            public ItemStack takeStack(int amount) {
                ItemStack result = super.takeStack(amount);
                // Apply reform when taking item out
                if (!reformInProgress && !result.isEmpty()) {
                    reform();
                }
                return result;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                super.onTakeItem(player, stack);
            }
        });

        // Player inventory slots
        int i;
        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();

            // If taking from reform slots
            if (index < 2) {
                // If taking from item slot (1), try to reform first
                if (index == 1 && !reformInProgress) {
                    reform();
                }

                if (!this.insertItem(slotStack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // If from player inventory
            else {
                // If it's a reform stone, try to put in material slot
                if (slotStack.isIn(ModItemTags.REFORM_MATERIAL)) {
                    if (!this.insertItem(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // If it's a reformable item, try to put in item slot
                else if (ReformManager.getItemCategory(slotStack) != null) {
                    if (!this.insertItem(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Otherwise, move between hotbar and main inventory
                else if (index < 29) {
                    if (!this.insertItem(slotStack, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 38) {
                    if (!this.insertItem(slotStack, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, slotStack);
        }

        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
    }

    public void reform() {
        reformInProgress = true;

        try {
            // Get both stacks from the inventory slots
            ItemStack reformStone = this.inventory.getStack(0);
            ItemStack targetItem = this.inventory.getStack(1);

            // Check if both slots have items
            if (targetItem.isEmpty() || reformStone.isEmpty()) {
                return;
            }

            // Check if we have a reformable item and a valid reform stone
            if (ReformManager.canReform(targetItem, reformStone)) {
                // Try to apply the reform
                boolean success = ReformManager.applyReform(targetItem, reformStone);

                if (success) {
                    // Reform was successful, consume the reform stone
                    reformStone.decrement(1);

                    // Play reform success sound
                    this.player.playSound(SoundEvents.BLOCK_ANVIL_USE, 1.0f, 1.0f);

                    // Mark the inventory as dirty to sync changes
                    this.inventory.markDirty();
                } else {
                    // Reform failed, play a failure sound
                    this.player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0f, 0.8f);
                }
            } else {
                // Items are incompatible, play an error sound
                this.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 0.5f, 0.5f);
            }
        } finally {
            // Reset flag
            reformInProgress = false;
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}