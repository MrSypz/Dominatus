package sypztep.dominatus.client.widget.tab;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.client.widget.TabWidgetButton;
import sypztep.dominatus.common.payload.RefineButtonPayloadC2S;

import java.util.Collections;

public class RefineButtonWidget extends TabWidgetButton {
    public RefineButtonWidget(int x, int y, int width, int height, Text message, Identifier icon) {
        super(x, y, width, height, message, null, icon,
                Collections.singletonList(Text.literal("Refinement")),
                Collections.singletonList(Text.literal("Refine your item to make it stronger")));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        RefineButtonPayloadC2S.send();
        super.onClick(mouseX, mouseY);
    }
}
