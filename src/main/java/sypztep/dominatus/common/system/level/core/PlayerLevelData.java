package sypztep.dominatus.common.system.level.core;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import sypztep.dominatus.common.system.level.benefit.BenefitCalculator;
import sypztep.dominatus.common.system.level.benefit.PlayerBenefitSystem;
import sypztep.dominatus.common.system.level.config.LevelConfiguration;

public class PlayerLevelData implements LevelData {
    private final LevelSystem levelSystem;
    private final PlayerBenefitSystem benefitSystem;

    public PlayerLevelData(LevelConfiguration config) {
        this.levelSystem = new LevelSystem(config);
        this.benefitSystem = new PlayerBenefitSystem();
    }

    public PlayerLevelData(LevelConfiguration config, BenefitCalculator benefitCalculator) {
        this.levelSystem = new LevelSystem(config);
        this.benefitSystem = new PlayerBenefitSystem(benefitCalculator);
    }

    @Override
    public int addExperience(long amount) {
        int levelsGained = levelSystem.addExperience(amount);

        // Award benefits for each level gained
        for (int i = 0; i < levelsGained; i++) {
            int currentLevel = levelSystem.getLevel() - levelsGained + i + 1;
            benefitSystem.onLevelUp(currentLevel);
        }

        return levelsGained;
    }

    // Delegate core level operations to levelSystem
    @Override public int getLevel() { return levelSystem.getLevel(); }
    @Override public void setLevel(int level) { levelSystem.setLevel(level); }
    @Override public long getExperience() { return levelSystem.getExperience(); }
    @Override public void setExperience(long experience) { levelSystem.setExperience(experience); }
    @Override public long getExperienceToNextLevel() { return levelSystem.getExperienceToNextLevel(); }
    @Override public double getExperiencePercentage() { return levelSystem.getExperiencePercentage(); }
    @Override public boolean isMaxLevel() { return levelSystem.isMaxLevel(); }
    @Override public int getMaxLevel() { return levelSystem.getMaxLevel(); }
    @Override public int getStartingLevel() { return levelSystem.getStartingLevel(); }

    // Benefit operations
    @Override public int getAvailableBenefits() { return benefitSystem.getAvailableBenefits(); }
    @Override public boolean spendBenefits(int amount) { return benefitSystem.spendBenefits(amount); }

    @Override public boolean isPlayer() { return true; }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        levelSystem.writeToNbt(tag, "");
        benefitSystem.writeToNbt(tag);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        levelSystem.readFromNbt(tag, "");
        benefitSystem.readFromNbt(tag);
    }
}