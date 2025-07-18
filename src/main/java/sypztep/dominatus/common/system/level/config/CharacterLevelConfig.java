package sypztep.dominatus.common.system.level.config;

import sypztep.dominatus.ModConfig;

/**
 * Character/Combat level configuration
 */
public class CharacterLevelConfig implements LevelConfiguration {
    @Override
    public int getStartingLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return ModConfig.maxLevel;
    }

    @Override
    public long getExperienceRequiredForLevel(int level) {
        if (level < 1 || level >= ModConfig.maxLevel) return 0L;
        return ModConfig.EXP_MAP[level - 1];
    }
}

