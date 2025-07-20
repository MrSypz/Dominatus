package sypztep.dominatus.client.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.client.util.ColorUtils;
import sypztep.dominatus.client.util.DrawContextUtils;
import sypztep.dominatus.common.component.living.LivingLevelComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class ActionWidgetButton extends ClickableWidget {
    private static final int HOVER_COLOR = 0xFF4D4D4D; // Example hover color
    private static final int DEFAULT_COLOR = 0xF0292929; // Default color
    protected final List<Text> tooltip = new ArrayList<>();
    private float transitionProgress = 0.0f; // 0.0 to 1.0
    private static final float TRANSITION_SPEED = 0.1f; // Speed of the transition
    protected int requiredStatPoints;
    protected LivingLevelComponent stats; // Changed from UniqueStatsComponent
    protected int localStatPoints;

    public ActionWidgetButton(int x, int y, int width, int height, Text message, LivingLevelComponent stats) {
        super(x, y, width, height, message);
        this.requiredStatPoints = 1;
        this.stats = stats;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Use the correct method from LivingLevelComponent
        localStatPoints = stats.getAvailableBenefits(); // Changed from getStatPoints()

        boolean hovered = isHovered() && localStatPoints >= requiredStatPoints; // Check against required points
        int lineColor = (localStatPoints >= requiredStatPoints) ? 0xFFFFFFFF : 0xFF888888; // Dim color if not enough points

        int targetColor = hovered ? HOVER_COLOR : DEFAULT_COLOR;

        // Clamp transition progress to the range [0, 1]
        transitionProgress = Math.min(Math.max(transitionProgress, 0.0f), 1.0f);

        // Apply cubic ease-out function to transition progress
        float easedProgress = easeOutCubic(transitionProgress);

        // Smoothly transition color based on eased progress
        int currentColor = ColorUtils.interpolateColor(DEFAULT_COLOR, targetColor, easedProgress);

        // Draw the widget with the current color
        DrawContextUtils.drawRect(context, getX(), getY(), getWidth(), getHeight(), currentColor);

        // Draw lines with conditional dimming based on available points
        DrawContextUtils.renderHorizontalLine(context, getX() + 4, getY() + getHeight() / 2, 9, 1, 400, lineColor);
        DrawContextUtils.renderVerticalLine(context, getX() + getWidth() / 2, getY() + 4, 9, 1, 400, lineColor);

        if (hovered) {
            transitionProgress += TRANSITION_SPEED * delta;
            if (ModConfig.tooltipinfo) // Assuming this config exists
                renderTooltip(context, mouseX, mouseY);
        } else {
            transitionProgress -= TRANSITION_SPEED * delta;
        }
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return super.clicked(mouseX, mouseY) && localStatPoints >= requiredStatPoints;
    }

    private float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    protected void renderTooltip(DrawContext context, int mouseX, int mouseY) {
        if (!tooltip.isEmpty()) {
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 6); // Push z-index by 6
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, mouseX, mouseY);
            context.getMatrices().pop();
        }
    }
}