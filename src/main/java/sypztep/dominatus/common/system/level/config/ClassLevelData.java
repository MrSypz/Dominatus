package sypztep.dominatus.common.system.level.config;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import sypztep.dominatus.common.system.level.benefit.BenefitCalculator;
import sypztep.dominatus.common.system.level.benefit.PlayerBenefitSystem;
import sypztep.dominatus.common.system.level.core.LevelData;
import sypztep.dominatus.common.system.level.core.LevelSystem;

/**
 * Class-specific level data - separate from character level
 * Each level up awards 1 "class point" for skill upgrades
 */
public class ClassLevelData implements LevelData {
    private final LevelSystem levelSystem;
    private final PlayerBenefitSystem benefitSystem; // "Class Points"

    public ClassLevelData(LevelConfiguration config) {
        this.levelSystem = new LevelSystem(config);
        this.benefitSystem = new PlayerBenefitSystem();
    }

    public ClassLevelData(LevelConfiguration config, BenefitCalculator benefitCalculator) {
        this.levelSystem = new LevelSystem(config);
        this.benefitSystem = new PlayerBenefitSystem(benefitCalculator);
    }

    @Override
    public int addExperience(long amount) {
        int levelsGained = levelSystem.addExperience(amount);

        // Award 1 class point per level gained
        for (int i = 0; i < levelsGained; i++) {
            benefitSystem.onLevelUp(levelSystem.getLevel() - levelsGained + i + 1);
        }

        return levelsGained;
    }

    /**
     * Reset this class level data (called when changing classes)
     */
    public void reset() {
        levelSystem.setLevel(1);
        levelSystem.setExperience(0);
        benefitSystem.setAvailableBenefits(0);
    }

    // Delegate core level operations to levelSystem (same as your PlayerLevelData)
    @Override public int getLevel() { return levelSystem.getLevel(); }
    @Override public void setLevel(int level) { levelSystem.setLevel(level); }
    @Override public long getExperience() { return levelSystem.getExperience(); }
    @Override public void setExperience(long experience) { levelSystem.setExperience(experience); }
    @Override public long getExperienceToNextLevel() { return levelSystem.getExperienceToNextLevel(); }
    @Override public double getExperiencePercentage() { return levelSystem.getExperiencePercentage(); }
    @Override public boolean isMaxLevel() { return levelSystem.isMaxLevel(); }
    @Override public int getMaxLevel() { return levelSystem.getMaxLevel(); }
    @Override public int getStartingLevel() { return levelSystem.getStartingLevel(); }

    // Benefit operations (class points)
    @Override public int getAvailableBenefits() { return benefitSystem.getAvailableBenefits(); }
    @Override public boolean spendBenefits(int amount) { return benefitSystem.spendBenefits(amount); }
    @Override public void addBenefits(int amount) { benefitSystem.addBenefits(amount); }
    @Override public void setBenefits(int amount) { benefitSystem.setAvailableBenefits(amount); }

    @Override public boolean isPlayer() { return true; }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        // LevelSystem supports prefix
        levelSystem.writeToNbt(tag, "Class");

        // PlayerBenefitSystem doesn't support prefix, so create a sub-compound
        NbtCompound classTag = new NbtCompound();
        benefitSystem.writeToNbt(classTag);
        tag.put("ClassBenefits", classTag);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        // LevelSystem supports prefix
        levelSystem.readFromNbt(tag, "Class");

        // PlayerBenefitSystem from sub-compound
        if (tag.contains("ClassBenefits")) {
            NbtCompound classTag = tag.getCompound("ClassBenefits");
            benefitSystem.readFromNbt(classTag);
        }
    }
}