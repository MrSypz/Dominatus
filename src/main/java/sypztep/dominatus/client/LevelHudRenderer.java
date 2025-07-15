package sypztep.dominatus.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.CharacterLevelSystem;

public class LevelHudRenderer implements HudRenderCallback {

    // Config accessors
    private static int getHudX() { return ModConfig.hudX; }
    private static int getHudY() { return ModConfig.hudY; }
    private static int getBarWidth() { return ModConfig.barWidth; }
    private static int getBarHeight() { return ModConfig.barHeight; }
    private static float getAnimationDuration() { return ModConfig.animationDurationMs / 1000.0f; }

    // Color configuration methods
    private static int getBackgroundColor() { return ModConfig.hudBackgroundColor; }
    private static int getBarBackgroundColor() { return ModConfig.barBackgroundColor; }
    private static int getBarColor() { return 0xFF000000 | ModConfig.barColor; } // Force full alpha
    private static int getBorderColor() { return ModConfig.borderColor; }
    private static int getTextColor() { return ModConfig.textColor; }
    private static int getTextShadowColor() { return ModConfig.textShadowColor; }
    private static int getMaxLevelBarColor() { return ModConfig.maxLevelBarColor; }

    // Animation variables
    private static long lastXp = 0;
    private static int lastLevel = 1;
    private static double lastXpPercentage = 0.0;
    private static float animationTime = 0f;
    private static boolean isAnimating = false;
    private static boolean leveledUp = false;

    // Number animation variables
    private static long displayXp = 0;
    private static long startXp = 0;
    private static long targetXp = 0;
    private static double displayPercentage = 0.0;
    private static double startDisplayPercentage = 0.0;
    private static double targetDisplayPercentage = 0.0;

    // HUD visibility and slide animation
    private static float slideAnimationTime = 0f;
    private static float inactivityTimer = 0f;
    private static boolean isSlideAnimating = false;
    private static boolean isHudVisible = true;
    private static final float INACTIVITY_DURATION = 8.0f; // 8 seconds
    private static final float SLIDE_DURATION = 1.25f; // 1.25 seconds slide animation

    public LevelHudRenderer() {
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Only render for players and when not in debug screen
        if (client.player == null || client.getDebugHud().shouldShowDebugHud()) {
            return;
        }

        // Get level component
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(client.player);

        CharacterLevelSystem levelSystem = levelComponent.getPlayerLevelSystem();
        if (levelSystem == null) {
            return;
        }

        renderLevelHud(drawContext, client, levelSystem);
    }

    private void renderLevelHud(DrawContext drawContext, MinecraftClient client, CharacterLevelSystem levelSystem) {
        TextRenderer textRenderer = client.textRenderer;

        // Update animation and slide states
        updateAnimation(levelSystem, client);
        updateSlideAnimation(client);

        // Calculate slide offset
        float slideOffset = getSlideOffset();
        int hudX = getHudX() + (int) slideOffset;

        // Calculate HUD dimensions
        int hudWidth = getBarWidth() + 10;
        int hudHeight = 35;

        // Draw HUD background
        drawContext.fill(hudX - 3, getHudY() - 3, hudX + hudWidth, getHudY() + hudHeight, getBackgroundColor());

        // Draw border
        drawContext.drawBorder(hudX - 3, getHudY() - 3, hudWidth + 3, hudHeight + 3, getBorderColor());

        int currentY = getHudY();

        // Player name on left, level on right
        String playerName = client.player.getName().getString();
        int level = levelSystem.getLevel();
        String levelText = String.format("Lvl: %d", level);

        // Draw player name (left side) - no scaling animation
        int playerNameColor = leveledUp && isAnimating ?
                ColorHelper.Argb.getArgb(255, 255, 165, 0) : getTextColor(); // Orange when leveled up

        drawContext.drawTextWithShadow(textRenderer,
                Text.literal(playerName).formatted(Formatting.YELLOW),
                hudX, currentY, playerNameColor);

        // Draw level (right side) - color change only
        int levelTextWidth = textRenderer.getWidth(levelText);
        int levelX = hudX + getBarWidth() - levelTextWidth;
        int levelColor = leveledUp && isAnimating ?
                ColorHelper.Argb.getArgb(255, 255, 165, 0) : getTextColor(); // Orange when leveled up

        drawContext.drawTextWithShadow(textRenderer,
                Text.literal(levelText).formatted(Formatting.WHITE),
                levelX, currentY, levelColor);

        currentY += textRenderer.fontHeight + 3;

        long currentXpInLevel = levelSystem.getExperience();
        long xpRequiredForNextLevel = levelSystem.getExperienceToNextLevel(); // Total XP needed for next level
        double actualXpPercentage = levelSystem.getExperiencePercentage();

        // Use animated percentage for BOTH progress bar and display
        double displayXpPercentage = isAnimating ? displayPercentage : actualXpPercentage;

        // Draw XP bar background
        drawContext.fill(hudX, currentY, hudX + getBarWidth(), currentY + getBarHeight(), getBarBackgroundColor());

        // Draw XP progress with animation
        if (level < levelSystem.getMaxLevel()) {
            int progressWidth = (int) (getBarWidth() * (displayXpPercentage / 100.0));

            // Add glow effect during XP gain
            int barColor = getBarColor();
            if (isAnimating && !leveledUp && ModConfig.enableGlowEffect) {
                float progress = 1.0f - (animationTime / getAnimationDuration());
                float glowIntensity = easeOutQuint(progress);
                int glowAmount = (int) (50 * glowIntensity);
                barColor = ColorHelper.Argb.getArgb(255,
                        Math.min(255, ColorHelper.Argb.getRed(getBarColor()) + glowAmount),
                        Math.min(255, ColorHelper.Argb.getGreen(getBarColor()) + glowAmount),
                        Math.min(255, ColorHelper.Argb.getBlue(getBarColor()) + glowAmount));
            }

            drawContext.fill(hudX, currentY, hudX + progressWidth, currentY + getBarHeight(), barColor);
        } else {
            // Full bar for max level
            drawContext.fill(hudX, currentY, hudX + getBarWidth(), currentY + getBarHeight(), getMaxLevelBarColor());
        }

        // Draw XP bar border
        drawContext.drawBorder(hudX, currentY, getBarWidth(), getBarHeight(), getBorderColor());

        // XP numbers on the progress bar (centered) - FIX: Show current/required format
        String xpText;
        if (level >= levelSystem.getMaxLevel()) {
            xpText = "MAX";
        } else {
            long animatedCurrentXp = isAnimating ? displayXp : currentXpInLevel;
            xpText = String.format("%s / %s", formatNumber(animatedCurrentXp), formatNumber(xpRequiredForNextLevel));
        }

        // Center the XP text on the bar
        int textWidth = textRenderer.getWidth(xpText);
        int textX = hudX + (getBarWidth() - textWidth) / 2;
        int textY = currentY + (getBarHeight() - textRenderer.fontHeight) / 2;

        // Draw XP text with shadow for better visibility
        drawContext.drawText(textRenderer, xpText, textX + 1, textY + 1, getTextShadowColor(), false);
        drawContext.drawText(textRenderer, xpText, textX, textY, getTextColor(), false);

        // Percentage text (bottom right of progress bar) - animated percentage
        currentY += getBarHeight() + 2;
        String percentageText;
        if (level >= levelSystem.getMaxLevel()) {
            percentageText = "MAX";
        } else {
            // Use the SAME animated percentage as the progress bar
            percentageText = String.format("%.1f%%", displayXpPercentage);
        }

        int percentageTextWidth = textRenderer.getWidth(percentageText);
        int percentageX = hudX + getBarWidth() - percentageTextWidth;

        // Draw percentage text without scaling animation
        drawContext.drawTextWithShadow(textRenderer,
                Text.literal(percentageText).formatted(Formatting.GRAY),
                percentageX, currentY, getTextColor());
    }

    private void updateAnimation(CharacterLevelSystem levelSystem, MinecraftClient client) {
        long currentXp = levelSystem.getExperience();
        int currentLevel = levelSystem.getLevel();
        double currentXpPercentage = levelSystem.getExperiencePercentage();

        // Check for level up
        boolean hasLeveledUp = currentLevel > lastLevel;

        // Check if values changed to trigger animation
        if (lastXp != currentXp || hasLeveledUp) {
            if (lastXp == 0) { // First time initialization
                lastXp = currentXp;
                lastLevel = currentLevel;
                lastXpPercentage = currentXpPercentage;
                displayXp = currentXp;
                startXp = currentXp;
                targetXp = currentXp;
                displayPercentage = currentXpPercentage;
                startDisplayPercentage = currentXpPercentage;
                targetDisplayPercentage = currentXpPercentage;
                return;
            }

            // Reset inactivity timer and show HUD
            inactivityTimer = 0f;
            if (!isHudVisible) {
                isHudVisible = true;
                isSlideAnimating = true;
                slideAnimationTime = SLIDE_DURATION;
            }

            // Start new animation
            isAnimating = ModConfig.enableAnimations;
            animationTime = getAnimationDuration();
            leveledUp = hasLeveledUp;

            if (hasLeveledUp) {
                startXp = 0;
                targetXp = currentXp;

                // Reset display percentage animation for level up
                startDisplayPercentage = 0.0;
                targetDisplayPercentage = currentXpPercentage;

                lastLevel = currentLevel;

                // Don't show exp gain for level up
            } else {
                startXp = lastXp;
                targetXp = currentXp;

                // Use the LAST ACTUAL percentage as start
                startDisplayPercentage = lastXpPercentage;
                targetDisplayPercentage = currentXpPercentage;
            }

            // Update last values AFTER setting up animation
            lastXp = currentXp;
            lastXpPercentage = currentXpPercentage;
        }

        // Update animation timer
        if (isAnimating && animationTime > 0) {
            animationTime -= client.getRenderTickCounter().getTickDelta(false) / 20.0f; // Convert to seconds

            // Update animated values
            float progress = 1.0f - (animationTime / getAnimationDuration());
            float easedProgress = easeOutCubic(progress);

            // Animate XP numbers
            displayXp = startXp + (long) ((targetXp - startXp) * easedProgress);

            // Animate percentage display (this drives both bar and text)
            displayPercentage = startDisplayPercentage + (targetDisplayPercentage - startDisplayPercentage) * easedProgress;

            if (animationTime <= 0) {
                // Animation finished - set final values
                animationTime = 0;
                isAnimating = false;
                leveledUp = false;
                displayXp = targetXp;
                displayPercentage = targetDisplayPercentage;
                startDisplayPercentage = targetDisplayPercentage;
            }
        }

        // Update inactivity timer only when not animating
        if (!isAnimating) {
            inactivityTimer += client.getRenderTickCounter().getTickDelta(false) / 20.0f;

            // Start hiding after inactivity
            if (inactivityTimer >= INACTIVITY_DURATION && isHudVisible) {
                isHudVisible = false;
                isSlideAnimating = true;
                slideAnimationTime = SLIDE_DURATION;
            }
        }
    }

    private void updateSlideAnimation(MinecraftClient client) {
        if (isSlideAnimating && slideAnimationTime > 0) {
            slideAnimationTime -= client.getRenderTickCounter().getTickDelta(false) / 20.0f;
            if (slideAnimationTime <= 0) {
                slideAnimationTime = 0;
                isSlideAnimating = false;
            }
        }
    }

    private float getSlideOffset() {
        if (!isSlideAnimating) {
            return isHudVisible ? 0f : -(getBarWidth() + 20); // Fully hidden or visible
        }

        float progress = 1.0f - (slideAnimationTime / SLIDE_DURATION);
        float easedProgress = easeOutCubic(progress);

        if (isHudVisible) {
            // Sliding in from left
            return -(getBarWidth() + 20) * (1.0f - easedProgress);
        } else {
            // Sliding out to left
            return -(getBarWidth() + 20) * easedProgress;
        }
    }

    /**
     * Ease-out-cubic: Fast start, slow end (smooth deceleration)
     */
    private float easeOutCubic(float t) {
        return 1.0f - (float) Math.pow(1.0 - t, 3.0);
    }

    /**
     * Ease-out-quint: Very smooth deceleration
     */
    private float easeOutQuint(float t) {
        return 1.0f - (float) Math.pow(1.0 - t, 5.0);
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