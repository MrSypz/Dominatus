package sypztep.dominatus.common.system.stat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import sypztep.dominatus.common.system.stat.elements.core.*;

import java.util.HashMap;
import java.util.Map;

public class EntityStatManager {
    private final Map<String, Stat> stats = new HashMap<>();

    public EntityStatManager() {
        stats.put("strength", new StrengthStat());
        stats.put("agility", new AgilityStat());
        stats.put("vitality", new VitalityStat());
        stats.put("intelligence", new IntelligenceStat());
        stats.put("dexterity", new DexterityStat());
        stats.put("luck", new LuckStat());
    }

    public Stat getStat(String name) {
        return stats.get(name);
    }

    // Convenience getters
    public StrengthStat getStrength() { return (StrengthStat) stats.get("strength"); }
    public AgilityStat getAgility() { return (AgilityStat) stats.get("agility"); }
    public VitalityStat getVitality() { return (VitalityStat) stats.get("vitality"); }
    public IntelligenceStat getIntelligence() { return (IntelligenceStat) stats.get("intelligence"); }
    public DexterityStat getDexterity() { return (DexterityStat) stats.get("dexterity"); }
    public LuckStat getLuck() { return (LuckStat) stats.get("luck"); }

    public void applyAllEffects(LivingEntity entity) {
        for (Stat stat : stats.values()) {
            stat.applyPrimaryEffect(entity);
            stat.applySecondaryEffect(entity);
        }
    }

    public void writeToNbt(NbtCompound tag) {
        NbtCompound statsTag = new NbtCompound();
        for (Map.Entry<String, Stat> entry : stats.entrySet()) {
            NbtCompound statTag = new NbtCompound();
            entry.getValue().writeToNbt(statTag);
            statsTag.put(entry.getKey(), statTag);
        }
        tag.put("Stats", statsTag);
    }

    public void readFromNbt(NbtCompound tag) {
        if (tag.contains("Stats")) {
            NbtCompound statsTag = tag.getCompound("Stats");
            for (Map.Entry<String, Stat> entry : stats.entrySet()) {
                String statName = entry.getKey();
                if (statsTag.contains(statName)) {
                    NbtCompound statTag = statsTag.getCompound(statName);
                    entry.getValue().readFromNbt(statTag);
                }
            }
        }
    }
}
