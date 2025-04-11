package sypztep.dominatus.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import sypztep.tyrannus.client.screen.BaseScreen;
import sypztep.tyrannus.client.screen.panel.Button;
import sypztep.tyrannus.client.screen.panel.InfoPanel;

public class RifMissingScreen extends BaseScreen {
    public RifMissingScreen() {
        super(Text.of("Missing Required Mod"));
    }

    @Override
    protected void initPanels() {
        int panelWidth = 300;
        int panelHeight = 160;
        int centerX = (width - panelWidth) / 2;
        int centerY = (height - panelHeight) / 2;

        InfoPanel infoPanel = new InfoPanel(centerX, centerY, panelWidth, panelHeight, Text.of("Missing Dependency"));
        infoPanel.addParagraph(Text.of("The MultiHit feature is currently unavailable because the required mod 'RIF' is missing."));
        infoPanel.addParagraph(Text.of("The game will continue to function normally, but for the full experience, please install the RIF mod."));

        int buttonWidth = 130;
        int buttonHeight = 20;
        int spacing = 10;
        int totalButtonWidth = buttonWidth * 2 + spacing;
        int buttonsX = centerX + (panelWidth - totalButtonWidth) / 2;
        int buttonsY = centerY + panelHeight + 10;

        Button modLinkButton = new Button(buttonsX, buttonsY, buttonWidth, buttonHeight, Text.of("Download RIF Mod"), btn -> {
            String url = "https://modrinth.com/mod/reduced-invincibility-frames";
            try {
                net.minecraft.util.Util.getOperatingSystem().open(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button backButton = new Button(buttonsX + buttonWidth + spacing, buttonsY, buttonWidth, buttonHeight, Text.of("Back to Title"), btn -> {
            MinecraftClient.getInstance().setScreen(new TitleScreen());
        });


        addPanel(infoPanel);
        addPanel(modLinkButton);
        addPanel(backButton);
    }
}
