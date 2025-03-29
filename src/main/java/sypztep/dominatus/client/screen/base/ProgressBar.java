package sypztep.dominatus.client.screen.base;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * A progress bar component for displaying experience, health, mana, etc.
 */
public class ProgressBar extends UIPanel {
    private float progress = 0.0f; // 0.0 to 1.0
    private float targetProgress = 0.0f; // For smooth animation
    private String valueText = "";
    private boolean showValue = true;
    private boolean showPercent = false;
    private boolean smoothAnimation = true;

    // Colors
    private int backgroundColor = 0xFF333333;
    private int borderColor = 0xFF555555;
    private int fillColor = 0xFF7FBD3E; // Default: green XP bar color
    private int textColor = 0xFFFFFFFF;

    // Size
    private int barHeight = 12;
    private int barPadding = 0; // Added to control padding around the bar

    public ProgressBar(int x, int y, int width, int height, Text title) {
        super(x, y, width, height, title);
    }

    /**
     * Create a progress bar without a title.
     */
    public ProgressBar(int x, int y, int width, int height) {
        super(x, y, width, height, null);
        setDrawHeader(false);
        setPadding(0); // Set panel padding to 0
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        // Animate progress smoothly towards target
        if (smoothAnimation && progress != targetProgress) {
            float diff = targetProgress - progress;
            progress += diff * 0.1f; // Adjust speed by changing this multiplier

            // Snap to target when very close to avoid floating point issues
            if (Math.abs(diff) < 0.001f) {
                progress = targetProgress;
            }
        }

        // Calculate bar dimensions
        int barX = getContentX() + barPadding;
        int barY = getContentY() + barPadding;
        int barWidth = getContentWidth() - (barPadding * 2);

        // Background and border
        context.fill(barX, barY, barX + barWidth, barY + barHeight, backgroundColor);
        context.fill(barX, barY, barX + barWidth, barY + 1, borderColor); // Top
        context.fill(barX, barY + barHeight - 1, barX + barWidth, barY + barHeight, borderColor); // Bottom
        context.fill(barX, barY, barX + 1, barY + barHeight, borderColor); // Left
        context.fill(barX + barWidth - 1, barY, barX + barWidth, barY + barHeight, borderColor); // Right

        // Fill bar based on progress
        int fillWidth = (int)(barWidth * progress);
        if (fillWidth > 0) {
            // Ensure fill doesn't exceed borders
            int fillX = barX + 1;
            int fillY = barY + 1;
            int fillHeight = barHeight - 2;
            int maxFillWidth = barWidth - 2;

            fillWidth = Math.min(fillWidth, maxFillWidth);

            context.fill(fillX, fillY, fillX + fillWidth, fillY + fillHeight, fillColor);

            // Add gradient shading for 3D effect
            int highlightColor = lightenColor(fillColor, 0.2f);
            int shadowColor = darkenColor(fillColor, 0.2f);

            // Highlight at top
            context.fill(fillX, fillY, fillX + fillWidth, fillY + 2, highlightColor);
            // Shadow at bottom
            context.fill(fillX, fillY + fillHeight - 2, fillX + fillWidth, fillY + fillHeight, shadowColor);
        }

        // Draw text value if enabled
        if (showValue && !valueText.isEmpty()) {
            String display;
            if (showPercent) {
                display = String.format("%s (%.0f%%)", valueText, progress * 100);
            } else {
                display = valueText;
            }

            int textWidth = textRenderer.getWidth(display);
            context.drawTextWithShadow(
                    textRenderer,
                    display,
                    barX + (barWidth - textWidth) / 2,
                    barY + (barHeight - textRenderer.fontHeight) / 2,
                    textColor
            );
        }
    }

    /**
     * Set the progress value (0.0 to 1.0).
     */
    public void setProgress(float progress) {
        this.targetProgress = Math.max(0.0f, Math.min(1.0f, progress));
        if (!smoothAnimation) {
            this.progress = this.targetProgress;
        }
    }

    /**
     * Set the text to display on the progress bar.
     */
    public void setValueText(String text) {
        this.valueText = text;
    }

    /**
     * Set whether to show the value text.
     */
    public void setShowValue(boolean show) {
        this.showValue = show;
    }

    /**
     * Set whether to show the percentage.
     */
    public void setShowPercent(boolean show) {
        this.showPercent = show;
    }

    /**
     * Set whether to animate progress changes smoothly.
     */
    public void setSmoothAnimation(boolean smooth) {
        this.smoothAnimation = smooth;
    }

    /**
     * Set the fill color of the progress bar.
     */
    public void setFillColor(int color) {
        this.fillColor = color;
    }

    /**
     * Set the background color of the progress bar.
     */
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    /**
     * Set the border color of the progress bar.
     */
    public void setBorderColor(int color) {
        this.borderColor = color;
    }

    /**
     * Set the text color.
     */
    public void setTextColor(int color) {
        this.textColor = color;
    }

    /**
     * Set the height of the progress bar.
     */
    public void setBarHeight(int height) {
        this.barHeight = height;
    }

    /**
     * Set the padding around the progress bar.
     */
    public void setBarPadding(int padding) {
        this.barPadding = padding;
    }

    /**
     * Lighten a color by a factor.
     */
    private int lightenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        r = Math.min(255, (int)(r + (255 - r) * factor));
        g = Math.min(255, (int)(g + (255 - g) * factor));
        b = Math.min(255, (int)(b + (255 - b) * factor));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Darken a color by a factor.
     */
    private int darkenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        r = Math.max(0, (int)(r * (1 - factor)));
        g = Math.max(0, (int)(g * (1 - factor)));
        b = Math.max(0, (int)(b * (1 - factor)));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}