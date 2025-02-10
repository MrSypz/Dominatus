package sypztep.dominatus.client.gui.components;

import net.minecraft.client.gui.DrawContext;
import sypztep.dominatus.client.gui.core.Component;

public class Text extends Component {
    private String text;
    private int color;

    public Text(int x, int y, String text, int color) {
        super(x, y, 0, 0); // Width and height will be set based on text
        this.text = text;
        this.color = color;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!visible) return;
        context.drawText(this.textRenderer, text, x, y, color, true);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(int color) {
        this.color = color;
    }
}