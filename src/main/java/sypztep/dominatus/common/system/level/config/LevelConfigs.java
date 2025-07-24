package sypztep.dominatus.common.system.level.config;

public class LevelConfigs {
    // Character/Combat leveling
    public static final LevelConfiguration CHARACTER = new CharacterLevelConfig();

    public static final LevelConfiguration CLASS_LEVEL = new ClassLevelConfig();

    // Skill configurations
//    public static final LevelConfiguration LUMBER_SKILL = new SkillLevelConfig(100, 100L, 1.15);
//    public static final LevelConfiguration MINING_SKILL = new SkillLevelConfig(100, 120L, 1.12);
//    public static final LevelConfiguration FISHING_SKILL = new SkillLevelConfig(80, 80L, 1.18);

    // Linear skills (easier progression)
//    public static final LevelConfiguration SIMPLE_SKILL = new LinearLevelConfig(50, 1000L);

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
