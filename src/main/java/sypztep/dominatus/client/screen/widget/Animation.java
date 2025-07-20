package sypztep.dominatus.client.screen.widget;

public class Animation {
    protected final float duration; // Duration of the animation in seconds
    protected float elapsedTime; // Time elapsed since animation started
    protected final boolean isLooping; // Whether the animation should loop
    protected boolean isCompleted; // Whether the animation is completed

    // Constructor
    public Animation(float duration, boolean isLooping) {
        this.duration = duration;
        this.isLooping = isLooping;
        this.elapsedTime = 0.0f;
        this.isCompleted = false;
    }

    // Update method to be called every frame
    public void update(float delta) {
        if (isCompleted && !isLooping) {
            return;
        }

        elapsedTime += delta;
        if (elapsedTime >= duration) {
            if (isLooping) {
                elapsedTime = elapsedTime % duration; // Loop the animation
            } else {
                elapsedTime = duration; // Clamp to duration
                isCompleted = true; // Mark as completed
            }
        }
    }

    // Get the progress of the animation as a value between 0 and 1
    public float getProgress() {
        return elapsedTime / duration;
    }
    // Check if the animation is completed
    public boolean isCompleted() {
        return isCompleted;
    }
}

