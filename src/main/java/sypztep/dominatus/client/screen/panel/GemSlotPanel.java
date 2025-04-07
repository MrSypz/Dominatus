package sypztep.dominatus.client.screen.panel;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.tyrannus.client.screen.panel.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GemSlotPanel extends Button {
    private final GemComponent gem;
    private final GemDataComponent gemData; // To track equipped count
    private final Identifier texture;

    public GemSlotPanel(int x, int y, int width, int height, GemComponent gem, Identifier texture, Consumer<GemSlotPanel> onClick, GemDataComponent gemData) {
        super(x, y, width, height, Text.empty(), null, onClick != null ? (button) -> onClick.accept((GemSlotPanel) button) : null);
        this.gem = gem;
        this.gemData = gemData;
        this.texture = texture != null ? texture : Identifier.ofVanilla("textures/item/diamond.png");
        setDrawHeader(false);
        setDrawBorder(true);
        setPadding(2);
        setPlaySounds(true, true); // We'll manage this differently
        setRoundedCorners(true, 4); // Use rounded corners
        setGlowIntensity(1.5f); // Add glow effect
        setBounceIntensity(1.2f); // Add bounce on hover
    }

    @Override
    protected void handleHoverSound() {
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderContents(context, mouseX, mouseY, delta); // Render button background and effects

        // Draw gem texture or "Empty" text
        if (gem != null && texture != null) {
            int iconSize = Math.min(width - 4, height - 4);
            int iconX = x + (width - iconSize) / 2;
            int iconY = y + (height - iconSize) / 2 + (int) (getPressAnimation() * 1.5f); // Apply press animation
            context.drawGuiTexture(texture, iconX, iconY, iconSize, iconSize);

            // Draw equipped count if applicable
            if (gemData != null && gem.maxPresets() < Integer.MAX_VALUE) {
                int equippedCount = (int) gemData.getGemPresets().values().stream()
                        .filter(g -> g != null && g.type().equals(gem.type()))
                        .count();
                int maxPresets = gem.maxPresets();
                String countText = equippedCount + "/" + maxPresets;
                int countColor = equippedCount >= maxPresets ? 0xFFFF5555 : 0xFF55FF55; // Red if maxed, green if not
                setEnabled(equippedCount < maxPresets); // Disable if maxed

                int textWidth = textRenderer.getWidth(countText);
                int textX = x + (width - textWidth) / 2;
                int textY = y + height - textRenderer.fontHeight - 2;
                context.drawTextWithShadow(textRenderer, countText, textX, textY, countColor);
            }
        } else {
            int textColor = isEnabled() ? 0xFF555555 : 0xFF333333;
            context.drawTextWithShadow(textRenderer, "Empty", x + (width - textRenderer.getWidth("Empty")) / 2,
                    y + (height - textRenderer.fontHeight) / 2 + (int) (getPressAnimation() * 1.5f), textColor);
        }

        // Render tooltip if hovered
        if (isHovered && isEnabled()) {
            List<Text> tooltip = getTooltip();
            if (!tooltip.isEmpty()) {
                context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
            }
        }
    }

    private List<Text> getTooltip() {
        List<Text> tooltip = new ArrayList<>();
        if (gem != null) {
            // Gem name
            String gemName = gem.type().toString().split(":")[1];
            tooltip.add(Text.translatable("item.dominatus.gem." + gemName).formatted(Formatting.WHITE));

            // Equipped count
            if (gemData != null && gem.maxPresets() < Integer.MAX_VALUE) {
                int equippedCount = (int) gemData.getGemPresets().values().stream()
                        .filter(g -> g != null && g.type().equals(gem.type()))
                        .count();
                int maxPresets = gem.maxPresets();
                Formatting countColor = equippedCount >= maxPresets ? Formatting.RED : Formatting.GREEN;
                tooltip.add(Text.literal("Equipped: ")
                        .formatted(Formatting.GRAY)
                        .append(Text.literal(equippedCount + "/" + maxPresets).formatted(countColor)));
            }

            // Spacer
            tooltip.add(Text.empty());

            // Effects header
            tooltip.add(Text.literal("【 ").formatted(Formatting.GRAY)
                    .append(Text.translatable("item.dominatus.gem.effects").formatted(Formatting.GRAY))
                    .append(" 】").formatted(Formatting.GRAY));

            // Attribute modifiers
            gem.attributeModifiers().forEach((attributeId, modifier) -> {
                EntityAttribute attribute = Registries.ATTRIBUTE.get(attributeId);
                if (attribute != null) {
                    String operation = switch (modifier.operation()) {
                        case ADD_VALUE -> "+";
                        case ADD_MULTIPLIED_BASE -> "×";
                        case ADD_MULTIPLIED_TOTAL -> "%";
                    };
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
        return tooltip;
    }

    public GemComponent getGem() {
        return gem;
    }
}