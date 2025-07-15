package sypztep.dominatus.common.system.level.config;

public class SkillLevelConfig implements LevelConfiguration {
    private final int maxLevel;
    private final long baseExperience;
    private final double multiplier;

    public SkillLevelConfig(int maxLevel, long baseExperience, double multiplier) {
        this.maxLevel = maxLevel;
        this.baseExperience = baseExperience;
        this.multiplier = multiplier;
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

        return (long) (baseExperience * Math.pow(multiplier, level - 2));
    }
}
