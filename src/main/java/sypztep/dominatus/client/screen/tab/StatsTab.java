package sypztep.dominatus.client.screen.tab;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.client.screen.panel.TableStatsPanel;
import sypztep.dominatus.common.stats.StatRegistry;
import sypztep.dominatus.common.stats.StatRegistry.StatDefinition;
import sypztep.tyrannus.client.screen.panel.ScrollablePanel;
import sypztep.tyrannus.client.screen.tab.Tab;

import java.util.List;

/**
 * Tab for displaying player statistics with a split panel layout:
 * - Left side (1/3): Stat description panel (scrollable)
 * - Right side (2/3): Stats table panel (Black Desert style)
 */
public class StatsTab extends Tab {
    private String selectedStat = "";

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

            // Get selected stat description from the registry
            StatDefinition stat = StatRegistry.getStat(selectedStat);

            // Get translated text
            Text titleText = Text.translatable(stat.nameKey());
            Text descText = Text.translatable(stat.descriptionKey());
            Text detailsText = stat.detailsKey().isEmpty() ? Text.empty() : Text.translatable(stat.detailsKey());

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