package sypztep.dominatus.client.screen.panel;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import sypztep.tyrannus.client.screen.panel.UIPanel;

public class GemInventoryPanel extends UIPanel {
    public static final int INVENTORY_WIDTH = 76;

    public GemInventoryPanel(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(x, y, x + INVENTORY_WIDTH, y + height, 0xFF1A1A1A);
        context.drawBorder(x, y, INVENTORY_WIDTH, height, 0xFF424242);
    }
}