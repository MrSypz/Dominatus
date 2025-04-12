package sypztep.dominatus.client.screen.panel;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.util.gemsystem.GemManagerHelper;
import sypztep.tyrannus.client.screen.panel.Button;

import java.util.List;
import java.util.function.Consumer;

public class GemSlotPanel extends Button {
    private final GemComponent gem;
    private final GemDataComponent gemData;
    private final Identifier texture;
    private final boolean isPresetSlot;
    private List<Text> activeTooltip; // Store tooltip for parent to render

    public GemSlotPanel(int x, int y, int width, int height, GemComponent gem, Identifier texture,
                        Consumer<GemSlotPanel> onClick, GemDataComponent gemData, boolean isPresetSlot) {
        super(x, y, width, height, Text.empty(), null, onClick != null ? (button) -> onClick.accept((GemSlotPanel) button) : null);
        this.gem = gem;
        this.gemData = gemData;
        this.texture = texture;
        this.isPresetSlot = isPresetSlot;
        this.activeTooltip = null;

        setDrawHeader(false);
        setDrawBorder(true);
        setPadding(2);
        setPlaySounds(true, true);

        if (isPresetSlot) {
            setRoundedCorners(true, 6);
            setGlowIntensity(2.0f);
            setBounceIntensity(1.5f);
            setShadowIntensity(1.5f);
        } else {
            setRoundedCorners(true, 4);
            setGlowIntensity(1.2f);
            setBounceIntensity(1.0f);
            setShadowIntensity(1.0f);
        }
    }

    @Override
    protected void handleHoverSound() {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY) || !isEnabled()) return false;
        if (button == 0 && getOnClick() != null) {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            getOnClick().accept(this);
            return true;
        }
        return button == 1;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderContents(context, mouseX, mouseY, delta);

        if (gem != null && texture != null) {
            int iconSize = Math.min(width - 4, height - 4);
            int iconX = x + (width - iconSize) / 2;
            int iconY = y + (height - iconSize) / 2 + (int) (getPressAnimation() * 1.5f);
            context.drawGuiTexture(texture, iconX, iconY, iconSize, iconSize);

            if (isPresetSlot) {
                drawSlotHighlight(context);
            }

            if (!isPresetSlot && gemData != null && gem.maxPresets() < Integer.MAX_VALUE) {
                int equippedCount = gemData.getEquippedCountForGroup(gem.group());
                int maxPresets = gem.maxPresets();
                String countText = equippedCount + "/" + maxPresets;
                int countColor = equippedCount >= maxPresets ? 0xFFFF5555 : 0xFF55FF55;

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

            if (isPresetSlot) {
                drawEmptySlotIndicator(context);
            }
        }

        if (isHovered && isEnabled()) {
            activeTooltip = getTooltip();
        } else {
            activeTooltip = null;
        }
    }

    public List<Text> getActiveTooltip() {
        return activeTooltip;
    }

    private void drawSlotHighlight(DrawContext context) {
        int borderWidth = 2;
        int colorPrimary = 0xFFFFD700;

        int cornerSize = 8;
        context.fill(x, y, x + cornerSize, y + borderWidth, colorPrimary);
        context.fill(x, y, x + borderWidth, y + cornerSize, colorPrimary);
        context.fill(x + width - cornerSize, y, x + width, y + borderWidth, colorPrimary);
        context.fill(x + width - borderWidth, y, x + width, y + cornerSize, colorPrimary);
        context.fill(x, y + height - borderWidth, x + cornerSize, y + height, colorPrimary);
        context.fill(x, y + height - cornerSize, x + borderWidth, y + height, colorPrimary);
        context.fill(x + width - cornerSize, y + height - borderWidth, x + width, y + height, colorPrimary);
        context.fill(x + width - borderWidth, y + height - cornerSize, x + width, y + height, colorPrimary);

        int glowAlpha = 0x33;
        int glowColor = (glowAlpha << 24) | 0xFFFFFF;
        context.fill(x + borderWidth, y + borderWidth, x + width - borderWidth, y + borderWidth * 2, glowColor);
    }

    private void drawEmptySlotIndicator(DrawContext context) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int plusSize = 10;
        int plusThickness = 2;
        int plusColor = 0x44FFFFFF;

        context.fill(centerX - plusSize, centerY - plusThickness / 2,
                centerX + plusSize, centerY + plusThickness / 2, plusColor);
        context.fill(centerX - plusThickness / 2, centerY - plusSize,
                centerX + plusThickness / 2, centerY + plusSize, plusColor);
    }
    private List<Text> getTooltip() {
        return GemManagerHelper.getGemTooltip(gem, isPresetSlot);
    }
}