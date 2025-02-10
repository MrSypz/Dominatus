package sypztep.dominatus.client.gui.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import sypztep.dominatus.client.gui.core.Component;

public class Button extends Component {
    private String text;
    private Runnable onClick;
    private boolean isPressed;

    public Button(int x, int y, int width, int height, String text, Runnable onClick) {
        super(x, y, width, height);
        this.text = text;
        this.onClick = onClick;
        this.isPressed = false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!visible) return;
        boolean hovered = isHovered(mouseX, mouseY);
        int color = hovered ? 0xFFAAAAAA : 0xFF888888;

        // Draw button background
        context.fill(x, y, x + width, y + height, color);

        // Draw button text
        int textX = x + (width - this.textRenderer.getWidth(text)) / 2;
        int textY = y + (height - 8) / 2;
        context.drawText(this.textRenderer, text, textX, textY, 0xFFFFFFFF, true);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            isPressed = true;
            if (onClick != null) {
                onClick.run();
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        isPressed = false;
    }
}