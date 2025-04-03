package sypztep.dominatus.client.widget.tab;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.payload.RefineButtonPayloadC2S;
import sypztep.tyrannus.client.widget.TabWidgetButton;

import java.util.Collections;

public class RefineButtonWidget extends TabWidgetButton {

    public static final TabWidgetButton REFINE_TAB = new RefineButtonWidget(
            Text.literal("refine"), Dominatus.id("hud/container/tab/icon/refine")
    );

    public RefineButtonWidget(Text message, Identifier icon) {
        super(message, null, icon,
                Collections.singletonList(Text.literal("Refinement")),
                Collections.singletonList(Text.literal("Refine your item to make it stronger")));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        RefineButtonPayloadC2S.send();
        super.onClick(mouseX, mouseY);
    }
}
