package sypztep.dominatus.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.attribute.EntityAttributes;
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
    private static final float PANEL_WIDTH_RATIO = 0.25f;
    private static final float PANEL_HEIGHT_RATIO = 0.45f; // Increased height for more content
    private static final float PANEL_X_RATIO = 0.02f;
    private static final float PANEL_Y_RATIO = 0.2f;

    // Category headers
    private static final Text OFFENSIVE_HEADER = Text.literal("Offensive").formatted(Formatting.RED, Formatting.BOLD);
    private static final Text DEFENSIVE_HEADER = Text.literal("Defensive").formatted(Formatting.BLUE, Formatting.BOLD);
    private static final Text CRITICAL_HEADER = Text.literal("Critical").formatted(Formatting.GOLD, Formatting.BOLD);

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

        // Main title
        Text title = Text.literal("Combat Statistics").setStyle(Style.EMPTY.withBold(true));
        drawContext.drawText(
                textRenderer,
                title,
                panelX + textPadding,
                panelY + textPadding,
                0xFFFFFFFF,
                true
        );

        // Render attribute categories
        renderAttributeCategories(drawContext, player);

        RenderSystem.disableBlend();
    }

    private void renderAttributeCategories(DrawContext drawContext, PlayerEntity player) {
        int startY = panelY + textPadding + lineHeight + 5;
        int categorySpacing = lineHeight * 2;

        // Offensive Stats
        startY = renderCategoryHeader(drawContext, OFFENSIVE_HEADER, startY);
        Map<Text, Double> offensiveStats = new LinkedHashMap<>();
        offensiveStats.put(
                Text.literal("Damage").formatted(Formatting.DARK_RED),
                player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE)
        );
        offensiveStats.put(
                Text.literal("Accuracy").formatted(Formatting.YELLOW),
                player.getAttributeValue(ModEntityAttributes.ACCURACY)
        );
        startY = renderStats(drawContext, offensiveStats, startY) + categorySpacing;

        // Defensive Stats
        startY = renderCategoryHeader(drawContext, DEFENSIVE_HEADER, startY);
        Map<Text, Double> defensiveStats = new LinkedHashMap<>();
        defensiveStats.put(
                Text.literal("Armor").formatted(Formatting.BLUE),
                player.getAttributeValue(EntityAttributes.ARMOR)
        );
        defensiveStats.put(
                Text.literal("Evasion").formatted(Formatting.AQUA),
                player.getAttributeValue(ModEntityAttributes.EVASION)
        );
        startY = renderStats(drawContext, defensiveStats, startY) + categorySpacing;

        // Critical Stats
        startY = renderCategoryHeader(drawContext, CRITICAL_HEADER, startY);
        Map<Text, Double> criticalStats = new LinkedHashMap<>();
        criticalStats.put(
                Text.literal("Crit Chance").formatted(Formatting.GOLD),
                player.getAttributeValue(ModEntityAttributes.CRIT_CHANCE) * 100
        );
        criticalStats.put(
                Text.literal("Crit Damage").formatted(Formatting.DARK_RED),
                player.getAttributeValue(ModEntityAttributes.CRIT_DAMAGE) * 100
        );
        renderStats(drawContext, criticalStats, startY);
    }

    private int renderCategoryHeader(DrawContext drawContext, Text header, int y) {
        drawContext.drawText(
                textRenderer,
                header,
                panelX + textPadding,
                y,
                0xFFFFFFFF,
                true
        );
        return y + lineHeight;
    }

    private int renderStats(DrawContext drawContext, Map<Text, Double> stats, int startY) {
        int y = startY;
        int barHeight = Math.max(4, lineHeight / 3);

        for (Map.Entry<Text, Double> stat : stats.entrySet()) {
            // Draw stat label
            drawContext.drawText(
                    textRenderer,
                    stat.getKey(),
                    panelX + textPadding,
                    y,
                    0xFFFFFFFF,
                    false
            );

            // Draw stat value with percentage if needed
            String value = formatStatValue(stat.getKey().getString(), stat.getValue());
            int valueWidth = textRenderer.getWidth(value);
            drawContext.drawText(
                    textRenderer,
                    value,
                    panelX + panelWidth - textPadding - valueWidth,
                    y,
                    0xFFFFFFFF,
                    false
            );

            // Draw stat bar
            drawStatBar(
                    drawContext,
                    panelX + textPadding,
                    y + lineHeight - barHeight - 1,
                    panelWidth - (textPadding * 2),
                    barHeight,
                    normalizeStatValue(stat.getKey().getString(), stat.getValue()),
                    getStatColor(stat.getKey().getString())
            );

            y += lineHeight;
        }
        return y;
    }

    private String formatStatValue(String statName, double value) {
        if (statName.contains("Crit")) {
            return String.format("%.1f%%", value);
        }
        return String.format("%.1f", value);
    }

    private float normalizeStatValue(String statName, double value) {
        return switch (statName.toLowerCase()) {
            case "damage" -> (float) (value / 30.0); // Normalize damage to max of 30
            case "armor" -> (float) (value / 20.0); // Normalize armor to max of 20
            case "crit damage" -> (float) ((value - 100) / 100.0); // Normalize relative to base 100%
            default -> (float) (value / 100.0);
        };
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
            case "damage", "crit damage" -> 0xFFFF4444;
            case "accuracy", "crit chance" -> 0xFFFFAA00;
            case "armor" -> 0xFF5555FF;
            case "evasion" -> 0xFF00AAFF;
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