package sypztep.dominatus.common.util.level.config;

public interface LevelConfiguration {
    /**
     * @return The starting level (usually 1)
     */
    int getStartingLevel();

    /**
     * @return The maximum achievable level
     */
    int getMaxLevel();

    /**
     * @param level The level to get experience requirement for
     * @return Experience required to reach this level from level 0
     */
    long getExperienceRequiredForLevel(int level);
}
