package sypztep.dominatus.common.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import sypztep.dominatus.common.init.ModScreenHandler;

public class RarityScreenHandler extends ScreenHandler {
    private final Inventory inventory = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            RarityScreenHandler.this.onContentChanged(this);
        }
    };
    private final ScreenHandlerContext context;
    private final PlayerEntity player;

    public RarityScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandler.RARITY_SCREEN_HANDLER_TYPE, syncId);

        this.context = context;
        this.player = playerInventory.player;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
