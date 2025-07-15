package sypztep.dominatus.common.util.level;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.data.MobExpEntry;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.LevelData;

public class ExpUtil {

    /**
     * Calculate experience reward based on damage percentage and level penalty
     * @param player The player who dealt damage
     * @param target The target entity that was killed
     * @param damagePercentage Percentage of damage dealt by the player (0.0 to 1.0)
     * @return The calculated experience reward
     */
    public static int calculateExpReward(PlayerEntity player, LivingEntity target, float damagePercentage) {
        EntityType<?> entityType = target.getType();
        int baseExp = MobExpEntry.getExpReward(entityType);

        if (baseExp <= 0) return 0;

        float expFromDamage = baseExp * damagePercentage;

        int playerLevel = getEntityLevel(player);
        int targetLevel = getEntityLevel(target);

        float levelMultiplier = calculateLevelPenalty(playerLevel, targetLevel);
        int finalExp = Math.round(expFromDamage * levelMultiplier);

        return Math.max(0, finalExp);
    }

    /**
     * Get the level of an entity using LivingLevelComponent
     */
    private static int getEntityLevel(LivingEntity entity) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(entity);
        if (levelComponent != null) return levelComponent.getLevel(); // Uses unified interface

        if (entity instanceof PlayerEntity player) return Math.max(1, player.experienceLevel);

        float maxHealth = entity.getMaxHealth();
        int level = Math.round(maxHealth / 2f);
        return Math.max(1, Math.min(100, level));
    }

    /**
     * Award experience using unified interface
     */
    public static void awardExperience(PlayerEntity player, long amount, String source, boolean showMessage) {
        if (amount <= 0) return;

        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(player);
        if (levelComponent != null) {
            LevelData levelData = levelComponent.getLevelData(); // Unified access!

            int oldLevel = levelData.getLevel();
            levelComponent.addExperience(amount); // This handles sync automatically
            int newLevel = levelData.getLevel();

            if (showMessage) {
                if (newLevel > oldLevel) {
                    Text levelUpMessage = Text.literal(String.format("§b§lLEVEL UP! §r§b%d → %d", oldLevel, newLevel))
                            .formatted(Formatting.AQUA);
                    player.sendMessage(levelUpMessage, false);
                }

                String message = source != null ?
                        String.format("§6+%d EXP §7(%s)", amount, source) :
                        String.format("§6+%d EXP", amount);

                Text expMessage = Text.literal(message).formatted(Formatting.GOLD);
                player.sendMessage(expMessage, true);
            }
        } else {
            // Fallback to vanilla
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

    // Convenience methods
    public static void awardExperience(PlayerEntity player, long amount, String source) {
        awardExperience(player, amount, source, true);
    }

    public static void awardExperienceSilent(PlayerEntity player, long amount) {
        awardExperience(player, amount, null, false);
    }

    public static long getPlayerExperience(PlayerEntity player) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(player);
        if (levelComponent != null) {
            return levelComponent.getExperience(); // Unified access
        }
        return player.totalExperience;
    }

    // Keep existing level penalty calculation
    public static float calculateLevelPenalty(int playerLevel, int targetLevel) {
        int levelDiff = targetLevel - playerLevel;

        return switch (levelDiff) {
            case 16 -> 0.40f;
            case 15 -> 1.15f;
            case 14 -> 1.20f;
            case 13 -> 1.25f;
            case 12 -> 1.30f;
            case 11 -> 1.35f;
            case 10 -> 1.40f;
            case 9 -> 1.35f;
            case 8 -> 1.30f;
            case 7 -> 1.25f;
            case 6 -> 1.20f;
            case 5 -> 1.15f;
            case 4 -> 1.10f;
            case 3 -> 1.05f;
            case 2 -> 1.00f;
            case 1 -> 1.00f;
            case 0 -> 1.00f;
            case -1 -> 1.00f;
            case -2 -> 1.00f;
            case -3 -> 1.00f;
            case -4 -> 1.00f;
            case -5 -> 1.00f;
            case -6 -> 0.95f;
            case -7 -> 0.95f;
            case -8 -> 0.95f;
            case -9 -> 0.95f;
            case -10 -> 0.95f;
            case -11 -> 0.90f;
            case -12 -> 0.90f;
            case -13 -> 0.90f;
            case -14 -> 0.90f;
            case -15 -> 0.90f;
            case -16 -> 0.85f;
            case -17 -> 0.85f;
            case -18 -> 0.85f;
            case -19 -> 0.85f;
            case -20 -> 0.85f;
            case -21 -> 0.60f;
            case -22 -> 0.60f;
            case -23 -> 0.60f;
            case -24 -> 0.60f;
            case -25 -> 0.60f;
            case -26 -> 0.35f;
            case -27 -> 0.35f;
            case -28 -> 0.35f;
            case -29 -> 0.35f;
            case -30 -> 0.35f;
            default -> levelDiff > 16 ? 0.4f : 0.1f;
        };
    }
}
