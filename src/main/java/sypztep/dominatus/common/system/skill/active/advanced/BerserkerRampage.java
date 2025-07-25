package sypztep.dominatus.common.system.skill.active.advanced;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.playerclass.PlayerClass;
import sypztep.dominatus.common.system.playerclass.ResourceType;
import sypztep.dominatus.common.system.skill.active.UpgradeableSkill;

import java.util.List;

/**
 * Advanced Warrior Skill: Devastating Cleave with scaling effects
 */
public class BerserkerRampage extends UpgradeableSkill {

    public BerserkerRampage() {
        super(
                Dominatus.id("berserker_rampage"),
                "Berserker Rampage",
                "Enter a berserker rage, increasing damage and speed",
                PlayerClass.BERSERKER,
                10, // Required class level
                5,  // Initial learn cost
                ResourceType.RAGE,
                40f, // Base resource cost
                120, // 6 second base cooldown
                15f, // Base damage
                1,   // Single hit
                5,   // Max level
                new int[]{3, 4, 5, 6} // Upgrade costs for levels 2-5
        );
    }

    @Override
    protected List<LivingEntity> getTargets(PlayerEntity caster) {
        // Self-buff skill targets the caster
        return List.of(caster);
    }

    @Override
    protected void applyScaledEffects(PlayerEntity caster, LivingEntity target, int skillLevel) {
        if (target instanceof PlayerEntity player) {
            // Duration scales with level (10s base + 2s per level)
            int duration = (10 + skillLevel * 2) * 20; // Convert to ticks

            // Strength effect scales with level
            int strengthLevel = Math.min(skillLevel - 1, 4); // Max level 5 strength
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, duration, strengthLevel));

            // Speed effect at higher levels
            if (skillLevel >= 3) {
                int speedLevel = Math.min(skillLevel - 3, 2); // Max level 3 speed
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, speedLevel));
            }

            // Resistance at max level
            if (skillLevel >= 5) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, duration, 0));
            }

            // Particle effects
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                Vec3d pos = player.getPos();
                serverWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                        pos.x, pos.y + 1, pos.z, 10, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }

    @Override
    protected void applyScaledDamage(PlayerEntity caster, LivingEntity target, int skillLevel) {
        // This is a buff skill, no direct damage
    }
}

