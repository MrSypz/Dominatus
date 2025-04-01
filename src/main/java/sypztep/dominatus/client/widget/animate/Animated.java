package sypztep.dominatus.client.widget.animate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public class Animated {
    private final Map<Animation<?>, Consumer<Float>> activeAnimations = new HashMap<>();

    public void updateAnimations(float delta) {
        Iterator<Map.Entry<Animation<?>, Consumer<Float>>> iterator = activeAnimations.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Animation<?>, Consumer<Float>> entry = iterator.next();
            Animation<?> animation = entry.getKey();
            Consumer<Float> logic = entry.getValue();

            if (!animation.isCompleted() || animation.isLooping()) {
                animation.update(delta);
            }

            logic.accept(animation.getProgress());

            if (animation.isCompleted() && !animation.isLooping()) {
                iterator.remove();
            }
        }
    }

    public <T> void animate(float duration, boolean isLooping, T data, Consumer<Float> animationConsumer) {
        Animation<T> animation = new Animation<>(duration, isLooping, data);
        activeAnimations.put(animation, animationConsumer);
    }

    public void fade(float duration, boolean isLooping, int targetColor, Consumer<Integer> colorConsumer) {
        animate(duration, isLooping, targetColor, progress -> {
            int finalColor;
            if (progress >= 1.0f) {
                finalColor = targetColor;
            } else if (progress <= 0.0f) {
                finalColor = (0) | (targetColor & 0xFFFFFF);
            } else {
                int targetAlpha = (targetColor >>> 24) & 0xFF;
                int currentAlpha = (int) (progress * targetAlpha);
                finalColor = (currentAlpha << 24) | (targetColor & 0xFFFFFF);
            }
            colorConsumer.accept(finalColor);
        });
    }

    public void move(float duration, boolean isLooping, float startX, float startY, float endX, float endY, Consumer<float[]> positionConsumer) {
        float[] moveData = {startX, startY, endX, endY};
        animate(duration, isLooping, moveData, progress -> {
            float x = moveData[0] + (moveData[2] - moveData[0]) * progress;
            float y = moveData[1] + (moveData[3] - moveData[1]) * progress;
            positionConsumer.accept(new float[]{x, y});
        });
    }

    public void clearAnimations() {
        activeAnimations.clear();
    }

    public int getActiveAnimationCount() {
        return activeAnimations.size();
    }
}
