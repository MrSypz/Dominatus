package sypztep.dominatus.client.screen;

import net.minecraft.text.Text;
import sypztep.dominatus.client.screen.tab.StatsTab;
import sypztep.tyrannus.client.screen.BaseScreen;
import sypztep.tyrannus.client.screen.tab.TabManager;

public final class PlayerInfoScreen extends BaseScreen {

    public PlayerInfoScreen() {
        super(Text.translatable("screen.dominatus.player_info"));
        tabManager = new TabManager(this);

        tabManager.registerTab(new StatsTab());
    }

    @Override
    protected void initPanels() {
    }
}