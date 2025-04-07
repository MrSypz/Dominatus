package sypztep.dominatus.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.client.screen.tab.GemTab;
import sypztep.dominatus.client.screen.tab.StatsTab;
import sypztep.tyrannus.client.screen.BaseScreen;
import sypztep.tyrannus.client.screen.tab.TabManager;

public final class PlayerInfoScreen extends BaseScreen {

    public PlayerInfoScreen() {
        super(Text.translatable("screen.dominatus.player_info"));
        tabManager = new TabManager(this);

        PlayerEntity player = MinecraftClient.getInstance().player;
        tabManager.registerTab(new StatsTab());
        tabManager.registerTab(new GemTab(player));
    }

    @Override
    protected void initPanels() {
    }
}