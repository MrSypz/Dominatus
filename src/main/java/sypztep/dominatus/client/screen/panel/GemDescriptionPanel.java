package sypztep.dominatus.client.screen.panel;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.tyrannus.client.screen.panel.UIPanel;

import java.util.Map;

public class GemDescriptionPanel extends UIPanel {
    private GemComponent gem; // Mutable to update
    private final GemDataComponent gemData;
    private boolean canEquip; // Mutable to update
    private float animationProgress = 0.0f; // 0.0 = hidden, 1.0 = fully shown
    private static final float ANIMATION_SPEED = 0.2f; // Faster animation for responsiveness
    private boolean isVisible = false;
    private static final int SOLID_BG_COLOR = 0xFF1A1A1A; // Flat, non-transparent background
    private static final int SOLID_BORDER_COLOR = 0xFF424242; // Flat border base color

    public GemDescriptionPanel(int x, int y, int width, int height, GemComponent gem, GemDataComponent gemData, boolean canEquip) {
        super(x, y, width, height, null); // No title needed
        this.gem = gem;
        this.gemData = gemData;
        this.canEquip = canEquip;
        this.setDrawHeader(false);
        this.setDrawBorder(true);
        this.setPadding(5);
        updateContentBounds(); // Ensure bounds are calculated correctly
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public void update(GemComponent gem, boolean canEquip, int x, int y, int width, int height) {
        this.gem = gem;
        this.canEquip = canEquip;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updateContentBounds();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!isVisible && animationProgress <= 0.0f) {
            return; // Skip rendering if fully hidden
        }

        // Update animation
        if (isVisible) {
            animationProgress = Math.min(1.0f, animationProgress + ANIMATION_SPEED);
        } else {
            animationProgress = Math.max(0.0f, animationProgress - ANIMATION_SPEED);
        }

        if (animationProgress <= 0.0f) {
            return;
        }

        // Apply animation: slide with fade integration
        context.getMatrices().push();
        float offsetY = (-3.0f - animationProgress) * -10; // Your original slide logic
        context.getMatrices().translate(0, offsetY, 10);
        int alpha = (int) (animationProgress * 255); // Alpha for fading elements
        int borderColor = (alpha << 24) | (SOLID_BORDER_COLOR & 0xFFFFFF); // Fade border

        // Draw solid background (no fade) and fading border
        context.fill(x, y, x + width, y + height, SOLID_BG_COLOR);
        if (drawBorder) {
            context.fill(x, y, x + width, y + 1, borderColor); // Top
            context.fill(x, y + height - 1, x + width, y + height, borderColor); // Bottom
            context.fill(x, y, x + 1, y + height, borderColor); // Left
            context.fill(x + width - 1, y, x + width, y + height, borderColor); // Right
        }

        // Render contents (text) after background
        renderContents(context, mouseX, mouseY, delta);

        context.getMatrices().pop();
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        if (gem == null) return; // Skip if no gem is set
        int currentY = y + padding; // Start text at top of panel, adjusted for padding
        int textAlpha = (int) (animationProgress * 255); // Fade text

        // Render gem name (bold, gold)
        String gemName = Text.translatable("item.dominatus.gem." + gem.type().toString().split(":")[1]).getString();
        int nameColor = (textAlpha << 24) | (0xFFFFD700 & 0xFFFFFF); // Gold, fading
        context.drawTextWithShadow(textRenderer, gemName, x + padding, currentY, nameColor);
        currentY += textRenderer.fontHeight + 2;

        // Render attribute modifiers
        for (Map.Entry<Identifier, EntityAttributeModifier> entry : gem.attributeModifiers().entrySet()) {
            EntityAttribute attribute = Registries.ATTRIBUTE.get(entry.getKey());
            if (attribute != null) {
                EntityAttributeModifier modifier = entry.getValue();
                String valueText = String.format("%.1f", modifier.value());
                String attributeText = Text.translatable(attribute.getTranslationKey()).getString();

                int xPos = x + padding;
                int valueColor = modifier.value() >= 0 ?
                        (textAlpha << 24) | (0xFF55FF55 & 0xFFFFFF) : // Green for positive
                        (textAlpha << 24) | (0xFFFF5555 & 0xFFFFFF); // Red for negative
                int attributeColor = (textAlpha << 24) | (0xFFFFFF); // White

                // Draw value
                context.drawTextWithShadow(textRenderer, valueText, xPos, currentY, valueColor);
                xPos += textRenderer.getWidth(valueText) + 2;

                // Draw attribute
                context.drawTextWithShadow(textRenderer, attributeText, xPos, currentY, attributeColor);

                currentY += textRenderer.fontHeight + 2;
            }
        }

        // Render group equipped info on separate lines
        int equippedCount = (int) gemData.getGemPresets().values().stream()
                .filter(g -> g != null && g.group().equals(gem.group()))
                .count();
        int maxPresets = gem.maxPresets();
        String groupName = " -Group- " + gem.group().toString().split(":")[1];
        String equippedText = String.format("▶ Equipped: %d/%d", equippedCount, maxPresets);
        int groupColor = (textAlpha << 24) | (0xFFAAAAAA & 0xFFFFFF); // Light gray for "Group"
        int equippedColor = equippedCount >= maxPresets ?
                (textAlpha << 24) | (0xFFFF5555 & 0xFFFFFF) : // Red if full
                (textAlpha << 24) | (canEquip ? 0xFF55FF55 & 0xFFFFFF : 0xFFFF9955 & 0xFFFFFF); // Green if equippable, orange if not

        int xPos = x + padding;
        context.drawTextWithShadow(textRenderer, groupName, xPos, currentY, groupColor);
        currentY += textRenderer.fontHeight + 2; // Move to next line
        context.drawTextWithShadow(textRenderer, equippedText, xPos, currentY, equippedColor);
    }
}