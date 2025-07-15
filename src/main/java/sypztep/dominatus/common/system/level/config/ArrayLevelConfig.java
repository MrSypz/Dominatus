package sypztep.dominatus.common.system.level.config;

public class ArrayLevelConfig implements LevelConfiguration {
    private final long[] experienceMap;

    public ArrayLevelConfig(long[] experienceMap) {
        this.experienceMap = experienceMap.clone();
    }

    @Override
    public int getStartingLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return experienceMap.length - 1;
    }

    @Override
    public long getExperienceRequiredForLevel(int level) {
        if (level < 1 || level >= experienceMap.length) {
            return level <= 1 ? 0L : Long.MAX_VALUE;
        }
        return experienceMap[level];
    }
}
