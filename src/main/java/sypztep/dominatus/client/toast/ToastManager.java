package sypztep.dominatus.client.toast;

import net.minecraft.text.Text;
import sypztep.dominatus.ModConfig;

import java.util.ArrayList;
import java.util.List;

public class ToastManager {
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

    public static void showExperience(Text message) {
        if (!ModConfig.enableToastNotifications) return;
        INSTANCE.addToast(new ToastNotification(message, ToastNotification.ToastType.EXPERIENCE));
    }

    public static void showLevelUp(Text message) {
        if (!ModConfig.enableToastNotifications) return;

        INSTANCE.addToast(new ToastNotification(message, ToastNotification.ToastType.LEVEL_UP));
    }

    public static void showLevelDown(Text message) {
        if (!ModConfig.enableToastNotifications) return;

        INSTANCE.addToast(new ToastNotification(message, ToastNotification.ToastType.LEVEL_DOWN));
    }

    public static void showDeathPenalty(Text message) {
        if (!ModConfig.enableToastNotifications) return;

        INSTANCE.addToast(new ToastNotification(message, ToastNotification.ToastType.DEATH_PENALTY));
    }

    public static void showInfo(Text message) {
        if (!ModConfig.enableToastNotifications) return;

        INSTANCE.addToast(new ToastNotification(message, ToastNotification.ToastType.INFO));
    }

    public static void showWarning(Text message) {
        if (!ModConfig.enableToastNotifications) return;

        INSTANCE.addToast(new ToastNotification(message, ToastNotification.ToastType.WARNING));
    }

    public static void showError(Text message) {
        if (!ModConfig.enableToastNotifications) return;

        INSTANCE.addToast(new ToastNotification(message, ToastNotification.ToastType.ERROR));
    }

    // Convenience methods with string input
    public static void showExperience(String message) {
        showExperience(Text.literal(message));
    }

    public static void showLevelUp(String message) {
        showLevelUp(Text.literal(message));
    }

    public static void showLevelDown(String message) {
        showLevelDown(Text.literal(message));
    }

    public static void showDeathPenalty(String message) {
        showDeathPenalty(Text.literal(message));
    }

    public static void showInfo(String message) {
        showInfo(Text.literal(message));
    }

    public static void showWarning(String message) {
        showWarning(Text.literal(message));
    }

    public static void showError(String message) {
        showError(Text.literal(message));
    }
}