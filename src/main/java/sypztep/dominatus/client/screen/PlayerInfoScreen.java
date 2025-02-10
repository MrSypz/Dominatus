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
    // Use relative positioning instead of fixed values
    private static final float PANEL_WIDTH_RATIO = 0.25f; // 25% of screen width
    private static final float PANEL_HEIGHT_RATIO = 0.35f; // 35% of screen height
    private static final float PANEL_X_RATIO = 0.02f; // 2% from left
    private static final float PANEL_Y_RATIO = 0.2f; // 20% from top

    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private int textPadding;
    private int lineHeight;

    public PlayerInfoScreen() {
        super(Text.literal("Player Info"));
    }

    @Override
    protected void init() {
        super.init();
        calculateDimensions();
    }

    private void calculateDimensions() {
        // Calculate responsive dimensions based on screen size
        this.panelWidth = (int)(this.width * PANEL_WIDTH_RATIO);
        this.panelHeight = (int)(this.height * PANEL_HEIGHT_RATIO);
        this.panelX = (int)(this.width * PANEL_X_RATIO);
        this.panelY = (int)(this.height * PANEL_Y_RATIO);

        // Scale padding and line height based on panel size
        this.textPadding = (int)(panelWidth * 0.04); // 4% of panel width
        this.lineHeight = Math.max(12, (int)(panelHeight * 0.08)); // 8% of panel height, minimum 12
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        drawPanel(drawContext);

        // Scale text based on panel size
        float scale = Math.min(panelWidth / 225f, panelHeight / 100f);
        scale = Math.max(1.0f, Math.min(scale, 1.5f)); // Limit scale between 1.0 and 1.5

        // Draw title with scaling
        Text title = Text.literal("Combat Statistics").setStyle(Style.EMPTY.withBold(true));
        float titleX = panelX + textPadding;
        float titleY = panelY + textPadding;
        drawContext.drawText(
                textRenderer,
                title,
                (int)titleX,
                (int)titleY,
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
        stats.put(
                Text.literal("Crit Chance").formatted(Formatting.RED),
                player.getAttributeValue(ModEntityAttributes.GENERIC_CRIT_CHANCE)
        );
        stats.put(
                Text.literal("Crit Damage").formatted(Formatting.DARK_RED),
                player.getAttributeValue(ModEntityAttributes.GENERIC_CRIT_DAMAGE)
        );

        renderStats(drawContext, stats);

        RenderSystem.disableBlend();
    }

    private void drawPanel(DrawContext drawContext) {
        // Main background with rounded corners
        drawContext.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x90000000);

        // Border with custom thickness based on panel size
        int borderThickness = Math.max(1, panelWidth / 225);
        int borderColor = 0xFF333333;

        // Draw borders
        drawContext.fill(panelX, panelY, panelX + panelWidth, panelY + borderThickness, borderColor);
        drawContext.fill(panelX, panelY + panelHeight - borderThickness, panelX + panelWidth, panelY + panelHeight, borderColor);
        drawContext.fill(panelX, panelY, panelX + borderThickness, panelY + panelHeight, borderColor);
        drawContext.fill(panelX + panelWidth - borderThickness, panelY, panelX + panelWidth, panelY + panelHeight, borderColor);
    }

    private void renderStats(DrawContext drawContext, Map<Text, Double> stats) {
        int yOffset = panelY + textPadding + lineHeight + 5;
        int barHeight = Math.max(4, lineHeight / 3);

        for (Map.Entry<Text, Double> stat : stats.entrySet()) {
            // Draw stat label
            drawContext.drawText(
                    textRenderer,
                    stat.getKey(),
                    panelX + textPadding,
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
                    panelX + panelWidth - textPadding - valueWidth,
                    yOffset,
                    0xFFFFFFFF,
                    false
            );

            // Draw stat bar with responsive height
            drawStatBar(
                    drawContext,
                    panelX + textPadding,
                    yOffset + lineHeight,
                    panelWidth - (textPadding * 2),
                    barHeight,
                    stat.getValue().floatValue(),
                    getStatColor(stat.getKey().getString())
            );

            yOffset += lineHeight * 2;
        }
    }

    private void drawStatBar(DrawContext drawContext, int x, int y, int width, int height, float value, int color) {
        // Background
        drawContext.fill(x, y, x + width, y + height, 0xFF333333);

        // Fill bar with smooth animation
        float fillPercentage = Math.min(value / 100f, 1.0f);
        int fillWidth = (int)(width * fillPercentage);
        if (fillWidth > 0) {
            drawContext.fill(x, y, x + fillWidth, y + height, color);
        }
    }

    private int getStatColor(String statName) {
        return switch (statName.toLowerCase()) {
            case "accuracy" -> 0xFFFFAA00;
            case "evasion" -> 0xFF00AAFF;
            case "crit chance" -> 0xFFFF0000;
            case "crit damage" -> 0xFFAA0000;
            default -> 0xFFFFFFFF;
        };
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        calculateDimensions();
    }
}