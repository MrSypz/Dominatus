package sypztep.dominatus.common.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.util.gemsystem.GemInventory;

import java.util.Optional;

public final class GemInventoryComponent implements AutoSyncedComponent {
    private final GemInventory inventory;

    public GemInventoryComponent(PlayerEntity player) {
        this.inventory = new GemInventory(player);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("GemInventory")) {
            NbtList listTag = tag.getList("GemInventory", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < listTag.size(); i++) {
                NbtCompound slotTag = listTag.getCompound(i);
                int slot = slotTag.getInt("Slot");
                if (slot >= 0 && slot < inventory.size()) {
                    Optional<ItemStack> itemStack = ItemStack.fromNbt(registryLookup, slotTag.get("Stack"));
                    itemStack.ifPresent(stack -> inventory.setStack(slot, stack));
                }
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList itemsList = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                NbtCompound slotTag = new NbtCompound();
                slotTag.putInt("Slot", i);
                try {
                    NbtElement stackNbt = stack.encode(registryLookup);
                    slotTag.put("Stack", stackNbt);
                    itemsList.add(slotTag);
                } catch (IllegalStateException ignored) {
                }
            }
        }
        tag.put("GemInventory", itemsList);
    }
    public GemInventory getInventory() {
        return this.inventory;
    }

    public static GemInventory getInventory(PlayerEntity player) {
        return ModEntityComponents.GEM_INVENTORY_COMPONENT.get(player).getInventory();
    }

    private ItemStack getGem(int slot) {
        return this.inventory.getStack(slot);
    }

    public static ItemStack getGem(PlayerEntity player, int slot) {
        return ModEntityComponents.GEM_INVENTORY_COMPONENT.get(player).getGem(slot);
    }

    private void setGem(int slot, ItemStack stack) {
        ItemStack previous = this.inventory.getStack(slot);
        if (!ItemStack.areEqual(previous, stack)) {
            this.inventory.setStack(slot, stack);
        }
    }

    public static void setGem(PlayerEntity player, int slot, ItemStack stack) {
        ModEntityComponents.GEM_INVENTORY_COMPONENT.get(player).setGem(slot, stack);
    }
    private void clear() {
        this.inventory.clear();
    }

    public static void clear(PlayerEntity player) {
        ModEntityComponents.GEM_INVENTORY_COMPONENT.get(player).clear();
    }
}