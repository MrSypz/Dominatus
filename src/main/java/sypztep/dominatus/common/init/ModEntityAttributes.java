package sypztep.dominatus.common.init;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import sypztep.dominatus.Dominatus;

public class ModEntityAttributes {
    public static final RegistryEntry<EntityAttribute> GENERIC_AP;
    public static final RegistryEntry<EntityAttribute> GENERIC_DP;
    public static final RegistryEntry<EntityAttribute> GENERIC_ACCURACY;
    public static final RegistryEntry<EntityAttribute> GENERIC_EVASION;
    public static final RegistryEntry<EntityAttribute> GENERIC_CRIT_DAMAGE;
    public static final RegistryEntry<EntityAttribute> GENERIC_CRIT_CHANCE;

    static {
        GENERIC_AP = register("generic.ap", new ClampedEntityAttribute("attribute.name.generic.ap", 0, 0.0, 1024.0D));
        GENERIC_DP = register("generic.dp", new ClampedEntityAttribute("attribute.name.generic.dp", 0, 0.0, 1024.0D));
        GENERIC_ACCURACY = register("generic.accuracy", new ClampedEntityAttribute("attribute.name.generic.accuracy", 50, 0.0, 2048.0D).setTracked(true));
        GENERIC_EVASION = register("generic.evasion", new ClampedEntityAttribute("attribute.name.generic.evasion", 20, 0.0, 2048.0D).setTracked(true));
        GENERIC_CRIT_CHANCE = register("generic.crit_chance", new ClampedEntityAttribute("attribute.name.generic.crit_chance", 0.05, 0.0, 2.0D).setTracked(true));
        GENERIC_CRIT_DAMAGE = register("generic.crit_damage", new ClampedEntityAttribute("attribute.name.generic.crit_damage", 0.5, 0.0, 10.24D).setTracked(true));
    }

    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Dominatus.id(id), attribute);
    }

}
