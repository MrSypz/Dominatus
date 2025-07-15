package sypztep.dominatus.common.util.level;

import net.minecraft.nbt.NbtCompound;
import sypztep.dominatus.common.util.level.config.LevelConfiguration;

public class LevelSystem {
    private int level;
    private long experience;
    private long experienceToNextLevel;
    private final LevelConfiguration implConfig;

    public LevelSystem(LevelConfiguration implConfig) {
        this.implConfig = implConfig;
        this.level = implConfig.getStartingLevel();
        this.experience = 0L;
        this.experienceToNextLevel = calculateExperienceForNextLevel(level);
    }

    private long calculateExperienceForNextLevel(int level) {
        return implConfig.getExperienceRequiredForLevel(level + 1);
    }

    public int addExperience(long amount) {
        if (level >= implConfig.getMaxLevel()) {
            experience = Math.min(experience + amount, experienceToNextLevel);
            return 0;
        }

        experience += amount;
        int levelsGained = 0;

        while (experience >= experienceToNextLevel && level < implConfig.getMaxLevel()) {
            levelUp();
            levelsGained++;
        }

        if (level >= implConfig.getMaxLevel()) {
            experience = Math.min(experience, experienceToNextLevel);
        }

        return levelsGained;
    }

    public void subtractExperience(long amount) {
        if (level >= implConfig.getMaxLevel()) {
            return;
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to subtract cannot be negative");
        }
        experience = Math.max(0L, experience - amount);
    }

    private void levelUp() {
        if (level >= implConfig.getMaxLevel()) return;
        experience -= experienceToNextLevel;
        level++;
        if (level < implConfig.getMaxLevel()) {
            updateExperienceToNextLevel();
        }
    }

    public void updateExperienceToNextLevel() {
        experienceToNextLevel = calculateExperienceForNextLevel(level);
    }

    // Getters and setters
    public int getLevel() { return level; }
    public void setLevel(int level) {
        this.level = Math.max(implConfig.getStartingLevel(), Math.min(level, implConfig.getMaxLevel()));
        updateExperienceToNextLevel();
    }

    public long getExperience() { return experience; }
    public void setExperience(long experience) { this.experience = Math.max(0L, experience); }

    public long getExperienceToNextLevel() { return experienceToNextLevel; }
    public int getMaxLevel() { return implConfig.getMaxLevel(); }
    public int getStartingLevel() { return implConfig.getStartingLevel(); }

    public double getExperiencePercentage() {
        if (experienceToNextLevel == 0) return 100.0; // Max level
        return ((double) experience / experienceToNextLevel) * 100.0;
    }

    public boolean isMaxLevel() {
        return level >= implConfig.getMaxLevel();
    }

    public void writeToNbt(NbtCompound tag, String prefix) {
        tag.putInt(prefix + "Level", level);
        tag.putLong(prefix + "Experience", experience);
        tag.putLong(prefix + "ExperienceToNextLevel", experienceToNextLevel);
    }

    public void readFromNbt(NbtCompound tag, String prefix) {
        this.level = tag.getInt(prefix + "Level");
        this.experience = tag.getLong(prefix + "Experience");
        this.experienceToNextLevel = tag.getLong(prefix + "ExperienceToNextLevel");
    }
}

