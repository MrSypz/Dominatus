package sypztep.dominatus.common.component.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageTrackerComponent implements Component {
    private final LivingEntity entity;

    private final Map<UUID, Float> damageMap = new HashMap<>();
    private float maxHealth = 0f;

    public DamageTrackerComponent(LivingEntity entity) {
        this.entity = entity;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    public float getMaxHealth() {
        return this.maxHealth;
    }

    public void addDamage(PlayerEntity player, float damage) {
        UUID playerId = player.getUuid();
        damageMap.put(playerId, damageMap.getOrDefault(playerId, 0f) + damage);
    }

    public float getDamage(PlayerEntity player) {
        return damageMap.getOrDefault(player.getUuid(), 0f);
    }

    public float getDamagePercentage(PlayerEntity player) {
        if (maxHealth <= 0) return 0f;
        float damage = getDamage(player);
        return Math.min(damage / maxHealth, 1.0f); // Cap at 100%
    }

    public Map<UUID, Float> getAllDamage() {
        return new HashMap<>(damageMap);
    }

    public void clearDamage() {
        damageMap.clear();
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        damageMap.clear();
        maxHealth = nbtCompound.getFloat("maxHealth");

        if (nbtCompound.contains("damageMap", NbtElement.LIST_TYPE)) {
            NbtList damageList = nbtCompound.getList("damageMap", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < damageList.size(); i++) {
                NbtCompound entry = damageList.getCompound(i);
                UUID playerId = entry.getUuid("playerId");
                float damage = entry.getFloat("damage");
                damageMap.put(playerId, damage);
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putFloat("maxHealth", maxHealth);

        NbtList damageList = new NbtList();
        for (Map.Entry<UUID, Float> entry : damageMap.entrySet()) {
            NbtCompound entryTag = new NbtCompound();
            entryTag.putUuid("playerId", entry.getKey());
            entryTag.putFloat("damage", entry.getValue());
            damageList.add(entryTag);
        }
        nbtCompound.put("damageMap", damageList);
    }
}