package sypztep.dominatus.common.util.gemsystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.item.GemItem;

public final class GemInventory implements Inventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(8, ItemStack.EMPTY);
    private final PlayerEntity player;

    public GemInventory(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public int size() {
        return 8;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(items, slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = Inventories.removeStack(items, slot);
        markDirty();
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (!(stack.getItem() instanceof GemItem)) {
            return;
        }
        items.set(slot, stack);
        markDirty();
    }

    @Override
    public void markDirty() {
        if (!player.getWorld().isClient()) {
            GemManager.updateEntityStats(player);
            ModEntityComponents.GEM_INVENTORY_COMPONENT.sync(player);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        items.clear();
        markDirty();
    }

    DefaultedList<ItemStack> getItems() {
        return items;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }
}