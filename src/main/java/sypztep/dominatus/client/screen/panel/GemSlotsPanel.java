package sypztep.dominatus.client.screen.panel;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import sypztep.tyrannus.client.screen.panel.UIPanel;

public class GemSlotsPanel extends UIPanel {
    private static final int SLOT_SIZE = 18; // Slightly larger slots for better visibility
    private static final int CENTER_EXTRA_SIZE = 22; // Extra slot is bigger

    // Slot positions relative to center
    private static final int[][] SLOT_POSITIONS = {
            {0, -64},  // Top
            {64, 0},   // Right
            {0, 64},   // Bottom
            {-64, 0},  // Left
            {48, -48}, // Top-Right
            {48, 48},  // Bottom-Right
            {-48, 48}, // Bottom-Left
            {-48, -48},// Top-Left
            {0, 0}     // Center (Extra slot)
    };

    // Animation properties
    private static final float HOVER_ANIMATION_SPEED = 0.5f; // Speed of animation (0.0-1.0)
    private final float[] slotHoverAnimations = new float[SLOT_POSITIONS.length]; // Tracks animation progress for each slot

    public GemSlotsPanel(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        // Calculate center position
        context.fill(x + 1, y + 1, x + width - 2, y + height - 2, 0xFF1A1A1A);

        int centerX = x + width / 2;
        int centerY = y + height / 2;

        // Process which slot is currently being hovered
        int hoveredSlotIndex = -1;
        for (int i = 0; i < SLOT_POSITIONS.length; i++) {
            int size = (i == 8) ? CENTER_EXTRA_SIZE : SLOT_SIZE;
            int slotX = centerX + SLOT_POSITIONS[i][0] - size/2;
            int slotY = centerY + SLOT_POSITIONS[i][1] - size/2;

            // Check if mouse is hovering this slot
            if (mouseX >= slotX && mouseX < slotX + size &&
                    mouseY >= slotY && mouseY < slotY + size) {
                hoveredSlotIndex = i;
                break;
            }
        }

        // Update animation progress for each slot
        updateSlotAnimations(hoveredSlotIndex, delta);

        // Draw all slots
        for (int i = 0; i < SLOT_POSITIONS.length; i++) {
            int size = (i == 8) ? CENTER_EXTRA_SIZE : SLOT_SIZE;
            int slotX = centerX + SLOT_POSITIONS[i][0] - size/2;
            int slotY = centerY + SLOT_POSITIONS[i][1] - size/2;

            // Draw the slot with appropriate parameters and animation progress
            drawSlot(context, slotX, slotY, size, i == 8, slotHoverAnimations[i]);
        }
    }

    /**
     * Updates the animation progress for all slots based on hover state
     *
     * @param hoveredIndex The index of the slot being hovered, or -1 if none
     * @param delta The time delta since last frame
     */
    private void updateSlotAnimations(int hoveredIndex, float delta) {
        float animationStep = HOVER_ANIMATION_SPEED * (delta * 0.6f);

        for (int i = 0; i < slotHoverAnimations.length; i++) {
            if (i == hoveredIndex) {
                slotHoverAnimations[i] = Math.min(1.0f, slotHoverAnimations[i] + animationStep);
            } else {
                slotHoverAnimations[i] = Math.max(0.0f, slotHoverAnimations[i] - animationStep);
            }
        }
    }

    /**
     * Draws a slot with animated styling.
     *
     * @param context The draw context
     * @param x X position of the slot
     * @param y Y position of the slot
     * @param size Size of the slot
     * @param isCenter Whether this is the center slot
     * @param hoverProgress Animation progress for hover effect (0.0 - 1.0)
     */
    private void drawSlot(DrawContext context, int x, int y, int size, boolean isCenter, float hoverProgress) {
        // Outer border with animation
        int borderColor = interpolateColor(0xFF2A2A2A, 0xFF3A3A3A, hoverProgress);
        context.fill(x, y, x + size, y + size, borderColor);

        // Inner background with animation
        int normalBgColor = isCenter ? 0xFF3A3A3A : 0xFF1A1A1A;
        int hoverBgColor = isCenter ? 0xFF4A4A4A : 0xFF2F2F2F;
        int bgColor = interpolateColor(normalBgColor, hoverBgColor, hoverProgress);

        context.fill(
                x + 1, y + 1,
                x + size - 1, y + size - 1,
                bgColor
        );

        // Highlight edge with animation
        int highlightColor = interpolateColor(0xFF4A4A4A, 0xFF5A5A5A, hoverProgress);
        context.fill(
                x + 1, y + 1,
                x + size - 1, y + 2,
                highlightColor
        );
        context.fill(
                x + 1, y + 1,
                x + 2, y + size - 1,
                highlightColor
        );

        // Shadow edge with animation
        int shadowColor = interpolateColor(0xFF202020, 0xFF303030, hoverProgress);
        context.fill(
                x + 1, y + size - 2,
                x + size - 1, y + size - 1,
                shadowColor
        );
        context.fill(
                x + size - 2, y + 1,
                x + size - 1, y + size - 1,
                shadowColor
        );

        // If this is the center slot, draw the plus symbol with animation
        if (isCenter) {
            int centerX = x + size/2;
            int centerY = y + size/2;
            int plusSize = 6;
            int plusColor = interpolateColor(0xFF666666, 0xFF888888, hoverProgress);

            // Vertical line
            context.fill(
                    centerX - 1, centerY - plusSize,
                    centerX + 1, centerY + plusSize,
                    plusColor
            );

            // Horizontal line
            context.fill(
                    centerX - plusSize, centerY - 1,
                    centerX + plusSize, centerY + 1,
                    plusColor
            );
        }

        // Add a subtle glow effect with animation
        if (hoverProgress > 0) {
            int glowAlpha = (int)(25 * hoverProgress); // Animated glow opacity
            int glowColor = (glowAlpha << 24) | 0xFFFFFF; // White glow with variable alpha

            // Thin outer glow line
            context.fill(x - 1, y - 1, x + size + 1, y, glowColor);
            context.fill(x - 1, y + size, x + size + 1, y + size + 1, glowColor);
            context.fill(x - 1, y, x, y + size, glowColor);
            context.fill(x + size, y, x + size + 1, y + size, glowColor);

            // Optional: subtle size increase animation for a "pop" effect
            if (hoverProgress > 0.7f) {
                float scaleFactor = 0.5f * (hoverProgress - 0.7f) / 0.3f; // Scale from 0 to 0.5 as progress goes from 0.7 to 1.0
                int expansionAlpha = (int)(15 * scaleFactor);
                int expansionColor = (expansionAlpha << 24) | 0xFFFFFF;

                // Even thinner outer glow line at the edge of the expansion
                context.fill(x - 2, y - 2, x + size + 2, y - 1, expansionColor);
                context.fill(x - 2, y + size + 1, x + size + 2, y + size + 2, expansionColor);
                context.fill(x - 2, y - 1, x - 1, y + size + 1, expansionColor);
                context.fill(x + size + 1, y - 1, x + size + 2, y + size + 1, expansionColor);
            }
        }
    }
}