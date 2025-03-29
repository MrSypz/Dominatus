package sypztep.dominatus.client.screen.tab;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.screen.base.ScrollablePanel;
import sypztep.dominatus.client.screen.base.Tab;

import java.util.ArrayList;
import java.util.List;

/**
 * Tab for displaying and tracking quests.
 */
public class QuestsTab extends Tab {
    // Icons
    private static final Identifier QUESTS_ICON = Dominatus.id("textures/gui/icons/quests.png");

    // Quest panel
    private QuestPanel questPanel;

    // Example quests
    private final List<Quest> activeQuests = new ArrayList<>();
    private final List<Quest> completedQuests = new ArrayList<>();

    public QuestsTab() {
        super("quests", Text.of("Quests"), QUESTS_ICON);

        // Initialize example quests
        activeQuests.add(new Quest(
                "The Lost Artifact",
                "Find the ancient artifact hidden in the abandoned temple.",
                2, false
        ));

        activeQuests.add(new Quest(
                "Goblin Threat",
                "Defeat 10 goblins terrorizing the local village. (3/10)",
                1, false
        ));

        completedQuests.add(new Quest(
                "First Steps",
                "Complete the tutorial and meet with the village elder.",
                0, true
        ));
    }

    @Override
    protected void initPanels() {
        int panelX = 10;
        int panelY = 65; // Below nav bar
        int panelWidth = parentScreen.width - 20;
        int panelHeight = parentScreen.height - 100; // Space for nav and bottom UI

        // Create quests panel
        questPanel = new QuestPanel(panelX, panelY, panelWidth, panelHeight, Text.of("Quests"));
        addPanel(questPanel);
    }

    /**
     * Custom panel for displaying quests.
     */
    private class QuestPanel extends ScrollablePanel {
        public QuestPanel(int x, int y, int width, int height, Text title) {
            super(x, y, width, height, title);
            updateContentHeight();
        }

        private void updateContentHeight() {
            // Calculate total content height based on number of quests
            int totalHeight = 50; // Header
            totalHeight += activeQuests.size() * 70;
            totalHeight += 50; // Completed header
            totalHeight += completedQuests.size() * 50;
            totalHeight += 20; // Padding

            setContentHeight(totalHeight);
        }

        @Override
        protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = getContentX();
            int width = getContentWidth() - scrollbarWidth - 10;
            int y = getContentY() - (int)scrollAmount;

            // Active Quests Header
            context.drawTextWithShadow(
                    textRenderer,
                    "ACTIVE QUESTS",
                    x + (width - textRenderer.getWidth("ACTIVE QUESTS")) / 2,
                    y + 10,
                    0xFFFFD700
            );
            y += 30;

            // Draw active quests
            for (Quest quest : activeQuests) {
                drawQuest(context, quest, x, y, width);
                y += 70;
            }

            // Completed Quests Header
            y += 20;
            context.drawTextWithShadow(
                    textRenderer,
                    "COMPLETED QUESTS",
                    x + (width - textRenderer.getWidth("COMPLETED QUESTS")) / 2,
                    y,
                    0xFFFFD700
            );
            y += 30;

            // Draw completed quests
            for (Quest quest : completedQuests) {
                drawQuest(context, quest, x, y, width);
                y += 50;
            }
        }

        /**
         * Draw a single quest entry.
         */
        private void drawQuest(DrawContext context, Quest quest, int x, int y, int width) {
            // Quest background
            int bgColor = quest.completed ? 0x40007700 : 0x40000077;
            context.fill(x, y, x + width, y + (quest.completed ? 40 : 60), bgColor);
            context.fill(x, y, x + 3, y + (quest.completed ? 40 : 60), quest.getDifficultyColor());

            // Quest title
            int titleColor = quest.completed ? 0xFF55FF55 : 0xFFFFFFFF;
            context.drawTextWithShadow(
                    textRenderer,
                    quest.title,
                    x + 10,
                    y + 5,
                    titleColor
            );

            // Quest description
            if (!quest.completed) {
                context.drawTextWithShadow(
                        textRenderer,
                        quest.description,
                        x + 10,
                        y + 25,
                        0xFFCCCCCC
                );
            }

            // Quest status
            String statusText = quest.completed ? "Completed" : "In Progress";
            context.drawTextWithShadow(
                    textRenderer,
                    statusText,
                    x + width - textRenderer.getWidth(statusText) - 10,
                    y + 5,
                    quest.completed ? 0xFF55FF55 : 0xFFFFAA00
            );
        }
    }

    /**
     * Class to store quest data.
     */
    private static class Quest {
        final String title;
        final String description;
        final int difficulty; // 0 = Easy, 1 = Medium, 2 = Hard, 3 = Legendary
        final boolean completed;

        Quest(String title, String description, int difficulty, boolean completed) {
            this.title = title;
            this.description = description;
            this.difficulty = difficulty;
            this.completed = completed;
        }

        int getDifficultyColor() {
            switch (difficulty) {
                case 0: return 0xFF55FF55; // Easy - Green
                case 1: return 0xFFFFAA00; // Medium - Orange
                case 2: return 0xFFFF5555; // Hard - Red
                case 3: return 0xFFAA00AA; // Legendary - Purple
                default: return 0xFFFFFFFF;
            }
        }
    }
}