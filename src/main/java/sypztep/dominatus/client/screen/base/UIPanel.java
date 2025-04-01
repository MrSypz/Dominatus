package sypztep.dominatus.client.screen.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a UI panel that can be added to a screen.
 * Supports child widgets and custom rendering with smooth hover effects.
 */
public class UIPanel {
    // Panel appearance
    protected static final int PANEL_BACKGROUND = 0xFF1A1A1A;
    protected static final int PANEL_BORDER = 0xFF424242;
    protected static final int PANEL_BORDER_HIGHLIGHT = 0xFF6D6D6D;
    protected static final int HEADER_COLOR = 0xFFFFD700;
    protected static final int HEADER_BG = 0xFF212121;

    // Panel dimensions and position
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    // Panel content area dimensions
    protected int contentX;
    protected int contentY;
    protected int contentWidth;
    protected int contentHeight;

    // Panel title and padding
    protected Text title;
    protected int padding = 10;
    protected boolean drawHeader = true;
    protected boolean drawBorder = true;
    protected boolean isHovered = false;

    // Animation state for smooth transitions
    protected float hoverAnimation = 0.0f; // 0.0 = not hovered, 1.0 = fully hovered
    protected static final float HOVER_ANIMATION_SPEED = 0.1f;

    // Client and text renderer references
    protected final MinecraftClient client;
    protected final TextRenderer textRenderer;

    // Child widgets
    protected final List<Drawable> drawables = new ArrayList<>();
    protected final List<Element> elements = new ArrayList<>();

    public UIPanel(int x, int y, int width, int height, Text title) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.title = title;
        this.client = MinecraftClient.getInstance();
        this.textRenderer = client.textRenderer;

        // Calculate content area
        updateContentBounds();
    }

    /**
     * Update the bounds of the content area based on the panel dimensions.
     */
    protected void updateContentBounds() {
        int headerHeight = drawHeader ? textRenderer.fontHeight + (padding * 2) : 0;

        contentX = x + padding;
        contentY = y + headerHeight + (drawHeader ? padding : 0);
        contentWidth = width - (padding * 2);
        contentHeight = height - headerHeight - padding - (drawHeader ? padding : 0);
    }

    /**
     * Render the panel.
     */
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Update hover state
        isHovered = isMouseOver(mouseX, mouseY);

        // Update hover animation
        if (isHovered) {
            hoverAnimation = Math.min(1.0f, hoverAnimation + HOVER_ANIMATION_SPEED);
        } else {
            hoverAnimation = Math.max(0.0f, hoverAnimation - HOVER_ANIMATION_SPEED);
        }

        // Draw panel background and border
        drawPanel(context);

        // Draw header if needed
        if (drawHeader && title != null) {
            drawHeader(context);
        }

        // Draw panel content
        renderContents(context, mouseX, mouseY, delta);

        // Draw child widgets
        for (Drawable drawable : drawables) {
            drawable.render(context, mouseX, mouseY, delta);
        }
    }

    /**
     * Draw the panel background and border.
     */
    protected void drawPanel(DrawContext context) {
        // Background - animate slight brightening when hovered
        int bgColor = interpolateColor(PANEL_BACKGROUND, 0xFF222222, hoverAnimation);
        context.fill(x, y, x + width, y + height, bgColor);

        // Borders - animate to highlight color
        if (drawBorder) {
            int borderColor = interpolateColor(PANEL_BORDER, PANEL_BORDER_HIGHLIGHT, hoverAnimation);

            // Top border
            context.fill(x, y, x + width, y + 1, borderColor);
            // Bottom border
            context.fill(x, y + height - 1, x + width, y + height, borderColor);
            // Left border
            context.fill(x, y, x + 1, y + height, borderColor);
            // Right border
            context.fill(x + width - 1, y, x + width, y + height, borderColor);

            // Corner details
            int cornerColor = interpolateColor(0xFF4A4A4A, PANEL_BORDER_HIGHLIGHT, hoverAnimation);
            context.fill(x + 1, y + 1, x + 3, y + 3, cornerColor);
            context.fill(x + width - 3, y + 1, x + width - 1, y + 3, cornerColor);
            context.fill(x + 1, y + height - 3, x + 3, y + height - 1, cornerColor);
            context.fill(x + width - 3, y + height - 3, x + width - 1, y + height - 1, cornerColor);

            // Add an elegant glow effect on hover
            if (hoverAnimation > 0.0f) {
                int glowAlpha = (int)(40 * hoverAnimation);
                int glowColor = (glowAlpha << 24) | 0xFFFFFF; // White glow with variable alpha

                // Subtle outer glow
                context.fill(x - 1, y - 1, x + width + 1, y, glowColor); // Top
                context.fill(x - 1, y + height, x + width + 1, y + height + 1, glowColor); // Bottom
                context.fill(x - 1, y, x, y + height, glowColor); // Left
                context.fill(x + width, y, x + width + 1, y + height, glowColor); // Right
            }
        }
    }

    /**
     * Draw the panel header.
     */
    protected void drawHeader(DrawContext context) {
        int headerHeight = textRenderer.fontHeight + padding * 2;

        // Header background with hover effect
        int headerBgColor = interpolateColor(HEADER_BG, 0xFF2A2A2A, hoverAnimation);
        context.fill(x + 1, y + 1, x + width - 1, y + headerHeight, headerBgColor);

        // Header text with subtle animation
        int titleWidth = textRenderer.getWidth(title);
        float scale = 1.0f + (0.05f * hoverAnimation); // Slight scale up on hover
        int titleColor = interpolateColor(HEADER_COLOR, 0xFFFFFFFF, hoverAnimation * 0.3f); // Subtle brightening

        context.getMatrices().push();
        context.getMatrices().translate(
                x + (width - titleWidth * scale) / 2,
                y + padding + (textRenderer.fontHeight * (1 - scale)) / 2,
                0);
        context.getMatrices().scale(scale, scale, 1.0f);

        context.drawTextWithShadow(
                textRenderer,
                title,
                0, 0,
                titleColor
        );

        context.getMatrices().pop();

        // Header divider with animation
        drawGradientDivider(context, x + padding, y + headerHeight + 1, width - (padding * 2), hoverAnimation);
    }

    /**
     * Draw a gradient divider line.
     */
    protected void drawGradientDivider(DrawContext context, int x, int y, int width, float hoverStrength) {
        int segments = width / 2;
        int segmentWidth = width / segments;

        for (int i = 0; i < segments; i++) {
            float ratio = (float)i / segments;

            float baseEdgeRatio = Math.min(ratio, 1 - ratio) * 2;
            float animatedRatio = baseEdgeRatio + ((1 - baseEdgeRatio) * 0.3f * hoverStrength);

            int alpha = (int)(animatedRatio * 255);
            // Shift color slightly toward gold when hovered
            int color;
            if (hoverStrength > 0) {
                int r = 0x66 + (int)((0xA0 - 0x66) * hoverStrength);
                int g = 0x66 + (int)((0x80 - 0x66) * hoverStrength);
                int b = 0x66;
                color = (alpha << 24) | (r << 16) | (g << 8) | b;
            } else {
                color = (alpha << 24) | 0x666666;
            }

            int segX = x + (i * segmentWidth);
            context.fill(segX, y, segX + segmentWidth, y + 1, color);
        }
    }

    /**
     * Render the panel contents.
     * Override this to add custom rendering.
     */
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        // Default implementation does nothing
    }

    /**
     * Interpolate between two colors based on a progress value (0.0 to 1.0).
     */
    protected static int interpolateColor(int color1, int color2, float progress) {
        if (progress <= 0) return color1;
        if (progress >= 1) return color2;

        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int)(a1 + (a2 - a1) * progress);
        int r = (int)(r1 + (r2 - r1) * progress);
        int g = (int)(g1 + (g2 - g1) * progress);
        int b = (int)(b1 + (b2 - b1) * progress);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Add a child widget to this panel.
     */
    public <T extends Drawable & Element> T addChild(T child) {
        drawables.add(child);
        elements.add(child);

        if (child instanceof ClickableWidget clickable) {
            int originalX = clickable.getX();
            int originalY = clickable.getY();
            clickable.setPosition(x + originalX, y + originalY);
        }

        return child;
    }

    /**
     * Handle mouse click in the panel.
     * @return true if the click was handled
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            for (Element element : elements) {
                if (element.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handle scrolling within the panel.
     * @return true if the scrolling was handled
     */
    public boolean handleScrolling(double horizontalAmount, double verticalAmount) {
        return false;
    }

    /**
     * Check if the mouse is over the panel.
     */
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    /**
     * Get the content area X position.
     */
    public int getContentX() {
        return contentX;
    }

    /**
     * Get the content area Y position.
     */
    public int getContentY() {
        return contentY;
    }

    /**
     * Get the content area width.
     */
    public int getContentWidth() {
        return contentWidth;
    }

    /**
     * Get the content area height.
     */
    public int getContentHeight() {
        return contentHeight;
    }

    /**
     * Set the panel title.
     */
    public void setTitle(Text title) {
        this.title = title;
    }

    /**
     * Set whether the header should be drawn.
     */
    public void setDrawHeader(boolean drawHeader) {
        this.drawHeader = drawHeader;
        updateContentBounds();
    }

    /**
     * Set whether the border should be drawn.
     */
    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }

    /**
     * Set the padding.
     */
    public void setPadding(int padding) {
        this.padding = padding;
        updateContentBounds();
    }
}