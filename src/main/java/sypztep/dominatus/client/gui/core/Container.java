package sypztep.dominatus.client.gui.core;

import net.minecraft.client.gui.DrawContext;

public class Container extends Component {
    protected String backgroundColor;

    public Container(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.backgroundColor = "#000000";
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        // Draw background if set
        if (backgroundColor != null) {
            context.fill(x, y, x + width, y + height,
                    Integer.parseInt(backgroundColor.replace("#", ""), 16) | 0xFF000000);
        }

        // Render all children
        for (Component child : children) {
            child.render(context, mouseX, mouseY, delta);
        }
    }

    public void setBackgroundColor(String color) {
        this.backgroundColor = color;
    }
}