package sypztep.dominatus.common.init;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import sypztep.dominatus.Dominatus;

import java.util.ArrayList;

public final class ModEntityAttributes {
    public ModEntityAttributes() {
    }

    public static final ArrayList<RegistryEntry<EntityAttribute>> ENTRIES = new ArrayList<>();

    public static final RegistryEntry<EntityAttribute> HEALTH_REGEN = register("health_regen", new ClampedEntityAttribute("attribute.name.health_regen", 0, 0.0, 2048).setTracked(true));

    public static final RegistryEntry<EntityAttribute> ACCURACY = register("accuracy", new ClampedEntityAttribute("attribute.name.accuracy", 75, 0.0, 2048.0D).setTracked(true));
    public static final RegistryEntry<EntityAttribute> EVASION = register("evasion", new ClampedEntityAttribute("attribute.name.evasion", 0, 0.0, 2048.0D).setTracked(true));

    public static final RegistryEntry<EntityAttribute> CRIT_DAMAGE = register("crit_damage", new ClampedEntityAttribute("attribute.name.crit_damage", 0.5, 0.0, 10.24D).setTracked(true));
    public static final RegistryEntry<EntityAttribute> CRIT_CHANCE = register("crit_chance", new ClampedEntityAttribute("attribute.name.crit_chance", 0.05, 0.0, 2.0D).setTracked(true));
    public static final RegistryEntry<EntityAttribute> BACK_ATTACK = register("back_attack", new ClampedEntityAttribute("attribute.name.back_attack", 0.5, 0.0, 10.24D).setTracked(true));

    public static final RegistryEntry<EntityAttribute> MELEE_ATTACK_DAMAGE = register("melee_attack_damage", new ClampedEntityAttribute("attribute.name.melee_attack_damage", 0, 0.0, 4.0).setTracked(true));
    public static final RegistryEntry<EntityAttribute> MAGIC_ATTACK_DAMAGE = register("magic_attack_damage", new ClampedEntityAttribute("attribute.name.magic_attack_damage", 0, 0.0, 10.24D).setTracked(true));
    public static final RegistryEntry<EntityAttribute> PROJECTILE_ATTACK_DAMAGE = register("projectile_attack_damage", new ClampedEntityAttribute("attribute.name.projectile_attack_damage", 0, 0.0, 10.24D).setTracked(true));

    public static final RegistryEntry<EntityAttribute> MAGIC_RESISTANCE = register("magic_resistance", new ClampedEntityAttribute("attribute.name.magic_resistance", 0, -10.0D, 0.75D).setTracked(true));
    public static final RegistryEntry<EntityAttribute> PHYSICAL_RESISTANCE = register("physical_resistance", new ClampedEntityAttribute("attribute.name.physical_resistance", 0, -10.0D, 0.75D).setTracked(true));

    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        RegistryEntry<EntityAttribute> entry = Registry.registerReference(Registries.ATTRIBUTE, Dominatus.id(id), attribute);
        ENTRIES.add(entry);
        return entry;
    }
}
