package sypztep.dominatus.client.toast;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;

public class ToastHudRenderer implements HudRenderCallback {

    // Position settings
    private static final int TOAST_MARGIN_RIGHT = 10;
    private static final int TOAST_MARGIN_TOP = 60; // Below level HUD
    private static final int TOAST_PADDING = 8;
    private static final int TOAST_SPACING = 5;
    private static final int MIN_TOAST_WIDTH = 200;
    private static final int MAX_TOAST_WIDTH = 350;

    // Border settings
    private static final int BORDER_SIZE = 2;

    public ToastHudRenderer() {
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.getDebugHud().shouldShowDebugHud()) {
            return;
        }

        // Update toast manager
        float deltaTime = tickCounter.getTickDelta(false) / 20.0f; // Convert to seconds
        ToastManager.getInstance().update(deltaTime);

        // Render toasts
        renderToasts(drawContext, client);
    }

    private void renderToasts(DrawContext drawContext, MinecraftClient client) {
        TextRenderer textRenderer = client.textRenderer;
        List<ToastNotification> toasts = ToastManager.getInstance().getActiveToasts();

        if (toasts.isEmpty()) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int currentY = TOAST_MARGIN_TOP;

        // Render toasts from bottom to top (newer toasts appear on top)
        for (int i = toasts.size() - 1; i >= 0; i--) {
            ToastNotification toast = toasts.get(i);

            if (!toast.isVisible()) continue;

            // Calculate toast dimensions
            ToastDimensions dimensions = calculateToastDimensions(textRenderer, toast.getMessage());

            // Calculate position with slide animation
            int toastX = (int) (screenWidth - dimensions.width - TOAST_MARGIN_RIGHT + toast.getSlideOffset());
            int toastY = currentY;

            // Render the toast
            renderToast(drawContext, textRenderer, toast, toastX, toastY, dimensions);

            // Update Y position for next toast
            currentY += dimensions.height + TOAST_SPACING;
        }
    }

    private void renderToast(DrawContext drawContext, TextRenderer textRenderer,
                             ToastNotification toast, int x, int y, ToastDimensions dimensions) {

        // Get colors with proper alpha
        int backgroundColor = toast.getBackgroundColor();
        int borderColor = toast.getBorderColor();
        int textColor = toast.getTextColor();

        // Draw background
        drawContext.fill(x, y, x + dimensions.width, y + dimensions.height, backgroundColor);

        // Draw border
        drawContext.drawBorder(x, y, dimensions.width, dimensions.height, borderColor);

        // Draw inner border for extra emphasis
        drawContext.drawBorder(x + 1, y + 1, dimensions.width - 2, dimensions.height - 2,
                ColorHelper.Argb.getArgb((int) (255 * toast.getAlpha() * 0.3), 255, 255, 255));

        // Draw text
        renderWrappedText(drawContext, textRenderer, toast.getMessage(),
                x + TOAST_PADDING, y + TOAST_PADDING,
                dimensions.contentWidth, textColor);
    }

    private ToastDimensions calculateToastDimensions(TextRenderer textRenderer, Text message) {
        String text = message.getString();
        int availableWidth = MAX_TOAST_WIDTH - (TOAST_PADDING * 2);

        // Calculate wrapped text dimensions
        List<String> lines = wrapText(textRenderer, text, availableWidth);

        int contentWidth = 0;
        for (String line : lines) {
            contentWidth = Math.max(contentWidth, textRenderer.getWidth(line));
        }

        int contentHeight = lines.size() * textRenderer.fontHeight + (lines.size() - 1) * 2; // 2px line spacing

        int totalWidth = Math.max(MIN_TOAST_WIDTH, contentWidth + (TOAST_PADDING * 2));
        int totalHeight = contentHeight + (TOAST_PADDING * 2);

        return new ToastDimensions(totalWidth, totalHeight, contentWidth, contentHeight, lines);
    }

    private List<String> wrapText(TextRenderer textRenderer, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");

        if (words.length == 0) {
            lines.add("");
            return lines;
        }

        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;

            if (textRenderer.getWidth(testLine) <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word);
                }
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private void renderWrappedText(DrawContext drawContext, TextRenderer textRenderer,
                                   Text message, int x, int y, int maxWidth, int color) {
        String text = message.getString();
        List<String> lines = wrapText(textRenderer, text, maxWidth);

        int currentY = y;
        for (String line : lines) {
            drawContext.drawTextWithShadow(textRenderer, line, x, currentY, color);
            currentY += textRenderer.fontHeight + 2; // 2px line spacing
        }
    }

    private record ToastDimensions(int width, int height, int contentWidth, int contentHeight, List<String> lines) {
    }

    public static void register() {
        HudRenderCallback.EVENT.register(new ToastHudRenderer());
    }
}