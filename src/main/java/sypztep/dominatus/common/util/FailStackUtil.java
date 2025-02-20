package sypztep.dominatus.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.init.ModEntityComponents;

import java.util.Optional;

public class FailStackUtil {
    private static final double[] normalSuccessRates = {
            100.0, 95.0, 90.0, 80.0, 70.0, 60.0, 50.0, 40.0, 30.0, 20.0, 10.0,
            9.0, 8.0, 7.0, 6.0, 5.0, // Levels 1-15
            25.0, // PRI (16)
            17.5, // DUO (17)
            12.5, // TRI (18)
            7.5,  // TET (19)
            2.5   // PEN (20)
    };
    private static final double SUCCESS_RATE_CAP = 90.0; // Cap for success rate
    private static double successRate;
    //------------set-----------//

    public static double getSuccessRate() {
        return successRate;
    }

    private static void setSuccessRate(double v) {
        successRate = v;
    }
    public static double calculateSuccessRate(ItemStack slotOutput, int failStack) {
        int currentRefineLvl = RefinementUtil.getRefineLvl(slotOutput);
        if (currentRefineLvl >= normalSuccessRates.length)
            return 0.0; // or handle appropriately for out-of-bounds condition

        double baseSuccessRate = normalSuccessRates[currentRefineLvl] * 0.01;
        double successRate = baseSuccessRate + (failStack * (baseSuccessRate * 0.1));
        successRate = Math.min(successRate, SUCCESS_RATE_CAP * 0.01);

        return successRate;
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
