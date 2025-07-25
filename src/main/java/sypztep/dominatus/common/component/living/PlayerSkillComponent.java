package sypztep.dominatus.common.component.living;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.skill.active.ActiveSkill;
import sypztep.dominatus.common.system.skill.active.ActiveSkillRegistry;
import sypztep.dominatus.common.system.skill.active.SkillResult;
import sypztep.dominatus.common.system.skill.active.UpgradeableSkill;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerSkillComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private final Map<Identifier, Integer> skillLevels = new HashMap<>(); // Skill ID -> Level
    private final Identifier[] hotbarSlots = new Identifier[6];
    private final long[] cooldownEndTimes = new long[6];

    public PlayerSkillComponent(PlayerEntity player) {
        this.player = player;
    }

    // === SKILL LEARNING WITH LEVELS ===

    /**
     * Learn a skill at level 1
     */
    public boolean learnSkill(Identifier skillId) {
        ActiveSkill skill = ActiveSkillRegistry.getSkill(skillId);
        if (skill == null) return false;

        if (skillLevels.containsKey(skillId)) return false; // Already learned

        if (!skill.canLearn(player)) return false;

        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
        if (!classComp.spendClassPoints(skill.getLearnCost())) {
            return false;
        }

        // Learn at level 1
        skillLevels.put(skillId, 1);
        ModEntityComponents.PLAYERSKILL.sync(player);

        return true;
    }

    /**
     * Upgrade a skill to the next level
     */
    public boolean upgradeSkill(Identifier skillId) {
        ActiveSkill skill = ActiveSkillRegistry.getSkill(skillId);
        if (!(skill instanceof UpgradeableSkill upgradeableSkill)) return false;

        int currentLevel = skillLevels.getOrDefault(skillId, 0);
        if (currentLevel <= 0) return false; // Not learned yet

        if (!upgradeableSkill.canUpgrade(player, currentLevel)) return false;

        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
        int upgradeCost = upgradeableSkill.getUpgradeCost(currentLevel);

        if (!classComp.spendClassPoints(upgradeCost)) {
            return false;
        }

        // Upgrade to next level
        skillLevels.put(skillId, currentLevel + 1);
        ModEntityComponents.PLAYERSKILL.sync(player);

        return true;
    }

    /**
     * Get skill level (0 if not learned)
     */
    public int getSkillLevel(Identifier skillId) {
        return skillLevels.getOrDefault(skillId, 0);
    }


    /**
     * Check if skill is learned
     */
    public boolean hasLearnedSkill(Identifier skillId) {
        return skillLevels.containsKey(skillId) && skillLevels.get(skillId) > 0;
    }

    /**
     * Get all learned skills with their levels
     */
    public Map<ActiveSkill, Integer> getLearnedSkillsWithLevels() {
        Map<ActiveSkill, Integer> result = new HashMap<>();
        for (Map.Entry<Identifier, Integer> entry : skillLevels.entrySet()) {
            ActiveSkill skill = ActiveSkillRegistry.getSkill(entry.getKey());
            if (skill != null && entry.getValue() > 0) {
                result.put(skill, entry.getValue());
            }
        }
        return result;
    }
//    public ActiveSkill getLernedSkill(Identifier skillId) {
//
//        return ActiveSkillRegistry.getSkill(skillId);
//    }

    // === HOTBAR SYSTEM (Enhanced) ===

    public boolean assignSkillToSlot(int slot, Identifier skillId) {
        if (slot < 0 || slot >= 6) return false;

        if (skillId != null && !hasLearnedSkill(skillId)) {
            return false;
        }

        hotbarSlots[slot] = skillId;
        ModEntityComponents.PLAYERSKILL.sync(player);
        return true;
    }

    public ActiveSkill getSkillInSlot(int slot) {
        if (slot < 0 || slot >= 6) return null;

        Identifier skillId = hotbarSlots[slot];
        if (skillId == null) return null;

        return ActiveSkillRegistry.getSkill(skillId);
    }

    /**
     * Use skill in slot with level scaling
     */
    public SkillResult useSkillInSlot(int slot) {
        if (slot < 0 || slot >= 6) {
            return SkillResult.failure("Invalid slot");
        }

        ActiveSkill skill = getSkillInSlot(slot);
        if (skill == null) {
            return SkillResult.failure("No skill in slot");
        }

        Identifier skillId = hotbarSlots[slot];
        int skillLevel = getSkillLevel(skillId);

        // Check cooldown
        long currentTime = System.currentTimeMillis();
        if (cooldownEndTimes[slot] > currentTime) {
            long remainingMs = cooldownEndTimes[slot] - currentTime;
            return SkillResult.failure("Cooldown: " + String.format("%.1f", remainingMs / 1000.0f) + "s");
        }

        // Execute skill with level scaling
        SkillResult result;
        if (skill instanceof UpgradeableSkill upgradeableSkill) {
            result = upgradeableSkill.executeWithLevel(player, skillLevel);

            // Apply scaled cooldown if successful
            if (result.success()) {
                int scaledCooldown = upgradeableSkill.getScaledCooldown(skillLevel);
                cooldownEndTimes[slot] = currentTime + (scaledCooldown * 50L);
            }
        } else {
            result = skill.execute(player);

            if (result.success()) {
                cooldownEndTimes[slot] = currentTime + (skill.getCooldownTicks() * 50L);
            }
        }

        return result;
    }

    // === COOLDOWN SYSTEM (Same as before) ===

    public boolean isSlotOnCooldown(int slot) {
        if (slot < 0 || slot >= 6) return false;
        return cooldownEndTimes[slot] > System.currentTimeMillis();
    }

    public long getCooldownRemaining(int slot) {
        if (slot < 0 || slot >= 6) return 0;

        long currentTime = System.currentTimeMillis();
        return Math.max(0, cooldownEndTimes[slot] - currentTime);
    }

    public float getCooldownPercentage(int slot) {
        ActiveSkill skill = getSkillInSlot(slot);
        if (skill == null) return 0.0f;

        Identifier skillId = hotbarSlots[slot];
        int skillLevel = getSkillLevel(skillId);

        long totalCooldownMs;
        if (skill instanceof UpgradeableSkill upgradeableSkill) {
            totalCooldownMs = upgradeableSkill.getScaledCooldown(skillLevel) * 50L;
        } else {
            totalCooldownMs = skill.getCooldownTicks() * 50L;
        }

        long remainingMs = getCooldownRemaining(slot);

        if (totalCooldownMs <= 0) return 0.0f;

        return (float) remainingMs / totalCooldownMs;
    }

    public void resetSkills() {
        skillLevels.clear();
        clearHotbar();
        ModEntityComponents.PLAYERSKILL.sync(player);
    }

    public void clearHotbar() {
        Arrays.fill(hotbarSlots, null);
        Arrays.fill(cooldownEndTimes, 0);
    }

    public Identifier[] getHotbarSlots() {
        return hotbarSlots.clone();
    }
    // Add this method to PlayerSkillComponent:

    public List<ActiveSkill> getLearnedSkills() {
        return skillLevels.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // Only skills with level > 0
                .map(entry -> ActiveSkillRegistry.getSkill(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    // === NBT SERIALIZATION ===

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        skillLevels.clear();

        // Read skill levels
        if (tag.contains("SkillLevels")) {
            NbtCompound skillLevelsTag = tag.getCompound("SkillLevels");
            for (String key : skillLevelsTag.getKeys()) {
                try {
                    Identifier skillId = Identifier.tryParse(key);
                    int level = skillLevelsTag.getInt(key);
                    if (skillId != null && level > 0) {
                        skillLevels.put(skillId, level);
                    }
                } catch (Exception e) {
                    // Invalid skill ID, skip
                }
            }
        }

        // Read hotbar slots
        if (tag.contains("HotbarSlots")) {
            NbtList hotbarList = tag.getList("HotbarSlots", 8);
            for (int i = 0; i < Math.min(hotbarList.size(), 6); i++) {
                String skillIdStr = hotbarList.getString(i);
                if (!skillIdStr.isEmpty() && !skillIdStr.equals("null")) {
                    try {
                        hotbarSlots[i] = Identifier.tryParse(skillIdStr);
                    } catch (Exception e) {
                        hotbarSlots[i] = null;
                    }
                } else {
                    hotbarSlots[i] = null;
                }
            }
        }

        // Read cooldowns
        if (tag.contains("CooldownEndTimes")) {
            long[] cooldowns = tag.getLongArray("CooldownEndTimes");
            for (int i = 0; i < Math.min(cooldowns.length, 6); i++) {
                cooldownEndTimes[i] = cooldowns[i];
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        // Write skill levels
        NbtCompound skillLevelsTag = new NbtCompound();
        for (Map.Entry<Identifier, Integer> entry : skillLevels.entrySet()) {
            skillLevelsTag.putInt(entry.getKey().toString(), entry.getValue());
        }
        tag.put("SkillLevels", skillLevelsTag);

        // Write hotbar slots
        NbtList hotbarList = new NbtList();
        for (Identifier skillId : hotbarSlots) {
            hotbarList.add(NbtString.of(skillId != null ? skillId.toString() : "null"));
        }
        tag.put("HotbarSlots", hotbarList);

        // Write cooldowns
        tag.putLongArray("CooldownEndTimes", cooldownEndTimes);
    }
}
