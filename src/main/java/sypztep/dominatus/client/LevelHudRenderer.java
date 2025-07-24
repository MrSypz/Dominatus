package sypztep.dominatus.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.MathHelper;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.component.living.PlayerClassComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.LevelData;
import sypztep.dominatus.common.system.playerclass.PlayerClass;
import sypztep.dominatus.common.util.NumberUtil;

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
    private static final int CLASS_XP_BAR_COLOR = 0xFF3366FF; // Blue for class XP
    private static final int XP_BAR_BG_COLOR = 0xFF222222; // Dark gray
    private static final int TEXT_COLOR = 0xFFFFFFFF; // White
    private static final int LEVEL_COLOR = 0xFFFFD700; // Gold
    private static final int CLASS_COLOR = 0xFF66AAFF; // Light blue for class
    private static final int XP_GAIN_GLOW_COLOR = 0xFFFFFFFF; // White glow
    private static final int MAX_LEVEL_COLOR = 0xFFFF6600; // Orange for max level

    // Animation state
    private static float animatedXpProgress = 0.0f;
    private static float animatedClassXpProgress = 0.0f;
    private static long lastXp = 0;
    private static long lastClassXp = 0;
    private static int lastLevel = 1;
    private static int lastClassLevel = 1;
    private static float xpGainGlowTimer = 0.0f;
    private static float classXpGainGlowTimer = 0.0f;

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
        PlayerClassComponent classComponent = ModEntityComponents.PLAYERCLASS.get(client.player);
        LevelData levelData = levelComponent.getLevelData();

        if (!levelData.isPlayer()) return;

        // Get current values
        int level = levelData.getLevel();
        long currentXp = levelData.getExperience();
        long xpToNext = levelData.getExperienceToNextLevel();
        boolean isMaxLevel = levelData.isMaxLevel();
        double charXpPercent = levelData.getExperiencePercentage();

        // Get class values
        int classLevel = classComponent.getClassLevel();
        long currentClassXp = classComponent.getClassExperience();
        long classXpToNext = classComponent.getClassExperienceToNextLevel();
        boolean isMaxClassLevel = classComponent.isMaxClassLevel();
        double classXpPercent = classComponent.getClassExperiencePercentage();
        PlayerClass playerClass = classComponent.getCurrentClass();

        // Detect XP/Level changes
        if (currentXp > lastXp || level > lastLevel) {
            xpGainGlowTimer = XP_GLOW_DURATION;
            shouldBeVisible = true;
            hideTimer = AUTO_HIDE_DELAY;
        }

        // Detect Class XP/Level changes
        if (currentClassXp > lastClassXp || classLevel > lastClassLevel) {
            classXpGainGlowTimer = XP_GLOW_DURATION;
            shouldBeVisible = true;
            hideTimer = AUTO_HIDE_DELAY;
        }

        // Update animations
        float deltaTime = tickCounter.getTickDelta(false) / 20.0f;
        updateAnimations(charXpPercent, classXpPercent, isMaxLevel, isMaxClassLevel, deltaTime);

        // Calculate slide position
        int currentHudX = calculateHudX();

        // Only render if HUD is at least partially visible
        if (slideOffset < 1.0f) renderLevelHud(drawContext, client, levelData, classComponent, currentHudX);

        // Update last values
        lastXp = currentXp;
        lastClassXp = currentClassXp;
        lastLevel = level;
        lastClassLevel = classLevel;
    }

    private void updateAnimations(double charXpPercent, double classXpPercent,
                                  boolean isMaxLevel, boolean isMaxClassLevel, float deltaTime) {
        // Character XP bar smooth animation
        float targetProgress = isMaxLevel ? 1.0f : (float) (charXpPercent / 100.0);
        float lerpSpeed = 0.08f;
        animatedXpProgress = MathHelper.lerp(lerpSpeed, animatedXpProgress, targetProgress);

        // Class XP bar smooth animation
        float targetClassProgress = isMaxClassLevel ? 1.0f : (float) (classXpPercent / 100.0);
        animatedClassXpProgress = MathHelper.lerp(lerpSpeed, animatedClassXpProgress, targetClassProgress);

        // XP gain glow timer countdown
        if (xpGainGlowTimer > 0) {
            xpGainGlowTimer -= deltaTime;
            if (xpGainGlowTimer < 0) xpGainGlowTimer = 0;
        }

        // Class XP gain glow timer countdown
        if (classXpGainGlowTimer > 0) {
            classXpGainGlowTimer -= deltaTime;
            if (classXpGainGlowTimer < 0) classXpGainGlowTimer = 0;
        }

        // Handle auto-hide functionality
        if (hideTimer > 0) {
            hideTimer -= deltaTime;
            if (hideTimer <= 0) shouldBeVisible = false;
        }

        // Calculate target slide offset
        float targetSlideOffset = shouldBeVisible ? 0.0f : 1.0f;

        // Smooth slide animation
        float slideSpeed = 1.0f / SLIDE_DURATION;
        if (slideOffset != targetSlideOffset) {
            float direction = targetSlideOffset > slideOffset ? 1.0f : -1.0f;
            slideOffset += direction * slideSpeed * deltaTime;

            // Clamp to target
            if (direction > 0 && slideOffset > targetSlideOffset) slideOffset = targetSlideOffset;
            else if (direction < 0 && slideOffset < targetSlideOffset) slideOffset = targetSlideOffset;
        }
    }

    private void renderLevelHud(DrawContext drawContext, MinecraftClient client, LevelData levelData,
                                PlayerClassComponent classComponent, int hudX) {
        TextRenderer textRenderer = client.textRenderer;

        // Get values
        String playerName = client.player.getName().getString();
        int level = levelData.getLevel();
        long currentXp = levelData.getExperience();
        long xpToNext = levelData.getExperienceToNextLevel();
        int availableBenefits = levelData.getAvailableBenefits();
        boolean isMaxLevel = levelData.isMaxLevel();

        // Get class values
        PlayerClass playerClass = classComponent.getCurrentClass();
        int classLevel = classComponent.getClassLevel();
        long currentClassXp = classComponent.getClassExperience();
        long classXpToNext = classComponent.getClassExperienceToNextLevel();
        int classPoints = classComponent.getAvailableClassPoints();
        boolean isMaxClassLevel = classComponent.isMaxClassLevel();

        // Calculate text elements
        String levelText = isMaxLevel ? "MAX" : "Lv." + level;
        String classText = isMaxClassLevel ? "MAX" : playerClass.getDisplayName() + " Lv." + classLevel;
        int playerNameWidth = textRenderer.getWidth(playerName);
        int levelTextWidth = textRenderer.getWidth(levelText);
        int classTextWidth = textRenderer.getWidth(classText);

        // Calculate dynamic HUD dimensions
        int minHudWidth = BAR_WIDTH + 3;
        int requiredWidth = Math.max(
                playerNameWidth + levelTextWidth + 20, // Player name + level
                classTextWidth + 20 // Class text
        );
        int hudWidth = Math.max(minHudWidth, requiredWidth);
        int hudHeight = 85; // Height for all content (increased for class info)

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

        currentY += textRenderer.fontHeight + 2;

        // Class name on the left
        int classColor = playerClass.getColor().getColorValue() != null ?
                playerClass.getColor().getColorValue() : CLASS_COLOR;
        drawContext.drawTextWithShadow(textRenderer, classText, hudX, currentY, classColor);

        currentY += textRenderer.fontHeight + 4;

        // Character XP Bar
        renderXpBar(drawContext, hudX, currentY, animatedXpProgress, xpGainGlowTimer,
                XP_BAR_COLOR, XP_GAIN_GLOW_COLOR, "Character XP");
        currentY += BAR_HEIGHT + 2;

        // Class XP Bar
        renderXpBar(drawContext, hudX, currentY, animatedClassXpProgress, classXpGainGlowTimer,
                CLASS_XP_BAR_COLOR, XP_GAIN_GLOW_COLOR, "Class XP");
        currentY += BAR_HEIGHT + 4;

        // Character XP Text and Benefits
        String xpText;
        if (isMaxLevel) {
            xpText = "MAX LEVEL";
        } else {
            xpText = NumberUtil.formatNumber(currentXp) + "/" + NumberUtil.formatNumber(xpToNext);
        }

        drawContext.drawTextWithShadow(textRenderer, xpText, hudX, currentY, TEXT_COLOR);

        // Benefits text on the right
        String benefitsText = "Stat Pts: " + availableBenefits;
        int benefitsX = hudX + BAR_WIDTH - textRenderer.getWidth(benefitsText);
        drawContext.drawTextWithShadow(textRenderer, benefitsText, benefitsX, currentY, LEVEL_COLOR);

        currentY += textRenderer.fontHeight + 1;

        // Class XP Text and Class Points
        String classXpText;
        if (isMaxClassLevel) {
            classXpText = "MAX CLASS LEVEL";
        } else {
            classXpText = NumberUtil.formatNumber(currentClassXp) + "/" + NumberUtil.formatNumber(classXpToNext);
        }

        drawContext.drawTextWithShadow(textRenderer, classXpText, hudX, currentY, TEXT_COLOR);

        // Class points text on the right
        String classPointsText = "Class Pts: " + classPoints;
        int classPointsX = hudX + BAR_WIDTH - textRenderer.getWidth(classPointsText);
        drawContext.drawTextWithShadow(textRenderer, classPointsText, classPointsX, currentY, classColor);

        // Percentage displays
        if (!isMaxLevel || !isMaxClassLevel) {
            currentY += textRenderer.fontHeight + 1;

            if (!isMaxLevel) {
                double charPercent = levelData.getExperiencePercentage();
                String percentText = String.format("Char: %.1f%%", charPercent);
                drawContext.drawTextWithShadow(textRenderer, percentText, hudX, currentY, 0xFFAAAAAA);
            }

            if (!isMaxClassLevel) {
                double classPercent = classComponent.getClassExperiencePercentage();
                String classPercentText = String.format("Class: %.1f%%", classPercent);
                int classPercentX = hudX + BAR_WIDTH - textRenderer.getWidth(classPercentText);
                drawContext.drawTextWithShadow(textRenderer, classPercentText, classPercentX, currentY, 0xFFAAAAAA);
            }
        }
    }

    private void renderXpBar(DrawContext drawContext, int x, int y, float progress, float glowTimer,
                             int barColor, int glowColor, String tooltip) {
        // XP Bar background
        drawContext.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, XP_BAR_BG_COLOR);

        // XP Bar progress with glow effect
        int progressWidth = (int) (BAR_WIDTH * progress);

        if (progressWidth > 0) {
            // XP gain glow effect
            if (glowTimer > 0) {
                float glowStrength = glowTimer / XP_GLOW_DURATION;
                float time = (XP_GLOW_DURATION - glowTimer) * 3.0f;
                float pulse = (float) (0.5f + 0.5f * Math.sin(time * Math.PI));
                float finalGlow = glowStrength * (0.7f + 0.3f * pulse);

                int glowAlpha = (int) (finalGlow * 130);
                int xpGlowColor = (glowAlpha << 24) | (glowColor & 0x00FFFFFF);

                // Subtle glow layers
                drawContext.fill(x - 2, y - 2, x + progressWidth + 2, y + BAR_HEIGHT + 2, xpGlowColor);
                drawContext.fill(x - 1, y - 1, x + progressWidth + 1, y + BAR_HEIGHT + 1, xpGlowColor);
            }

            // Main XP bar
            drawContext.fill(x, y, x + progressWidth, y + BAR_HEIGHT, barColor);
        }

        // XP Bar border
        drawBorder(drawContext, x, y, BAR_WIDTH, BAR_HEIGHT);
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

    public static void register() {
        HudRenderCallback.EVENT.register(new LevelHudRenderer());
    }
}