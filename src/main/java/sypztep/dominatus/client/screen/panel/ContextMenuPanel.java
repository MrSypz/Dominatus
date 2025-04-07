package sypztep.dominatus.client.screen.panel;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import sypztep.tyrannus.client.screen.panel.UIPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ContextMenuPanel extends UIPanel {
    private final List<MenuItem> items = new ArrayList<>();
    private static final int ITEM_HEIGHT = 20;
    private static final int ITEM_WIDTH = 100;
    private static final int TEXT_COLOR = 0xFFFE0000;

    public ContextMenuPanel(int x, int y) {
        super(x, y, ITEM_WIDTH, 0, null); // No title
        this.setDrawHeader(false);
        this.setPadding(2);
    }

    public void addItem(Text text, Consumer<ContextMenuPanel> onClick) {
        items.add(new MenuItem(text, onClick));
        this.height = items.size() * ITEM_HEIGHT + padding * 2; // Update height dynamically
        updateContentBounds();
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        int itemY = y + padding;

        for (MenuItem item : items) {
            boolean itemHovered = mouseX >= x && mouseX < x + width && mouseY >= itemY && mouseY < itemY + ITEM_HEIGHT;

            int bgColor = itemHovered ? interpolateColor(PANEL_BACKGROUND, 0xFF333333, 0.8f) : PANEL_BACKGROUND;
            context.fill(x, itemY, x + width, itemY + ITEM_HEIGHT,10, bgColor);

            context.getMatrices().push();
            context.getMatrices().translate(0.0f, 0.0f, 10f);
            context.drawTextWithShadow(
                    textRenderer,
                    item.text,
                    x + padding,
                    itemY + (ITEM_HEIGHT - textRenderer.fontHeight) / 2,
                    itemHovered ? HEADER_COLOR : TEXT_COLOR
            );

            context.getMatrices().pop();

            itemY += ITEM_HEIGHT;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && button == 0) { // Left-click to select
            int itemY = y + padding;
            for (MenuItem item : items) {
                if (mouseY >= itemY && mouseY < itemY + ITEM_HEIGHT) {
                    item.onClick.accept(this);
                    return true;
                }
                itemY += ITEM_HEIGHT;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void clearItems() {
        items.clear();
        this.height = padding * 2; // Reset height
        updateContentBounds();
    }

    private record MenuItem(Text text, Consumer<ContextMenuPanel> onClick) {
    }
}