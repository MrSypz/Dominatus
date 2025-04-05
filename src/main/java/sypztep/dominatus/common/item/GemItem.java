package sypztep.dominatus.common.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.data.GemComponent;

import java.util.List;
import java.util.Optional;


public class GemItem extends Item {
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

            // Show attributes
            gem.attributeModifiers().forEach((attributeId, modifier) -> {
                EntityAttribute attribute = Registries.ATTRIBUTE.get(attributeId);
                if (attribute != null) {
                    String operation = switch (modifier.operation()) {
                        case ADD_VALUE -> "+";
                        case ADD_MULTIPLIED_BASE -> "×";
                        case ADD_MULTIPLIED_TOTAL -> "%";
                    };

                    tooltip.add(Text.literal(operation + " " +
                                    (modifier.value()) + " " +
                                    Text.translatable(attribute.getTranslationKey()).getString())
                            .formatted(Formatting.BLUE));
                }
            });
        }
    }
}
