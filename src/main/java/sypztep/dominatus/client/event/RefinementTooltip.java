package sypztep.dominatus.client.event;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.util.RefinementCalculator;
import sypztep.dominatus.common.util.RefinementManager;

import java.util.List;

public final class RefinementTooltip implements ItemTooltipCallback {
    private static final Text BULLET = Text.literal(" ▶ ").formatted(Formatting.GOLD);

    @Override
    public void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> lines) {
        if (stack.contains(ModDataComponents.REFINEMENT)) {
            Refinement refinement = stack.get(ModDataComponents.REFINEMENT);
            if (refinement == null) return;

            addTooltipLine(lines, "Refine", refinement.refine());
            addTooltipLine(lines, "Accuracy", refinement.accuracy());
            addTooltipLine(lines, "Evasion", refinement.evasion());
            addTooltipLine(lines, "Durability", refinement.durability());
            addTooltipLine(lines, "Damage", refinement.damage(), true); // For float values
            addTooltipLine(lines, "Protection", refinement.protection());

            // Optional: Add success rate information if available
//            if (stack.get(ModDataComponents.REFINEMENT).refine() < RefinementManager.MAX_ENHANCED_LEVEL) {
//                int failStack = 0; // You might want to get this from somewhere
//                double successRate = RefinementCalculator.calculateSuccessRate(
//                        refinement.refine(),
//                        failStack
//                );
//                addTooltipLine(lines, "Success Rate", String.format("%.1f%%", successRate * 100));
//            }
        }
    }

    private void addTooltipLine(List<Text> lines, String label, Number value) {
        addTooltipLine(lines, label, String.valueOf(value));
    }

    private void addTooltipLine(List<Text> lines, String label, Number value, boolean isFloat) {
        String format = isFloat ? "%.1f" : "%d";
        addTooltipLine(lines, label, String.format(format, value));
    }

    private void addTooltipLine(List<Text> lines, String label, String value) {
        lines.add(BULLET.copy().append(
                Text.literal(" " + label + ": " + value)
                        .formatted(Formatting.GRAY)
        ));
    }
}