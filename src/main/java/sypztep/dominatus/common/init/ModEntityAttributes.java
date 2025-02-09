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

    static {
        GENERIC_AP = register("generic.ap", new ClampedEntityAttribute("attribute.name.generic.ap", 0, 0.0, 1000.0));
        GENERIC_DP = register("generic.dp", new ClampedEntityAttribute("attribute.name.generic.dp", 0, 0.0, 1000.0));
        GENERIC_ACCURACY = register("generic.accuracy", new ClampedEntityAttribute("attribute.name.generic.accuracy", 50, 0.0, 500.0));
        GENERIC_EVASION = register("generic.evasion", new ClampedEntityAttribute("attribute.name.generic.evasion", 20, 0.0, 500.0));
    }

    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Dominatus.id(id), attribute);
    }
}
