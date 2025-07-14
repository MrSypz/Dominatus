package sypztep.dominatus.common.data;

import net.minecraft.entity.EntityType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record MobExpEntry(int expReward) {

    public static final Map<EntityType<?>, MobExpEntry> MOBEXP_MAP = new ConcurrentHashMap<>();

    public static MobExpEntry getEntry(EntityType<?> entityType) {
        return MOBEXP_MAP.get(entityType);
    }

    public static int getExpReward(EntityType<?> entityType) {
        MobExpEntry entry = MOBEXP_MAP.get(entityType);
        return entry != null ? entry.expReward() : 0;
    }

    public static boolean hasEntry(EntityType<?> entityType) {
        return MOBEXP_MAP.containsKey(entityType);
    }

    public static void addEntry(EntityType<?> entityType, MobExpEntry entry) {
        MOBEXP_MAP.put(entityType, entry);
    }

    public static void removeEntry(EntityType<?> entityType) {
        MOBEXP_MAP.remove(entityType);
    }

    public static void clearAll() {
        MOBEXP_MAP.clear();
    }
}