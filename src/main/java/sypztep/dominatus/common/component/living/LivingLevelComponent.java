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
import sypztep.dominatus.common.system.skill.PassiveAbility;
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

    // Read-only methods (no sync needed)
    public int getLevel() { return levelData.getLevel(); }
    public long getExperience() { return levelData.getExperience(); }
    public long getExperienceToNextLevel() { return levelData.getExperienceToNextLevel(); }
    public double getExperiencePercentage() { return levelData.getExperiencePercentage(); }
    public boolean isMaxLevel() { return levelData.isMaxLevel(); }
    public int getMaxLevel() { return levelData.getMaxLevel(); }
    public int getAvailableBenefits() { return levelData.getAvailableBenefits(); }
    public boolean isPlayer() { return levelData.isPlayer(); }
    public LevelData getLevelData() { return levelData; }
    @Deprecated
    public int getStatPoints() { return getAvailableBenefits(); }

    public void setLevel(int level) {
        levelData.setLevel(level);
        sync();
    }

    public void setExperience(long experience) {
        levelData.setExperience(experience);
        sync();
    }

    public int addExperience(long amount) {
        int levelsGained = levelData.addExperience(amount);
        sync();
        return levelsGained;
    }

    public boolean spendBenefits(int amount) {
        boolean success = levelData.spendBenefits(amount);
        if (success) sync();
        return success;
    }

    public void addBenefits(int amount) {
        levelData.addBenefits(amount);
        sync();
    }

    public void setBenefits(int amount) {
        levelData.setBenefits(amount);
        sync();
    }

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
    // PASSIVE SKILL SYSTEM ACCESS WITH AUTO-SYNC
    // ====================

    public PassiveSkillManager getPassiveSkillManager() {
        return passiveSkillManager;
    }

    /**
     * Check for new passive unlocks when a stat changes - AUTO-SYNC
     */
    public void checkPassiveUnlocks(String statType, int newStatValue) {
        if (isPlayer && passiveSkillManager != null) {
            passiveSkillManager.checkForNewUnlocks(living, statType, newStatValue);
        }
        sync();
    }

    /**
     * Unlock a new passive ability - AUTO-SYNC
     */
    public boolean unlockPassive(PassiveAbility passive) {
        if (!isPlayer || passiveSkillManager == null) return false;

        boolean success = passiveSkillManager.unlockPassive(living, passive);
        if (success) sync();
        return success;
    }

    public void applyAllStatEffects() {
        if (isPlayer && playerStatManager != null) {
            playerStatManager.applyAllEffects(living);

            if (passiveSkillManager != null) {
                passiveSkillManager.applyAllPassives(living);
            }
        } else if (!isPlayer && entityStatManager != null) {
            entityStatManager.applyAllEffects(living);
        }
        sync();
    }

    /**
     * Reset all passive skills - AUTO-SYNC
     */
    public void resetAllPassiveSkills() {
        if (isPlayer && passiveSkillManager != null) {
            passiveSkillManager.removeAllPassives(living);
            passiveSkillManager.getUnlockedPassives().clear();
            if (playerStatManager != null) {
                playerStatManager.applyAllEffects(living);
            }
        }
        sync();
    }

    // ====================
    // BATCH OPERATIONS (SINGLE SYNC)
    // ====================

    public void performBatchUpdate(Runnable updates) {
        updates.run();
        sync();
    }

    /**
     * Level up and refresh all effects in one operation
     */
    public void levelUpAndRefresh(int newLevel) {
        performBatchUpdate(() -> {
            levelData.setLevel(newLevel);
            refreshAllStatEffectsInternal();
        });
    }

    public void handleRespawn() {
        performBatchUpdate(this::refreshAllStatEffectsInternal);
    }

    // ====================
    // INTERNAL METHODS (NO SYNC) - for batch operations
    // ====================

    public void refreshAllStatEffectsInternal() {
        if (isPlayer && passiveSkillManager != null) {
            passiveSkillManager.removeAllPassives(living);
        }

        // Apply effects
        if (isPlayer && playerStatManager != null) {
            playerStatManager.applyAllEffects(living);

            if (passiveSkillManager != null) {
                passiveSkillManager.applyAllPassives(living);
            }
        } else if (!isPlayer && entityStatManager != null) {
            entityStatManager.applyAllEffects(living);
        }
    }

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