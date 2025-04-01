package sypztep.dominatus.client.widget.animate;

import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class AnimationManager {
    private final Animated animated = new Animated();
    private final List<FadeAnimation> fadeAnimations = new ArrayList<>();

    public FadeAnimation createFadeAnimation(float duration, boolean isLooping, int targetColor,
                                             BiConsumer<DrawContext, Integer> renderFunction) {
        FadeAnimation fadeAnimation = new FadeAnimation(animated, duration, isLooping, targetColor)
                .withRender(renderFunction);
        fadeAnimations.add(fadeAnimation);
        return fadeAnimation;
    }

    public FadeAnimation createFadeAnimation(float duration, boolean isLooping, int targetColor) {
        FadeAnimation fadeAnimation = new FadeAnimation(animated, duration, isLooping, targetColor);
        fadeAnimations.add(fadeAnimation);
        return fadeAnimation;
    }

    public void updateAnimations(float delta) {
        animated.updateAnimations(delta);
    }

    public void renderAll(DrawContext context) {
        for (FadeAnimation animation : fadeAnimations) {
            if (animation.isActive()) {
                animation.render(context);
            }
        }
    }

    public void clearAnimations() {
        animated.clearAnimations();
        for (FadeAnimation animation : fadeAnimations) {
            animation.stop();
        }
    }

    public void clearFadeAnimations() {
        fadeAnimations.clear();
    }

    public Animated getAnimated() {
        return animated;
    }
}