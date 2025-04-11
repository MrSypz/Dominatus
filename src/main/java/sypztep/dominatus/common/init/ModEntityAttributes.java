package sypztep.dominatus.common.init;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import sypztep.dominatus.Dominatus;

import java.util.ArrayList;

public final class ModEntityAttributes {
    public static final ArrayList<RegistryEntry<EntityAttribute>> ENTRIES = new ArrayList<>();

    public static final RegistryEntry<EntityAttribute> ACCURACY = register("accuracy", new ClampedEntityAttribute("attribute.name.accuracy", 50, 0.0, 2048.0D).setTracked(true));
    public static final RegistryEntry<EntityAttribute> EVASION = register("evasion", new ClampedEntityAttribute("attribute.name.evasion", 20, 0.0, 2048.0D).setTracked(true));;
    public static final RegistryEntry<EntityAttribute> CRIT_DAMAGE = register("crit_damage", new ClampedEntityAttribute("attribute.name.crit_damage", 0.5, 0.0, 10.24D).setTracked(true));
    public static final RegistryEntry<EntityAttribute> CRIT_CHANCE = register("crit_chance", new ClampedEntityAttribute("attribute.name.crit_chance", 0.05, 0.0, 2.0D).setTracked(true));
    public static final RegistryEntry<EntityAttribute> BACK_ATTACK = register("back_attack", new ClampedEntityAttribute("attribute.name.back_attack", 0.5, 0.0, 10.24D).setTracked(true));
    public static final RegistryEntry<EntityAttribute> AIR_ATTACK = register("air_attack", new ClampedEntityAttribute("attribute.name.air_attack", 1.0, 0.0, 10.24D).setTracked(true));
    public static final RegistryEntry<EntityAttribute> DOWN_ATTACK = register("down_attack", new ClampedEntityAttribute("attribute.name.down_attack", 0.5, 0.0, 10.24D).setTracked(true));

    public static final RegistryEntry<EntityAttribute> HEALTH_REGEN = register("health_regen", new ClampedEntityAttribute("attribute.name.health_regen", 0.0, 0.0, 1024D).setTracked(true));

    public static final RegistryEntry<EntityAttribute> PLAYER_VERS_ENTITY_DAMAGE = register("player_vers_entity_damage", new ClampedEntityAttribute("attribute.name.player_vers_entity_damage", 0, 0.0, 1024).setTracked(true));
    public static final RegistryEntry<EntityAttribute> PLAYER_VERS_PLAYER_DAMAGE = register("player_vers_player_damage", new ClampedEntityAttribute("attribute.name.player_vers_player_damage", 0, 0.0, 1024).setTracked(true));

    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        RegistryEntry<EntityAttribute> entry = Registry.registerReference(Registries.ATTRIBUTE, Dominatus.id(id), attribute);
        ENTRIES.add(entry);
        return entry;
    }
}
