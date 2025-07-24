package sypztep.dominatus.common.system.level.config;

public class ClassLevelConfig implements LevelConfiguration {
    @Override
    public int getStartingLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 50;
    }

    @Override
    public long getExperienceRequiredForLevel(int level) {
        if (level <= 1) return 0L;
        if (level > 50) return Long.MAX_VALUE;

        // Moderate exp curve - not too easy, not too hard
        // Level 2: 100 exp, Level 10: ~2000 exp, Level 50: ~125,000 exp
        return (long) (100 * level * Math.pow(1.15, level - 1));
    }
}
