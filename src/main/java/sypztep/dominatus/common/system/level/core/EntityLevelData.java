package sypztep.dominatus.common.system.level.core;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import sypztep.dominatus.common.system.level.config.LevelConfiguration;

public class EntityLevelData implements LevelData {
    private final LevelSystem levelSystem;

    public EntityLevelData(LevelConfiguration config) {
        this.levelSystem = new LevelSystem(config);
    }

    @Override public int getLevel() { return levelSystem.getLevel(); }
    @Override public void setLevel(int level) { levelSystem.setLevel(level); }
    @Override public long getExperience() { return levelSystem.getExperience(); }
    @Override public void setExperience(long experience) { levelSystem.setExperience(experience); }
    @Override public int addExperience(long amount) { return levelSystem.addExperience(amount); }
    @Override public long getExperienceToNextLevel() { return levelSystem.getExperienceToNextLevel(); }
    @Override public double getExperiencePercentage() { return levelSystem.getExperiencePercentage(); }
    @Override public boolean isMaxLevel() { return levelSystem.isMaxLevel(); }
    @Override public int getMaxLevel() { return levelSystem.getMaxLevel(); }
    @Override public int getStartingLevel() { return levelSystem.getStartingLevel(); }

    @Override public int getAvailableBenefits() { return 0; }
    @Override public boolean spendBenefits(int amount) { return false; }
    @Override public void addBenefits(int amount) { /* No-op for entities */ }
    @Override public void setBenefits(int amount) { /* No-op for entities */ }

    @Override public boolean isPlayer() { return false; }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        levelSystem.writeToNbt(tag, "");
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        levelSystem.readFromNbt(tag, "");
    }
}