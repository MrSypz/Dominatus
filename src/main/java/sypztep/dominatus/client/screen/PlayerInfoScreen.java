package sypztep.dominatus.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import sypztep.dominatus.client.screen.tab.StatsTab;
import sypztep.tyrannus.client.screen.BaseScreen;
import sypztep.tyrannus.client.screen.tab.TabManager;

public final class PlayerInfoScreen extends BaseScreen {
//    private ProgressBar xpBar;

    public PlayerInfoScreen() {
        super(Text.translatable("screen.dominatus.player_info"));
        tabManager = new TabManager(this);

        tabManager.registerTab(new StatsTab());
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
//            float xpProgress = client.player.experienceProgress;
//            int xpLevel = client.player.experienceLevel;

//            xpBar.setProgress(xpProgress);
//            xpBar.setValueText("Level " + xpLevel);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Update XP bar each frame
//        updateXpBar();

        // Continue with normal render
        super.render(context, mouseX, mouseY, delta);
    }
}