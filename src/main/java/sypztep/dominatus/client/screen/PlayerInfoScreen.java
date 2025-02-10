package sypztep.dominatus.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import com.mojang.blaze3d.systems.RenderSystem;
import sypztep.dominatus.common.init.ModEntityAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class PlayerInfoScreen extends Screen {
    private static final int PANEL_X = 25;
    private static final int PANEL_Y = 80;
    private static final int PANEL_WIDTH = 225;
    private static final int PANEL_HEIGHT = 100;
    private static final int TEXT_PADDING = 10;
    private static final int LINE_HEIGHT = 12;

    public PlayerInfoScreen() {
        super(Text.literal("Player Info"));
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        // Enable blending for transparency
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Draw the main panel
        drawPanel(drawContext);

        // Draw title
        drawContext.drawText(
                textRenderer,
                Text.literal("Combat Statistics").setStyle(Style.EMPTY.withBold(true)),
                PANEL_X + TEXT_PADDING,
                PANEL_Y + TEXT_PADDING,
                0xFFFFFFFF,
                true
        );

        // Setup stats map
        Map<Text, Double> stats = new LinkedHashMap<>();
        stats.put(
                Text.literal("Accuracy").formatted(Formatting.YELLOW),
                player.getAttributeValue(ModEntityAttributes.GENERIC_ACCURACY)
        );
        stats.put(
                Text.literal("Evasion").formatted(Formatting.AQUA),
                player.getAttributeValue(ModEntityAttributes.GENERIC_EVASION)
        );

        // Render stats
        renderStats(drawContext, stats);

        RenderSystem.disableBlend();
    }

    private void drawPanel(DrawContext drawContext) {
        // Main background
        drawContext.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_WIDTH, PANEL_Y + PANEL_HEIGHT, 0x90000000);

        // Border
        int borderColor = 0xFF333333;
        drawContext.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_WIDTH, PANEL_Y + 1, borderColor); // Top
        drawContext.fill(PANEL_X, PANEL_Y + PANEL_HEIGHT - 1, PANEL_X + PANEL_WIDTH, PANEL_Y + PANEL_HEIGHT, borderColor); // Bottom
        drawContext.fill(PANEL_X, PANEL_Y, PANEL_X + 1, PANEL_Y + PANEL_HEIGHT, borderColor); // Left
        drawContext.fill(PANEL_X + PANEL_WIDTH - 1, PANEL_Y, PANEL_X + PANEL_WIDTH, PANEL_Y + PANEL_HEIGHT, borderColor); // Right
    }

    private void renderStats(DrawContext drawContext, Map<Text, Double> stats) {
        int yOffset = PANEL_Y + TEXT_PADDING + LINE_HEIGHT + 5;

        for (Map.Entry<Text, Double> stat : stats.entrySet()) {
            // Draw stat label
            drawContext.drawText(
                    textRenderer,
                    stat.getKey(),
                    PANEL_X + TEXT_PADDING,
                    yOffset,
                    0xFFFFFFFF,
                    false
            );

            // Draw stat value
            String value = String.format("%.1f", stat.getValue());
            int valueWidth = textRenderer.getWidth(value);
            drawContext.drawText(
                    textRenderer,
                    value,
                    PANEL_X + PANEL_WIDTH - TEXT_PADDING - valueWidth,
                    yOffset,
                    0xFFFFFFFF,
                    false
            );

            // Draw stat bar
            drawStatBar(
                    drawContext,
                    PANEL_X + TEXT_PADDING,
                    yOffset + LINE_HEIGHT,
                    PANEL_WIDTH - (TEXT_PADDING * 2),
                    4,
                    stat.getValue().floatValue(),
                    getStatColor(stat.getKey().getString())
            );

            yOffset += LINE_HEIGHT * 2;
        }
    }

    private void drawStatBar(DrawContext drawContext, int x, int y, int width, int height, float value, int color) {
        // Background
        drawContext.fill(x, y, x + width, y + height, 0xFF333333);

        // Calculate and draw fill
        float fillPercentage = Math.min(value / 100f, 1.0f);
        int fillWidth = (int)(width * fillPercentage);
        if (fillWidth > 0) {
            drawContext.fill(x, y, x + fillWidth, y + height, color);
        }
    }

    private int getStatColor(String statName) {
        return switch (statName.toLowerCase()) {
            case "accuracy" -> 0xFFFFAA00; // Orange
            case "evasion" -> 0xFF00AAFF; // Light Blue
            default -> 0xFFFFFFFF; // White
        };
    }

    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game when this screen is open
    }
}