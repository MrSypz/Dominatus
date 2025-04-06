package sypztep.dominatus.common.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import sypztep.dominatus.client.screen.panel.GemInventoryPanel;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.init.ModScreenHandler;
import sypztep.dominatus.common.util.gemsystem.GemInventory;
import sypztep.dominatus.common.util.gemsystem.GemSlot;

public final class GemScreenHandler extends ScreenHandler {

    public static final int[][] CROSS_SLOTS = {
            {0, -64},   // Top
            {64, 0},    // Right
            {0, 64},    // Bottom
            {-64, 0},   // Left
            {48, -48},  // Top Right
            {48, 48},   // Bottom Right
            {-48, 48},  // Bottom Left
            {-48, -48}, // Top Left
    };

    public final PlayerInventory playerInventory;
    public final GemInventory gemInventory;

    public GemScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory,
                ModEntityComponents.GEM_INVENTORY_COMPONENT.get(playerInventory.player).getInventory());
    }

    public GemScreenHandler(int syncId, PlayerInventory playerInventory, GemInventory inventory) {
        super(ModScreenHandler.GEM_SCREEN_HANDLER_TYPE, syncId);
        this.playerInventory = playerInventory;
        this.gemInventory = inventory;

        // Calculate center position for slots - adjusted more to the right
        int centerX = 90;
        int centerY = 108;

        // Add gem slots in cross pattern
        for (int i = 0; i < CROSS_SLOTS.length; i++) {
            int slotX = centerX + CROSS_SLOTS[i][0];
            int slotY = centerY + CROSS_SLOTS[i][1];

            slotX -= 9;
            slotY -= 9;
            this.addSlot(new GemSlot(inventory, i, slotX, slotY));
        }

        // Add vertical gem inventory slots (4x6 grid = 24 slots, plus 3 extra = 27 total)
        // First add the 4x6 grid (24 slots)
        for (int row = 0; row < 6; ++row) {
            for (int col = 0; col < 4; ++col) {
                this.addSlot(new GemStorageSlot(playerInventory,
                        col + row * 4 + 9,  // Offset by 9 for gem slots
                        176 + 4 + col * 18,  // Right side inventory
                        26 + row * 18));     // Starting from top
            }
        }

        // Add the remaining 3 slots in the last row
        for (int col = 0; col < 3; ++col) {
            this.addSlot(new GemStorageSlot(playerInventory,
                    33 + col,  // 33, 34, 35 are the last three inventory slots
                    176 + 4 + col * 18,  // Same X spacing as the grid above
                    26 + 6 * 18));       // One row below the 6x4 grid
        }

        // Add hotbar vertically in a separate column (9 slots)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i,
                    176 + GemInventoryPanel.INVENTORY_WIDTH + 8,
                    26 + i * 18));
        }
    }

    private static class GemStorageSlot extends Slot {
        public GemStorageSlot(PlayerInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        // Unlike FilteredGemSlot, this doesn't restrict insertion - allowing any item
        @Override
        public boolean canInsert(ItemStack stack) {
            return true; // Allow all items to be stored and moved freely
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        ModEntityComponents.GEM_INVENTORY_COMPONENT.sync(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int fromIndex) {
        return ItemStack.EMPTY;
    }
}