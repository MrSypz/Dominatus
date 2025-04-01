package sypztep.dominatus.client.screen.base;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A panel with scrollable content and smooth hover effects.
 */
public abstract class ScrollablePanel extends UIPanel {
    protected double scrollAmount = 0;
    protected int contentTotalHeight = 0;
    protected int maxScroll = 0;
    protected boolean isDragging = false;
    protected boolean enableScrollbar = true;
    protected int scrollbarWidth = 6;
    protected int scrollbarPadding = 2;

    // Animation for scrollbar
    protected float scrollbarHoverAnimation = 0.0f;
    protected boolean scrollbarHovered = false;

    public ScrollablePanel(int x, int y, int width, int height, Text title) {
        super(x, y, width, height, title);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw panel background and header with hover animation
        super.render(context, mouseX, mouseY, delta);

        // Update max scroll based on content height
        updateMaxScroll();

        // Setup scissor to clip content within panel boundaries
        int scissorX = getContentX();
        int scissorY = getContentY();
        int scissorWidth = getContentWidth();
        int scissorHeight = getContentHeight();

        context.enableScissor(scissorX, scissorY, scissorX + scissorWidth, scissorY + scissorHeight);

        // Render scrollable content
        renderScrollableContent(context, mouseX, mouseY, delta);

        context.disableScissor();

        // Render scrollbar if needed
        if (enableScrollbar && maxScroll > 0) {
            // Check if scrollbar is being hovered
            int scrollbarX = x + width - scrollbarWidth - scrollbarPadding;
            int scrollbarY = getContentY();
            int scrollbarHeight = getContentHeight();

            scrollbarHovered = mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight;

            // Update animation
            if (scrollbarHovered || isDragging) {
                scrollbarHoverAnimation = Math.min(1.0f, scrollbarHoverAnimation + 0.15f);
            } else {
                scrollbarHoverAnimation = Math.max(0.0f, scrollbarHoverAnimation - 0.1f);
            }

            renderScrollbar(context, mouseX, mouseY);
        }
    }

    /**
     * Render the content that should scroll.
     * This is called with scissor already enabled.
     */
    protected abstract void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta);

    /**
     * Update the maximum scroll amount based on content height.
     */
    protected void updateMaxScroll() {
        int visibleHeight = getContentHeight();
        maxScroll = Math.max(0, contentTotalHeight - visibleHeight);
        scrollAmount = MathHelper.clamp(scrollAmount, 0, maxScroll);
    }

    /**
     * Set the total height of the content.
     */
    protected void setContentHeight(int height) {
        this.contentTotalHeight = height;
        updateMaxScroll();
    }

    /**
     * Render the scrollbar with hover animation.
     */
    protected void renderScrollbar(DrawContext context, int mouseX, int mouseY) {
        int visibleHeight = getContentHeight();

        if (contentTotalHeight > visibleHeight) {
            int scrollbarX = x + width - scrollbarWidth - scrollbarPadding;
            int scrollbarY = getContentY();
            int scrollbarHeight = getContentHeight();

            // Scrollbar background with subtle animation
            int bgAlpha = 25 + (int)(55 * scrollbarHoverAnimation); // More visible when hovered
            context.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight,
                    (bgAlpha << 24));

            // Scrollbar handle
            if (maxScroll > 0) {
                int handleHeight = Math.max(20, scrollbarHeight * visibleHeight / contentTotalHeight);
                int handleY = scrollbarY + (int)((scrollbarHeight - handleHeight) * scrollAmount / maxScroll);

                // Animate handle size when hovered
                int handleExpansion = (int)(scrollbarHoverAnimation);
                scrollbarX -= handleExpansion;
                handleHeight += handleExpansion * 2;

                // Animate handle color
                int baseAlpha = 120 + (int)(135 * scrollbarHoverAnimation);
                int handleBgColor = (baseAlpha << 24) | 0x666666;
                int handleFgColor = interpolateColor(0xFFAAAAAA, 0xFFFFFFFF, scrollbarHoverAnimation);

                // Draw handle with subtle glow effect
                if (scrollbarHoverAnimation > 0.1f) {
                    int glowSize = (int)(4 * scrollbarHoverAnimation);
                    int glowAlpha = (int)(40 * scrollbarHoverAnimation);
                    int glowColor = (glowAlpha << 24) | 0xFFFFFF; // White glow

                    // Draw glow
                    context.fill(scrollbarX - glowSize, handleY - glowSize,
                            scrollbarX + scrollbarWidth + glowSize, handleY + handleHeight + glowSize,
                            glowColor);
                }

                // Draw handle background
                context.fill(scrollbarX, handleY, scrollbarX + scrollbarWidth, handleY + handleHeight, handleBgColor);

                // Draw handle foreground with rounded corners effect
                context.fill(scrollbarX + 1, handleY + 1, scrollbarX + scrollbarWidth - 1, handleY + handleHeight - 1, handleFgColor);

                // Draw handle grip lines when hovered
                if (scrollbarHoverAnimation > 0.5f) {
                    int lineY = handleY + handleHeight / 2 - 3;
                    int lineAlpha = (int)(255 * ((scrollbarHoverAnimation - 0.5f) * 2));
                    int lineColor = (lineAlpha << 24) | 0x999999;

                    for (int i = 0; i < 3; i++) {
                        context.fill(scrollbarX + 2, lineY + (i * 3), scrollbarX + scrollbarWidth - 2, lineY + (i * 3) + 1, lineColor);
                    }
                }
            }
        }
    }

    @Override
    public boolean handleScrolling(double horizontalAmount, double verticalAmount) {
        if (maxScroll > 0) {
            double targetScroll = scrollAmount - verticalAmount * 20;

            scrollAmount = MathHelper.lerp(0.3, scrollAmount, targetScroll);
            scrollAmount = MathHelper.clamp(scrollAmount, 0, maxScroll);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if clicking on the scrollbar
        if (enableScrollbar && maxScroll > 0) {
            int scrollbarX = x + width - scrollbarWidth - scrollbarPadding;
            int scrollbarY = getContentY();
            int scrollbarHeight = getContentHeight();

            if (mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight) {
                isDragging = true;
                // Calculate new scroll position
                double scrollRatio = (mouseY - scrollbarY) / (double)scrollbarHeight;
                scrollAmount = scrollRatio * maxScroll;
                scrollAmount = MathHelper.clamp(scrollAmount, 0, maxScroll);
                return true;
            }
        }

        // Handle normal clicks
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Handle mouse drag for scrollbar dragging.
     */
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging) {
            int scrollbarY = getContentY();
            int scrollbarHeight = getContentHeight();

            double scrollRatio = (mouseY - scrollbarY) / (double)scrollbarHeight;
            scrollAmount = scrollRatio * maxScroll;
            scrollAmount = MathHelper.clamp(scrollAmount, 0, maxScroll);
            return true;
        }
        return false;
    }

    /**
     * Handle mouse release to end dragging.
     */
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        return false;
    }
    /**
     * Wrap text to fit within a given width.
     */
    protected List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.toString().isEmpty()
                    ? word
                    : currentLine + " " + word;

            if (textRenderer.getWidth(testLine) <= maxWidth) {
                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    public double getScrollAmount() {
        return scrollAmount;
    }
}