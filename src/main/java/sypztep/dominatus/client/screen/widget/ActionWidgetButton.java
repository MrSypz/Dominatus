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
    private static final int MAXED_COLOR = 0xFF8B4513; // Brown color for maxed stats
    private static final int DISABLED_COLOR = 0xFF333333; // Dark gray for disabled

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

        boolean isMaxedStat = requiredStatPoints == Integer.MAX_VALUE; // Special value for maxed stats
        boolean hasEnoughPoints = localStatPoints >= requiredStatPoints;
        boolean canInteract = hasEnoughPoints && !isMaxedStat;
        boolean isHovering = isHovered(); // Separate hover detection from interaction
        boolean hovered = isHovering && canInteract; // Only for visual effects

        // Determine colors based on state
        int baseColor;
        int lineColor;

        if (isMaxedStat) {
            baseColor = MAXED_COLOR;
            lineColor = 0xFFFFD700; // Gold color for maxed stats
        } else if (!hasEnoughPoints) {
            baseColor = DISABLED_COLOR;
            lineColor = 0xFF666666; // Dim gray for insufficient points
        } else {
            baseColor = DEFAULT_COLOR;
            lineColor = 0xFFFFFFFF; // White for normal state
        }

        int targetColor = hovered ? HOVER_COLOR : baseColor;

        // Clamp transition progress to the range [0, 1]
        transitionProgress = Math.min(Math.max(transitionProgress, 0.0f), 1.0f);

        // Apply cubic ease-out function to transition progress
        float easedProgress = easeOutCubic(transitionProgress);

        // Smoothly transition color based on eased progress
        int currentColor = ColorUtils.interpolateColor(baseColor, targetColor, easedProgress);

        DrawContextUtils.drawRect(context, getX(), getY(), getWidth(), getHeight(), currentColor);

        if (isMaxedStat) {
            DrawContextUtils.renderHorizontalLine(context, getX() + 3, getY() + getHeight() / 2 - 1, 10, 1, 400, lineColor);
            DrawContextUtils.renderHorizontalLine(context, getX() + 3, getY() + getHeight() / 2 + 1, 10, 1, 400, lineColor);
        } else {
            DrawContextUtils.renderHorizontalLine(context, getX() + 4, getY() + getHeight() / 2, 9, 1, 400, lineColor);
            DrawContextUtils.renderVerticalLine(context, getX() + getWidth() / 2, getY() + 4, 9, 1, 400, lineColor);
        }

        // Show tooltip whenever hovering, regardless of interaction state
        if (isHovering) {
            if (canInteract) {
                transitionProgress += TRANSITION_SPEED * delta;
            }
            if (ModConfig.tooltipinfo) // Always show tooltip when hovering
                renderTooltip(context, mouseX, mouseY);
        } else {
            transitionProgress -= TRANSITION_SPEED * delta;
        }
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        boolean isMaxedStat = requiredStatPoints == Integer.MAX_VALUE;
        boolean hasEnoughPoints = localStatPoints >= requiredStatPoints;
        return super.clicked(mouseX, mouseY) && hasEnoughPoints && !isMaxedStat;
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