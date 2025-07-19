package sypztep.dominatus.common.data;

import net.minecraft.entity.EntityType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record MobExpEntry(
        int expReward,
        int baseLevel,
        MobStats stats
) {

    public static final Map<EntityType<?>, MobExpEntry> MOBEXP_MAP = new ConcurrentHashMap<>();

    public static class MobStats {
        public final int strength;
        public final int agility;
        public final int vitality;
        public final int intelligence;
        public final int dexterity;
        public final int luck;

        public MobStats(int strength, int agility, int vitality, int intelligence, int dexterity, int luck) {
            this.strength = strength;
            this.agility = agility;
            this.vitality = vitality;
            this.intelligence = intelligence;
            this.dexterity = dexterity;
            this.luck = luck;
        }

        // Default constructor for basic mobs
        public MobStats() {
            this(1, 1, 1, 1, 1, 1);
        }

        // Constructor for balanced stats based on level
        public static MobStats forLevel(int level) {
            return new MobStats(level, level, level, level, level, level);
        }

        // Constructor for specialized mob types
        public static MobStats warrior(int level) {
            return new MobStats(level + 2, level, level + 1, level - 1, level, level);
        }

        public static MobStats archer(int level) {
            return new MobStats(level, level + 2, level, level, level + 2, level + 1);
        }

        public static MobStats mage(int level) {
            return new MobStats(level - 1, level, level, level + 3, level, level + 1);
        }

        public static MobStats tank(int level) {
            return new MobStats(level + 1, level - 1, level + 3, level, level, level);
        }

        public static MobStats boss(int level) {
            return new MobStats(level + 5, level + 3, level + 5, level + 3, level + 3, level + 3);
        }

        @Override
        public String toString() {
            return String.format("MobStats{str=%d, agi=%d, vit=%d, int=%d, dex=%d, luk=%d}",
                    strength, agility, vitality, intelligence, dexterity, luck);
        }
    }

    // Convenience constructor with default stats
    public MobExpEntry(int expReward, int baseLevel) {
        this(expReward, baseLevel, new MobStats());
    }

    // Convenience constructor with level-based stats
    public static MobExpEntry withLevelStats(int expReward, int baseLevel) {
        return new MobExpEntry(expReward, baseLevel, MobStats.forLevel(baseLevel));
    }

    // Convenience constructor for specialized mobs
    public static MobExpEntry warrior(int expReward, int baseLevel) {
        return new MobExpEntry(expReward, baseLevel, MobStats.warrior(baseLevel));
    }

    public static MobExpEntry archer(int expReward, int baseLevel) {
        return new MobExpEntry(expReward, baseLevel, MobStats.archer(baseLevel));
    }

    public static MobExpEntry mage(int expReward, int baseLevel) {
        return new MobExpEntry(expReward, baseLevel, MobStats.mage(baseLevel));
    }

    public static MobExpEntry tank(int expReward, int baseLevel) {
        return new MobExpEntry(expReward, baseLevel, MobStats.tank(baseLevel));
    }

    public static MobExpEntry boss(int expReward, int baseLevel) {
        return new MobExpEntry(expReward, baseLevel, MobStats.boss(baseLevel));
    }

    // Existing static methods
    public static MobExpEntry getEntry(EntityType<?> entityType) {
        return MOBEXP_MAP.get(entityType);
    }

    public static int getExpReward(EntityType<?> entityType) {
        MobExpEntry entry = MOBEXP_MAP.get(entityType);
        return entry != null ? entry.expReward() : 0;
    }

    public static int getBaseLevel(EntityType<?> entityType) {
        MobExpEntry entry = MOBEXP_MAP.get(entityType);
        return entry != null ? entry.baseLevel() : 1;
    }

    public static MobStats getStats(EntityType<?> entityType) {
        MobExpEntry entry = MOBEXP_MAP.get(entityType);
        return entry != null ? entry.stats() : new MobStats();
    }

    public static boolean hasEntry(EntityType<?> entityType) {
        return MOBEXP_MAP.containsKey(entityType);
    }

    public static void addEntry(EntityType<?> entityType, MobExpEntry entry) {
        MOBEXP_MAP.put(entityType, entry);
    }

    public static void removeEntry(EntityType<?> entityType) {
        MOBEXP_MAP.remove(entityType);
    }

    public static void clearAll() {
        MOBEXP_MAP.clear();
    }

    @Override
    public String toString() {
        return String.format("MobExpEntry{expReward=%d, baseLevel=%d, %s}",
                expReward, baseLevel, stats);
    }
}