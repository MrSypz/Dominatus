package sypztep.dominatus.common.system.skill.active.advanced;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.playerclass.PlayerClass;
import sypztep.dominatus.common.system.playerclass.ResourceType;
import sypztep.dominatus.common.system.skill.active.UpgradeableSkill;

import java.util.List;

/**
 * Advanced Mage Skill: Projectile Fireball
 */
public class WizardMeteor extends UpgradeableSkill {

    public WizardMeteor() {
        super(
                Dominatus.id("wizard_meteor"),
                "Meteor Strike",
                "Call down a meteor that creates a massive explosion",
                PlayerClass.WIZARD,
                15, // Required class level
                8,  // Initial learn cost
                ResourceType.MANA,
                80f, // Base resource cost
                200, // 10 second base cooldown
                25f, // Base damage
                1,   // Single hit but area effect
                5,   // Max level
                new int[]{5, 6, 7, 8} // Upgrade costs
        );
    }

    @Override
    protected List<LivingEntity> getTargets(PlayerEntity caster) {
        // Target area in front of caster
        Vec3d lookDirection = caster.getRotationVec(1.0f);
        Vec3d targetPos = caster.getPos().add(lookDirection.multiply(8.0)); // 8 blocks ahead

        World world = caster.getWorld();
        double radius = 4.0 + (getSkillLevel(caster) * 0.5); // Radius increases with level

        return world.getEntitiesByClass(LivingEntity.class,
                new net.minecraft.util.math.Box(targetPos.subtract(radius, radius, radius),
                        targetPos.add(radius, radius, radius)),
                entity -> entity != caster && entity.isAlive());
    }

    private int getSkillLevel(PlayerEntity caster) {
        // Helper method to get skill level - would need to be implemented properly
        return 1; // Placeholder
    }

    @Override
    protected void applyScaledEffects(PlayerEntity caster, LivingEntity target, int skillLevel) {
        // Fire effect duration scales with level
        int fireDuration = 3 + skillLevel;
        target.setOnFireFor(fireDuration);

        // Apply slowness at higher levels
        if (skillLevel >= 3) {
            int slowDuration = (5 + skillLevel) * 20;
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, slowDuration, 1));
        }

        // Apply weakness at max level
        if (skillLevel >= 5) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 0));
        }
    }

    @Override
    protected void applyScaledDamage(PlayerEntity caster, LivingEntity target, int skillLevel) {
        float damage = getScaledDamage(skillLevel);

        // Magic damage that bypasses armor at higher levels
        if (skillLevel >= 4) {
            target.damage(caster.getDamageSources().magic(), damage);
        } else {
            target.damage(caster.getDamageSources().playerAttack(caster), damage);
        }
    }
}
