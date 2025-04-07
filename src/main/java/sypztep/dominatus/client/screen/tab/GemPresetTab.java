package sypztep.dominatus.client.screen.tab;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.client.screen.panel.GemInventoryPanel;
import sypztep.dominatus.client.screen.panel.GemPresetPanel;
import sypztep.tyrannus.client.screen.tab.Tab;

public class GemPresetTab extends Tab {
    private final PlayerEntity player;

    public GemPresetTab(PlayerEntity player) {
        super("gempreset", Text.translatable("tab.dominatus.gem_preset"));
        this.player = player;
    }

    @Override
    protected void initPanels() {
        if (parentScreen == null) {
            return; // Safety check
        }

        int totalWidth = parentScreen.width - 20;
        int panelHeight = parentScreen.height - 100; // Leave space for nav bar and bottom UI
        int panelY = 65; // Below nav bar, matching StatsTab

        // Calculate panel dimensions (1:3 ratio - left:right)
        int leftWidth = totalWidth / 3;
        int rightWidth = totalWidth - leftWidth - 5; // 5px gap between panels

        int leftX = 10;
        int rightX = leftX + leftWidth + 5;

        // Gem Inventory Panel (left side - 1/3 width)
        GemPresetPanel presetPanel = new GemPresetPanel(rightX, panelY, rightWidth, panelHeight, player);
        GemInventoryPanel inventoryPanel = new GemInventoryPanel(leftX, panelY, leftWidth, panelHeight, player, presetPanel);
        addPanel(inventoryPanel);
        addPanel(presetPanel);
    }
}