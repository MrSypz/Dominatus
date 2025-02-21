package sypztep.dominatus.client.event;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.util.RefinementCalculator;
import sypztep.dominatus.common.util.RefinementManager;

import java.util.List;

public final class RefinementTooltip implements ItemTooltipCallback {
    @Override
    public void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> lines) {
        if (stack.contains(ModDataComponents.REFINEMENT)) {
            Refinement refinement = stack.get(ModDataComponents.REFINEMENT);
            if (refinement == null) return;

            boolean isArmor = stack.isIn(ItemTags.ARMOR_ENCHANTABLE);

            if (refinement.accuracy() > 0) addStatTooltip(lines, "Accuracy", refinement.accuracy(), isArmor);
            if (refinement.evasion() > 0) addStatTooltip(lines, "Evasion", refinement.evasion(), isArmor);
        }
    }

    private void addStatTooltip(List<Text> lines, String label, int value, boolean isArmor) {
        if (isArmor) lines.add(Text.literal("+" + String.format("%d", value) + " " + label)
                    .formatted(Formatting.BLUE));
         else lines.add(Text.literal(" " + String.format("%d", value) + " " + label)
                    .formatted(Formatting.DARK_GREEN));

    }
}