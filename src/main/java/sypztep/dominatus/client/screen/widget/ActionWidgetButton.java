package sypztep.dominatus.client.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import sypztep.dominatus.client.util.ColorUtils;
import sypztep.dominatus.common.component.living.LivingLevelComponent;

public abstract class ActionWidgetButton extends ClickableWidget {
    protected static final int DEFAULT_BG_COLOR = 0xFF2C2C2C;
    protected static final int DEFAULT_HOVER_COLOR = 0xFF3C3C3C;
    protected static final int DEFAULT_PRESSED_COLOR = 0xFF1C1C1C;
    protected static final int DEFAULT_DISABLED_COLOR = 0xFF1A1A1A;

    protected static final int DEFAULT_BORDER_COLOR = 0xFF555555;
    protected static final int DEFAULT_HOVER_BORDER_COLOR = 0xFF777777;
    protected static final int DEFAULT_DISABLED_BORDER_COLOR = 0xFF333333;

    protected static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
    protected static final int DEFAULT_DISABLED_TEXT_COLOR = 0xFF888888;
    protected static final int DEFAULT_HOVER_TEXT_COLOR = 0xFFFFFF00; // Yellow on hover

    // Widget state
    protected float hoverAnimation = 0f;
    protected boolean wasHovered = false;
    protected boolean isPressed = false;
    protected final LivingLevelComponent stats;

    protected MinecraftClient client;

    public ActionWidgetButton(int x, int y, int width, int height, Text message, LivingLevelComponent stats, MinecraftClient client) {
        super(x, y, width, height, message);
        this.stats = stats;
        this.client = client;
    }


    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        updateAnimations(delta);

        boolean isHovered = isMouseOver(mouseX, mouseY);
        boolean isEnabled = active;

        // Get colors based on state
        ButtonColors colors = getButtonColors(isHovered, isPressed, isEnabled);

        // Render button background
        renderBackground(context, colors);

        // Render button border
        renderBorder(context, colors);

        // Render button text
        renderText(context, colors);

        // Render additional overlays (for subclasses)
        renderAdditionalOverlays(context, mouseX, mouseY, delta, isHovered, isPressed);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !active) return false;

        if (isMouseOver(mouseX, mouseY) && button == 0) { // Left click only
            isPressed = true;
            playClickSound();
            onClick(mouseX, mouseY);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            isPressed = false;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false; // No drag behavior by default
    }

    public abstract void onClick(double mouseX, double mouseY);

    protected void updateAnimations(float delta) {
        boolean isHovered = isHovered();

        if (isHovered && !wasHovered) {
            hoverAnimation = Math.min(1f, hoverAnimation + delta * 4f);
        } else if (!isHovered && wasHovered) {
            hoverAnimation = Math.max(0f, hoverAnimation - delta * 4f);
        } else if (isHovered) {
            hoverAnimation = Math.min(1f, hoverAnimation + delta * 4f);
        } else {
            hoverAnimation = Math.max(0f, hoverAnimation - delta * 4f);
        }

        wasHovered = isHovered;
    }

    /**
     * Get button colors based on current state
     */
    protected ButtonColors getButtonColors(boolean isHovered, boolean isPressed, boolean isEnabled) {
        int bgColor, borderColor, textColor;

        if (!isEnabled) {
            bgColor = getDisabledBackgroundColor();
            borderColor = getDisabledBorderColor();
            textColor = getDisabledTextColor();
        } else if (isPressed) {
            bgColor = getPressedBackgroundColor();
            borderColor = getHoverBorderColor();
            textColor = getHoverTextColor();
        } else if (isHovered) {
            // Interpolate hover colors
            bgColor = ColorUtils.interpolateColor(getBackgroundColor(), getHoverBackgroundColor(), hoverAnimation);
            borderColor = ColorUtils.interpolateColor(getBorderColor(), getHoverBorderColor(), hoverAnimation);
            textColor = ColorUtils.interpolateColor(getTextColor(), getHoverTextColor(), hoverAnimation);
        } else {
            bgColor = getBackgroundColor();
            borderColor = getBorderColor();
            textColor = getTextColor();
        }

        return new ButtonColors(bgColor, borderColor, textColor);
    }

    protected void renderBackground(DrawContext context, ButtonColors colors) {
        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), colors.background());
    }
    protected void renderBorder(DrawContext context, ButtonColors colors) {
        int borderThickness = getBorderThickness();

        context.fill(getX(), getY(), getX() + getWidth(), getY() + borderThickness, colors.border());
        context.fill(getX(), getY() + getHeight() - borderThickness, getX() + getWidth(), getY() + getHeight(), colors.border());
        context.fill(getX(), getY(), getX() + borderThickness, getY() + getHeight(), colors.border());
        context.fill(getX() + getWidth() - borderThickness, getY(), getX() + getWidth(), getY() + getHeight(), colors.border());
    }

    protected void renderText(DrawContext context, ButtonColors colors) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int textX = getX() + (getWidth() - textRenderer.getWidth(getMessage())) / 2;
        int textY = getY() + (getHeight() - textRenderer.fontHeight) / 2;

        if (isPressed) {
            textX += getPressedOffsetX();
            textY += getPressedOffsetY();
        }

        // Render text with shadow if enabled
        if (hasTextShadow()) {
            context.drawTextWithShadow(textRenderer, getMessage(), textX, textY, colors.text());
        } else {
            context.drawText(textRenderer, getMessage(), textX, textY, colors.text(), false);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    /**
     * Override this method in subclasses for additional rendering
     */
    protected void renderAdditionalOverlays(DrawContext context, int mouseX, int mouseY, float delta, boolean isHovered, boolean isPressed) {
        // Default: no additional overlays
    }

    /**
     * Play click sound - can be overridden
     */
    protected void playClickSound() {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean isHovered() {
        boolean currentlyHovered = super.isHovered();
        if (currentlyHovered && !wasHovered && visible && active)
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_HAT, 1.8F));

        return currentlyHovered;
    }

    protected int getBorderThickness() {
        return 1;
    }

    protected int getPressedOffsetX() {
        return 1;
    }

    protected int getPressedOffsetY() {
        return 1;
    }

    protected boolean hasTextShadow() {
        return false;
    }

    // Color getters (can be overridden by subclasses)
    protected int getBackgroundColor() {
        return DEFAULT_BG_COLOR;
    }

    protected int getHoverBackgroundColor() {
        return DEFAULT_HOVER_COLOR;
    }

    protected int getPressedBackgroundColor() {
        return DEFAULT_PRESSED_COLOR;
    }

    protected int getDisabledBackgroundColor() {
        return DEFAULT_DISABLED_COLOR;
    }

    protected int getBorderColor() {
        return DEFAULT_BORDER_COLOR;
    }

    protected int getHoverBorderColor() {
        return DEFAULT_HOVER_BORDER_COLOR;
    }

    protected int getDisabledBorderColor() {
        return DEFAULT_DISABLED_BORDER_COLOR;
    }

    protected int getTextColor() {
        return DEFAULT_TEXT_COLOR;
    }

    protected int getHoverTextColor() {
        return DEFAULT_HOVER_TEXT_COLOR;
    }

    protected int getDisabledTextColor() {
        return DEFAULT_DISABLED_TEXT_COLOR;
    }

    // Getters for widget state
    public boolean isPressed() {
        return isPressed;
    }

    public float getHoverAnimation() {
        return hoverAnimation;
    }

    public LivingLevelComponent getStats() {
        return stats;
    }

    // Position and size methods from ClickableWidget
    public void setPosition(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // Enable/disable methods
    public void setEnabled(boolean enabled) {
        this.active = enabled;
    }

    public boolean isEnabled() {
        return this.active;
    }

    // Visibility methods
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Record for button color state
     */
    protected record ButtonColors(int background, int border, int text) {
    }
}