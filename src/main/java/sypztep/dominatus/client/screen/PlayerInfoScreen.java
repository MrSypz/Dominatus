package sypztep.dominatus.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import sypztep.dominatus.client.screen.base.DominatusScreen;
import sypztep.dominatus.client.screen.base.ProgressBar;
import sypztep.dominatus.client.screen.base.TabManager;
import sypztep.dominatus.client.screen.tab.QuestsTab;
import sypztep.dominatus.client.screen.tab.SkillsTab;
import sypztep.dominatus.client.screen.tab.StatsTab;

public class PlayerInfoScreen extends DominatusScreen {
//    private ProgressBar xpBar;

    public PlayerInfoScreen() {
        super(Text.translatable("screen.dominatus.player_info"));
        tabManager = new TabManager(this);

        // Register tabs
        tabManager.registerTab(new StatsTab());
//        tabManager.registerTab(new SkillsTab());
//        tabManager.registerTab(new QuestsTab());
    }

    @Override
    protected void initPanels() {
//        xpBar = new ProgressBar(10, height - 30, width - 20, 14);
//        xpBar.setBarHeight(14);
//        xpBar.setFillColor(0xFF00AA00); // Green XP bar
//        updateXpBar();
//        addPanel(xpBar);
    }

    /**
     * Update the XP bar with the player's current XP.
     */
    private void updateXpBar() {
        if (client.player != null) {
            float xpProgress = client.player.experienceProgress;
            int xpLevel = client.player.experienceLevel;

//            xpBar.setProgress(xpProgress);
//            xpBar.setValueText("Level " + xpLevel);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Update XP bar each frame
        updateXpBar();

        // Continue with normal render
        super.render(context, mouseX, mouseY, delta);
    }
}