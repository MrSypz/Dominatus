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
    private final GemDataComponent gemData;
    private final Identifier texture;
    private final boolean isPresetSlot;

    public GemSlotPanel(int x, int y, int width, int height, GemComponent gem, Identifier texture,
                        Consumer<GemSlotPanel> onClick, GemDataComponent gemData, boolean isPresetSlot) {
        super(x, y, width, height, Text.empty(), null, onClick != null ? (button) -> onClick.accept((GemSlotPanel) button) : null);
        this.gem = gem;
        this.gemData = gemData;
        this.texture = texture;
        this.isPresetSlot = isPresetSlot;

        setDrawHeader(false);
        setDrawBorder(true);
        setPadding(2);
        setPlaySounds(true, true);

        // Different visual appearances based on slot type
        if (isPresetSlot) {
            setRoundedCorners(true, 6); // More rounded corners for preset slots
            setGlowIntensity(2.0f);     // Stronger glow for preset slots
            setBounceIntensity(1.5f);   // More bounce effect
            setShadowIntensity(1.5f);   // Deeper shadow for 3D effect
        } else {
            setRoundedCorners(true, 4); // Standard rounded corners
            setGlowIntensity(1.2f);     // Standard glow
            setBounceIntensity(1.0f);   // Standard bounce
            setShadowIntensity(1.0f);   // Standard shadow
        }
    }

    @Override
    protected void handleHoverSound() {
        // ว่างไว้ ไม่เอาเสียง แม่งบัคโง่ๆ
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderContents(context, mouseX, mouseY, delta); // Render button background and effects

        if (gem != null && texture != null) {
            int iconSize = Math.min(width - 4, height - 4);
            int iconX = x + (width - iconSize) / 2;
            int iconY = y + (height - iconSize) / 2 + (int) (getPressAnimation() * 1.5f); // Apply press animation
            context.drawGuiTexture(texture, iconX, iconY, iconSize, iconSize);

            if (isPresetSlot) {
                drawSlotHighlight(context);
            }

            if (!isPresetSlot && gemData != null && gem.maxPresets() < Integer.MAX_VALUE) {
                int equippedCount = (int) gemData.getGemPresets().values().stream()
                        .filter(g -> g != null && g.type().equals(gem.type()))
                        .count();
                int maxPresets = gem.maxPresets();
                String countText = equippedCount + "/" + maxPresets;
                int countColor = equippedCount >= maxPresets ? 0xFFFF5555 : 0xFF55FF55; // Red if maxed, green if not

                int textWidth = textRenderer.getWidth(countText);
                int textX = x + (width - textWidth) / 2;
                int textY = y + height - textRenderer.fontHeight - 2;
                context.drawTextWithShadow(textRenderer, countText, textX, textY, countColor);
            }
        } else {
            String emptyText = isPresetSlot ? "Empty" : "No Gem";
            int textColor = isEnabled() ? (isPresetSlot ? 0xFF777777 : 0xFF555555) : 0xFF333333;

            context.drawTextWithShadow(textRenderer, emptyText,
                    x + (width - textRenderer.getWidth(emptyText)) / 2,
                    y - 15 + (height - textRenderer.fontHeight) / 2 + (int) (getPressAnimation() * 1.5f),
                    textColor);

            // For empty preset slots, draw a subtle indicator
            if (isPresetSlot) {
                drawEmptySlotIndicator(context);
            }
        }

        // Render tooltip if hovered
        if (isHovered && isEnabled()) {
            List<Text> tooltip = getTooltip();
            if (!tooltip.isEmpty()) {
                context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
            }
        }
    }
    private void drawSlotHighlight(DrawContext context) {
        int borderWidth = 2;
        int colorPrimary = 0xFFFFD700;   // Gold

        // Draw corner accents - this creates a subtle "star" feeling
        int cornerSize = 8;

        // Top-left corner
        context.fill(x, y, x + cornerSize, y + borderWidth, colorPrimary);
        context.fill(x, y, x + borderWidth, y + cornerSize, colorPrimary);

        // Top-right corner
        context.fill(x + width - cornerSize, y, x + width, y + borderWidth, colorPrimary);
        context.fill(x + width - borderWidth, y, x + width, y + cornerSize, colorPrimary);

        // Bottom-left corner
        context.fill(x, y + height - borderWidth, x + cornerSize, y + height, colorPrimary);
        context.fill(x, y + height - cornerSize, x + borderWidth, y + height, colorPrimary);

        // Bottom-right corner
        context.fill(x + width - cornerSize, y + height - borderWidth, x + width, y + height, colorPrimary);
        context.fill(x + width - borderWidth, y + height - cornerSize, x + width, y + height, colorPrimary);

        // Draw subtle inner glow
        int glowAlpha = 0x33;
        int glowColor = (glowAlpha << 24) | 0xFFFFFF;
        context.fill(x + borderWidth, y + borderWidth, x + width - borderWidth, y + borderWidth * 2, glowColor);
    }

    // Draw indicator for empty preset slots
    private void drawEmptySlotIndicator(DrawContext context) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int plusSize = 10;
        int plusThickness = 2;
        int plusColor = 0x44FFFFFF; // Semi-transparent white

        context.fill(centerX - plusSize, centerY - plusThickness / 2,
                centerX + plusSize, centerY + plusThickness / 2, plusColor);
        context.fill(centerX - plusThickness / 2, centerY - plusSize,
                centerX + plusThickness / 2, centerY + plusSize, plusColor);
    }

    private List<Text> getTooltip() {
        List<Text> tooltip = new ArrayList<>();
        if (gem != null) {
            // Gem name with rarity color
            String gemName = gem.type().toString().split(":")[1];
            tooltip.add(Text.translatable("item.dominatus.gem." + gemName).formatted(Formatting.GOLD));

            // Show equip/unequip instruction
            if (isPresetSlot) {
                tooltip.add(Text.literal("Left-click to unequip").formatted(Formatting.YELLOW));
            } else {
                tooltip.add(Text.literal("Click to equip").formatted(Formatting.GREEN));

                // Show inventory count
                int inventoryCount = (int) gemData.getGemInventory().stream()
                        .filter(g -> g.type().equals(gem.type()))
                        .count();
                tooltip.add(Text.literal("In Inventory: " + inventoryCount).formatted(Formatting.AQUA));
            }

            tooltip.add(Text.empty());

            // Effects header with fancy formatting
            tooltip.add(Text.literal("✧ ").formatted(Formatting.GOLD)
                    .append(Text.translatable("item.dominatus.gem.effects").formatted(Formatting.YELLOW))
                    .append(" ✧").formatted(Formatting.GOLD));

            // Attribute modifiers with improved formatting
            gem.attributeModifiers().forEach((attributeId, modifier) -> {
                EntityAttribute attribute = Registries.ATTRIBUTE.get(attributeId);
                if (attribute != null) {
                    String operation = switch (modifier.operation()) {
                        case ADD_VALUE -> "➕";
                        case ADD_MULTIPLIED_BASE -> "✕";
                        case ADD_MULTIPLIED_TOTAL -> "⚝";
                    };
                    MutableText effectText = Text.literal(operation + " ")
                            .formatted(Formatting.AQUA)
                            .append(Text.literal(String.format("%.1f", modifier.value()))
                                    .formatted(Formatting.GREEN))
                            .append(" ")
                            .append(Text.translatable(attribute.getTranslationKey())
                                    .formatted(Formatting.WHITE));
                    tooltip.add(effectText);
                }
            });
        } else if (isPresetSlot) {
            tooltip.add(Text.literal("Empty Preset Slot").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Click the gem in Inventory to equip").formatted(Formatting.YELLOW));
        }
        return tooltip;
    }
}