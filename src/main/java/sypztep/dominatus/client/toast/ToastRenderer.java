package sypztep.dominatus.client.toast;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import sypztep.dominatus.ModConfig;

import java.util.ArrayList;
import java.util.List;

public final class ToastRenderer {
    private static final int TOAST_PADDING = 8;
    private static final int TOAST_SPACING = 2;
    private static final int MIN_TOAST_WIDTH = 200;
    private static final int MAX_TOAST_WIDTH = 300;
    private static final int BORDER_SIZE = 1;
    private static final int PROGRESS_BAR_HEIGHT = 2;

    // Prevent instantiation - utility class
    private ToastRenderer() {}

    public static void renderToasts(DrawContext context, int screenWidth, float deltaTime) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Update toast manager
        ToastManager.getInstance().update(deltaTime);

        List<ToastNotification> toasts = ToastManager.getInstance().getActiveToasts();
        if (toasts.isEmpty()) return;

        renderToastList(context, client.textRenderer, toasts, screenWidth);
    }

    /**
     * Render a list of toasts with proper scaling and positioning
     */
    private static void renderToastList(DrawContext context, TextRenderer textRenderer, List<ToastNotification> toasts, int screenWidth) {

        float toastScale = ModConfig.toastScale / 100.0f;
        int currentY = ModConfig.toastYOffset;

        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();
        matrixStack.scale(toastScale, toastScale, 1.0f);

        int scaledScreenWidth = (int) (screenWidth / toastScale);
        int scaledCurrentY = (int) (currentY / toastScale);

        // Render from bottom to top (newer toasts on top)
        for (int i = toasts.size() - 1; i >= 0; i--) {
            ToastNotification toast = toasts.get(i);
            if (!toast.isVisible()) continue;

            ToastDimensions dimensions = calculateToastDimensions(textRenderer, toast.getMessage());
            int toastX = calculateToastX(scaledScreenWidth, dimensions.width(), toast.getSlideOffset(), toastScale);

            renderSingleToast(context, textRenderer, toast, toastX, scaledCurrentY, dimensions);
            scaledCurrentY += dimensions.height() + TOAST_SPACING;
        }

        matrixStack.pop();
    }

    /**
     * Calculate X position for toast based on configuration
     */
    private static int calculateToastX(int scaledScreenWidth, int toastWidth, float slideOffset, float scale) {
        float scaledMargin = ModConfig.toastMargin / scale;
        float scaledSlideOffset = slideOffset / scale;

        return ModConfig.toastPositionLeft ? (int) (scaledMargin - scaledSlideOffset) : (int) (scaledScreenWidth - toastWidth - scaledMargin + scaledSlideOffset);
    }

    /**
     * Render a single toast notification
     */
    private static void renderSingleToast(DrawContext context, TextRenderer textRenderer, ToastNotification toast, int x, int y, ToastDimensions dimensions) {
        ToastColors colors = new ToastColors(toast.getBackgroundColor(), toast.getBorderColor(), toast.getTextColor(), toast.getProgressBarColor(), toast.getProgressBarBackgroundColor());

        // Render background
        context.fill(x, y, x + dimensions.width(), y + dimensions.height(), colors.background());

        // Render border
        renderBorder(context, x, y, dimensions, colors.border());

        // Render progress bar
        renderProgressBar(context, x, y, dimensions, toast.getRemainingProgress(), colors);

        // Render text
        renderToastText(context, textRenderer, toast.getMessage(), x, y, dimensions, colors.text());
    }

    /**
     * Render toast border based on position configuration
     */
    private static void renderBorder(DrawContext context, int x, int y, ToastDimensions dimensions, int borderColor) {
        if (ModConfig.toastPositionLeft) context.fill(x + dimensions.width() - BORDER_SIZE, y, x + dimensions.width(), y + dimensions.height(), borderColor);

         else context.fill(x, y, x + BORDER_SIZE, y + dimensions.height(), borderColor);
    }

    /**
     * Render progress bar at bottom of toast
     */
    private static void renderProgressBar(DrawContext context, int x, int y, ToastDimensions dimensions, float remainingProgress, ToastColors colors) {
        int progressBarY = y + dimensions.height() - PROGRESS_BAR_HEIGHT;

        // Background
        context.fill(x, progressBarY, x + dimensions.width(), y + dimensions.height(), colors.progressBarBg());

        // Fill based on remaining time
        int progressWidth = (int) (dimensions.width() * remainingProgress);
        context.fill(x, progressBarY, x + progressWidth, y + dimensions.height(), colors.progressBar());
    }

    /**
     * Render text with word wrapping and proper line spacing
     */
    private static void renderToastText(DrawContext context, TextRenderer textRenderer, Text message, int x, int y, ToastDimensions dimensions, int textColor) {
        List<String> lines = dimensions.lines();

        int currentY = y + TOAST_PADDING;
        for (String line : lines) {
            context.drawTextWithShadow(textRenderer, line, x + TOAST_PADDING, currentY, textColor);
            currentY += textRenderer.fontHeight + 2; // 2px line spacing to match your implementation
        }
    }

    /**
     * Calculate toast dimensions based on text content
     */
    private static ToastDimensions calculateToastDimensions(TextRenderer textRenderer, Text message) {
        String text = message.getString();
        int availableWidth = MAX_TOAST_WIDTH - (TOAST_PADDING * 2);

        List<String> lines = wrapText(textRenderer, text, availableWidth);

        int contentWidth = lines.stream().mapToInt(textRenderer::getWidth).max().orElse(0);

        int contentHeight = lines.size() * textRenderer.fontHeight + (lines.size() - 1) * 2; // 2px line spacing

        int totalWidth = Math.max(MIN_TOAST_WIDTH, contentWidth + (TOAST_PADDING * 2));
        int totalHeight = contentHeight + (TOAST_PADDING * 2) + PROGRESS_BAR_HEIGHT;

        return new ToastDimensions(totalWidth, totalHeight, contentWidth, contentHeight, lines);
    }

    /**
     * Wrap text to fit within specified width - handles explicit line breaks
     */
    private static List<String> wrapText(TextRenderer textRenderer, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();

        String[] explicitLines = text.split("\\n"); // for spacing

        for (String line : explicitLines) {
            String[] words = line.split(" ");

            if (words.length == 0) {
                lines.add("");
                continue;
            }

            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;

                if (textRenderer.getWidth(testLine) <= maxWidth) currentLine = new StringBuilder(testLine);
                else {
                    if (!currentLine.isEmpty()) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder(word);
                    } else lines.add(word);
                }
            }

            if (!currentLine.isEmpty()) lines.add(currentLine.toString());

        }

        return lines;
    }

    public record ToastDimensions(int width, int height, int contentWidth, int contentHeight, List<String> lines) {    }

    private record ToastColors(int background, int border, int text, int progressBar, int progressBarBg) {    }
}