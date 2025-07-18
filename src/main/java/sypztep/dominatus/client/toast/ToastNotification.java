package sypztep.dominatus.client.toast;

import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class ToastNotification {
    // Animation states
    public enum AnimationState {
        SLIDING_IN,
        VISIBLE,
        FADING_OUT,
        FINISHED
    }

    // Toast types with different styling
    public enum ToastType {
        EXPERIENCE(0xFF4CAF50, 3000), // Green
        LEVEL_UP(0xFFFFD700, 4000),   // Gold
        LEVEL_DOWN(0xFFFF5722, 4000), // Red-Orange
        DEATH_PENALTY(0xFFD32F2F, 5000), // Dark Red
        INFO(0xFF2196F3, 3000),       // Blue
        WARNING(0xFFFF9800, 4000),    // Orange
        ERROR(0xFFF44336, 4000);      // Red

        public final int color;
        public final long durationMs;

        ToastType(int color, long durationMs) {
            this.color = color;
            this.durationMs = durationMs;
        }
    }

    // Constants
    private static final float SLIDE_DURATION = 0.3f; // 300ms
    private static final float FADE_DURATION = 0.4f; // 400ms

    // Toast properties
    private final Text message;
    private final ToastType type;
    private final long creationTime;

    // Animation properties
    private AnimationState state;
    private float stateTime;
    private float alpha;
    private float slideOffset;
    private boolean isExpired;

    public ToastNotification(Text message, ToastType type) {
        this.message = message;
        this.type = type;
        this.creationTime = System.currentTimeMillis();
        this.state = AnimationState.SLIDING_IN;
        this.stateTime = 0f;
        this.alpha = 0f;
        this.slideOffset = 200f; // Start off-screen to the right
        this.isExpired = false;
    }

    public void update(float deltaTime) {
        if (state == AnimationState.FINISHED) return;

        stateTime += deltaTime;
        long elapsed = System.currentTimeMillis() - creationTime;

        switch (state) {
            case SLIDING_IN:
                float slideProgress = Math.min(stateTime / SLIDE_DURATION, 1.0f);
                slideOffset = 200f * (1.0f - easeOutCubic(slideProgress));
                alpha = easeOutCubic(slideProgress);

                if (slideProgress >= 1.0f) {
                    state = AnimationState.VISIBLE;
                    stateTime = 0f;
                    slideOffset = 0f;
                    alpha = 1.0f;
                }
                break;

            case VISIBLE:
                if (elapsed >= type.durationMs) {
                    state = AnimationState.FADING_OUT;
                    stateTime = 0f;
                }
                break;

            case FADING_OUT:
                float fadeProgress = Math.min(stateTime / FADE_DURATION, 1.0f);
                alpha = 1.0f - easeInCubic(fadeProgress);
                slideOffset = 100f * easeInCubic(fadeProgress); // Slide out to the right

                if (fadeProgress >= 1.0f) {
                    state = AnimationState.FINISHED;
                    isExpired = true;
                }
                break;
        }
    }

    // Easing functions
    private float easeOutCubic(float t) {
        return 1.0f - (float) Math.pow(1.0 - t, 3.0);
    }

    private float easeInCubic(float t) {
        return (float) Math.pow(t, 3.0);
    }

    // Getters
    public Text getMessage() { return message; }
    public float getAlpha() { return alpha; }
    public float getSlideOffset() { return slideOffset; }
    public boolean isExpired() { return isExpired; }
    public boolean isVisible() { return state != AnimationState.FINISHED && alpha > 0; }
    public long getCreationTime() { return creationTime; }

    // Get background color with alpha
    public int getBackgroundColor() {
        int baseAlpha = (int) (180 * alpha); // 70% opacity when fully visible
        return ColorHelper.Argb.getArgb(baseAlpha, 0, 0, 0);
    }

    // Get border color with alpha
    public int getBorderColor() {
        int borderAlpha = (int) (255 * alpha);
        int baseColor = type.color;
        return ColorHelper.Argb.getArgb(
                borderAlpha,
                ColorHelper.Argb.getRed(baseColor),
                ColorHelper.Argb.getGreen(baseColor),
                ColorHelper.Argb.getBlue(baseColor)
        );
    }

    // Get text color with alpha
    public int getTextColor() {
        int textAlpha = (int) (255 * alpha);
        return ColorHelper.Argb.getArgb(textAlpha, 255, 255, 255);
    }
}