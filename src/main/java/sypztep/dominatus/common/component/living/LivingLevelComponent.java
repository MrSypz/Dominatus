package sypztep.dominatus.common.component.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.config.LevelConfigs;
import sypztep.dominatus.common.system.level.core.EntityLevelData;
import sypztep.dominatus.common.system.level.core.LevelData;
import sypztep.dominatus.common.system.level.core.PlayerLevelData;

public class LivingLevelComponent implements AutoSyncedComponent {
    private final LivingEntity living;
    private final LevelData levelData;

    public LivingLevelComponent(LivingEntity living) {
        this.living = living;
        if (living instanceof PlayerEntity) this.levelData = new PlayerLevelData(LevelConfigs.CHARACTER);
        else this.levelData = new EntityLevelData(LevelConfigs.CHARACTER); // not write benefit
    }

    // ====================
    // UNIFIED ACCESS METHODS
    // ====================

    // Direct delegation to levelData - no more conditional logic!
    public int getLevel() { return levelData.getLevel(); }
    public void setLevel(int level) { levelData.setLevel(level); sync(); }

    public long getExperience() { return levelData.getExperience(); }
    public void setExperience(long experience) { levelData.setExperience(experience); sync(); }

    public int addExperience(long amount) {
        int levelsGained = levelData.addExperience(amount);
        sync();
        return levelsGained;
    }

    public long getExperienceToNextLevel() { return levelData.getExperienceToNextLevel(); }
    public double getExperiencePercentage() { return levelData.getExperiencePercentage(); }
    public boolean isMaxLevel() { return levelData.isMaxLevel(); }
    public int getMaxLevel() { return levelData.getMaxLevel(); }

    public int getAvailableBenefits() { return levelData.getAvailableBenefits(); }
    public boolean spendBenefits(int amount) {
        boolean success = levelData.spendBenefits(amount);
        if (success) sync();
        return success;
    }
    public void addBenefits(int amount) { levelData.addBenefits(amount); sync(); }
    public void setBenefits(int amount) { levelData.setBenefits(amount); sync(); }


    public boolean isPlayer() { return levelData.isPlayer(); }

    // ====================
    // DIRECT ACCESS TO LEVEL DATA
    // ====================

    /**
     * Get the unified level data interface
     * This is the preferred way to access level information
     */
    public LevelData getLevelData() {
        return levelData;
    }

    /**
     * @deprecated Use getLevelData() instead
     */
    @Deprecated
    public int getStatPoints() { return getAvailableBenefits(); }

    // ====================
    // COMPONENT LIFECYCLE
    // ====================

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        levelData.readFromNbt(nbtCompound, wrapperLookup);
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        levelData.writeToNbt(nbtCompound, wrapperLookup);
    }

    public void sync() {
        ModEntityComponents.LIVINGLEVEL.sync(this.living);
    }
}