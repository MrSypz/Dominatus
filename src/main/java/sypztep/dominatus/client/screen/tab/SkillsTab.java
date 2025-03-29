package sypztep.dominatus.client.screen.tab;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.screen.base.ScrollablePanel;
import sypztep.dominatus.client.screen.base.Tab;
import sypztep.dominatus.client.screen.base.UIPanel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Tab for displaying and managing player skills in a Dark Souls-like stat allocation system.
 */
public class SkillsTab extends Tab {
    // Icons
    private static final Identifier SKILLS_ICON = Dominatus.id("textures/gui/icons/skills.png");
    private static final Identifier PLUS_ICON = Dominatus.id("textures/gui/icons/plus.png");
    private static final Identifier ATTRIBUTE_ICON = Dominatus.id("textures/gui/icons/attribute.png");

    // Available skill points
    private int availablePoints = 5;

    // Skills data
    private final Map<String, SkillAttribute> attributes = new HashMap<>();

    // Reference to the scrollable panel that contains skills
    private SkillsScrollPanel skillsPanel;

    // Tooltip data
    private String hoverTooltip = null;

    public SkillsTab() {
        super("skills", Text.of("Skills"), SKILLS_ICON);

        // Initialize example attributes
        attributes.put("strength", new SkillAttribute("Strength", 10,
                "Increases physical damage and carrying capacity"));
        attributes.put("dexterity", new SkillAttribute("Dexterity", 8,
                "Improves attack speed and evasion"));
        attributes.put("vitality", new SkillAttribute("Vitality", 12,
                "Increases health points and stamina recovery"));
        attributes.put("intelligence", new SkillAttribute("Intelligence", 7,
                "Improves magic damage and mana capacity"));
        attributes.put("faith", new SkillAttribute("Faith", 5,
                "Enhances healing abilities and light magic"));
        attributes.put("endurance", new SkillAttribute("Endurance", 9,
                "Increases stamina and equipment load"));
        attributes.put("luck", new SkillAttribute("Luck", 6,
                "Improves item drop rates and critical hit chance"));
    }

    @Override
    protected void initPanels() {
        int panelX = 10;
        int panelY = 65; // Below nav bar
        int panelWidth = parentScreen.width - 20;
        int panelHeight = parentScreen.height - 100; // Space for nav and bottom UI

        // Create scrollable skills panel
        skillsPanel = new SkillsScrollPanel(panelX, panelY, panelWidth, panelHeight,
                Text.of("Character Attributes"));
        addPanel(skillsPanel);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw tooltip if hovering over an attribute
        if (hoverTooltip != null) {
            int tooltipWidth = client.textRenderer.getWidth(hoverTooltip);
            int tooltipX = Math.min(mouseX + 12, parentScreen.width - tooltipWidth - 12);
            int tooltipY = mouseY + 12;

            // Draw tooltip background
            context.fill(tooltipX - 4, tooltipY - 4,
                    tooltipX + tooltipWidth + 4, tooltipY + client.textRenderer.fontHeight + 4,
                    0xE0000000);

            // Draw tooltip text
            context.drawText(client.textRenderer, hoverTooltip, tooltipX, tooltipY, 0xFFFFFFFF, false);

            // Reset tooltip for next frame
            hoverTooltip = null;
        }
    }

    /**
     * Attempt to upgrade an attribute.
     */
    private void upgradeAttribute(String attributeId) {
        if (availablePoints > 0 && attributes.containsKey(attributeId)) {
            SkillAttribute attribute = attributes.get(attributeId);
            attribute.level++;
            availablePoints--;

            // Here you would normally save the attribute change
            // For now we just update the UI
        }
    }

    /**
     * Custom scrollable panel for skills.
     */
    private class SkillsScrollPanel extends ScrollablePanel {
        public SkillsScrollPanel(int x, int y, int width, int height, Text title) {
            super(x, y, width, height, title);
            initContent();
        }

        private void initContent() {
            // Calculate content height
            int itemHeight = 40;
            int headerHeight = 60;
            int contentHeight = headerHeight + (attributes.size() * itemHeight) + 20; // Extra padding
            setContentHeight(contentHeight);
        }

        @Override
        protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = getContentX();
            int y = getContentY() - (int)scrollAmount;
            int availableWidth = getContentWidth() - (enableScrollbar ? scrollbarWidth + scrollbarPadding + 10 : 5);

            // Draw header with available points
            int headerY = y + 10;
            String headerText = "CHARACTER ATTRIBUTES";
            context.drawTextWithShadow(
                    textRenderer,
                    headerText,
                    x + (availableWidth - textRenderer.getWidth(headerText)) / 2,
                    headerY,
                    0xFFFFD700
            );

            // Draw points info
            String pointsText = "Available Points: " + availablePoints;
            context.drawTextWithShadow(
                    textRenderer,
                    pointsText,
                    x + (availableWidth - textRenderer.getWidth(pointsText)) / 2,
                    headerY + 20,
                    availablePoints > 0 ? 0xFF55FF55 : 0xFFAAAAAA
            );

            // Draw attributes section
            y += 60;
            int attributeHeight = 40;

            // Draw attribute name column header
            context.drawTextWithShadow(
                    textRenderer,
                    "ATTRIBUTE",
                    x + 45,
                    y,
                    0xFFAAAAAA
            );

            // Draw level column header
            context.drawTextWithShadow(
                    textRenderer,
                    "LEVEL",
                    x + availableWidth - 100,
                    y,
                    0xFFAAAAAA
            );

            y += 20;

            // Draw separator line
            context.fill(x, y - 5, x + availableWidth, y - 4, 0x80FFFFFF);

            // Draw attributes
            int index = 0;
            for (Map.Entry<String, SkillAttribute> entry : attributes.entrySet()) {
                String attrId = entry.getKey();
                SkillAttribute attribute = entry.getValue();

                // Alternate row backgrounds
                boolean isEven = (index % 2 == 0);
                int rowBgColor = isEven ? 0x10FFFFFF : 0x20FFFFFF;
                context.fill(x, y, x + availableWidth, y + attributeHeight, rowBgColor);

                // Draw attribute icon
                context.drawTexture(ATTRIBUTE_ICON, x + 10, y + (attributeHeight - 16) / 2, 0, 0, 16, 16, 16, 16);

                // Draw attribute name
                context.drawTextWithShadow(
                        textRenderer,
                        attribute.name,
                        x + 35,
                        y + (attributeHeight - textRenderer.fontHeight) / 2,
                        0xFFFFFFFF
                );

                // Draw attribute level
                String levelText = String.valueOf(attribute.level);
                context.drawTextWithShadow(
                        textRenderer,
                        levelText,
                        x + availableWidth - 100 + (40 - textRenderer.getWidth(levelText)) / 2,
                        y + (attributeHeight - textRenderer.fontHeight) / 2,
                        0xFFFFCC00
                );

                // Draw upgrade button if points are available
                int buttonX = x + availableWidth - 40;
                int buttonY = y + (attributeHeight - 20) / 2;

                if (availablePoints > 0) {
                    // Button background
                    context.fill(buttonX, buttonY, buttonX + 20, buttonY + 20, 0xFF444444);
                    context.fill(buttonX + 1, buttonY + 1, buttonX + 19, buttonY + 19, 0xFF666666);

                    // Plus icon or text
                    if (PLUS_ICON != null) {
                        context.drawTexture(PLUS_ICON, buttonX + 2, buttonY + 2, 0, 0, 16, 16, 16, 16);
                    } else {
                        context.drawCenteredTextWithShadow(
                                textRenderer,
                                "+",
                                buttonX + 10,
                                buttonY + 6,
                                0xFFFFFFFF
                        );
                    }

                    // Check if mouse is over button
                    if (mouseX >= buttonX && mouseX <= buttonX + 20 &&
                            mouseY >= buttonY && mouseY <= buttonY + 20) {

                        // Highlight button
                        context.fill(buttonX, buttonY, buttonX + 20, buttonY + 20, 0x80FFFFFF);

                        // Set tooltip with attribute description
                        hoverTooltip = attribute.description;

//                        // Check for click
//                        if (client.currentScreen.mouseClicked) {
//                            upgradeAttribute(attrId);
//                            client.currentScreen.mouseClicked = false;
//                        }
                    }
                }

                // Check if hovering over attribute name for tooltip
                if (mouseX >= x + 35 && mouseX <= x + availableWidth - 150 &&
                        mouseY >= y && mouseY <= y + attributeHeight) {
                    hoverTooltip = attribute.description;
                }

                y += attributeHeight;
                index++;
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean result = super.mouseClicked(mouseX, mouseY, button);

            // Check for clicks on attribute buttons
            if (button == 0 && availablePoints > 0 && isMouseOver(mouseX, mouseY)) {
                int y = getContentY() - (int)scrollAmount + 80; // Start of attributes
                int attributeHeight = 40;
                int availableWidth = getContentWidth() - (enableScrollbar ? scrollbarWidth + scrollbarPadding + 10 : 5);

                // Check each attribute button
                int index = 0;
                for (String attrId : attributes.keySet()) {
                    int buttonX = getContentX() + availableWidth - 40;
                    int buttonY = y + (index * attributeHeight) + (attributeHeight - 20) / 2;

                    if (mouseX >= buttonX && mouseX <= buttonX + 20 &&
                            mouseY >= buttonY && mouseY <= buttonY + 20) {
                        upgradeAttribute(attrId);
                        return true;
                    }

                    index++;
                }
            }

            return result;
        }
    }

    /**
     * Class to store attribute data.
     */
    private static class SkillAttribute {
        String name;
        int level;
        String description;

        SkillAttribute(String name, int level, String description) {
            this.name = name;
            this.level = level;
            this.description = description;
        }
    }
}