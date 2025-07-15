package sypztep.dominatus.common.component.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.util.level.CharacterLevelSystem;
import sypztep.dominatus.common.util.level.LevelSystem;
import sypztep.dominatus.common.util.level.LevelConfigs;

/**
 * Level component for all living entities.
 * - Players: Get CharacterLevelSystem (levels + benefits)
 * - Monsters: Get basic LevelSystem (levels only)
 */
public class LivingLevelComponent implements AutoSyncedComponent {
    private final LivingEntity living;
    private final LevelSystem monsterLevelSystem;
    private final CharacterLevelSystem playerLevelSystem;
    private final boolean isPlayer;

    public LivingLevelComponent(LivingEntity living) {
        this.living = living;
        this.isPlayer = living instanceof PlayerEntity;

        if (isPlayer) {
            this.playerLevelSystem = new CharacterLevelSystem();
            this.monsterLevelSystem = null;
        } else {
            this.monsterLevelSystem = new LevelSystem(LevelConfigs.CHARACTER);
            this.playerLevelSystem = null;
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (isPlayer) {
            playerLevelSystem.readFromNbt(nbtCompound);
        } else {
            monsterLevelSystem.readFromNbt(nbtCompound, "");
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (isPlayer) {
            playerLevelSystem.writeToNbt(nbtCompound);
        } else {
            monsterLevelSystem.writeToNbt(nbtCompound, "");
        }
    }

    public int getLevel() {
        return isPlayer ? playerLevelSystem.getLevel() : monsterLevelSystem.getLevel();
    }

    public long getExperience() {
        return isPlayer ? playerLevelSystem.getExperience() : monsterLevelSystem.getExperience();
    }

    public long getExperienceToNextLevel() {
        return isPlayer ? playerLevelSystem.getExperienceToNextLevel() : monsterLevelSystem.getExperienceToNextLevel();
    }

    public double getExperiencePercentage() {
        return isPlayer ? playerLevelSystem.getExperiencePercentage() : monsterLevelSystem.getExperiencePercentage();
    }

    public boolean isMaxLevel() {
        return isPlayer ? playerLevelSystem.isMaxLevel() : monsterLevelSystem.isMaxLevel();
    }

    public void addExperience(long amount) {
        if (isPlayer) playerLevelSystem.addExperience(amount);
        else monsterLevelSystem.addExperience(amount);
        sync();
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public int getAvailableBenefits() {
        return isPlayer ? playerLevelSystem.getAvailableBenefits() : 0;
    }

    public boolean spendBenefits(int amount) {
        if (isPlayer) {
            boolean success = playerLevelSystem.spendBenefits(amount);
            if (success) sync();
            return success;
        }
        return false;
    }

    public LevelSystem getMonsterLevelSystem() {
        return monsterLevelSystem;
    }

    public CharacterLevelSystem getPlayerLevelSystem() {
        return playerLevelSystem;
    }

    @Deprecated
    public int getStatPoints() {
        return getAvailableBenefits();
    }

    public void sync() {
        ModEntityComponents.LIVINGLEVEL.sync(this.living);
    }
}