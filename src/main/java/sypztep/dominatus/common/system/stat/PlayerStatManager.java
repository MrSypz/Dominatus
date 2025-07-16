package sypztep.dominatus.common.system.stat;


import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
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

    // Convenience getters
    public PlayerStrengthStat getStrength() { return (PlayerStrengthStat) stats.get("strength"); }
    public PlayerAgilityStat getAgility() { return (PlayerAgilityStat) stats.get("agility"); }
    public PlayerVitalityStat getVitality() { return (PlayerVitalityStat) stats.get("vitality"); }
    public PlayerIntelligenceStat getIntelligence() { return (PlayerIntelligenceStat) stats.get("intelligence"); }
    public PlayerDexterityStat getDexterity() { return (PlayerDexterityStat) stats.get("dexterity"); }
    public PlayerLuckStat getLuck() { return (PlayerLuckStat) stats.get("luck"); }

    public Collection<StatUI> getUIStats() {
        return stats.values().stream()
                .filter(stat -> stat instanceof StatUI)
                .map(stat -> (StatUI) stat)
                .toList();
    }

    public void applyAllEffects(LivingEntity entity) {
        for (PlayerStatBehavior stat : stats.values()) {
            if (stat instanceof Stat baseStat) {
                baseStat.applyPrimaryEffect(entity);
                baseStat.applySecondaryEffect(entity);
            }
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

    public void writeToNbt(NbtCompound tag) {
        NbtCompound statsTag = new NbtCompound();
        for (Map.Entry<String, PlayerStatBehavior> entry : stats.entrySet()) {
            if (entry.getValue() instanceof Stat baseStat) {
                NbtCompound statTag = new NbtCompound();
                baseStat.writeToNbt(statTag);
                statsTag.put(entry.getKey(), statTag);
            }
        }
        tag.put("Stats", statsTag);
    }

    public void readFromNbt(NbtCompound tag) {
        if (tag.contains("Stats")) {
            NbtCompound statsTag = tag.getCompound("Stats");
            for (Map.Entry<String, PlayerStatBehavior> entry : stats.entrySet()) {
                String statName = entry.getKey();
                if (statsTag.contains(statName) && entry.getValue() instanceof Stat baseStat) {
                    NbtCompound statTag = statsTag.getCompound(statName);
                    baseStat.readFromNbt(statTag);
                }
            }
        }
    }
}