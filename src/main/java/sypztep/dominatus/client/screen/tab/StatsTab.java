package sypztep.dominatus.client.screen.tab;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.screen.base.ScrollablePanel;
import sypztep.dominatus.client.screen.base.StatsPanel;
import sypztep.dominatus.client.screen.base.Tab;
import sypztep.dominatus.client.widget.ListElement;
import sypztep.dominatus.common.init.ModEntityAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tab for displaying player statistics with a split panel layout:
 * - Left side (1/3): Stat description panel (scrollable)
 * - Right side (2/3): Stats list panel
 */
public class StatsTab extends Tab {
    // Icons
    private static final Identifier STATS_ICON = Dominatus.id("textures/gui/icons/stats.png");
//    private static final Identifier ACCURACY_ICON = Dominatus.id("textures/gui/stats/accuracy.png");
//    private static final Identifier EVASION_ICON = Dominatus.id("textures/gui/stats/evasion.png");
//    private static final Identifier CRIT_CHANCE_ICON = Dominatus.id("textures/gui/stats/crit_chance.png");
//    private static final Identifier CRIT_DAMAGE_ICON = Dominatus.id("textures/gui/stats/crit_damage.png");

    // Panels
    private StatsPanel statsPanel;
    private DescriptionScrollPanel descriptionPanel;

    // Currently selected stat for description
    private String selectedStat = "";
    private static final Map<String, StatDescription> statDescriptions = new HashMap<>();

    // Initialize stat descriptions with translation keys
    static {
        statDescriptions.put("accuracy", new StatDescription(
                "stat.dominatus.accuracy",
                "stat.dominatus.accuracy.desc",
                "stat.dominatus.accuracy.details"
        ));

        statDescriptions.put("evasion", new StatDescription(
                "stat.dominatus.evasion",
                "stat.dominatus.evasion.desc",
                "stat.dominatus.evasion.details"
        ));

        statDescriptions.put("crit_chance", new StatDescription(
                "stat.dominatus.crit_chance",
                "stat.dominatus.crit_chance.desc",
                "stat.dominatus.crit_chance.details"
        ));

        statDescriptions.put("crit_damage", new StatDescription(
                "stat.dominatus.crit_damage",
                "stat.dominatus.crit_damage.desc",
                "stat.dominatus.crit_damage.details"
        ));

        statDescriptions.put("health", new StatDescription(
                "stat.dominatus.health",
                "stat.dominatus.health.desc",
                "stat.dominatus.health.details"
        ));

        statDescriptions.put("armor", new StatDescription(
                "stat.dominatus.armor",
                "stat.dominatus.armor.desc",
                "stat.dominatus.armor.details"
        ));

        statDescriptions.put("armor_toughness", new StatDescription(
                "stat.dominatus.armor_toughness",
                "stat.dominauts.armor_toughness.desc",
                "stat.dominatus.armor_toughness.details"
        ));

        statDescriptions.put("movement_speed", new StatDescription(
                "stat.dominatus.movement_speed",
                "stat.dominatus.movement_speed.desc",
                "stat.dominatus.movement_speed.details"
        ));

        statDescriptions.put("attack_damage", new StatDescription(
                "stat.dominatus.attack_damage",
                "stat.dominatus.attack_damage.desc",
                "stat.dominatus.attack_damage.details"
        ));
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
        descriptionPanel = new DescriptionScrollPanel(leftX, panelY, leftWidth, panelHeight,
                Text.translatable("panel.dominatus.stat_description"));
        addPanel(descriptionPanel);

        // Create stats panel (right side - 2/3 width)
        StatsPanel.StatsProvider statsProvider = createStatsProvider();
        statsPanel = new StatsPanel(rightX, panelY, rightWidth, panelHeight,
                Text.translatable("panel.dominatus.player_statistics"), statsProvider);

        // Setup click handler to update selected stat
        statsPanel.setOnStatClicked(index -> {
            switch (index) {
                case 1:
                    selectedStat = "accuracy";
                    break;
                case 2:
                    selectedStat = "evasion";
                    break;
                case 3:
                    selectedStat = "crit_chance";
                    break;
                case 4:
                    selectedStat = "crit_damage";
                    break;
                case 6:
                    selectedStat = "health";
                    break;
                case 7:
                    selectedStat = "armor";
                    break;
                case 8:
                    selectedStat = "movement_speed";
                    break;
                case 9:
                    selectedStat = "attack_damage";
                    break;
            }
        });

        addPanel(statsPanel);
    }

    /**
     * Create the stats provider for this tab.
     */
    private StatsPanel.StatsProvider createStatsProvider() {
        return new StatsPanel.StatsProvider() {
            @Override
            public List<ListElement> createListElements() {
                List<ListElement> items = new ArrayList<>();

                // Add combat stats header
                items.add(ListElement.header(Text.translatable("header.dominatus.combat_stats")));

                // Add stats with icons
                items.add(ListElement.text(Text.translatable("stat.dominatus.accuracy.label", "$accuracy_value")));
                items.add(ListElement.text(Text.translatable("stat.dominatus.evasion.label", "$evasion_value")));
                items.add(ListElement.text(Text.translatable("stat.dominatus.crit_chance.label", "$crit_chance_value")));
                items.add(ListElement.text(Text.translatable("stat.dominatus.crit_damage.label", "$crit_damage_value")));

                // Add player stats header
                items.add(ListElement.header(Text.translatable("header.dominatus.player_attributes")));

                // Add basic stats
                items.add(ListElement.text(Text.translatable("stat.dominatus.health.label", "$health_value")));
                items.add(ListElement.text(Text.translatable("stat.dominatus.armor.label", "$armor_value")));
                items.add(ListElement.text(Text.translatable("stat.dominatus.movement_speed.label", "$movement_speed")));
                items.add(ListElement.text(Text.translatable("stat.dominatus.attack_damage.label", "$attack_damage")));

                return items;
            }

            @Override
            public Map<String, Object> collectValues(ClientPlayerEntity player) {
                Map<String, Object> values = new HashMap<>();

                values.put("accuracy_value", String.format("%d", Math.round(getAttributeValue(player, ModEntityAttributes.ACCURACY))));
                values.put("evasion_value", String.format("%d", Math.round(getAttributeValue(player, ModEntityAttributes.EVASION))));
                values.put("crit_chance_value", String.format("%.1f%%", getAttributeValue(player, ModEntityAttributes.CRIT_CHANCE) * 100f));
                values.put("crit_damage_value", String.format("%.1f%%", getAttributeValue(player, ModEntityAttributes.CRIT_DAMAGE) * 100f));

                values.put("health_value", String.format("%.1f/%.1f", player.getHealth(), player.getMaxHealth()));
                values.put("armor_value", String.format("%d", player.getArmor()));
                values.put("movement_speed", String.format("%.2f", player.getMovementSpeed()));
                values.put("attack_damage", String.format("%.1f", player.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE)));

                return values;
            }
        };
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
            StatDescription description = statDescriptions.getOrDefault(selectedStat,
                    new StatDescription(
                            "stat.dominatus.unknown",
                            "stat.dominatus.unknown.desc",
                            ""
                    ));

            // Get translated text
            Text titleText = Text.translatable(description.titleKey);
            Text descText = Text.translatable(description.descriptionKey);
            Text detailsText = description.detailsKey.isEmpty() ? Text.empty() : Text.translatable(description.detailsKey);

            // Draw title
            int titleColor = 0xFFFFCC00; // Yellow color for titles
            context.drawTextWithShadow(
                    textRenderer,
                    titleText,
                    x + (width - textRenderer.getWidth(titleText)) / 2,
                    currentY,
                    titleColor
            );
            currentY += textRenderer.fontHeight + 10;

            // Draw icon if available
            Identifier icon = null;

            if (icon != null) {
                int iconSize = 32;
                context.drawTexture(
                        icon,
                        x + (width - iconSize) / 2,
                        currentY,
                        0, 0,
                        iconSize, iconSize,
                        iconSize, iconSize
                );
                currentY += iconSize + 10;
            } else {
                currentY += 5;
            }

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
            int totalHeight = currentY - (getContentY() - (int)scrollAmount) ; // Add some padding
            setContentHeight(totalHeight);
        }
    }

    private double getAttributeValue(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        return instance != null ? instance.getValue() : 0;
    }

    /**
     * Class to store stat descriptions with translation keys.
     */
    private record StatDescription(String titleKey, String descriptionKey, String detailsKey) {
    }
}