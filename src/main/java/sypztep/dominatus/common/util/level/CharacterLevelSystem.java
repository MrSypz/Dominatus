package sypztep.dominatus.common.util.level;

import net.minecraft.nbt.NbtCompound;
import sypztep.dominatus.common.util.level.benefit.BenefitCalculator;
import sypztep.dominatus.common.util.level.benefit.PlayerBenefitSystem;

public class CharacterLevelSystem {
    private final LevelSystem levelSystem;
    private final PlayerBenefitSystem benefitSystem;

    public CharacterLevelSystem() {
        this.levelSystem = new LevelSystem(LevelConfigs.CHARACTER);
        this.benefitSystem = new PlayerBenefitSystem();
    }

    public CharacterLevelSystem(BenefitCalculator benefitCalculator) {
        this.levelSystem = new LevelSystem(LevelConfigs.CHARACTER);
        this.benefitSystem = new PlayerBenefitSystem(benefitCalculator);
    }

    public void addExperience(long amount) {
        int levelsGained = levelSystem.addExperience(amount);

        for (int i = 0; i < levelsGained; i++) {
            int currentLevel = levelSystem.getLevel() - levelsGained + i + 1;
            benefitSystem.onLevelUp(currentLevel);
        }
    }

    public int getLevel() { return levelSystem.getLevel(); }
    public long getExperience() { return levelSystem.getExperience(); }
    public long getExperienceToNextLevel() { return levelSystem.getExperienceToNextLevel(); }
    public double getExperiencePercentage() { return levelSystem.getExperiencePercentage(); }
    public boolean isMaxLevel() { return levelSystem.isMaxLevel(); }
    public int getMaxLevel() { return levelSystem.getMaxLevel(); }

    public int getAvailableBenefits() { return benefitSystem.getAvailableBenefits(); }
    public boolean spendBenefits(int amount) { return benefitSystem.spendBenefits(amount); }

    @Deprecated
    public int getStatPoints() { return getAvailableBenefits(); }

    public void writeToNbt(NbtCompound tag) {
        tag.putInt("Level", levelSystem.getLevel());
        tag.putLong("Experience", levelSystem.getExperience());
        tag.putLong("ExperienceToNextLevel", levelSystem.getExperienceToNextLevel());
        tag.putInt("AvailableBenefits", benefitSystem.getAvailableBenefits());
    }

    public void readFromNbt(NbtCompound tag) {
        levelSystem.setLevel(tag.getInt("Level"));
        levelSystem.setExperience(tag.getLong("Experience"));
        levelSystem.updateExperienceToNextLevel();
        benefitSystem.setAvailableBenefits(tag.getInt("AvailableBenefits"));
    }
}


