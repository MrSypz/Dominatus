package sypztep.dominatus.common.util.level;

import sypztep.dominatus.ModConfig;
import sypztep.dominatus.common.util.level.config.LevelConfiguration;

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
        if (level < 1 || level >= ModConfig.EXP_MAP.length) return 0L;
        return ModConfig.EXP_MAP[level];
    }
}

