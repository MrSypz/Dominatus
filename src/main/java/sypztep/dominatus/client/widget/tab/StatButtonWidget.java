package sypztep.dominatus.client.widget.tab;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.client.screen.PlayerInfoScreen;
import sypztep.dominatus.client.screen.exampleimlpement.PlayerStatsScreen;
import sypztep.dominatus.client.widget.TabWidgetButton;

import java.util.Collections;

public class StatButtonWidget extends TabWidgetButton {

    public StatButtonWidget(int x, int y, int width, int height, Text message, MinecraftClient player, Identifier icon) {
        super(x, y, width, height, message, player, icon,
                Collections.singletonList(Text.literal("Stat Information")),
                Collections.singletonList(Text.literal("Information about yourself :)")));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        if (client != null) client.setScreen(new PlayerInfoScreen());
    }
}


