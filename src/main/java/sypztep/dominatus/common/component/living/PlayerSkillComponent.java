package sypztep.dominatus.common.component.living;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.skill.active.ActiveSkill;
import sypztep.dominatus.common.system.skill.active.ActiveSkillRegistry;
import sypztep.dominatus.common.system.skill.active.SkillResult;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.*;

public class PlayerSkillComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private final Set<Identifier> learnedSkills = new HashSet<>();

    // HOTBAR SYSTEM (NEW!)
    private final Identifier[] hotbarSlots = new Identifier[6]; // Keys 1-6
    private final long[] cooldownEndTimes = new long[6]; // When each slot comes off cooldown

    public PlayerSkillComponent(PlayerEntity player) {
        this.player = player;
    }

    // === SKILL LEARNING (from Phase 2) ===

    public boolean learnSkill(Identifier skillId) {
        ActiveSkill skill = ActiveSkillRegistry.getSkill(skillId);
        if (skill == null) return false;

        if (learnedSkills.contains(skillId)) return false;

        if (!skill.canLearn(player)) return false;

        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
        if (!classComp.spendClassPoints(skill.getLearnCost())) {
            return false;
        }

        learnedSkills.add(skillId);
        ModEntityComponents.PLAYERSKILL.sync(player);

        return true;
    }

    public boolean hasLearnedSkill(Identifier skillId) {
        return learnedSkills.contains(skillId);
    }

    public List<ActiveSkill> getLearnedSkills() {
        return learnedSkills.stream()
                .map(ActiveSkillRegistry::getSkill)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void resetSkills() {
        learnedSkills.clear();
        clearHotbar(); // Also clear hotbar when resetting skills
        ModEntityComponents.PLAYERSKILL.sync(player);
    }

    // === HOTBAR MANAGEMENT (NEW!) ===

    /**
     * Assign a skill to a hotbar slot (0-5 for keys 1-6)
     */
    public boolean assignSkillToSlot(int slot, Identifier skillId) {
        if (slot < 0 || slot >= 6) return false;

        // Check if skill is learned
        if (skillId != null && !learnedSkills.contains(skillId)) {
            return false;
        }

        hotbarSlots[slot] = skillId; // null to clear slot
        ModEntityComponents.PLAYERSKILL.sync(player);
        return true;
    }

    /**
     * Get skill assigned to slot (null if empty)
     */
    public ActiveSkill getSkillInSlot(int slot) {
        if (slot < 0 || slot >= 6) return null;

        Identifier skillId = hotbarSlots[slot];
        if (skillId == null) return null;

        return ActiveSkillRegistry.getSkill(skillId);
    }

    /**
     * Use skill in the specified slot
     */
    public SkillResult useSkillInSlot(int slot) {
        if (slot < 0 || slot >= 6) {
            return SkillResult.failure("Invalid slot");
        }

        ActiveSkill skill = getSkillInSlot(slot);
        if (skill == null) {
            return SkillResult.failure("No skill in slot");
        }

        // Check cooldown
        long currentTime = System.currentTimeMillis();
        if (cooldownEndTimes[slot] > currentTime) {
            long remainingMs = cooldownEndTimes[slot] - currentTime;
            return SkillResult.failure("Cooldown: " + (remainingMs / 1000.0f) + "s");
        }

        // Execute skill
        SkillResult result = skill.execute(player);

        // Apply cooldown if skill was successful
        if (result.success()) {
            cooldownEndTimes[slot] = currentTime + (skill.getCooldownTicks() * 50L); // Convert ticks to ms
        }

        return result;
    }

    /**
     * Check if slot is on cooldown
     */
    public boolean isSlotOnCooldown(int slot) {
        if (slot < 0 || slot >= 6) return false;
        return cooldownEndTimes[slot] > System.currentTimeMillis();
    }
    /**
     * Use a skill
     */
    public SkillResult useSkill(Identifier skillId) {
        ActiveSkill skill = ActiveSkillRegistry.getSkill(skillId);
        if (skill == null) {
            return SkillResult.failure("Unknown skill");
        }

        if (!learnedSkills.contains(skillId)) {
            return SkillResult.failure("Skill not learned");
        }

        return skill.execute(player);
    }
    /**
     * Get remaining cooldown in milliseconds
     */
    public long getCooldownRemaining(int slot) {
        if (slot < 0 || slot >= 6) return 0;

        long currentTime = System.currentTimeMillis();
        return Math.max(0, cooldownEndTimes[slot] - currentTime);
    }

    /**
     * Get cooldown percentage (0.0 = ready, 1.0 = just used)
     */
    public float getCooldownPercentage(int slot) {
        ActiveSkill skill = getSkillInSlot(slot);
        if (skill == null) return 0.0f;

        long totalCooldownMs = skill.getCooldownTicks() * 50L;
        long remainingMs = getCooldownRemaining(slot);

        if (totalCooldownMs <= 0) return 0.0f;

        return (float) remainingMs / totalCooldownMs;
    }

    /**
     * Clear all hotbar slots
     */
    public void clearHotbar() {
        Arrays.fill(hotbarSlots, null);
        Arrays.fill(cooldownEndTimes, 0);
    }

    /**
     * Get all hotbar assignments (for UI)
     */
    public Identifier[] getHotbarSlots() {
        return hotbarSlots.clone();
    }

    // === NBT SERIALIZATION ===

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        learnedSkills.clear();

        // Read learned skills
        if (tag.contains("LearnedSkills")) {
            NbtList skillList = tag.getList("LearnedSkills", 8); // STRING type
            for (int i = 0; i < skillList.size(); i++) {
                String skillIdStr = skillList.getString(i);
                try {
                    Identifier skillId = Identifier.tryParse(skillIdStr);
                    if (skillId != null) {
                        learnedSkills.add(skillId);
                    }
                } catch (Exception e) {
                    // Invalid skill ID, skip
                }
            }
        }

        // Read hotbar slots
        if (tag.contains("HotbarSlots")) {
            NbtList hotbarList = tag.getList("HotbarSlots", 8); // STRING type
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
        // Write learned skills
        NbtList skillList = new NbtList();
        for (Identifier skillId : learnedSkills) {
            skillList.add(NbtString.of(skillId.toString()));
        }
        tag.put("LearnedSkills", skillList);

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
