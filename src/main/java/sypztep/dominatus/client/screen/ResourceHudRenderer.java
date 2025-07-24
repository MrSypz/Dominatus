package sypztep.dominatus.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import sypztep.dominatus.common.component.living.PlayerClassComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.playerclass.ResourceType;

@Environment(EnvType.CLIENT)
public class ResourceHudRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register(ResourceHudRenderer::renderResourceBar);
    }

    public static void renderResourceBar(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Don't render if no player or HUD is hidden
        if (client.player == null || client.options.hudHidden) return;

        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(client.player);

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Position resource bar below food bar (right side of screen)
        int barWidth = 81; // Same width as vanilla bars
        int barHeight = 9;
        int barX = screenWidth / 2 + 10; // Right side, after hotbar
        int barY = screenHeight - 49; // Above the hotbar

        renderResourceBar(context, classComp, barX, barY, barWidth, barHeight,tickCounter);
    }

    private static void renderResourceBar(DrawContext context, PlayerClassComponent classComp,
                                          int x, int y, int width, int height, RenderTickCounter tickCounter) {

        ResourceType resourceType = classComp.getResourceType();
        float currentResource = classComp.getCurrentResource();
        float maxResource = classComp.getMaxResource();
        float percentage = classComp.getResourcePercentage();

        // Background (dark border)
        context.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF000000);

        // Inner background (dark gray)
        context.fill(x, y, x + width, y + height, 0xFF2A2A2A);

        // Resource bar (filled portion)
        int filledWidth = (int) (width * percentage);
        if (filledWidth > 0) {
            // Add slight transparency to make it look more polished
            int resourceColor = resourceType.getColor() | 0xFF000000; // Ensure full alpha
            context.fill(x, y, x + filledWidth, y + height, resourceColor);

            // Add a brighter highlight on top for depth
            int highlightColor = brightenColor(resourceColor, 0.3f);
            context.fill(x, y, x + filledWidth, y + 2, highlightColor);
        }

        // Resource text (centered)
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        String resourceText = String.format("%.0f/%.0f", currentResource, maxResource);
        int textWidth = textRenderer.getWidth(resourceText);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - textRenderer.fontHeight) / 2;

        // Text with shadow for readability
        context.drawTextWithShadow(textRenderer, resourceText, textX, textY, 0xFFFFFF);

        // Resource type label (above the bar)
        String typeLabel = resourceType.getDisplayName();
        int labelWidth = textRenderer.getWidth(typeLabel);
        int labelX = x + (width - labelWidth) / 2;
        int labelY = y - textRenderer.fontHeight - 2;

        context.drawTextWithShadow(textRenderer, typeLabel, labelX, labelY, resourceType.getColor());

        // Regeneration indicator (small animated dots when regenerating)
        if (currentResource < maxResource) {
            renderRegenIndicator(context, x + width + 3, y + height / 2 - 2, tickCounter.getTickDelta(false));
        }
    }

    /**
     * Render small animated dots to show resource is regenerating
     */
    private static void renderRegenIndicator(DrawContext context, int x, int y, float tickDelta) {
        long time = System.currentTimeMillis();

        for (int i = 0; i < 3; i++) {
            // Animate each dot with a phase offset
            float phase = (time + i * 200) / 800.0f;
            float alpha = (float) ((Math.sin(phase * Math.PI * 2) + 1) / 2); // 0 to 1

            int dotColor = (int) (alpha * 255) << 24 | 0x00FF00; // Green with varying alpha
            context.fill(x, y + i * 2, x + 1, y + i * 2 + 1, dotColor);
        }
    }

    /**
     * Brighten a color by a given factor for highlights
     */
    private static int brightenColor(int color, float factor) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        r = Math.min(255, (int) (r + (255 - r) * factor));
        g = Math.min(255, (int) (g + (255 - g) * factor));
        b = Math.min(255, (int) (b + (255 - b) * factor));

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}