package sypztep.dominatus.client.widget.animate;

import net.minecraft.client.gui.DrawContext;
import java.util.function.BiConsumer;

public class FadeAnimation {
    private final Animated animated;
    private final float duration;
    private final boolean isLooping;
    private final int targetColor;
    private int currentColor = 0x00000000;
    private boolean isActive = false;

    // Default render method
    private BiConsumer<DrawContext, Integer> renderFunction;

    public FadeAnimation(Animated animated, float duration, boolean isLooping, int targetColor) {
        this.animated = animated;
        this.duration = duration;
        this.isLooping = isLooping;
        this.targetColor = targetColor;

        // Default render implementation (can be overridden)
        this.renderFunction = (context, color) -> {
            // Default implementation does nothing
        };
    }

    public void start() {
        if (isActive) return;

        currentColor = 0x00000000;
        animated.fade(duration, isLooping, targetColor, color -> {
            currentColor = color;
        });
        isActive = true;
    }

    public void stop() {
        isActive = false;
    }

    /**
     * Set a custom render function for this animation
     * @param renderFunction Function that takes DrawContext and current color
     * @return this animation instance for method chaining
     */
    public FadeAnimation withRender(BiConsumer<DrawContext, Integer> renderFunction) {
        this.renderFunction = renderFunction;
        return this;
    }

    /**
     * Render this animation using the provided render function
     */
    public void render(DrawContext context) {
        if (isActive && renderFunction != null) {
            renderFunction.accept(context, currentColor);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public float getDuration() {
        return duration;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public int getTargetColor() {
        return targetColor;
    }
}