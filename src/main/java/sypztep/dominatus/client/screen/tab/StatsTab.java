package sypztep.dominatus.client.screen.tab;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.client.screen.panel.TableStatsPanel;
import sypztep.tyrannus.client.screen.panel.ScrollablePanel;
import sypztep.tyrannus.client.screen.tab.Tab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tab for displaying player statistics with a split panel layout:
 * - Left side (1/3): Stat description panel (scrollable)
 * - Right side (2/3): Stats table panel (Black Desert style)
 */
public class StatsTab extends Tab {
    private String selectedStat = "";
    private static final Map<String, TableStatsPanel.StatDescription> statDescriptions = new HashMap<>();

    static {
        statDescriptions.put("accuracy", new TableStatsPanel.StatDescription("stat.dominatus.accuracy", "stat.dominatus.accuracy.desc", "stat.dominatus.accuracy.details"));

        statDescriptions.put("evasion", new TableStatsPanel.StatDescription("stat.dominatus.evasion", "stat.dominatus.evasion.desc", "stat.dominatus.evasion.details"));

        statDescriptions.put("crit_chance", new TableStatsPanel.StatDescription("stat.dominatus.crit_chance", "stat.dominatus.crit_chance.desc", "stat.dominatus.crit_chance.details"));

        statDescriptions.put("crit_damage", new TableStatsPanel.StatDescription("stat.dominatus.crit_damage", "stat.dominatus.crit_damage.desc", "stat.dominatus.crit_damage.details"));

        statDescriptions.put("damage_reduction", new TableStatsPanel.StatDescription("stat.dominatus.damage_reduction", "stat.dominatus.damage_reduction.desc", "stat.dominatus.damage_reduction.details")); // New

        statDescriptions.put("max_health", new TableStatsPanel.StatDescription("stat.dominatus.max_health", "stat.dominatus.max_health.desc", "stat.dominatus.max_health.details"));

        statDescriptions.put("health_regen", new TableStatsPanel.StatDescription("stat.dominatus.health_regen","stat.dominatus.health_regen.desc","stat.dominatus.health_regen.details"));

        statDescriptions.put("armor", new TableStatsPanel.StatDescription("stat.dominatus.armor", "stat.dominatus.armor.desc", "stat.dominatus.armor.details"));

        statDescriptions.put("armor_toughness", new TableStatsPanel.StatDescription("stat.dominatus.armor_toughness", "stat.dominatus.armor_toughness.desc", "stat.dominatus.armor_toughness.details"));

        statDescriptions.put("movement_speed", new TableStatsPanel.StatDescription("stat.dominatus.movement_speed", "stat.dominatus.movement_speed.desc", "stat.dominatus.movement_speed.details"));

        statDescriptions.put("attack_damage", new TableStatsPanel.StatDescription("stat.dominatus.attack_damage", "stat.dominatus.attack_damage.desc", "stat.dominatus.attack_damage.details"));

        statDescriptions.put("attack_speed", new TableStatsPanel.StatDescription("stat.dominatus.attack_speed", "stat.dominatus.attack_speed.desc", "stat.dominatus.attack_speed.details"));
    }

    public StatsTab() {
        super("stats", Text.translatable("tab.dominatus.stats"), Identifier.ofVanilla("icon/accessibility"));
    }

    @Override
    protected void initPanels() {
        int totalWidth = parentScreen.width - 20;
        int panelHeight = parentScreen.height - 100; // Leave space for nav bar and bottom UI
        int panelY = 65; // Below nav bar

        // Calculate panel dimensions (1:3 ratio - left:right)
        int leftWidth = totalWidth / 3;
        int rightWidth = totalWidth - leftWidth - 5; // 5px gap between panels

        int leftX = 10;
        int rightX = leftX + leftWidth + 5;

        // Create scrollable description panel (left side - 1/3 width)
        DescriptionScrollPanel descriptionPanel = new DescriptionScrollPanel(leftX, panelY, leftWidth, panelHeight, Text.translatable("panel.dominatus.stat_description"));
        addPanel(descriptionPanel);

        // Create stats panel (right side - 2/3 width)
        TableStatsPanel statsPanel = new TableStatsPanel(rightX, panelY, rightWidth, panelHeight, Text.translatable("panel.dominatus.player_statistics"));

        statsPanel.setOnStatClicked(statIndex -> {
            List<String> statKeys = statsPanel.getStatKeys();
            if (statIndex >= 0 && statIndex < statKeys.size()) {
                selectedStat = statKeys.get(statIndex);
            }
        });
        addPanel(statsPanel);
    }

    /**
     * Custom scrollable panel for displaying stat descriptions.
     */
    private class DescriptionScrollPanel extends ScrollablePanel {
        public DescriptionScrollPanel(int x, int y, int width, int height, Text title) {
            super(x, y, width, height, title);
        }

        @Override
        protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = getContentX();
            int y = getContentY() - (int) scrollAmount;
            int width = getContentWidth() - (enableScrollbar ? scrollbarWidth + scrollbarPadding : 0);

            // Track the current Y position as we render content
            int currentY = y + 10;

            // Get selected stat description
            TableStatsPanel.StatDescription description = statDescriptions.getOrDefault(selectedStat, new TableStatsPanel.StatDescription("stat.dominatus.unknown", "stat.dominatus.unknown.desc", ""));

            // Get translated text
            Text titleText = Text.translatable(description.titleKey());
            Text descText = Text.translatable(description.descriptionKey());
            Text detailsText = description.detailsKey().isEmpty() ? Text.empty() : Text.translatable(description.detailsKey());

            // Draw title
            int titleColor = 0xFFFFCC00; // Yellow color for titles
            context.drawTextWithShadow(textRenderer, titleText, x + (width - textRenderer.getWidth(titleText)) / 2, currentY, titleColor);
            currentY += textRenderer.fontHeight + 10;

            currentY += 5;

            // Draw divider
            drawGradientDivider(context, x + 20, currentY, width - 40, 0.8f);
            currentY += 10;

            // Draw description text - wrapped to fit panel width
            int textColor = 0xFFFFFFFF;
            int detailColor = 0xFFAAAAFF; // Light blue for details

            // Main description
            List<String> wrappedText = wrapText(descText.getString(), width - 10);
            for (String line : wrappedText) {
                context.drawTextWithShadow(textRenderer, line, x + 10, currentY, textColor);
                currentY += textRenderer.fontHeight + 2;
            }

            // Add space between description and details
            if (!detailsText.getString().isEmpty()) {
                currentY += 10;

                // Detail text (in different color)
                List<String> wrappedDetails = wrapText(detailsText.getString(), width - 10);
                for (String line : wrappedDetails) {
                    context.drawTextWithShadow(textRenderer, line, x + 10, currentY, detailColor);
                    currentY += textRenderer.fontHeight + 2;
                }
            }
            currentY += textRenderer.fontHeight;

            // Calculate total content height based on the final Y position
            int totalHeight = currentY - (getContentY() - (int) scrollAmount); // Add some padding
            setContentHeight(totalHeight);
        }
    }
}