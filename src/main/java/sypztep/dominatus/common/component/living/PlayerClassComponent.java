package sypztep.dominatus.common.component.living;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.payload.SendToastPayloadS2C;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.config.ClassLevelData;
import sypztep.dominatus.common.system.level.config.LevelConfigs;
import sypztep.dominatus.common.system.playerclass.PlayerClass;
import sypztep.dominatus.common.system.playerclass.ResourceType;

public class PlayerClassComponent implements AutoSyncedComponent, CommonTickingComponent {
    private final PlayerEntity player;
    private PlayerClass currentClass = PlayerClass.NOVICE;

    // Resource system (same as before)
    private float currentResource = 100f;
    private float maxResource = 100f;
    private ResourceType resourceType = ResourceType.MANA;
    private long lastRegenTime = System.currentTimeMillis();
    private float regenMultiplier = 1.0f;

    // CLASS LEVEL SYSTEM (NEW!)
    private final ClassLevelData classLevelData;

    public PlayerClassComponent(PlayerEntity player) {
        this.player = player;
        this.classLevelData = new ClassLevelData(LevelConfigs.CLASS_LEVEL);
        initializeResource();
    }

    /**
     * Set the player's class and RESET ALL LEVELS
     */
    public void setClass(PlayerClass newClass) {
        if (this.currentClass == newClass) return;

        PlayerClass oldClass = this.currentClass;

        // Remove old class modifiers
        oldClass.removeAttributeModifiers(player);

        // RESET CHARACTER LEVEL (base level)
        LivingLevelComponent livingComp = ModEntityComponents.LIVINGLEVEL.get(player);
        livingComp.setLevel(1);
        livingComp.setExperience(0);
        livingComp.setBenefits(0); // Reset stat points too

        // RESET CLASS LEVEL
        classLevelData.reset();

        // Set new class
        this.currentClass = newClass;

        // Apply new class modifiers
        newClass.applyAttributeModifiers(player);

        // Update resource system for new class
        updateResourceForNewClass(newClass);

        // Sync to client
        ModEntityComponents.PLAYERCLASS.sync(player);

        // Feedback message
        player.sendMessage(Text.literal("You are now a ")
                .append(newClass.getFormattedName())
                .append("! All levels have been reset."), false);

        // Send toast notification for class change
        if (player instanceof ServerPlayerEntity serverPlayer) {
//            SendToastPayloadS2C.sendClassChange(serverPlayer, oldClass, newClass);
        }

        Dominatus.LOGGER.info("Player {} changed class from {} to {} (levels reset)",
                player.getName().getString(), oldClass.name(), newClass.name());
    }

    // === CLASS EXPERIENCE METHODS (matches your ExpUtil pattern) ===

    /**
     * Award class experience (follows your ExpUtil.awardExperience pattern)
     */
    public void awardClassExperience(long amount, String source, boolean showMessage) {
        if (amount <= 0) return;

        int oldLevel = classLevelData.getLevel();
        int levelsGained = classLevelData.addExperience(amount);
        int newLevel = classLevelData.getLevel();

        // Sync after experience change
        ModEntityComponents.PLAYERCLASS.sync(player);

        if (showMessage && player instanceof ServerPlayerEntity serverPlayer) {
            if (levelsGained > 0) {
                // Class level up toast
//                SendToastPayloadS2C.sendClassLevelUp(serverPlayer, currentClass, oldLevel, newLevel);
            }
            // Class exp gain toast
//            SendToastPayloadS2C.sendClassExperience(serverPlayer, amount, source);
        }

        Dominatus.LOGGER.debug("Player {} gained {} class exp from {} (Level {} -> {})",
                player.getName().getString(), amount, source, oldLevel, newLevel);
    }

    /**
     * Convenience method for awarding class experience
     */
    public void awardClassExperience(long amount, String source) {
        awardClassExperience(amount, source, true);
    }

    /**
     * Spend class points (for learning/upgrading skills)
     */
    public boolean spendClassPoints(int amount) {
        boolean success = classLevelData.spendBenefits(amount);
        if (success) {
            ModEntityComponents.PLAYERCLASS.sync(player);
        }
        return success;
    }

    /**
     * Check if player has enough class points
     */
    public boolean hasClassPoints(int amount) {
        return classLevelData.getAvailableBenefits() >= amount;
    }

    /**
     * Get available class points
     */
    public int getAvailableClassPoints() {
        return classLevelData.getAvailableBenefits();
    }

    // === CLASS LEVEL GETTERS (read-only, no sync needed) ===

    public int getClassLevel() { return classLevelData.getLevel(); }
    public long getClassExperience() { return classLevelData.getExperience(); }
    public long getClassExperienceToNextLevel() { return classLevelData.getExperienceToNextLevel(); }
    public double getClassExperiencePercentage() { return classLevelData.getExperiencePercentage(); }
    public boolean isMaxClassLevel() { return classLevelData.isMaxLevel(); }
    public int getMaxClassLevel() { return classLevelData.getMaxLevel(); }

    /**
     * Force set class experience (for debugging/admin commands)
     */
    public void setClassExperience(long experience) {
        classLevelData.setExperience(experience);
        ModEntityComponents.PLAYERCLASS.sync(player);
    }

    /**
     * Add class points directly (for debugging/admin commands)
     */
    public void addClassPoints(int amount) {
        classLevelData.addBenefits(amount);
        ModEntityComponents.PLAYERCLASS.sync(player);
    }
    public boolean canEvolveTo(PlayerClass targetClass) {
        // TODO: Add specific requirements per class
        // For now: Class level 25+ and Character level 15+
        LivingLevelComponent livingComp = ModEntityComponents.LIVINGLEVEL.get(player);

        return targetClass != currentClass &&
                getClassLevel() >= 25 &&
                livingComp.getLevel() >= 15;
    }

    // === RESOURCE SYSTEM (same as before, keeping for reference) ===

    private void updateResourceForNewClass(PlayerClass newClass) {
        this.resourceType = newClass.getPrimaryResource();
        this.maxResource = newClass.getMaxResource();
        this.currentResource = this.maxResource;
        this.lastRegenTime = System.currentTimeMillis();
    }

    private void initializeResource() {
        this.resourceType = currentClass.getPrimaryResource();
        this.maxResource = currentClass.getMaxResource();
        this.currentResource = this.maxResource;
        this.lastRegenTime = System.currentTimeMillis();
    }

    public boolean consumeResource(float amount) {
        if (currentResource >= amount) {
            currentResource -= amount;
            ModEntityComponents.PLAYERCLASS.sync(player);
            return true;
        }
        return false;
    }

    public void addResource(float amount) {
        currentResource = Math.min(maxResource, currentResource + amount);
        ModEntityComponents.PLAYERCLASS.sync(player);
    }

    public boolean hasResource(float amount) {
        return currentResource >= amount;
    }

    public float getResourcePercentage() {
        return maxResource > 0 ? currentResource / maxResource : 0f;
    }

    /**
     * Force set resource to specific amount (for debugging/admin commands)
     */
    public void setResource(float amount) {
        currentResource = Math.max(0, Math.min(maxResource, amount));
        ModEntityComponents.PLAYERCLASS.sync(player);
    }

    /**
     * Restore resource to full (for potions, class change, etc.)
     */
    public void restoreResource() {
        currentResource = maxResource;
        ModEntityComponents.PLAYERCLASS.sync(player);
    }

    @Override
    public void tick() {
        // Resource regeneration (same as before)
        if (currentResource < maxResource) {
            long currentTime = System.currentTimeMillis();
            long timeDelta = currentTime - lastRegenTime;

            if (timeDelta >= 50) {
                float baseRegenRate = resourceType.getBaseRegenRate();
                float actualRegenRate = baseRegenRate * regenMultiplier;
                float regenAmount = actualRegenRate * (timeDelta / 1000f);

                float oldResource = currentResource;
                currentResource = Math.min(maxResource, currentResource + regenAmount);

                if (Math.abs(currentResource - oldResource) >= 0.5f) {
                    ModEntityComponents.PLAYERCLASS.sync(player);
                }

                lastRegenTime = currentTime;
            }
        } else {
            lastRegenTime = System.currentTimeMillis();
        }
    }

    // === GETTERS ===

    public PlayerClass getCurrentClass() { return currentClass; }
    public ResourceType getResourceType() { return resourceType; }
    public float getCurrentResource() { return currentResource; }
    public float getMaxResource() { return maxResource; }
    public float getRegenMultiplier() { return regenMultiplier; }
    public void setRegenMultiplier(float multiplier) {
        this.regenMultiplier = Math.max(0.1f, multiplier);
    }

    // === NBT SERIALIZATION (handles both resources and class levels) ===

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        // Read class
        if (tag.contains("PlayerClass")) {
            try {
                String className = tag.getString("PlayerClass");
                this.currentClass = PlayerClass.valueOf(className);
            } catch (IllegalArgumentException e) {
                Dominatus.LOGGER.warn("Invalid player class in NBT: {}", tag.getString("PlayerClass"));
                this.currentClass = PlayerClass.NOVICE;
            }
        }

        // Read class level data (uses the corrected NBT methods)
        classLevelData.readFromNbt(tag, registryLookup);

        // Read resource data
        this.resourceType = currentClass.getPrimaryResource();
        this.maxResource = tag.contains("MaxResource") ?
                tag.getFloat("MaxResource") : currentClass.getMaxResource();
        this.currentResource = tag.contains("CurrentResource") ?
                tag.getFloat("CurrentResource") : maxResource;
        this.regenMultiplier = tag.contains("RegenMultiplier") ?
                tag.getFloat("RegenMultiplier") : 1.0f;

        this.lastRegenTime = System.currentTimeMillis();

        // Apply class modifiers after loading
        currentClass.applyAttributeModifiers(player);
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putString("PlayerClass", currentClass.name());

        // Write class level data (uses the corrected NBT methods)
        classLevelData.writeToNbt(tag, registryLookup);

        // Write resource data
        tag.putFloat("MaxResource", maxResource);
        tag.putFloat("CurrentResource", currentResource);
        tag.putFloat("RegenMultiplier", regenMultiplier);
    }
}
