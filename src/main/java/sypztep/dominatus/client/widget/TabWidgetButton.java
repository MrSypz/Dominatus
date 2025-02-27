package sypztep.dominatus.client.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import sypztep.dominatus.Dominatus;

import java.util.List;

public abstract class  TabWidgetButton extends ClickableWidget {
    protected final MinecraftClient client;
    private final Identifier buttonTexture;
    private final Identifier buttonHoverTexture;
    private final Identifier icon;
    private final List<Text> tooltip;
    private final List<Text> shiftTooltip;

    public TabWidgetButton(int x, int y, int width, int height, Text message, MinecraftClient player, Identifier icon, List<Text> tooltip, List<Text> shiftTooltip) {
        super(x, y, width, height, message);
        this.client = player;
        this.tooltip = tooltip;
        this.shiftTooltip = shiftTooltip;
        this.buttonTexture = Dominatus.id("hud/container/tab/tab_left_unselected");
        this.buttonHoverTexture = Dominatus.id("hud/container/tab/tab_left_selected");
        this.icon = icon;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawGuiTexture(RenderLayer::getGuiTextured,buttonTexture, getX(), getY(), getWidth(), getHeight());
        context.drawGuiTexture(RenderLayer::getGuiTextured,icon, getX() + 10, getY() + 4,18,18);

        if (isHovered()) {
            context.drawGuiTexture(RenderLayer::getGuiTextured, buttonHoverTexture, getX(), getY(), getWidth(), getHeight());
            context.drawGuiTexture(RenderLayer::getGuiTextured, icon, getX() + 8, getY() + 4,18,18);

            boolean isShiftHeld = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);
            if (isShiftHeld) {
                renderShiftTooltip(context, mouseX, mouseY);
            } else {
                renderTooltip(context, mouseX, mouseY);
            }
        }
    }
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
    protected void renderTooltip(DrawContext context, int mouseX, int mouseY) {
        if (!tooltip.isEmpty()) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, mouseX, mouseY);
        }
    }

    protected void renderShiftTooltip(DrawContext context, int mouseX, int mouseY) {
        if (!shiftTooltip.isEmpty()) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, shiftTooltip, mouseX, mouseY);
        }
    }
}
