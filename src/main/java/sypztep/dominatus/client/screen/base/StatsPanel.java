package sypztep.dominatus.client.screen.base;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.client.widget.ListElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A panel that displays player stats with animations, white text, gray shadows, and yellow values.
 */
public class StatsPanel extends ScrollablePanel {
    private final StatsProvider statsProvider;
    private List<ListElement> elements;
    private Map<String, Object> values;
    private int lineHeight = 20;

    // Click handler for stats
    private Consumer<Integer> onStatClicked;

    // Animations
    private final Map<Integer, Float> hoverAnimations = new HashMap<>();

    // Animation constants
    private static final float HOVER_ANIMATION_SPEED = 0.2f;

    // Colors
    private static final int HEADER_COLOR = 0xFFFFD700;      // Gold for headers
    private static final int ATTRIBUTE_COLOR = 0xFFFFFFFF;   // White for attribute names
    private static final int VALUE_COLOR = 0xFFFFCC00;       // Yellow for values
    private static final int SHADOW_COLOR = 0xFF555555;      // Dark gray for shadows

    /**
     * Interface for providing stats data.
     */
    public interface StatsProvider {
        List<ListElement> createListElements();
        Map<String, Object> collectValues(ClientPlayerEntity player);
    }

    public StatsPanel(int x, int y, int width, int height, Text title, StatsProvider statsProvider) {
        super(x, y, width, height, title);
        this.statsProvider = statsProvider;
        initStats();
    }

    /**
     * Initialize the stats.
     */
    private void initStats() {
        if (client.player != null) {
            elements = statsProvider.createListElements();
            values = statsProvider.collectValues(client.player);

            setContentHeight(elements.size() * lineHeight + 10);
        }
    }

    /**
     * Update the stats values.
     */
    public void updateValues() {
        if (client.player != null) {
            values = statsProvider.collectValues(client.player);
        }
    }

    @Override
    protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {
        if (elements == null || values == null) {
            return;
        }

        int x = getContentX();
        int contentWidth = getContentWidth() - (enableScrollbar ? scrollbarWidth + scrollbarPadding + 5 : 0);
        int yOffset = -(int) scrollAmount;

        // Calculate which item is being hovered for highlighting
        int hoveredIndex = -1;
        if (isMouseOver(mouseX, mouseY)) {
            int relativeY = (int)(mouseY - getContentY() + scrollAmount);
            hoveredIndex = relativeY / lineHeight;
            // Only highlight non-header items
            if (hoveredIndex >= 0 && hoveredIndex < elements.size() && elements.get(hoveredIndex).isHeader()) {
                hoveredIndex = -1;
            }
        }

        // Update animations
        updateAnimations(hoveredIndex, delta);

        for (int i = 0; i < elements.size(); i++) {
            ListElement element = elements.get(i);
            int elementY = getContentY() + yOffset + (i * lineHeight);

            // Skip rendering if element is outside visible area
            if (elementY + lineHeight < getContentY() || elementY > getContentY() + getContentHeight()) {
                continue;
            }

            // Draw hover animations for non-header items
            if (!element.isHeader()) {
                float hoverAlpha = hoverAnimations.getOrDefault(i, 0f);

                // Draw highlight background if there's any hover animation
                if (hoverAlpha > 0.001f) {
                    int hoverAlphaInt = (int)(hoverAlpha * 48); // 0x30 = 48
                    int highlightColor = (hoverAlphaInt << 24) | 0xFFFFFF;
                    context.fill(x, elementY, x + contentWidth, elementY + lineHeight, highlightColor);
                }
            }

            if (element.isHeader()) {
                // Draw header - centered and styled
                String headerText = element.text().getString();
                int headerWidth = textRenderer.getWidth(headerText);

                // Draw shadow first (shifted down and right)
                context.drawText(
                        textRenderer,
                        headerText,
                        x + (contentWidth - headerWidth) / 2 + 1,
                        elementY + (lineHeight - textRenderer.fontHeight) / 2 + 1,
                        SHADOW_COLOR,
                        false
                );

                // Draw main text
                context.drawText(
                        textRenderer,
                        headerText,
                        x + (contentWidth - headerWidth) / 2,
                        elementY + (lineHeight - textRenderer.fontHeight) / 2,
                        HEADER_COLOR,
                        false
                );

                // Draw subtle divider below header
                int dividerY = elementY + lineHeight - 2;
                int dividerPadding = 20;
                drawGradientDivider(
                        context,
                        x + dividerPadding,
                        dividerY,
                        contentWidth - (dividerPadding * 2),
                        0.7f
                );
            } else {
                // For normal stats, process the text with variables
                String displayText = element.text().getString();
                String attributeText = displayText;
                String valueText = "";

                // Find and replace variables in text
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    String key = "$" + entry.getKey();
                    if (displayText.contains(key)) {
                        valueText = entry.getValue().toString();
                        attributeText = displayText.replace(key, "").trim();
                        break;
                    }
                }

                int textY = elementY + (lineHeight - textRenderer.fontHeight) / 2;

                // Start position for text
                int textX = x;
                if (element.icon() != null) {
                    // Draw icon
                    int iconSize = 16;
                    context.drawTexture(
                            element.icon(),
                            textX,
                            elementY + (lineHeight - iconSize) / 2,
                            0, 0,
                            iconSize, iconSize,
                            iconSize, iconSize
                    );
                    textX += iconSize + 5;
                }

                // Get hover animation alpha for text color adjustment
                float hoverAlpha = hoverAnimations.getOrDefault(i, 0f);

                // Calculate brighter text colors based on hover state
                int attributeTextColor = interpolateColor(ATTRIBUTE_COLOR, 0xFFFFFFFF, hoverAlpha * 0.3f);
                int valueTextColor = interpolateColor(VALUE_COLOR, 0xFFFFFFFF, hoverAlpha * 0.3f);

                // Draw attribute text with shadow
                // Shadow first
                context.drawText(
                        textRenderer,
                        attributeText,
                        textX + 1,
                        textY + 1,
                        SHADOW_COLOR,
                        false
                );

                // Then attribute text
                context.drawText(
                        textRenderer,
                        attributeText,
                        textX,
                        textY,
                        attributeTextColor,
                        false
                );

                // Draw value text if we have one
                if (!valueText.isEmpty()) {
                    int valueWidth = textRenderer.getWidth(valueText);
                    int valueX = x + contentWidth - valueWidth - 5; // Right-aligned

                    // Shadow first
                    context.drawText(
                            textRenderer,
                            valueText,
                            valueX + 1,
                            textY + 1,
                            SHADOW_COLOR,
                            false
                    );

                    // Then value text in yellow
                    context.drawText(
                            textRenderer,
                            valueText,
                            valueX,
                            textY,
                            valueTextColor,
                            false
                    );
                }
            }
        }
    }

    /**
     * Update hover animations.
     */
    private void updateAnimations(int hoveredIndex, float delta) {
        // Update hover animations
        for (int i = 0; i < (elements != null ? elements.size() : 0); i++) {
            if (elements.get(i).isHeader()) continue;

            float currentHoverAnimation = hoverAnimations.getOrDefault(i, 0f);
            float targetHoverValue = (i == hoveredIndex) ? 1f : 0f;

            // Smoothly animate toward target
            float newValue;
            if (currentHoverAnimation < targetHoverValue) {
                newValue = Math.min(currentHoverAnimation + HOVER_ANIMATION_SPEED * delta, targetHoverValue);
            } else {
                newValue = Math.max(currentHoverAnimation - HOVER_ANIMATION_SPEED * delta, targetHoverValue);
            }

            if (newValue > 0.001f) {
                hoverAnimations.put(i, newValue);
            } else hoverAnimations.remove(i);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && button == 0 && onStatClicked != null) {
            int relativeY = (int)(mouseY - getContentY() + scrollAmount);
            int index = relativeY / lineHeight;

            if (index >= 0 && index < elements.size() && !elements.get(index).isHeader()) {
                onStatClicked.accept(index);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Set a callback for when a stat is clicked.
     */
    public void setOnStatClicked(Consumer<Integer> onStatClicked) {
        this.onStatClicked = onStatClicked;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateValues();
        super.render(context, mouseX, mouseY, delta);
    }
}