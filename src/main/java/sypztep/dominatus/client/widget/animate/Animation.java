package sypztep.dominatus.client.widget.animate;

public class Animation<T> {
    protected final float duration;
    protected float elapsedTime;
    protected final boolean isLooping;
    protected boolean isCompleted;
    private final T data;

    public Animation(float duration, boolean isLooping, T data) {
        this.duration = duration;
        this.isLooping = isLooping;
        this.elapsedTime = 0.0f;
        this.isCompleted = false;
        this.data = data;
    }

    public void update(float delta) {
        if (isCompleted && !isLooping) {
            return;
        }

        elapsedTime += delta;
        if (elapsedTime >= duration) {
            if (isLooping) {
                elapsedTime = elapsedTime % duration;
            } else {
                elapsedTime = duration;
                isCompleted = true;
            }
        }
    }

    public float getProgress() {
        return elapsedTime / duration;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public T getData() {
        return data;
    }
}