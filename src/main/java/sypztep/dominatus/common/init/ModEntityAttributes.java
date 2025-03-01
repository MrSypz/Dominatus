package sypztep.dominatus.common.init;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEntityAttributes {
    public static final RegistryEntry<EntityAttribute> ACCURACY;
    public static final RegistryEntry<EntityAttribute> EVASION;
    public static final RegistryEntry<EntityAttribute> CRIT_DAMAGE;
    public static final RegistryEntry<EntityAttribute> CRIT_CHANCE;

    static {
        ACCURACY = register("accuracy", new ClampedEntityAttribute("attribute.name.accuracy", 50, 0.0, 2048.0D).setTracked(true));
        EVASION = register("evasion", new ClampedEntityAttribute("attribute.name.evasion", 20, 0.0, 2048.0D).setTracked(true));
        CRIT_CHANCE = register("crit_chance", new ClampedEntityAttribute("attribute.name.crit_chance", 0.05, 0.0, 2.0D).setTracked(true));
        CRIT_DAMAGE = register("crit_damage", new ClampedEntityAttribute("attribute.name.crit_damage", 0.5, 0.0, 10.24D).setTracked(true));
    }

    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Identifier.ofVanilla(id), attribute);
    }
}
