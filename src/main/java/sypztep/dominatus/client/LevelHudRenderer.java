package sypztep.dominatus.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.MathHelper;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.LevelData;

public class LevelHudRenderer implements HudRenderCallback {
    // Position settings
    private static final int HUD_X = 10;
    private static final int HUD_Y = 10;
    private static final int BAR_WIDTH = 140;
    private static final int BAR_HEIGHT = 8;

    // Animation settings
    private static final float XP_GLOW_DURATION = 2.0f; // 2 seconds
    private static final float SLIDE_DURATION = 0.5f; // Animation duration
    private static final float AUTO_HIDE_DELAY = 8.0f; // 8 seconds

    // Colors - Dark luxury theme
    private static final int BACKGROUND_COLOR = 0xA0000000; // Semi-transparent black
    private static final int BORDER_COLOR = 0xFF444444; // Medium gray
    private static final int XP_BAR_COLOR = 0xFF00CC00; // Bright green
    private static final int XP_BAR_BG_COLOR = 0xFF222222; // Dark gray
    private static final int TEXT_COLOR = 0xFFFFFFFF; // White
    private static final int LEVEL_COLOR = 0xFFFFD700; // Gold
    private static final int XP_GAIN_GLOW_COLOR = 0xFFFFFFFF; // White glow
    private static final int MAX_LEVEL_COLOR = 0xFFFF6600; // Orange for max level

    // Animation state
    private static float animatedXpProgress = 0.0f;
    private static long lastXp = 0;
    private static int lastLevel = 1;
    private static float xpGainGlowTimer = 0.0f;

    // Slide animation state
    private static float slideOffset = 0.0f;
    private static float hideTimer = 0.0f;
    private static boolean shouldBeVisible = true;

    public LevelHudRenderer() {
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.getDebugHud().shouldShowDebugHud()) return;


        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(client.player);
        LevelData levelData = levelComponent.getLevelData();

        if (!levelData.isPlayer()) {
            return;
        }

        // Get current values
        int level = levelData.getLevel();
        long currentXp = levelData.getExperience();
        long xpToNext = levelData.getExperienceToNextLevel();
        boolean isMaxLevel = levelData.isMaxLevel();

        // Detect XP/Level changes
        if (currentXp > lastXp || level > lastLevel) {
            xpGainGlowTimer = XP_GLOW_DURATION;
            shouldBeVisible = true;
            hideTimer = AUTO_HIDE_DELAY;
        }

        // Update animations
        float deltaTime = tickCounter.getTickDelta(false) / 20.0f;
        updateAnimations(currentXp, xpToNext, isMaxLevel, deltaTime);

        // Calculate slide position
        int currentHudX = calculateHudX();

        // Only render if HUD is at least partially visible
        if (slideOffset < 1.0f) {
            renderLevelHud(drawContext, client, levelData, currentHudX);
        }

        // Update last values
        lastXp = currentXp;
        lastLevel = level;
    }

    private void updateAnimations(long currentXp, long xpToNext, boolean isMaxLevel, float deltaTime) {
        // XP bar smooth animation
        float targetProgress = isMaxLevel ? 1.0f : (float) ((double) currentXp / (double) xpToNext);
        float lerpSpeed = 0.08f;
        animatedXpProgress = MathHelper.lerp(lerpSpeed, animatedXpProgress, targetProgress);

        // XP gain glow timer countdown
        if (xpGainGlowTimer > 0) {
            xpGainGlowTimer -= deltaTime;
            if (xpGainGlowTimer < 0) {
                xpGainGlowTimer = 0;
            }
        }

        // Handle auto-hide functionality
        if (hideTimer > 0) {
            hideTimer -= deltaTime;
            if (hideTimer <= 0) {
                shouldBeVisible = false;
            }
        }

        // Calculate target slide offset
        float targetSlideOffset = shouldBeVisible ? 0.0f : 1.0f;

        // Smooth slide animation
        float slideSpeed = 1.0f / SLIDE_DURATION;
        if (slideOffset != targetSlideOffset) {
            float direction = targetSlideOffset > slideOffset ? 1.0f : -1.0f;
            slideOffset += direction * slideSpeed * deltaTime;

            // Clamp to target
            if (direction > 0 && slideOffset > targetSlideOffset) {
                slideOffset = targetSlideOffset;
            } else if (direction < 0 && slideOffset < targetSlideOffset) {
                slideOffset = targetSlideOffset;
            }
        }
    }

    private void renderLevelHud(DrawContext drawContext, MinecraftClient client, LevelData levelData, int hudX) {
        TextRenderer textRenderer = client.textRenderer;

        // Get values
        String playerName = client.player.getName().getString();
        int level = levelData.getLevel();
        long currentXp = levelData.getExperience();
        long xpToNext = levelData.getExperienceToNextLevel();
        int availableBenefits = levelData.getAvailableBenefits();
        boolean isMaxLevel = levelData.isMaxLevel();

        // Calculate text elements
        String levelText = isMaxLevel ? "MAX" : "Lv." + level;
        int playerNameWidth = textRenderer.getWidth(playerName);
        int levelTextWidth = textRenderer.getWidth(levelText);

        // Calculate dynamic HUD dimensions
        int minHudWidth = BAR_WIDTH + 3;
        int requiredWidth = playerNameWidth + levelTextWidth + 20; // 20px spacing between name and level
        int hudWidth = Math.max(minHudWidth, requiredWidth);
        int hudHeight = 50; // Height for all content

        int currentY = HUD_Y;

        // Background panel with border
        drawContext.fill(hudX - 3, currentY - 3, hudX + hudWidth, currentY + hudHeight, BACKGROUND_COLOR);
        drawBorder(drawContext, hudX - 3, currentY - 3, hudWidth + 3, hudHeight + 3);

        // Player name on the left
        drawContext.drawTextWithShadow(textRenderer, playerName, hudX, currentY, TEXT_COLOR);

        // Level on the right
        int levelColor = isMaxLevel ? MAX_LEVEL_COLOR : LEVEL_COLOR;
        int levelX = hudX + hudWidth - levelTextWidth - 6; // 6px padding from right edge
        drawContext.drawTextWithShadow(textRenderer, levelText, levelX, currentY, levelColor);

        currentY += textRenderer.fontHeight + 3;

        // XP Bar background
        int barY = currentY;
        drawContext.fill(hudX, barY, hudX + BAR_WIDTH, barY + BAR_HEIGHT, XP_BAR_BG_COLOR);

        // XP Bar progress with glow effect
        int progressWidth;
        if (isMaxLevel) {
            progressWidth = BAR_WIDTH; // Always full width for max level
        } else {
            progressWidth = (int) (BAR_WIDTH * animatedXpProgress);
        }

        if (progressWidth > 0) {
            // XP gain glow effect
            if (xpGainGlowTimer > 0) {
                float glowStrength = xpGainGlowTimer / XP_GLOW_DURATION;
                float time = (XP_GLOW_DURATION - xpGainGlowTimer) * 3.0f;
                float pulse = (float) (0.5f + 0.5f * Math.sin(time * Math.PI));
                float finalGlow = glowStrength * (0.7f + 0.3f * pulse);

                int glowAlpha = (int) (finalGlow * 130);
                int xpGlowColor = (glowAlpha << 24) | (XP_GAIN_GLOW_COLOR & 0x00FFFFFF);

                // Subtle glow layers
                drawContext.fill(hudX - 2, barY - 2, hudX + progressWidth + 2, barY + BAR_HEIGHT + 2, xpGlowColor);
                drawContext.fill(hudX - 1, barY - 1, hudX + progressWidth + 1, barY + BAR_HEIGHT + 1, xpGlowColor);
            }

            // Main XP bar
            drawContext.fill(hudX, barY, hudX + progressWidth, barY + BAR_HEIGHT, XP_BAR_COLOR);
        }

        // XP Bar border
        drawBorder(drawContext, hudX, barY, BAR_WIDTH, BAR_HEIGHT);
        currentY += BAR_HEIGHT + 4;

        // XP Text and Benefits
        String xpText;
        if (isMaxLevel) {
            xpText = "MAX LEVEL";
        } else {
            xpText = formatNumber(currentXp) + "/" + formatNumber(xpToNext);
        }

        drawContext.drawTextWithShadow(textRenderer, xpText, hudX, currentY, TEXT_COLOR);

        // Benefits text on the right
        String benefitsText = "Benefits: " + availableBenefits;
        int benefitsX = hudX + BAR_WIDTH - textRenderer.getWidth(benefitsText);
        drawContext.drawTextWithShadow(textRenderer, benefitsText, benefitsX, currentY, LEVEL_COLOR);

        // XP Percentage (below on left)
        if (!isMaxLevel) {
            currentY += textRenderer.fontHeight + 1;
            String percentText = String.format("%.1f%%", animatedXpProgress * 100);
            drawContext.drawTextWithShadow(textRenderer, percentText, hudX, currentY, 0xFFAAAAAA);
        }
    }

    private void drawBorder(DrawContext drawContext, int x, int y, int width, int height) {
        // Top
        drawContext.fill(x, y, x + width, y + 1, BORDER_COLOR);
        // Bottom
        drawContext.fill(x, y + height - 1, x + width, y + height, BORDER_COLOR);
        // Left
        drawContext.fill(x, y, x + 1, y + height, BORDER_COLOR);
        // Right
        drawContext.fill(x + width - 1, y, x + width, y + height, BORDER_COLOR);
    }

    private int calculateHudX() {
        int hudWidth = BAR_WIDTH + 16; // Include padding
        int hiddenX = -hudWidth; // Off-screen to the left

        // Apply cubic easing for smooth animation
        float easedOffset = cubicEaseInOut(slideOffset);

        return MathHelper.lerp(easedOffset, HUD_X, hiddenX);
    }

    private float cubicEaseInOut(float t) {
        if (t < 0.5f) {
            return 4.0f * t * t * t;
        } else {
            float p = 2.0f * t - 2.0f;
            return 1.0f + p * p * p / 2.0f;
        }
    }

    private String formatNumber(long number) {
        if (number >= 1_000_000_000L) {
            return String.format("%.1fB", number / 1_000_000_000.0);
        } else if (number >= 1_000_000L) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000L) {
            return String.format("%.1fK", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }

    public static void register() {
        HudRenderCallback.EVENT.register(new LevelHudRenderer());
    }
}