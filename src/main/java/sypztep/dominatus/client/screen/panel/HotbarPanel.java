package sypztep.dominatus.client.screen.panel;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import sypztep.tyrannus.client.screen.panel.UIPanel;

public class HotbarPanel extends UIPanel {
    public HotbarPanel(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw hotbar background
        context.fill(x, y, x + width, y + height, 0xFF1A1A1A);
        context.drawBorder(x, y, width, height, 0xFF424242);
    }
}