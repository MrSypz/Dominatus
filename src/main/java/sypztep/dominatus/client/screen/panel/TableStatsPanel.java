package sypztep.dominatus.client.screen.panel;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.tyrannus.client.screen.panel.ScrollablePanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Extended ScrollablePanel with table-like display for stat components
 */
public class TableStatsPanel extends ScrollablePanel {
    private static final int TABLE_HEADER_COLOR = 0xFFCCCCAA;  // Light tan for header
    private static final int TABLE_BORDER_COLOR = 0xFF777777;  // Gray for borders
    private static final int BASE_VALUE_COLOR = 0xFFFFFFFF;    // White for base values
    private static final int ADDITION_COLOR = 0xFF77FF77;      // Green for additions
    private static final int TOTAL_VALUE_COLOR = 0xFFFFDD00;   // Gold for totals
    private static final int ATTRIBUTE_COLOR = 0xFFFFFFFF;     // White for attribute names
    private static final int SHADOW_COLOR = 0xFF555555;        // Dark gray for shadows
    private static final int HEADER_COLOR = 0xFFFFD700;        // Gold for category headers
    private static final int SELECTION_BORDER_COLOR = 0xFF4488FF; // Brighter blue for selection border

    private List<StatItem> statItems;
    private Consumer<Integer> onStatClicked;
    private final int lineHeight = 22;
    private int selectedIndex = -1;  // Track selected item

    // Animations
    private final Map<Integer, Float> hoverAnimations = new HashMap<>();
    private final Map<Integer, Float> selectAnimations = new HashMap<>(); // Added selection animations
    private static final float HOVER_ANIMATION_SPEED = 0.2f;
    private static final float SELECT_ANIMATION_SPEED = 0.15f; // Slightly slower than hover

    public TableStatsPanel(int x, int y, int width, int height, Text title) {
        super(x, y, width, height, title);
        initStats();
    }

    /**
     * Initialize the stats.
     */
    private void initStats() {
        statItems = createStatItems();
        setContentHeight(statItems.size() * lineHeight + 25);
    }

    /**
     * Create the stat items to display.
     */
    private List<StatItem> createStatItems() {
        List<StatItem> items = new ArrayList<>();

        // Combat stats header
        items.add(new StatItem(Text.translatable("header.dominatus.combat_stats").getString(), true));

        // Combat stats
        items.add(new StatItem(Text.translatable("stat.dominatus.accuracy").getString(), "accuracy", false));
        items.add(new StatItem(Text.translatable("stat.dominatus.evasion").getString(), "evasion", false));
        items.add(new StatItem(Text.translatable("stat.dominatus.crit_chance").getString(), "crit_chance", false));
        items.add(new StatItem(Text.translatable("stat.dominatus.crit_damage").getString(), "crit_damage", false));

        // Player attributes header
        items.add(new StatItem(Text.translatable("header.dominatus.player_attributes").getString(), true));

        // Player attributes
        items.add(new StatItem(Text.translatable("stat.dominatus.health").getString(), "health", false));
        items.add(new StatItem(Text.translatable("stat.dominatus.armor").getString(), "armor", false));
        items.add(new StatItem(Text.translatable("stat.dominatus.movement_speed").getString(), "movement_speed", false));
        items.add(new StatItem(Text.translatable("stat.dominatus.attack_damage").getString(), "attack_damage", false));

        return items;
    }

    /**
     * Update the stat values.
     */
    private Map<String, StatValue> collectStatValues() {
        if (client.player == null) {
            return new HashMap<>();
        }

        Map<String, StatValue> values = new HashMap<>();
        ClientPlayerEntity player = client.player;

        // Combat stats
        values.put("accuracy", new StatValue(
                /* base */ Math.round(getAttributeBaseValue(player, ModEntityAttributes.ACCURACY)),
                /* addition */ Math.round(getAttributeValue(player, ModEntityAttributes.ACCURACY) - getAttributeBaseValue(player, ModEntityAttributes.ACCURACY))));

        values.put("evasion", new StatValue(
                /* base */ Math.round(getAttributeBaseValue(player, ModEntityAttributes.EVASION)),
                /* addition */ Math.round(getAttributeValue(player, ModEntityAttributes.EVASION) - getAttributeBaseValue(player, ModEntityAttributes.EVASION))));

        values.put("crit_chance", new StatValue(
                /* base */ Math.round(getAttributeBaseValue(player, ModEntityAttributes.CRIT_CHANCE) * 100f),
                /* addition */ Math.round((getAttributeValue(player, ModEntityAttributes.CRIT_CHANCE) - getAttributeBaseValue(player, ModEntityAttributes.CRIT_CHANCE)) * 100f)));

        values.put("crit_damage", new StatValue(
                /* base */ Math.round(getAttributeBaseValue(player, ModEntityAttributes.CRIT_DAMAGE) * 100f),
                /* addition */ Math.round((getAttributeValue(player, ModEntityAttributes.CRIT_DAMAGE) - getAttributeBaseValue(player, ModEntityAttributes.CRIT_DAMAGE)) * 100f)));

        // Player attributes
        values.put("health", new StatValue(
                /* base */ 20,
                /* addition */ Math.round(player.getMaxHealth() - 20)));

        values.put("armor", new StatValue(
                /* base */ 0,
                /* addition */ player.getArmor()));

        // For movement speed, we can simulate a negative addition in some cases (for demonstration)
        int movementBase = (int) (0.1 * 100); // Base movement speed (0.1) * 100 for display
        int movementAddition = (int) ((player.getMovementSpeed() - 0.1) * 100);

        values.put("movement_speed", new StatValue(
                /* base */ movementBase,
                /* addition */ movementAddition));

        values.put("attack_damage", new StatValue(
                /* base */ 1,
                /* addition */ (int) (player.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE) - 1)));

        return values;
    }

    public void setOnStatClicked(java.util.function.Consumer<Integer> onStatClicked) {
        this.onStatClicked = onStatClicked;
    }

    @Override
    protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {
        if (statItems == null || client.player == null) {
            return;
        }

        // Get latest stat values
        Map<String, StatValue> values = collectStatValues();

        int x = getContentX();
        int contentWidth = getContentWidth() - (enableScrollbar ? scrollbarWidth + scrollbarPadding + 5 : 0);
        int yOffset = -(int) scrollAmount;

        // Column widths for table
        final int nameWidth = (int) (contentWidth * 0.4);
        final int baseWidth = (int) (contentWidth * 0.2);
        final int additionWidth = (int) (contentWidth * 0.2);
        final int totalWidth = contentWidth - nameWidth - baseWidth - additionWidth;

        // Calculate column positions - center points for each column
        final int[] columnCenters = new int[4];
        columnCenters[0] = x + nameWidth / 2;  // Name column center
        columnCenters[1] = x + nameWidth + baseWidth / 2;  // Base column center
        columnCenters[2] = x + nameWidth + baseWidth + additionWidth / 2;  // Addition column center
        columnCenters[3] = x + nameWidth + baseWidth + additionWidth + totalWidth / 2;  // Total column center

        // Column start positions (for drawing separators and backgrounds)
        final int[] columnStarts = new int[4];
        columnStarts[0] = x;
        columnStarts[1] = x + nameWidth;
        columnStarts[2] = columnStarts[1] + baseWidth;
        columnStarts[3] = columnStarts[2] + additionWidth;

        // Calculate which item is being hovered for highlighting
        int hoveredIndex = -1;
        if (isMouseOver(mouseX, mouseY)) {
            int relativeY = (int) (mouseY - getContentY() + scrollAmount) - 22; // Adjust for header
            if (relativeY >= 0) { // Only if below the header
                hoveredIndex = relativeY / lineHeight;
                // Only highlight non-header items
                if (hoveredIndex < statItems.size() && statItems.get(hoveredIndex).isHeader()) {
                    hoveredIndex = -1;
                }
            }
        }

        updateAnimations(hoveredIndex, selectedIndex, delta);

        // Draw table header
        int headerY = getContentY() + yOffset;

        // Draw table header background
        context.fill(x, headerY, x + contentWidth, headerY + 22, 0xFF333344);

        // Draw header columns
        String[] headers = {"Stat", "Base", "Addition", "Total"};

        for (int i = 0; i < headers.length; i++) {
            // Draw column header
            String header = headers[i];
            int headerWidth = textRenderer.getWidth(header);

            context.drawTextWithShadow(textRenderer, header, columnCenters[i] - headerWidth / 2, headerY + 7, TABLE_HEADER_COLOR);

            // Draw vertical separators between columns
            if (i > 0) {
                context.fill(columnStarts[i], headerY, columnStarts[i] + 1, headerY + 22, TABLE_BORDER_COLOR);
            }
        }

        // Draw bottom border of header
        context.fill(x, headerY + 22, x + contentWidth, headerY + 23, TABLE_BORDER_COLOR);

        // Draw each stat row
        for (int i = 0; i < statItems.size(); i++) {
            StatItem item = statItems.get(i);
            int elementY = getContentY() + yOffset + (i * lineHeight) + 23; // Add header height

            // Skip rendering if element is outside visible area
            if (elementY + lineHeight < getContentY() || elementY > getContentY() + getContentHeight()) {
                continue;
            }

            // Draw hover and selection effects for non-header items
            if (!item.isHeader()) {
                float hoverAlpha = hoverAnimations.getOrDefault(i, 0f);
                float selectAlpha = selectAnimations.getOrDefault(i, 0f);

                // Draw selection effect (appears beneath hover effect)
                if (selectAlpha > 0.001f) {
                    // Selection background
                    int alphaValue = (int)(selectAlpha * 255);
                    int selectBgColor = ((alphaValue/3) << 24) | 0x4488FF;
                    context.fill(x, elementY, x + contentWidth, elementY + lineHeight, selectBgColor);

                    // Left border indicator for selected item
                    context.fill(x, elementY, x + 3, elementY + lineHeight, SELECTION_BORDER_COLOR);
                }

                // Draw hover highlight on top if item is hovered
                if (hoverAlpha > 0.001f) {
                    int hoverAlphaInt = (int) (hoverAlpha * 48);
                    int highlightColor = (hoverAlphaInt << 24) | 0xFFFFFF;
                    context.fill(x, elementY, x + contentWidth, elementY + lineHeight, highlightColor);
                }
            }

            if (item.isHeader()) {
                // Draw category header - centered and styled
                String headerText = item.name();

                // Draw background for category header
                context.fill(x, elementY, x + contentWidth, elementY + lineHeight, 0xFF222233);

                // Draw shadow first
                context.drawText(textRenderer, headerText, x + 10 + 1, elementY + (lineHeight - textRenderer.fontHeight) / 2 + 1, SHADOW_COLOR, false);

                // Draw main text
                context.drawText(textRenderer, headerText, x + 10, elementY + (lineHeight - textRenderer.fontHeight) / 2, HEADER_COLOR, false);
            } else {
                // Get the stat values if we have them
                StatValue statValue = values.get(item.statKey());

                int textY = elementY + (lineHeight - textRenderer.fontHeight) / 2;

                // Get hover and selection animation values for text color adjustment
                float hoverAlpha = hoverAnimations.getOrDefault(i, 0f);
                float selectAlpha = selectAnimations.getOrDefault(i, 0f);

                // Calculate brighter text colors based on hover and selection state
                int attributeTextColor = ATTRIBUTE_COLOR;
                if (selectAlpha > 0.001f) {
                    // Brighten text for selected items
                    attributeTextColor = interpolateColor(attributeTextColor, 0xFFFFFFFF, selectAlpha * 0.4f);
                } else if (hoverAlpha > 0.001f) {
                    // Slightly brighten text for hovered items
                    attributeTextColor = interpolateColor(attributeTextColor, 0xFFFFFFFF, hoverAlpha * 0.3f);
                }

                // Define max width for the name column (adjust this based on your layout)
                int maxNameWidth = columnStarts[1] - (x + 10) - 2; // Space between left margin and first column separator

                // Wrap the item name text
                List<String> wrappedNameLines = wrapText(item.name(), maxNameWidth);

                // Draw the wrapped name with shadow (left-aligned)
                for (int lineIdx = 0; lineIdx < wrappedNameLines.size(); lineIdx++) {
                    int lineOffset = lineIdx * textRenderer.fontHeight; // Vertical offset for each line
                    String lineText = wrappedNameLines.get(lineIdx);

                    // Draw shadow
                    context.drawText(textRenderer, lineText, x + 10 + 1, textY + lineOffset + 1, SHADOW_COLOR, false);

                    // Draw text
                    context.drawText(textRenderer, lineText, x + 10, textY + lineOffset, attributeTextColor, false);
                }

                // Adjust textY for stat values to account for wrapped lines
                int adjustedTextY = textY + (wrappedNameLines.size() - 1) * textRenderer.fontHeight;

                // Draw the values if we have them
                if (statValue != null) {
                    // Format values appropriately based on stat
                    String baseText = String.valueOf(statValue.base());
                    String additionText = String.valueOf(statValue.addition());
                    String totalText = String.valueOf(statValue.getTotal());

                    // Add percentage sign for crit stats
                    if (item.statKey().equals("crit_chance") || item.statKey().equals("crit_damage")) {
                        baseText += "%";
                        additionText += "%";
                        totalText += "%";
                    }

                    // For additions, add a plus sign if positive
                    if (statValue.addition() > 0) {
                        additionText = "+" + additionText;
                    }

                    // Determine if this is selected for possible highlighting
                    int baseValueColor = BASE_VALUE_COLOR;
                    int totalValueColor = TOTAL_VALUE_COLOR;

                    if (selectAlpha > 0.001f) {
                        // Brighten values for selected item
                        baseValueColor = interpolateColor(baseValueColor, 0xFFFFFFFF, selectAlpha * 0.3f);
                        totalValueColor = interpolateColor(totalValueColor, 0xFFFFFFFF, selectAlpha * 0.2f);
                    }

                    // Draw base value (centered in column)
                    drawCenteredTextWithShadow(context, baseText, columnCenters[1], adjustedTextY, baseValueColor);

                    // Draw addition value (centered, colored green for positive, red for negative)
                    int addColor = statValue.addition() > 0 ? ADDITION_COLOR : statValue.addition() < 0 ? 0xFFFF7777 : ADDITION_COLOR;
                    drawCenteredTextWithShadow(context, additionText, columnCenters[2], adjustedTextY, addColor);

                    // Draw total value (centered, in gold)
                    drawCenteredTextWithShadow(context, totalText, columnCenters[3], adjustedTextY, totalValueColor);
                }

                // Draw column separators
                for (int colIdx = 1; colIdx < 4; colIdx++) {
                    context.fill(columnStarts[colIdx], elementY, columnStarts[colIdx] + 1, elementY + lineHeight, 0x50777777);
                }
            }

            // Draw horizontal separator between rows
            context.fill(x, elementY + lineHeight - 1, x + contentWidth, elementY + lineHeight, 0x30777777);
        }
    }

    /**
     * Update hover and selection animations.
     */
    private void updateAnimations(int hoveredIndex, int selectedIndex, float delta) {
        for (int i = 0; i < (statItems != null ? statItems.size() : 0); i++) {
            if (statItems.get(i).isHeader()) continue;

            // Update hover animations
            float currentHoverAnimation = hoverAnimations.getOrDefault(i, 0f);
            float targetHoverValue = (i == hoveredIndex) ? 1f : 0f;

            // Smoothly animate toward target
            float newHoverValue;
            if (currentHoverAnimation < targetHoverValue) {
                newHoverValue = Math.min(currentHoverAnimation + HOVER_ANIMATION_SPEED * delta, targetHoverValue);
            } else {
                newHoverValue = Math.max(currentHoverAnimation - HOVER_ANIMATION_SPEED * delta, targetHoverValue);
            }

            if (newHoverValue > 0.001f) {
                hoverAnimations.put(i, newHoverValue);
            } else {
                hoverAnimations.remove(i);
            }

            // Update selection animations
            float currentSelectAnimation = selectAnimations.getOrDefault(i, 0f);
            float targetSelectValue = (i == selectedIndex) ? 1f : 0f;

            // Smoothly animate toward target
            float newSelectValue;
            if (currentSelectAnimation < targetSelectValue) {
                newSelectValue = Math.min(currentSelectAnimation + SELECT_ANIMATION_SPEED * delta, targetSelectValue);
            } else {
                newSelectValue = Math.max(currentSelectAnimation - SELECT_ANIMATION_SPEED * delta, targetSelectValue);
            }

            if (newSelectValue > 0.001f) {
                selectAnimations.put(i, newSelectValue);
            } else {
                selectAnimations.remove(i);
            }
        }
    }

    /**
     * Helper to draw centered text with shadow
     */
    private void drawCenteredTextWithShadow(DrawContext context, String text, int x, int y, int color) {
        int textWidth = textRenderer.getWidth(text);

        // Draw shadow first
        context.drawText(textRenderer, text, x - textWidth / 2 + 1, y + 1, SHADOW_COLOR, false);

        // Draw text
        context.drawText(textRenderer, text, x - textWidth / 2, y, color, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isScrollbarClicked(mouseX, mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button); // Let parent handle scrollbar
        }

        if (isMouseOver(mouseX, mouseY) && button == 0 && onStatClicked != null) {
            int relativeY = (int) (mouseY - getContentY() + scrollAmount) - 23;
            if (relativeY < 0) return false;

            int index = relativeY / lineHeight;

            if (index < statItems.size() && !statItems.get(index).isHeader()) {
                // Update selected index
                selectedIndex = index;
                // Notify callback
                onStatClicked.accept(index);
                return true;
            }
        }

        // Default to parent behavior
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // Helper methods from StatsTab
    private double getAttributeValue(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        return instance != null ? instance.getValue() : 0;
    }

    private double getAttributeBaseValue(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        return instance != null ? instance.getBaseValue() : 0;
    }

    /**
     * Records for storing stat information
     */
    public record StatItem(String name, String statKey, boolean isHeader) {
        public StatItem(String name, boolean isHeader) {
            this(name, "", isHeader);
        }
    }

    public record StatValue(long base, long addition) {
        public long getTotal() {
            return base + addition;
        }

        @Override
        public String toString() {
            return String.valueOf(getTotal());
        }
    }

    public record StatDescription(String titleKey, String descriptionKey, String detailsKey) {
    }
}