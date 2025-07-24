package sypztep.dominatus.client.toast;

import java.util.ArrayList;
import java.util.List;

public final class ToastManager {
    private static final ToastManager INSTANCE = new ToastManager();

    private static final int MAX_TOASTS = 5;

    private final List<ToastNotification> activeToasts = new ArrayList<>();

    private ToastManager() {}

    public static ToastManager getInstance() {
        return INSTANCE;
    }

    public void update(float deltaTime) {
        for (ToastNotification toast : activeToasts) toast.update(deltaTime);

        activeToasts.removeIf(ToastNotification::isExpired);
    }

    public void addToast(ToastNotification toast) {
        activeToasts.add(toast);
        // FIFO
        while (activeToasts.size() > MAX_TOASTS) activeToasts.removeFirst();
    }

    public List<ToastNotification> getActiveToasts() {
        return new ArrayList<>(activeToasts);
    }

    public void clear() {
        activeToasts.clear();
    }
}