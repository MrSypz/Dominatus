package sypztep.dominatus.common.init;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import sypztep.dominatus.Dominatus;

public class ModDamageSources {
    public static final RegistryKey<DamageType> SKILL_DAMAGE_KEY = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,
            Dominatus.id("skill_damage")
    );

    // Method to get the actual RegistryEntry (call this when you need it)
    public static RegistryEntry<DamageType> getSkillDamageType(ServerWorld world) {
        return world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(SKILL_DAMAGE_KEY);
    }

    // Create skill damage source
    public static DamageSource skillDamage(ServerWorld world, PlayerEntity caster) {
        return new DamageSource(getSkillDamageType(world), caster);
    }
}
