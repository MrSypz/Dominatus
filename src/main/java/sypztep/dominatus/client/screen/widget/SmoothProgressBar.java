package sypztep.dominatus.client.screen.widget;

import net.minecraft.client.gui.DrawContext;
import sypztep.dominatus.ModConfig;

public final class SmoothProgressBar extends Animation {
    private final int barWidth;  // Width of the progress bar
    private final int barHeight; // Height of the progress bar
    private final int barColor;  // Color of the filled part of the bar
    private final int backgroundColor; // Background color of the bar
    private float currentProgress; // Current progress value
    private float targetProgress; // Target progress value

    // Constructor
    public SmoothProgressBar(float duration, boolean isLooping, int barWidth, int barHeight) {
        super(duration, isLooping);
        this.barWidth = barWidth;
        this.barHeight = barHeight;
        this.barColor = ModConfig.barColor;
        this.backgroundColor = ModConfig.barBGColor;
        this.currentProgress = 0.0f;
        this.targetProgress = 0.0f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // Calculate the progress ratio using elapsed time and duration
        float progressRatio = Math.min(1.0f, elapsedTime / duration);
        float easedRatio = easeOutQuint(progressRatio);

        // Interpolate the current progress towards the target progress
        currentProgress = currentProgress + (targetProgress - currentProgress) * easedRatio;

        // Optionally clamp the currentProgress to the targetProgress
        if (currentProgress < targetProgress) {
            currentProgress = Math.min(currentProgress, targetProgress);
        } else if (currentProgress > targetProgress) {
            currentProgress = Math.max(currentProgress, targetProgress);
        }
    }

    // Ease-out quint function
    private float easeOutQuint(float t) {
        return (float) (1 - Math.pow(1 - t, 5));
    }

    // Set the progress based on current XP and XP needed for the next level
    public void setProgress(long currentXp, long xpToNextLevel) {
        if (xpToNextLevel > 0) {
            this.targetProgress = Math.max(0, Math.min(1, (float) currentXp / xpToNextLevel));
        } else {
            this.targetProgress = 0.0f; // In case xpToNextLevel is zero, set progress to 0
        }
        this.elapsedTime = 0.0f; // Reset elapsed time for smooth transition
        this.isCompleted = false; // Reset completion status
    }

    public void render(DrawContext context, int x, int y) {
        if (ModConfig.renderStyle == ModConfig.RenderStyle.BAR) renderBar(context,x,y);
         else renderSlate(context,x,y);
    }
    public void renderBar(DrawContext context, int x, int y) {
        float progress = Math.max(0, Math.min(1, currentProgress));
        int filledWidth = (int) (progress * barWidth);
        context.fill(x, y, x + barWidth, y + barHeight, backgroundColor);
        context.fill(x, y, x + filledWidth, y + barHeight, barColor);
    }
    public void renderSlate(DrawContext context, int x, int y) {
        float progress = Math.max(0, Math.min(1, currentProgress));

        // Define the number of segments and the spacing between them
        int segments = 24; // Number of segments
        int segmentWidth = barWidth / (2 * segments); // Width of each segment

        // Calculate how many segments should be filled based on the current progress
        int filledSegments = (int) (progress * segments);

        for (int i = 0; i < segments; i++) {
            int segmentX = x + i * (segmentWidth + segmentWidth);

            if (i < filledSegments) {
                // Draw filled segment
                context.fill(segmentX, y, segmentX + segmentWidth, y + barHeight, barColor);
            } else {
                // Optionally, draw the background for empty segments
                context.fill(segmentX, y, segmentX + segmentWidth, y + barHeight, backgroundColor);
            }
        }
    }
}

