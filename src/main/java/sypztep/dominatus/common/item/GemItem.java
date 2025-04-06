package sypztep.dominatus.common.item;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.data.GemComponent;

import java.util.List;
import java.util.Optional;


public final class GemItem extends Item {
    public GemItem() {
        super(new Item.Settings().maxCount(1));
    }

    @Override
    public Text getName(ItemStack stack) {
        return GemComponent.fromStack(stack)
                .map(gem -> Text.translatable("item.dominatus.gem." + gem.type().getPath()))
                .orElseGet(() -> (net.minecraft.text.MutableText) super.getName(stack));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Optional<GemComponent> gemComponent = GemComponent.fromStack(stack);

        if (gemComponent.isPresent()) {
            GemComponent gem = gemComponent.get();

            // Add gem name with grade color (you might want to add grade to your GemComponent)
            tooltip.add(Text.literal("◈ ")
                    .formatted(Formatting.YELLOW)
                    .append(Text.translatable("gem." + gem.type().getNamespace() + "." + gem.type().getPath())
                            .formatted(Formatting.YELLOW)));

            // Spacer
            tooltip.add(Text.empty());

            // Show "Effects" header
            tooltip.add(Text.literal("【 ")
                    .formatted(Formatting.GRAY)
                    .append(Text.translatable("item.dominatus.gem.effects"))
                    .append(" 】")
                    .formatted(Formatting.GRAY));

            // Show attributes with BDO-style formatting
            gem.attributeModifiers().forEach((attributeId, modifier) -> {
                EntityAttribute attribute = Registries.ATTRIBUTE.get(attributeId);
                if (attribute != null) {
                    String operation = switch (modifier.operation()) {
                        case ADD_VALUE -> "+";
                        case ADD_MULTIPLIED_BASE -> "×";
                        case ADD_MULTIPLIED_TOTAL -> "%";
                    };

                    // Create the effect box with colored symbol and value
                    MutableText effectText = Text.literal("▣ ")
                            .formatted(Formatting.AQUA)
                            .append(Text.literal(operation + String.format("%.1f", modifier.value()))
                                    .formatted(Formatting.GREEN))
                            .append(" ")
                            .append(Text.translatable(attribute.getTranslationKey())
                                    .formatted(Formatting.WHITE));

                    tooltip.add(effectText);
                }
            });
        }
    }
}
