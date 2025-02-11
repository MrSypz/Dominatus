package sypztep.dominatus.common.tag;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import sypztep.dominatus.Dominatus;

public class ModDamageTags {
    public static final TagKey<DamageType> PHYSICAL_DAMAGE = TagKey.of(RegistryKeys.DAMAGE_TYPE, Dominatus.id("physical_damage"));
    public static final TagKey<DamageType> MELEE_DAMAGE = TagKey.of(RegistryKeys.DAMAGE_TYPE, Dominatus.id("melee_damage"));
    public static final TagKey<DamageType> MAGIC_DAMAGE = TagKey.of(RegistryKeys.DAMAGE_TYPE, Dominatus.id("magic_damage"));
    public static final TagKey<DamageType> FIRE_DAMAGE = TagKey.of(RegistryKeys.DAMAGE_TYPE, Dominatus.id("fire_damage"));
    public static final TagKey<DamageType> PROJECTILE_DAMAGE = TagKey.of(RegistryKeys.DAMAGE_TYPE, Dominatus.id("projectile_damage"));
}
