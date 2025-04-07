package sypztep.dominatus.client.widget.tab;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.client.screen.PlayerInfoScreen;
import sypztep.tyrannus.client.widget.TabWidgetButton;

import java.util.Collections;

public class StatButtonWidget extends TabWidgetButton {
    public static final TabWidgetButton STATS_TAB = new StatButtonWidget(
            Text.literal("stats"), MinecraftClient.getInstance(), Identifier.ofVanilla("icon/info")
    );

    public StatButtonWidget(Text message, MinecraftClient player, Identifier icon) {
        super(message, player, icon,
                Collections.singletonList(Text.literal("Stat Information")),
                Collections.singletonList(Text.literal("Information about yourself :)")));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        if (client != null) client.setScreen(new PlayerInfoScreen());
    }
}


