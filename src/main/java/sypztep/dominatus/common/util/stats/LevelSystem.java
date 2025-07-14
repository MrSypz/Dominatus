package sypztep.dominatus.common.util.stats;

import net.minecraft.nbt.NbtCompound;
import sypztep.dominatus.ModConfig;

public class LevelSystem {
    private int level;
    private long xp;                    // Changed from int to long
    private long xpToNextLevel;         // Changed from int to long
    private int statPoints;
    private static final int MAX_LEVEL = ModConfig.maxLevel;

    public LevelSystem() {
        this.level = 1;
        this.xp = 0L;
        this.statPoints = ModConfig.startStatpoints;
        this.xpToNextLevel = calculateXpForNextLevel(level);
    }

    private long calculateXpForNextLevel(int level) {
        if (level < 1 || level >= ModConfig.EXP_MAP.length) {
            return 0L;
        }
        return ModConfig.EXP_MAP[level];
    }

    public void addExperience(long amount) {
        if (level >= MAX_LEVEL && xp >= xpToNextLevel) {
            xp = xpToNextLevel;
            return;
        }
        xp += amount;
        while (xp >= xpToNextLevel && level < MAX_LEVEL) {
            levelUp();
        }
        if (level >= MAX_LEVEL) {
            xp = Math.min(xp, xpToNextLevel);
        }
    }

    public void subtractExperience(long amount) {
        if (level >= MAX_LEVEL) {
            return;
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to subtract cannot be negative");
        }
        xp = Math.max(0L, xp - amount);
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    private void levelUp() {
        if (level >= MAX_LEVEL) return;
        xp -= xpToNextLevel;
        level++;
        statPoints += getStatPointsForLevel(level);
        if (level < MAX_LEVEL) {
            updateNextLvl();
        }
    }

    private int getStatPointsForLevel(int level) {
        return level / 5 + 3;
    }

    public void updateNextLvl() {
        xpToNextLevel = calculateXpForNextLevel(level);
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getXp() {
        return xp;
    }

    public long getXpToNextLevel() {
        return xpToNextLevel;
    }

    public double getXpPercentage() {
        if (this.xpToNextLevel == 0) {
            return 0;
        }
        return ((double) this.xp / this.xpToNextLevel) * 100;
    }

    public int getStatPoints() {
        return statPoints;
    }

    public void setStatPoints(int statPoints) {
        this.statPoints = statPoints;
    }

    public void addStatPoints(int statPoints) {
        this.statPoints += statPoints;
    }

    public void subtractStatPoints(int points) {
        this.statPoints -= points;
    }

    public void writeToNbt(NbtCompound tag) {
        tag.putInt("Level", level);
        tag.putLong("XP", xp);
        tag.putLong("XPToNextLevel", xpToNextLevel);
        tag.putInt("StatPoints", statPoints);
    }

    public void readFromNbt(NbtCompound tag) {
        this.level = tag.getInt("Level");
        this.xp = tag.getLong("XP");
        this.xpToNextLevel = tag.getLong("XPToNextLevel");
        this.statPoints = tag.getInt("StatPoints");
    }
}