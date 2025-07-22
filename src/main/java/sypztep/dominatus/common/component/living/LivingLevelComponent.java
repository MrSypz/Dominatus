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
import sypztep.dominatus.common.system.skill.PassiveSkillManager;
import sypztep.dominatus.common.system.stat.EntityStatManager;
import sypztep.dominatus.common.system.stat.PlayerStatManager;

public class LivingLevelComponent implements AutoSyncedComponent {
    private final LivingEntity living;
    private final LevelData levelData;

    // Separate stat managers for different entity types
    private final EntityStatManager entityStatManager;
    private final PlayerStatManager playerStatManager;

    // Passive skill system (only for players)
    private final PassiveSkillManager passiveSkillManager;

    private final boolean isPlayer;

    public LivingLevelComponent(LivingEntity living) {
        this.living = living;
        this.isPlayer = living instanceof PlayerEntity;

        if (isPlayer) {
            this.levelData = new PlayerLevelData(LevelConfigs.CHARACTER);
            this.playerStatManager = new PlayerStatManager();
            this.entityStatManager = null;
            this.passiveSkillManager = new PassiveSkillManager();
        } else {
            this.levelData = new EntityLevelData(LevelConfigs.CHARACTER);
            this.entityStatManager = new EntityStatManager();
            this.playerStatManager = null;
            this.passiveSkillManager = null;
        }
    }

    // ====================
    // UNIFIED ACCESS METHODS
    // ====================

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
    // STAT MANAGER ACCESS
    // ====================

    public EntityStatManager getEntityStatManager() {
        return entityStatManager;
    }

    public PlayerStatManager getPlayerStatManager() {
        return playerStatManager;
    }

    // ====================
    // PASSIVE SKILL SYSTEM ACCESS
    // ====================

    public PassiveSkillManager getPassiveSkillManager() {
        return passiveSkillManager;
    }

    /**
     * Check for new passive unlocks when a stat changes
     */
    public void checkPassiveUnlocks(String statType, int newStatValue) {
        if (isPlayer && passiveSkillManager != null) {
            passiveSkillManager.checkForNewUnlocks(living, statType, newStatValue);
        }
    }

    public void applyAllStatEffects() {
        if (isPlayer && playerStatManager != null) {
            playerStatManager.applyAllEffects(living);

            // Apply passive abilities
            if (passiveSkillManager != null) {
                passiveSkillManager.applyAllPassives(living);
            }
        } else if (!isPlayer && entityStatManager != null) {
            entityStatManager.applyAllEffects(living);
        }
    }

    /**
     * Remove all stat effects (for respawn, etc.)
     */
    public void removeAllStatEffects() {
        if (isPlayer && passiveSkillManager != null) {
            passiveSkillManager.removeAllPassives(living);
        }
    }

    // ====================
    // DIRECT ACCESS TO LEVEL DATA
    // ====================

    public LevelData getLevelData() {
        return levelData;
    }

    @Deprecated
    public int getStatPoints() { return getAvailableBenefits(); }

    // ====================
    // COMPONENT LIFECYCLE
    // ====================

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        levelData.readFromNbt(nbtCompound, wrapperLookup);

        if (isPlayer && playerStatManager != null) {
            playerStatManager.readFromNbt(nbtCompound);

            // Load passive skills
            if (passiveSkillManager != null) {
                passiveSkillManager.readFromNbt(nbtCompound);
            }
        } else if (!isPlayer && entityStatManager != null) {
            entityStatManager.readFromNbt(nbtCompound);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        levelData.writeToNbt(nbtCompound, wrapperLookup);

        if (isPlayer && playerStatManager != null) {
            playerStatManager.writeToNbt(nbtCompound);

            // Save passive skills
            if (passiveSkillManager != null) {
                passiveSkillManager.writeToNbt(nbtCompound);
            }
        } else if (!isPlayer && entityStatManager != null) {
            entityStatManager.writeToNbt(nbtCompound);
        }
    }

    public void sync() {
        ModEntityComponents.LIVINGLEVEL.sync(this.living);
    }
}