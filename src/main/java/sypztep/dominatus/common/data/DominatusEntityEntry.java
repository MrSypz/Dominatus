package sypztep.dominatus.common.data;

import net.minecraft.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public record DominatusEntityEntry(double accuracy, double evasion, double critChance, double critDamage, double backAttack, double airAttack, double downAttack) {
    public static final Map<EntityType<?>, DominatusEntityEntry> BASEMOBSTATS_MAP = new HashMap<>();
}
