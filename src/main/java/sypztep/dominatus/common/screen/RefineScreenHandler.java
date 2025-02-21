package sypztep.dominatus.common.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import sypztep.dominatus.client.payload.RefinePayloadS2C;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.init.ModItems;
import sypztep.dominatus.common.init.ModScreenHandler;
import sypztep.dominatus.common.util.RefinementCalculator;
import sypztep.dominatus.common.util.RefinementManager;

public class RefineScreenHandler extends ScreenHandler {
    private final Inventory inventory = new SimpleInventory(3) {
        @Override
        public void markDirty() {
            super.markDirty();
            RefineScreenHandler.this.onContentChanged(this);
        }
    };
    private final ScreenHandlerContext context;
    private final PlayerEntity player;

    public PlayerEntity getPlayer() {
        return player;
    }

    public RefineScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandler.REFINE_SCREEN_HANDLER_TYPE, syncId);

        this.context = context;
        this.player = playerInventory.player;
        addSlot(new Slot(this.inventory, 0, 80, 7) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return isRefineMaterial(stack);
            }
        });
        addSlot(new Slot(this.inventory, 1, 132, 7) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return isValidItem(stack);
            }
        });
        addSlot(new Slot(this.inventory, 2, 80, 27) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });
        int i;
        for (i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (!player.getWorld().isClient() && inventory == this.inventory) {
            this.doRefineTask();
        }
    }

    private void doRefineTask() {
        ItemStack slotOutput = this.getSlot(1).getStack();
        ItemStack material = this.getSlot(0).getStack();

        boolean allSlotInsert = this.getSlot(0).hasStack() && this.getSlot(1).hasStack();
        boolean canRefine = false;

        if (allSlotInsert && isValidItem(slotOutput)) {
            if (!slotOutput.contains(ModDataComponents.REFINEMENT)) {
                RefinementManager.initializeRefinement(slotOutput);
            }

            // Get current refinement state
            Refinement currentRef = slotOutput.get(ModDataComponents.REFINEMENT);
            int currentRefineLvl = currentRef.refine();
            int currentDurability = currentRef.durability();

            if (currentRefineLvl < RefinementManager.MAX_ENHANCED_LEVEL) {
                if (isRepairMaterial(material) && currentDurability < 100) {
                    canRefine = true;
                }
                else if (currentDurability > 20) {
                    canRefine = RefinementManager.MaterialValidator.isValidMaterial(material, slotOutput, currentRefineLvl);
                }
            }

            // Calculate success rate for valid refinement attempts
            if (canRefine) {
                int failStack = ModEntityComponents.FAILSTACK_COMPONENT.get(this.player).getFailstack();
                double successRate = RefinementCalculator.calculateSuccessRate(currentRefineLvl, failStack);
            }
        }

        RefinePayloadS2C.send((ServerPlayerEntity) this.player, !canRefine);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public boolean isValidItem(ItemStack stack) {
        String itemID = DominatusItemEntry.getItemId(stack);
        return DominatusItemEntry.getDominatusItemData(itemID).isPresent();
    }

    private boolean isRefineMaterial(ItemStack stack) {
        return stack.isOf(ModItems.REFINE_WEAPON_STONE) || stack.isOf(ModItems.REFINE_ARMOR_STONE) || stack.isOf(ModItems.REFINE_ARMORENFORGE_STONE) || stack.isOf(ModItems.REFINE_WEAPONENFORGE_STONE) || isRepairMaterial(stack);
    }

    private boolean isRepairMaterial(ItemStack stack) {
        return stack.isOf(ModItems.MOONLIGHT_CRESCENT) || stack.isOf(this.getSlot(1).getStack().getItem());
    }

    public void refine() {
        ItemStack item = this.getSlot(1).getStack();
        ItemStack material = this.getSlot(0).getStack();
        int failStack = ModEntityComponents.FAILSTACK_COMPONENT.get(this.player).getFailstack();

        RefinementManager.RefinementResult result = RefinementManager.processRefinement(
                item,
                material,
                failStack,
                player
        );

        ModEntityComponents.FAILSTACK_COMPONENT.get(this.player).setFailstack(result.newFailStack());

        if (result.consumeMaterial()) {
            this.decrementStack();
        }

        if (result.success()) {
            // TODO: Send success message/sound
        } else {
            // TODO: Send failure message/sound
        }
    }

    private void decrementStack() {
        ItemStack itemStack = this.inventory.getStack(0);
        if (!itemStack.isEmpty()) {
            itemStack.decrement(1);
            this.inventory.setStack(0, itemStack);
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (index < 3) { // Container slots
                if (!insertItem(slotStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(slotStack, stack);
            } else {
                // Handle material slots
                if (isRefineMaterial(slotStack)) {
                    if (!insertItem(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Handle refineable items
                else if (isValidItem(slotStack)) {
                    if (!slotStack.contains(ModDataComponents.REFINEMENT)) {
                        RefinementManager.initializeRefinement(slotStack);
                    }
                    if (!insertItem(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else {
                    if (index < 30) { // From inventory to hotbar
                        if (!insertItem(slotStack, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 39) { // From hotbar to inventory
                        if (!insertItem(slotStack, 3, 30, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        return ItemStack.EMPTY;
                    }
                }
            }

            // Update slot states
            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, slotStack);
        }
        return stack;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.inventory && super.canInsertIntoSlot(stack, slot);
    }
}
