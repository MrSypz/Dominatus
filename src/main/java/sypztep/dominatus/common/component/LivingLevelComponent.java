package sypztep.dominatus.common.component;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.util.stats.LevelSystem;

public class LivingLevelComponent implements AutoSyncedComponent {
    private final LivingEntity living;
    private final LevelSystem levelSystem;

    public LivingLevelComponent(LivingEntity living) {
        this.living = living;
        this.levelSystem = new LevelSystem();
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        levelSystem.readFromNbt(nbtCompound);
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        levelSystem.writeToNbt(nbtCompound);
    }

    // Getter for the level system
    public LevelSystem getLevelSystem() {
        return levelSystem;
    }

    // Convenience methods
    public int getLevel() {
        return levelSystem.getLevel();
    }

    public long getXp() {
        return levelSystem.getXp();
    }

    public long getXpToNextLevel() {
        return levelSystem.getXpToNextLevel();
    }

    public double getXpPercentage() {
        return levelSystem.getXpPercentage();
    }

    public int getStatPoints() {
        return levelSystem.getStatPoints();
    }

    public void addExperience(long amount) {
        levelSystem.addExperience(amount);
        sync();
    }

    public void sync() {
        ModEntityComponents.LIVINGLEVEL.sync(this.living);
    }
}