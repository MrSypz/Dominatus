package sypztep.dominatus.common.util.level.config;

/**
 * Linear level configuration (simple progression)
 */
public class LinearLevelConfig implements LevelConfiguration {
    private final int maxLevel;
    private final long experiencePerLevel;

    public LinearLevelConfig(int maxLevel, long experiencePerLevel) {
        this.maxLevel = maxLevel;
        this.experiencePerLevel = experiencePerLevel;
    }

    @Override
    public int getStartingLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public long getExperienceRequiredForLevel(int level) {
        if (level <= 1) return 0L;
        if (level > maxLevel) return Long.MAX_VALUE;

        return experiencePerLevel * (level - 1);
    }
}
