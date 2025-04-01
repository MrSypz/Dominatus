package sypztep.dominatus.client.screen.base;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A navigation bar component for switching between different sections/panels.
 */
public class NavBar extends UIPanel {
    private final List<NavItem> items = new ArrayList<>();
    private int selectedIndex = 0;
    private int itemPadding = 10;
    private int itemSpacing = 5;
    private boolean horizontal = true; // true for horizontal, false for vertical

    // Animation state
    private final Map<Integer, Float> hoverAnimations = new HashMap<>();
    private float selectionAnimX = 0;
    private float selectionAnimWidth = 0;
    private float selectionTargetX = 0;
    private float selectionTargetWidth = 0;

    // Animation speed constants
    private static final float HOVER_ANIMATION_SPEED = 0.7f; // Faster hover speed
    private static final float SELECTION_ANIMATION_SPEED = 0.4f; // Faster selection animation

    // Colors
    private static final int BG_COLOR = 0xFF1E1E1E;
    private static final int INACTIVE_COLOR = 0xFFAAAAAA;
    private static final int ACTIVE_COLOR = 0xFFFFFFFF;
    private static final int HOVER_COLOR = 0xFFE0E0E0;
    private static final int ACTIVE_INDICATOR = 0xFFFFCC00; // Yellow

    public NavBar(int x, int y, int width, int height) {
        super(x, y, width, height, null);
        setDrawHeader(false);
    }

    /**
     * Add a navigation item.
     */
    public NavBar addItem(String id, Text label, Identifier icon, Consumer<String> onSelect) {
        NavItem item = new NavItem(id, label, icon, onSelect);
        items.add(item);
        return this;
    }

    /**
     * Add a navigation item without an icon.
     */
    public NavBar addItem(String id, Text label, Consumer<String> onSelect) {
        return addItem(id, label, null, onSelect);
    }

    /**
     * Set the active item by ID.
     */
    public void setActive(String id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).id.equals(id)) {
                selectedIndex = i;
                updateSelectionAnimation();
                break;
            }
        }
    }

    /**
     * Update the target position for selection animation.
     */
    private void updateSelectionAnimation() {
        if (items.isEmpty()) return;

        if (horizontal) {
            int itemX = x + itemPadding;
            for (int i = 0; i < items.size(); i++) {
                if (i == selectedIndex) {
                    int width = getItemWidth(items.get(i));
                    selectionTargetX = itemX;
                    selectionTargetWidth = width;

                    // Initialize position if this is the first selection
                    if (selectionAnimX == 0 && selectionAnimWidth == 0) {
                        selectionAnimX = selectionTargetX;
                        selectionAnimWidth = selectionTargetWidth;
                    }
                    break;
                }
                itemX += getItemWidth(items.get(i)) + itemSpacing;
            }
        } else {
            int itemY = y + itemPadding;
            for (int i = 0; i < items.size(); i++) {
                if (i == selectedIndex) {
                    int height = getItemHeight(items.get(i));
                    selectionTargetX = itemY;
                    selectionTargetWidth = height;

                    // Initialize position if this is the first selection
                    if (selectionAnimX == 0 && selectionAnimWidth == 0) {
                        selectionAnimX = selectionTargetX;
                        selectionAnimWidth = selectionTargetWidth;
                    }
                    break;
                }
                itemY += getItemHeight(items.get(i)) + itemSpacing;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Animate selection indicator with faster speed
        selectionAnimX = MathHelper.lerp(SELECTION_ANIMATION_SPEED * delta, selectionAnimX, selectionTargetX);
        selectionAnimWidth = MathHelper.lerp(SELECTION_ANIMATION_SPEED * delta, selectionAnimWidth, selectionTargetWidth);

        // Update hover animations
        updateHoverAnimations(mouseX, mouseY, delta);

        // Draw background
        context.fill(x, y, x + width, y + height, BG_COLOR);

        // Draw items
        if (horizontal) {
            renderHorizontal(context, mouseX, mouseY);
        } else {
            renderVertical(context, mouseX, mouseY);
        }
    }

    /**
     * Update hover animation states with faster speed.
     */
    private void updateHoverAnimations(int mouseX, int mouseY, float delta) {
        if (horizontal) {
            int itemX = x + itemPadding;

            for (int i = 0; i < items.size(); i++) {
                NavItem item = items.get(i);
                int width = getItemWidth(item);
                boolean isHovered = mouseX >= itemX && mouseX < itemX + width &&
                        mouseY >= y && mouseY < y + height;

                updateSingleHoverAnimation(i, isHovered, delta);

                itemX += width + itemSpacing;
            }
        } else {
            int itemY = y + itemPadding;

            for (int i = 0; i < items.size(); i++) {
                NavItem item = items.get(i);
                int height = getItemHeight(item);
                boolean isHovered = mouseX >= x && mouseX < x + width &&
                        mouseY >= itemY && mouseY < itemY + height;

                updateSingleHoverAnimation(i, isHovered, delta);

                itemY += height + itemSpacing;
            }
        }
    }

    /**
     * Update a single item's hover animation with increased speed.
     */
    private void updateSingleHoverAnimation(int index, boolean isHovered, float delta) {
        float currentAnim = hoverAnimations.getOrDefault(index, 0f);
        float targetAnim = isHovered ? 1f : 0f;

        // Smooth animation with faster speeds
        if (currentAnim != targetAnim) {
            float speed = HOVER_ANIMATION_SPEED * delta;
            if (isHovered) {
                currentAnim = Math.min(1f, currentAnim + speed);
            } else {
                currentAnim = Math.max(0f, currentAnim - speed);
            }

            if (Math.abs(currentAnim) < 0.01f) {
                hoverAnimations.remove(index);
            } else {
                hoverAnimations.put(index, currentAnim);
            }
        }
    }

    /**
     * Render items horizontally.
     */
    private void renderHorizontal(DrawContext context, int mouseX, int mouseY) {
        int itemX = x + itemPadding;
        int itemHeight = height - (itemPadding * 2);

        // Draw indicator for selected item with animation
        context.fill((int)selectionAnimX, y + height - 3,
                (int)(selectionAnimX + selectionAnimWidth), y + height, ACTIVE_INDICATOR);

        for (int i = 0; i < items.size(); i++) {
            NavItem item = items.get(i);
            int itemWidth = getItemWidth(item);

            boolean isSelected = i == selectedIndex;
            float hoverAnim = hoverAnimations.getOrDefault(i, 0f);

            // Draw item with hover animation
            renderNavItem(context, item, itemX, y + itemPadding, itemWidth, itemHeight, isSelected, hoverAnim);

            itemX += itemWidth + itemSpacing;
        }
    }

    /**
     * Render items vertically.
     */
    private void renderVertical(DrawContext context, int mouseX, int mouseY) {
        int itemY = y + itemPadding;
        int itemWidth = width - (itemPadding * 2);

        // Draw indicator for selected item with animation
        context.fill(x, (int)selectionAnimX, x + 3,
                (int)(selectionAnimX + selectionAnimWidth), ACTIVE_INDICATOR);

        for (int i = 0; i < items.size(); i++) {
            NavItem item = items.get(i);
            int itemHeight = getItemHeight(item);

            boolean isSelected = i == selectedIndex;
            float hoverAnim = hoverAnimations.getOrDefault(i, 0f);

            // Draw item with hover animation
            renderNavItemVertical(context, item, x + itemPadding, itemY, itemWidth, itemHeight,
                    isSelected, hoverAnim);

            itemY += itemHeight + itemSpacing;
        }
    }

    /**
     * Render a single navigation item with hover effects.
     */
    private void renderNavItem(DrawContext context, NavItem item, int x, int y, int width, int height,
                               boolean isSelected, float hoverAnim) {
        // Calculate text color with smooth transition based on hover state
        int baseColor = isSelected ? ACTIVE_COLOR : INACTIVE_COLOR;
        int hoverTargetColor = isSelected ? ACTIVE_COLOR : HOVER_COLOR;
        int textColor = interpolateColor(baseColor, hoverTargetColor, hoverAnim);

        int iconSize = 16;
        int textY = y + (height - textRenderer.fontHeight) / 2;
        int textX = x;

        // Hover effect - subtle scaling
        float scale = 1.0f + (0.05f * hoverAnim);

        context.getMatrices().push();

        // Center the scaling effect
        float centerX = textX + width / 2f;
        float centerY = y + height / 2f;
        context.getMatrices().translate(centerX, centerY, 0);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.getMatrices().translate(-centerX, -centerY, 0);

        // Draw icon if available
        if (item.icon != null) {
            int iconY = y + (height - iconSize) / 2;
            context.drawGuiTexture(item.icon, textX, iconY,  iconSize, iconSize);
            textX += iconSize + 5;
        }

        // Draw text with shadow
        context.drawTextWithShadow(textRenderer, item.label, textX, textY, textColor);

        context.getMatrices().pop();
    }

    /**
     * Render a single vertical navigation item with hover effects.
     */
    private void renderNavItemVertical(DrawContext context, NavItem item, int x, int y, int width, int height,
                                       boolean isSelected, float hoverAnim) {
        // Calculate text color with smooth transition based on hover state
        int baseColor = isSelected ? ACTIVE_COLOR : INACTIVE_COLOR;
        int hoverTargetColor = isSelected ? ACTIVE_COLOR : HOVER_COLOR;
        int textColor = interpolateColor(baseColor, hoverTargetColor, hoverAnim);

        int iconSize = 16;
        int textY = y + (height - textRenderer.fontHeight) / 2;

        // Hover effect - subtle scaling
        float scale = 1.0f + (0.05f * hoverAnim);

        context.getMatrices().push();

        // Center the scaling effect
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        context.getMatrices().translate(centerX, centerY, 0);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.getMatrices().translate(-centerX, -centerY, 0);

        // Center both icon and text
        int contentWidth = 0;
        if (item.icon != null) contentWidth += iconSize + 5;
        contentWidth += textRenderer.getWidth(item.label);

        int startX = x + (width - contentWidth) / 2;
        int currentX = startX;

        // Draw icon if available
        if (item.icon != null) {
            int iconY = y + (height - iconSize) / 2;
            context.drawTexture(item.icon, currentX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
            currentX += iconSize + 5;
        }

        // Draw text with shadow
        context.drawTextWithShadow(textRenderer, item.label, currentX, textY, textColor);

        context.getMatrices().pop();
    }

    /**
     * Calculate the width of an item.
     */
    private int getItemWidth(NavItem item) {
        int width = textRenderer.getWidth(item.label);
        if (item.icon != null) {
            width += 16 + 5; // Icon + spacing
        }
        return width;
    }

    /**
     * Calculate the height of an item.
     */
    private int getItemHeight(NavItem item) {
        return Math.max(textRenderer.fontHeight, 16); // Max of text height or icon height
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            if (horizontal) {
                int itemX = x + itemPadding;
                int itemY = y + itemPadding;
                int itemHeight = height - (itemPadding * 2);

                for (int i = 0; i < items.size(); i++) {
                    NavItem item = items.get(i);
                    int itemWidth = getItemWidth(item);

                    if (mouseX >= itemX && mouseX < itemX + itemWidth &&
                            mouseY >= itemY && mouseY < itemY + itemHeight) {
                        if (i != selectedIndex) {
                            selectedIndex = i;
                            updateSelectionAnimation();
                            if (item.onSelect != null) {
                                item.onSelect.accept(item.id);
                            }
                        }
                        return true;
                    }

                    itemX += itemWidth + itemSpacing;
                }
            } else { // Vertical
                int itemX = x + itemPadding;
                int itemY = y + itemPadding;
                int itemWidth = width - (itemPadding * 2);

                for (int i = 0; i < items.size(); i++) {
                    NavItem item = items.get(i);
                    int itemHeight = getItemHeight(item);

                    if (mouseX >= itemX && mouseX < itemX + itemWidth &&
                            mouseY >= itemY && mouseY < itemY + itemHeight) {
                        if (i != selectedIndex) {
                            selectedIndex = i;
                            updateSelectionAnimation();
                            if (item.onSelect != null) {
                                item.onSelect.accept(item.id);
                            }
                        }
                        return true;
                    }

                    itemY += itemHeight + itemSpacing;
                }
            }
        }
        return false;
    }

    /**
     * Set the orientation of the navigation bar.
     *
     * @param horizontal true for horizontal, false for vertical
     */
    public void setOrientation(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * Set the padding inside each item.
     */
    public void setItemPadding(int padding) {
        this.itemPadding = padding;
    }

    /**
     * Set the spacing between items.
     */
    public void setItemSpacing(int spacing) {
        this.itemSpacing = spacing;
    }

    /**
     * Class representing a navigation item.
     */
    private record NavItem(String id, Text label, Identifier icon, Consumer<String> onSelect) {
    }
}