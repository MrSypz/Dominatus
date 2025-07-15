package sypztep.dominatus.common.util.level;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.component.LivingLevelComponent;
import sypztep.dominatus.common.data.MobExpEntry;
import sypztep.dominatus.common.init.ModEntityComponents;

public class ExpUtil {

    /**
     * Calculate experience reward based on damage percentage and level penalty
     * @param player The player who dealt damage
     * @param target The target entity that was killed
     * @param damagePercentage Percentage of damage dealt by the player (0.0 to 1.0)
     * @return The calculated experience reward
     */
    public static int calculateExpReward(PlayerEntity player, LivingEntity target, float damagePercentage) {
        // Get base exp reward from MobExpEntry
        EntityType<?> entityType = target.getType();
        int baseExp = MobExpEntry.getExpReward(entityType);

        if (baseExp <= 0) return 0;

        // Apply damage percentage
        float expFromDamage = baseExp * damagePercentage;

        // Get player and target levels using LivingLevelComponent
        int playerLevel = getPlayerLevel(player);
        int targetLevel = getEntityLevel(target);

        // Apply level penalty
        float levelMultiplier = calculateLevelPenalty(playerLevel, targetLevel);

        // Calculate final exp
        int finalExp = Math.round(expFromDamage * levelMultiplier);

        return Math.max(0, finalExp); // Ensure non-negative
    }

    /**
     * Get the level of an entity using LivingLevelComponent
     */
    private static int getEntityLevel(LivingEntity entity) {
        // Try to get level from LivingLevelComponent first
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(entity);
        if (levelComponent != null) return levelComponent.getLevel();

        // Fallback to health-based calculation if component not available
        float maxHealth = entity.getMaxHealth();
        int level = Math.round(maxHealth / 2f);
        return Math.max(1, Math.min(100, level));
    }

    /**
     * Get player level using LivingLevelComponent
     */
    private static int getPlayerLevel(PlayerEntity player) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(player);
        if (levelComponent != null) return levelComponent.getLevel();

        // Fallback to vanilla experience level
        return Math.max(1, player.experienceLevel);
    }

    /**
     * Calculate level penalty based on player level vs target level
     * Based on your Ragnarok-style penalty system
     */
    public static float calculateLevelPenalty(int playerLevel, int targetLevel) {
        int levelDiff = targetLevel - playerLevel;

        // Your penalty table
        return switch (levelDiff) {
            case 16 -> 0.40f; // +16 > 40%
            case 15 -> 1.15f; // +15 115%
            case 14 -> 1.20f; // +14 120%
            case 13 -> 1.25f; // +13 125%
            case 12 -> 1.30f; // +12 130%
            case 11 -> 1.35f; // +11 135%
            case 10 -> 1.40f; // +10 140%
            case 9 -> 1.35f;  // +9 135%
            case 8 -> 1.30f;  // +8 130%
            case 7 -> 1.25f;  // +7 125%
            case 6 -> 1.20f;  // +6 120%
            case 5 -> 1.15f;  // +5 115%
            case 4 -> 1.10f;  // +4 110%
            case 3 -> 1.05f;  // +3 105%
            case 2 -> 1.00f;  // +2 100%
            case 1 -> 1.00f;  // +1 100%
            case 0 -> 1.00f;  // Equal 100%
            case -1 -> 1.00f; // -1 100%
            case -2 -> 1.00f; // -2 100%
            case -3 -> 1.00f; // -3 100%
            case -4 -> 1.00f; // -4 100%
            case -5 -> 1.00f; // -5 100%
            case -6 -> 0.95f; // -6 95%
            case -7 -> 0.95f; // -7 95%
            case -8 -> 0.95f; // -8 95%
            case -9 -> 0.95f; // -9 95%
            case -10 -> 0.95f; // -10 95%
            case -11 -> 0.90f; // -11 90%
            case -12 -> 0.90f; // -12 90%
            case -13 -> 0.90f; // -13 90%
            case -14 -> 0.90f; // -14 90%
            case -15 -> 0.90f; // -15 90%
            case -16 -> 0.85f; // -16 85%
            case -17 -> 0.85f; // -17 85%
            case -18 -> 0.85f; // -18 85%
            case -19 -> 0.85f; // -19 85%
            case -20 -> 0.85f; // -20 85%
            case -21 -> 0.60f; // -21 60%
            case -22 -> 0.60f; // -22 60%
            case -23 -> 0.60f; // -23 60%
            case -24 -> 0.60f; // -24 60%
            case -25 -> 0.60f; // -25 60%
            case -26 -> 0.35f; // -26 35%
            case -27 -> 0.35f; // -27 35%
            case -28 -> 0.35f; // -28 35%
            case -29 -> 0.35f; // -29 35%
            case -30 -> 0.35f; // -30 35%
            default -> levelDiff > 16 ? 0.4f : 0.1f;
        };
    }
    /**
     * Award experience to a player using their LivingLevelComponent
     * @param player The player to award experience to
     * @param amount The amount of experience to award
     * @param source Optional source description for the message
     * @param showMessage Whether to show a message to the player
     */
    public static void awardExperience(PlayerEntity player, long amount, String source, boolean showMessage) {
        if (amount <= 0) return;

        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(player);
        if (levelComponent != null) {
            int oldLevel = levelComponent.getLevel();
            levelComponent.addExperience(amount);
            int newLevel = levelComponent.getLevel();

            if (showMessage) {
                // Check if player leveled up
                if (newLevel > oldLevel) {
                    Text levelUpMessage = Text.literal(String.format("§b§lLEVEL UP! §r§b%d → %d", oldLevel, newLevel))
                            .formatted(Formatting.AQUA);
                    player.sendMessage(levelUpMessage, false);
                }

                // Show experience gain message
                String message = source != null ?
                        String.format("§6+%d EXP §7(%s)", amount, source) :
                        String.format("§6+%d EXP", amount);

                Text expMessage = Text.literal(message).formatted(Formatting.GOLD);
                player.sendMessage(expMessage, true); // Action bar
            }
        } else {
            // Fallback to vanilla experience
            player.addExperience((int) Math.min(amount, Integer.MAX_VALUE));

            if (showMessage) {
                String message = source != null ?
                        String.format("§6+%d EXP §7(%s)", amount, source) :
                        String.format("§6+%d EXP", amount);

                Text expMessage = Text.literal(message).formatted(Formatting.GOLD);
                player.sendMessage(expMessage, true);
            }
        }
    }

    /**
     * Award experience with a message (convenience method)
     */
    public static void awardExperience(PlayerEntity player, long amount, String source) {
        awardExperience(player, amount, source, true);
    }

    /**
     * Award experience without a message (convenience method)
     */
    public static void awardExperienceSilent(PlayerEntity player, long amount) {
        awardExperience(player, amount, null, false);
    }

    /**
     * Get player's current experience using LivingLevelComponent
     */
    public static long getPlayerExperience(PlayerEntity player) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(player);
        if (levelComponent != null) {
            return levelComponent.getExperience();
        }
        return player.totalExperience;
    }
}