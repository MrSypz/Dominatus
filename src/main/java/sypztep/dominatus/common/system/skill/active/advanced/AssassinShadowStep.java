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
import sypztep.dominatus.common.system.skill.collision.CollisionShapes;

import java.util.List;

/**
 * Advanced Ninja Skill: Shadow Dash with teleportation
 */
public class AssassinShadowStep extends UpgradeableSkill {

    public AssassinShadowStep() {
        super(
                Dominatus.id("assassin_shadow_step"),
                "Shadow Step",
                "Teleport behind target and strike with increased critical chance",
                PlayerClass.ASSASSIN,
                8, // Required class level
                4, // Initial learn cost
                ResourceType.MANA,
                30f, // Base resource cost
                80,  // 4 second base cooldown
                12f, // Base damage
                3,   // Triple hit
                5,   // Max level
                new int[]{2, 3, 4, 5} // Upgrade costs
        );
    }

    @Override
    protected List<LivingEntity> getTargets(PlayerEntity caster) {
        // Find closest enemy within range
        return CollisionShapes.getSingleTarget(caster, 8.0 + getSkillLevel(caster)); // Range increases with level
    }

    private int getSkillLevel(PlayerEntity caster) {
        return 1; // Placeholder - implement properly
    }

    @Override
    protected void applyScaledDamage(PlayerEntity caster, LivingEntity target, int skillLevel) {
        // Teleport behind target
        Vec3d targetPos = target.getPos();
        Vec3d behindPos = targetPos.add(target.getRotationVec(1.0f).multiply(-2.0));
        caster.teleport(behindPos.x, behindPos.y, behindPos.z,true);

        // Critical hit chance increases with level
        float critChance = 0.5f + (skillLevel * 0.1f); // 50% base + 10% per level

        float damage = getScaledDamage(skillLevel);
        if (Math.random() < critChance) {
            damage *= 2.0f; // Critical hit

            // Particle effect for crit
            if (caster.getWorld() instanceof ServerWorld serverWorld) {
                Vec3d pos = target.getPos();
                serverWorld.spawnParticles(ParticleTypes.CRIT,
                        pos.x, pos.y + 1, pos.z, 15, 0.5, 0.5, 0.5, 0.1);
            }
        }

        target.damage(caster.getDamageSources().playerAttack(caster), damage);
    }

    @Override
    protected void applyScaledEffects(PlayerEntity caster, LivingEntity target, int skillLevel) {
        // Apply blindness at higher levels
        if (skillLevel >= 3) {
            int blindDuration = (2 + skillLevel) * 20;
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, blindDuration, 0));
        }

        // Grant invisibility to caster at max level
        if (skillLevel >= 5) {
            caster.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 100, 0)); // 5 seconds
        }
    }
}
