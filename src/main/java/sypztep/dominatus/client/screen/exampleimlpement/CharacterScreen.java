package sypztep.dominatus.client.screen.exampleimlpement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.screen.base.DominatusScreen;
import sypztep.dominatus.client.screen.base.InfoPanel;
import sypztep.dominatus.client.screen.base.StatsPanel;
import sypztep.dominatus.client.widget.ListElement;
import sypztep.dominatus.common.init.ModEntityAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example of a screen with multiple panels.
 */
@Environment(EnvType.CLIENT)
public class CharacterScreen extends DominatusScreen {
    // Icons for stats
    private static final Identifier ACCURACY_ICON = Dominatus.id("textures/gui/stats/accuracy.png");
    private static final Identifier EVASION_ICON = Dominatus.id("textures/gui/stats/evasion.png");
    private static final Identifier CRIT_CHANCE_ICON = Dominatus.id("textures/gui/stats/crit_chance.png");
    private static final Identifier CRIT_DAMAGE_ICON = Dominatus.id("textures/gui/stats/crit_damage.png");

    public CharacterScreen() {
        super(Text.translatable("screen.dominatus.character"));
    }

    @Override
    protected void initPanels() {
        // Stats panel on the right
        int statsWidth = width / 3;
        int statsHeight = height - 40;
        int statsX = width - statsWidth - 10;
        int statsY = 20;

        // Info panel on the left
        int infoWidth = width / 3;
        int infoHeight = height - 40;
        int infoX = 10;
        int infoY = 20;

        // Create the stats provider
        StatsPanel.StatsProvider combatStatsProvider = new StatsPanel.StatsProvider() {
            @Override
            public List<ListElement> createListElements() {
                List<ListElement> items = new ArrayList<>();

                // Add combat stats header
                items.add(ListElement.header(Text.of("COMBAT STATISTICS")));

                // Add stats with icons
                items.add(ListElement.withIcon(Text.of("Accuracy $accuracy_value"), ACCURACY_ICON));
                items.add(ListElement.withIcon(Text.of("Evasion $evasion_value"), EVASION_ICON));
                items.add(ListElement.withIcon(Text.of("Crit Chance $crit_chance_value"), CRIT_CHANCE_ICON));
                items.add(ListElement.withIcon(Text.of("Crit Damage $crit_damage_value"), CRIT_DAMAGE_ICON));

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

                return values;
            }
        };

        // Create the panel and add it to the screen
        StatsPanel statsPanel = new StatsPanel(
                statsX,
                statsY,
                statsWidth,
                statsHeight,
                Text.of("Player Statistics"),
                combatStatsProvider
        );

        InfoPanel infoPanel = new InfoPanel(infoX, infoY, infoWidth, infoHeight, Text.of("Character Info"));
        infoPanel.addParagraph(Text.of("Character information and background story goes here. This panel can contain multiple paragraphs of text that will automatically wrap and scroll if needed."));
        infoPanel.addParagraph(Text.of("You can add as much text as you want, and the panel will handle scrolling."));
        infoPanel.addParagraph(Text.of("This framework makes it easy to create complex screens with multiple panels."));

        // Add panels to the screen
        panels.add(statsPanel);
        panels.add(infoPanel);
    }

    private double getAttributeValue(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        return instance != null ? instance.getValue() : 0;
    }
}