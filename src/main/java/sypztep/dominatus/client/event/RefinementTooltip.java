package sypztep.dominatus.client.event;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.util.RefinementUtil;

import java.util.List;

public final class RefinementTooltip implements ItemTooltipCallback {
    @Override
    public void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> lines) {
        if (stack.contains(ModDataComponents.REFINEMENT)) {
            Refinement refinement = RefinementUtil.getRefinement(stack);
            lines.add(Text.literal(" ▶ ").formatted(Formatting.GOLD)
                    .append(Text.literal(" Refine: " + refinement.refine()).formatted(Formatting.GRAY)));
                lines.add(Text.literal(" ▶ ").formatted(Formatting.GOLD)
                        .append(Text.literal(" Accuracy: " + refinement.accuracy()).formatted(Formatting.GRAY)));
                lines.add(Text.literal(" ▶ ").formatted(Formatting.GOLD)
                        .append(Text.literal(" Evasion: " + refinement.evasion()).formatted(Formatting.GRAY)));
                lines.add(Text.literal(" ▶ ").formatted(Formatting.GOLD)
                        .append(Text.literal(" Durability: " + refinement.durability()).formatted(Formatting.GRAY)));
                lines.add(Text.literal(" ▶ ").formatted(Formatting.GOLD)
                        .append(Text.literal(" Damage: " + refinement.damage()).formatted(Formatting.GRAY)));
                lines.add(Text.literal(" ▶ ").formatted(Formatting.GOLD)
                        .append(Text.literal(" Protection: " + refinement.protection()).formatted(Formatting.GRAY)));
        }
    }
}
