package sypztep.dominatus.common.system.level.config;

public class LevelConfigs {
    // Character/Combat leveling
    public static final LevelConfiguration CHARACTER = new CharacterLevelConfig();


    // Custom configurations
    public static LevelConfiguration fromArray(long[] experienceMap) {
        return new ArrayLevelConfig(experienceMap);
    }

    public static LevelConfiguration skill(int maxLevel, long baseExp, double multiplier) {
        return new SkillLevelConfig(maxLevel, baseExp, multiplier);
    }

    public static LevelConfiguration linear(int maxLevel, long expPerLevel) {
        return new LinearLevelConfig(maxLevel, expPerLevel);
    }
}
