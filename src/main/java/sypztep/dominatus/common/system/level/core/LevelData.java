package sypztep.dominatus.common.system.level.core;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

/**
 * Unified interface for all level-related data and operations
 */
public interface LevelData {
    // Core level operations
    int getLevel();
    void setLevel(int level);

    // Experience operations
    long getExperience();
    void setExperience(long experience);
    int addExperience(long amount); // Returns levels gained

    // Experience calculations
    long getExperienceToNextLevel();
    double getExperiencePercentage();

    // Level bounds
    boolean isMaxLevel();
    int getMaxLevel();
    int getStartingLevel();

    // Benefit operations (returns 0 for non-players)
    int getAvailableBenefits();
    boolean spendBenefits(int amount);
    void addBenefits(int amount);
    void setBenefits(int amount);

    // Persistence
    void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup);
    void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup);

    // Type checking
    boolean isPlayer();
}
