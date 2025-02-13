package sypztep.dominatus.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.init.ModEntityComponents;

import java.util.Optional;

public class FailStackUtil {
    private static double successRate;
    private static final float SUCCESS_RATE_CAP = 90.0F; // Cap for success rate

    public static double getSuccessRate() {
        return successRate;
    }

    private static void setSuccessRate(double v) {
        successRate = v;
    }

    // Calculate Success Rate based on item and failstack
    private static double calculateSuccessRate(ItemStack slotOutput, int failStack) {
        Optional<DominatusItemEntry> itemDataOpt = DominatusItemEntry.getDominatusItemData(slotOutput);
        if (itemDataOpt.isEmpty()) return 0.0; // If item data is not found, return 0.0


        DominatusItemEntry itemData = itemDataOpt.get();

        // Check if the failstack rate exists for the given failstack level
        Float failstackRate = itemData.failStackRates().get(failStack);
        if (failstackRate == null) return 0.0; // If no specific failstack rate for the level, return 0.0

        // Get the base success rate for the item based on its refine level
        int currentRefineLvl = RefinementUtil.getRefineLvl(slotOutput);
        if (currentRefineLvl >= itemData.maxLvl()) return 0.0; // Handle out-of-bounds refine level

        // Get the base success rate from the item data and adjust based on failstack rate
        double baseSuccessRate = failstackRate;
        double successRate = baseSuccessRate + (failStack * (baseSuccessRate * 0.1));
        return Math.min(successRate, SUCCESS_RATE_CAP * 0.01); // Apply success rate cap
    }

    // Method to calculate and set the success rate for an item
    public static void getCalculateSuccessRate(ItemStack slotOutput, int failStack) {
        setSuccessRate(calculateSuccessRate(slotOutput, failStack));
    }

    // Handle success refinement
    public static void successRefine(PlayerEntity player) {
        ModEntityComponents.FAILSTACK_COMPONENT.get(player).setFailstack(0);
    }

    // Handle fail refinement and increase failstack
    public static void failRefine(PlayerEntity player, int failstack) {
        ModEntityComponents.FAILSTACK_COMPONENT.get(player).setFailstack(failstack + 1);
    }
}
