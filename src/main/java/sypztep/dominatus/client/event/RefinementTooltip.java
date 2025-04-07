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
import sypztep.tyrannus.common.util.ItemStackHelper;

import java.util.List;

public final class RefinementTooltip implements ItemTooltipCallback {
    @Override
    public void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> lines) {
        if (stack.contains(ModDataComponents.REFINEMENT)) {
            Refinement refinement = stack.get(ModDataComponents.REFINEMENT);
            if (refinement == null) return;

            if (refinement.accuracy() > 0) lines.add(Text.literal(" ▶ ").formatted(Formatting.GOLD)
                    .append(Text.literal(" Accuracy: " + refinement.accuracy()).formatted(Formatting.GRAY)));
            if (refinement.evasion() > 0)  lines.add(Text.literal(" ▶ ").formatted(Formatting.GOLD)
                    .append(Text.literal(" Evasion: " + refinement.evasion()).formatted(Formatting.GRAY)));
            if (refinement.durability() > 0) lines.add(Text.literal(" ▶ ").formatted(Formatting.GOLD)
                    .append(Text.literal(" Durability: " + refinement.durability()).formatted(Formatting.GRAY)));

            if (refinement.durability() <= 0 || ItemStackHelper.willBreakNextUse(stack))
                lines.add(Text.literal("Broken ✗").formatted(Formatting.RED));
            if (refinement.durability() <= 20 || ItemStackHelper.willBreakNextUse(stack))
                lines.add(Text.literal("Durability too low ✗").formatted(Formatting.RED));
            else lines.add(Text.literal("Can Refine ✔").formatted(Formatting.GREEN));
        }
    }
}