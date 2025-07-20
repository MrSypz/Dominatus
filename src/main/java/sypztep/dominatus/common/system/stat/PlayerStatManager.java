package sypztep.dominatus.common.system.stat;


import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import sypztep.dominatus.common.system.stat.elements.core.*;
import sypztep.dominatus.common.system.stat.elements.player.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayerStatManager {
    private final Map<String, PlayerStatBehavior> stats = new HashMap<>();

    public PlayerStatManager() {
        stats.put("strength", new PlayerStrengthStat());
        stats.put("agility", new PlayerAgilityStat());
        stats.put("vitality", new PlayerVitalityStat());
        stats.put("intelligence", new PlayerIntelligenceStat());
        stats.put("dexterity", new PlayerDexterityStat());
        stats.put("luck", new PlayerLuckStat());
    }

    public PlayerStatBehavior getStat(String name) {
        return stats.get(name);
    }

    public PlayerStatBehavior getStrength() { return stats.get("strength"); }
    public PlayerStatBehavior getAgility() { return stats.get("agility"); }
    public PlayerStatBehavior getVitality() { return stats.get("vitality"); }
    public PlayerStatBehavior getIntelligence() { return stats.get("intelligence"); }
    public PlayerStatBehavior getDexterity() { return stats.get("dexterity"); }
    public PlayerStatBehavior getLuck() { return stats.get("luck"); }

    public void applyAllEffects(LivingEntity entity) {
        for (PlayerStatBehavior stat : stats.values()) {
            stat.applyPrimaryEffect(entity);    // No casting needed!
            stat.applySecondaryEffect(entity);
        }
    }
    public void resetAllStats(ServerPlayerEntity player) {
        for (PlayerStatBehavior stat : stats.values()) {
            stat.resetWithRefund(player);
        }
    }

    public int getTotalPointsSpent() {
        return stats.values().stream()
                .mapToInt(PlayerStatBehavior::getTotalPointsSpent)
                .sum();
    }
    public PlayerStatBehavior getStatByName(String statName) {
        return switch (statName.toLowerCase()) {
            case "strength" -> getStrength();
            case "agility" -> getAgility();
            case "vitality" -> getVitality();
            case "intelligence" -> getIntelligence();
            case "dexterity" -> getDexterity();
            case "luck" -> getLuck();
            default -> null;
        };
    }

    public void writeToNbt(NbtCompound tag) {
        NbtCompound statsTag = new NbtCompound();
        for (Map.Entry<String, PlayerStatBehavior> entry : stats.entrySet()) {
            NbtCompound statTag = new NbtCompound();
            entry.getValue().writeToNbt(statTag); // Use the complete writeToNbt
            statsTag.put(entry.getKey(), statTag);
        }
        tag.put("Stats", statsTag);
    }

    public void readFromNbt(NbtCompound tag) {
        if (tag.contains("Stats")) {
            NbtCompound statsTag = tag.getCompound("Stats");
            for (Map.Entry<String, PlayerStatBehavior> entry : stats.entrySet()) {
                String statName = entry.getKey();
                if (statsTag.contains(statName)) {
                    NbtCompound statTag = statsTag.getCompound(statName);
                    entry.getValue().readFromNbt(statTag); // Use the complete readFromNbt
                }
            }
        }
    }
}