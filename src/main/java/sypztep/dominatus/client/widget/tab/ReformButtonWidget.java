package sypztep.dominatus.client.widget.tab;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.client.widget.TabWidgetButton;
import sypztep.dominatus.common.payload.ReformButtonPayloadC2S;

import java.util.Collections;

public class ReformButtonWidget extends TabWidgetButton {
    public ReformButtonWidget(int x, int y, int width, int height, Text message, Identifier icon) {
        super(x, y, width, height, message, null, icon,
                Collections.singletonList(Text.literal("Reform")),
                Collections.singletonList(Text.literal("Reform your item to make it stronger")));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ReformButtonPayloadC2S.send();
        super.onClick(mouseX, mouseY);
    }
}
