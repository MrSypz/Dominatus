package sypztep.dominatus.common.system.skill;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.client.payload.SendToastPayloadS2C;

import java.util.*;

/**
 * Manages passive abilities for players based on stat progression
 */
public class PassiveSkillManager {
    private final Map<Identifier, PassiveAbility> unlockedPassives = new HashMap<>();
    private final Map<String, List<PassiveAbility>> passivesByStatType = new HashMap<>();

    public PassiveSkillManager() {
        initializePassiveCategories();
    }

    private void initializePassiveCategories() {
        passivesByStatType.put("strength", new ArrayList<>());
        passivesByStatType.put("agility", new ArrayList<>());
        passivesByStatType.put("vitality", new ArrayList<>());
        passivesByStatType.put("intelligence", new ArrayList<>());
        passivesByStatType.put("dexterity", new ArrayList<>());
        passivesByStatType.put("luck", new ArrayList<>());
    }

    /**
     * Check for new passive abilities to unlock based on stat values
     */
    public void checkForNewUnlocks(LivingEntity entity, String statType, int newStatValue) {
        List<PassiveAbility> availablePassives = PassiveAbilityRegistry.getPassivesForStat(statType);

        for (PassiveAbility passive : availablePassives) {
            if (newStatValue >= passive.getRequiredStatValue() && !isUnlocked(passive.getId())) {
                unlockPassive(entity, passive);
            }
        }
    }

    /**
     * Unlock a new passive ability
     */
    public boolean unlockPassive(LivingEntity entity, PassiveAbility passive) {
        if (isUnlocked(passive.getId())) {
            return false; // Already unlocked
        }

        unlockedPassives.put(passive.getId(), passive);
        passivesByStatType.get(passive.getStatType()).add(passive);

        // Apply the passive effect
        passive.onUnlock(entity);
        passive.applyEffect(entity);

        // Notify player if it's a player
        if (entity instanceof ServerPlayerEntity player) {
            String message = String.format("New %s Passive Unlocked: %s",
                    passive.getStatType().toUpperCase(), passive.getName().getString());
            SendToastPayloadS2C.sendInfo(player, message);
        }

        return true;
    }

    /**
     * Apply all unlocked passive effects
     */
    public void applyAllPassives(LivingEntity entity) {
        for (PassiveAbility passive : unlockedPassives.values()) {
            if (!passive.isActive()) {
                passive.applyEffect(entity);
            }
        }
    }

    /**
     * Remove all passive effects (for respawn, etc.)
     */
    public void removeAllPassives(LivingEntity entity) {
        for (PassiveAbility passive : unlockedPassives.values()) {
            if (passive.isActive()) {
                passive.removeEffect(entity);
            }
        }
    }

    /**
     * Check if a passive is unlocked
     */
    public boolean isUnlocked(Identifier passiveId) {
        return unlockedPassives.containsKey(passiveId);
    }

    /**
     * Get all unlocked passives
     */
    public Collection<PassiveAbility> getUnlockedPassives() {
        return unlockedPassives.values();
    }

    /**
     * Get unlocked passives for a specific stat type
     */
    public List<PassiveAbility> getUnlockedPassivesForStat(String statType) {
        return passivesByStatType.getOrDefault(statType, new ArrayList<>());
    }

    /**
     * Get total number of unlocked passives
     */
    public int getTotalUnlockedCount() {
        return unlockedPassives.size();
    }

    /**
     * Get count of unlocked passives by stat type
     */
    public int getUnlockedCountForStat(String statType) {
        return passivesByStatType.getOrDefault(statType, new ArrayList<>()).size();
    }

    // ====================
    // NBT PERSISTENCE
    // ====================

    public void writeToNbt(NbtCompound tag) {
        NbtList passivesList = new NbtList();

        for (Identifier passiveId : unlockedPassives.keySet()) {
            passivesList.add(NbtString.of(passiveId.toString()));
        }

        tag.put("UnlockedPassives", passivesList);
    }

    public void readFromNbt(NbtCompound tag) {
        unlockedPassives.clear();
        for (List<PassiveAbility> list : passivesByStatType.values()) {
            list.clear();
        }

        if (tag.contains("UnlockedPassives", NbtElement.LIST_TYPE)) {
            NbtList passivesList = tag.getList("UnlockedPassives", NbtElement.STRING_TYPE);

            for (int i = 0; i < passivesList.size(); i++) {
                String passiveIdStr = passivesList.getString(i);
                Identifier passiveId = Identifier.tryParse(passiveIdStr);

                if (passiveId != null) {
                    PassiveAbility passive = PassiveAbilityRegistry.getPassive(passiveId);
                    if (passive != null) {
                        unlockedPassives.put(passiveId, passive);
                        passivesByStatType.get(passive.getStatType()).add(passive);
                    }
                }
            }
        }
    }

    // ====================
    // UTILITY METHODS
    // ====================

    /**
     * Get progress towards next passive unlock for a stat
     */
    public PassiveAbility getNextPassiveForStat(String statType, int currentStatValue) {
        List<PassiveAbility> availablePassives = PassiveAbilityRegistry.getPassivesForStat(statType);

        return availablePassives.stream()
                .filter(passive -> !isUnlocked(passive.getId()))
                .filter(passive -> passive.getRequiredStatValue() > currentStatValue)
                .min(Comparator.comparingInt(PassiveAbility::getRequiredStatValue))
                .orElse(null);
    }

    /**
     * Create tooltip information for stat progression
     */
    public List<Text> createStatProgressionTooltip(String statType, int currentStatValue) {
        List<Text> tooltip = new ArrayList<>();

        List<PassiveAbility> unlockedForStat = getUnlockedPassivesForStat(statType);
        PassiveAbility nextPassive = getNextPassiveForStat(statType, currentStatValue);

        if (!unlockedForStat.isEmpty()) {
            tooltip.add(Text.literal("§6Unlocked Passives:"));
            for (PassiveAbility passive : unlockedForStat) {
                tooltip.add(Text.literal("  §a✓ ").append(passive.getName()).append(Text.literal(" §7(Lv." + passive.getRequiredStatValue() + ")")));
            }
        }

        if (nextPassive != null) {
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("§eNext Unlock:"));
            tooltip.add(Text.literal("  §7" + nextPassive.getName().getString() + " §7(Lv." + nextPassive.getRequiredStatValue() + ")"));
            tooltip.add(Text.literal("  §8" + nextPassive.getDescription().getString()));
        }

        return tooltip;
    }
}