package sypztep.dominatus.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.LevelData;

public class LevelHudRenderer implements HudRenderCallback {
    // Position settings
    private static final int HUD_X = 10;
    private static final int HUD_Y = 10;

    // Size settings
    private static final int BAR_WIDTH = 140;
    private static final int BAR_HEIGHT = 8;

    // Animation settings
    private static final boolean ENABLE_ANIMATIONS = true;
    private static final float ANIMATION_DURATION_MS = 1500.0f; // 1.5 seconds
    private static final boolean ENABLE_GLOW_EFFECT = true;

    // Color settings
    private static final int BAR_COLOR = 0xFFFFA500; // Orange with full alpha
    private static final int HUD_BACKGROUND_COLOR = ColorHelper.Argb.getArgb(180, 0, 0, 0); // Semi-transparent black
    private static final int BAR_BACKGROUND_COLOR = ColorHelper.Argb.getArgb(255, 50, 50, 50); // Dark gray
    private static final int BORDER_COLOR = ColorHelper.Argb.getArgb(255, 255, 255, 255); // White
    private static final int TEXT_COLOR = 0xFFFFFFFF; // White
    private static final int TEXT_SHADOW_COLOR = 0xFF000000; // Black
    private static final int MAX_LEVEL_BAR_COLOR = ColorHelper.Argb.getArgb(255, 255, 215, 0); // Gold

    // Slide animation settings
    private static final float INACTIVITY_DURATION = 8.0f; // 8 seconds
    private static final float SLIDE_DURATION = 1.25f; // 1.25 seconds slide animation

    // ====================
    // ANIMATION STATE VARIABLES
    // ====================

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

    public LevelHudRenderer() {
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.getDebugHud().shouldShowDebugHud()) {
            return;
        }

        // Get level component and use unified interface
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(client.player);
        LevelData levelData = levelComponent.getLevelData();

        // Only render for players
        if (!levelData.isPlayer()) {
            return;
        }

        renderLevelHud(drawContext, client, levelData);
    }

    private void renderLevelHud(DrawContext drawContext, MinecraftClient client, LevelData levelData) {
        TextRenderer textRenderer = client.textRenderer;

        // Update animation and slide states
        updateAnimation(levelData, client);
        updateSlideAnimation(client);

        // Calculate slide offset
        float slideOffset = getSlideOffset();
        int hudX = HUD_X + (int) slideOffset;

        // Calculate HUD dimensions
        int hudWidth = BAR_WIDTH + 10;
        int hudHeight = 35;

        // Draw HUD background
        drawContext.fill(hudX - 3, HUD_Y - 3, hudX + hudWidth, HUD_Y + hudHeight, HUD_BACKGROUND_COLOR);

        // Draw border
        drawContext.drawBorder(hudX - 3, HUD_Y - 3, hudWidth + 3, hudHeight + 3, BORDER_COLOR);

        int currentY = HUD_Y;

        // Player name on left, level on right
        String playerName = client.player.getName().getString();
        long currentXpInLevel = levelData.getExperience();
        long xpRequiredForNextLevel = levelData.getExperienceToNextLevel();
        double actualXpPercentage = levelData.getExperiencePercentage();
        int level = levelData.getLevel();
        int maxLevel = levelData.getMaxLevel();
        String levelText = String.format("Lvl: %d", level);

        // Draw player name (left side) - no scaling animation
        int playerNameColor = leveledUp && isAnimating ?
                ColorHelper.Argb.getArgb(255, 255, 165, 0) : TEXT_COLOR; // Orange when leveled up

        drawContext.drawTextWithShadow(textRenderer,
                Text.literal(playerName).formatted(Formatting.YELLOW),
                hudX, currentY, playerNameColor);

        // Draw level (right side) - color change only
        int levelTextWidth = textRenderer.getWidth(levelText);
        int levelX = hudX + BAR_WIDTH - levelTextWidth;
        int levelColor = leveledUp && isAnimating ?
                ColorHelper.Argb.getArgb(255, 255, 165, 0) : TEXT_COLOR; // Orange when leveled up

        drawContext.drawTextWithShadow(textRenderer,
                Text.literal(levelText).formatted(Formatting.WHITE),
                levelX, currentY, levelColor);

        currentY += textRenderer.fontHeight + 3;

        double displayXpPercentage = isAnimating ? displayPercentage : actualXpPercentage;

        // Draw XP bar background
        drawContext.fill(hudX, currentY, hudX + BAR_WIDTH, currentY + BAR_HEIGHT, BAR_BACKGROUND_COLOR);

        // Draw XP progress with animation
        if (level < maxLevel) {
            int progressWidth = (int) (BAR_WIDTH * (displayXpPercentage / 100.0));

            // Add glow effect during XP gain
            int barColor = BAR_COLOR;
            if (isAnimating && !leveledUp && ENABLE_GLOW_EFFECT) {
                float progress = 1.0f - (animationTime / (ANIMATION_DURATION_MS / 1000.0f));
                float glowIntensity = easeOutQuint(progress);
                int glowAmount = (int) (50 * glowIntensity);
                barColor = ColorHelper.Argb.getArgb(255,
                        Math.min(255, ColorHelper.Argb.getRed(BAR_COLOR) + glowAmount),
                        Math.min(255, ColorHelper.Argb.getGreen(BAR_COLOR) + glowAmount),
                        Math.min(255, ColorHelper.Argb.getBlue(BAR_COLOR) + glowAmount));
            }

            drawContext.fill(hudX, currentY, hudX + progressWidth, currentY + BAR_HEIGHT, barColor);
        } else {
            // Full bar for max level
            drawContext.fill(hudX, currentY, hudX + BAR_WIDTH, currentY + BAR_HEIGHT, MAX_LEVEL_BAR_COLOR);
        }

        // Draw XP bar border
        drawContext.drawBorder(hudX, currentY, BAR_WIDTH, BAR_HEIGHT, BORDER_COLOR);

        // XP numbers on the progress bar (centered)
        String xpText;
        if (level >= maxLevel) {
            xpText = "MAX";
        } else {
            long animatedCurrentXp = isAnimating ? displayXp : currentXpInLevel;
            xpText = String.format("%s / %s", formatNumber(animatedCurrentXp), formatNumber(xpRequiredForNextLevel));
        }

        // Center the XP text on the bar
        int textWidth = textRenderer.getWidth(xpText);
        int textX = hudX + (BAR_WIDTH - textWidth) / 2;
        int textY = currentY + (BAR_HEIGHT - textRenderer.fontHeight) / 2;

        // Draw XP text with shadow for better visibility
        drawContext.drawText(textRenderer, xpText, textX + 1, textY + 1, TEXT_SHADOW_COLOR, false);
        drawContext.drawText(textRenderer, xpText, textX, textY, TEXT_COLOR, false);

        // Percentage text (bottom right of progress bar)
        currentY += BAR_HEIGHT + 2;
        String percentageText;
        if (level >= maxLevel) {
            percentageText = "100%";
        } else {
            percentageText = String.format("%.1f%%", displayXpPercentage);
        }

        int percentageTextWidth = textRenderer.getWidth(percentageText);
        int percentageX = hudX + BAR_WIDTH - percentageTextWidth;

        // Draw percentage text without scaling animation
        drawContext.drawTextWithShadow(textRenderer,
                Text.literal(percentageText).formatted(Formatting.GRAY),
                percentageX, currentY, TEXT_COLOR);
    }

    private void updateAnimation(LevelData levelData, MinecraftClient client) {
        long currentXp = levelData.getExperience();
        int currentLevel = levelData.getLevel();
        double currentXpPercentage = levelData.getExperiencePercentage();

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
            isAnimating = ENABLE_ANIMATIONS;
            animationTime = ANIMATION_DURATION_MS / 1000.0f;
            leveledUp = hasLeveledUp;

            if (hasLeveledUp) {
                startXp = 0;
                targetXp = currentXp;

                // Reset display percentage animation for level up
                startDisplayPercentage = 0.0;
                targetDisplayPercentage = currentXpPercentage;

                lastLevel = currentLevel;
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
            float progress = 1.0f - (animationTime / (ANIMATION_DURATION_MS / 1000.0f));
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
            return isHudVisible ? 0f : -(BAR_WIDTH + 20); // Fully hidden or visible
        }

        float progress = 1.0f - (slideAnimationTime / SLIDE_DURATION);
        float easedProgress = easeOutCubic(progress);

        if (isHudVisible) {
            // Sliding in from left
            return -(BAR_WIDTH + 20) * (1.0f - easedProgress);
        } else {
            // Sliding out to left
            return -(BAR_WIDTH + 20) * easedProgress;
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