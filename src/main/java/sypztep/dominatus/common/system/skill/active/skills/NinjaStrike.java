package sypztep.dominatus.common.system.skill.active.skills;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.playerclass.PlayerClass;
import sypztep.dominatus.common.system.playerclass.ResourceType;
import sypztep.dominatus.common.system.skill.active.ActiveSkill;
import sypztep.dominatus.common.system.skill.collision.CollisionShapes;

import java.util.List;

/**
 * Ninja skill: Quick strike on single target
 */
public class NinjaStrike extends ActiveSkill {

    public NinjaStrike() {
        super(
                Dominatus.id("ninja_strike"),
                "Shadow Strike",
                "Quick strike on a single enemy with chance to crit",
                PlayerClass.NINJA,
                2, // Required class level
                1, // Class points to learn
                ResourceType.MANA,
                15f, // Resource cost
                40, // 2 second cooldown
                6f, // Base damage
                2 // Double hit for ninja speed
        );
    }

    @Override
    protected List<LivingEntity> getTargets(PlayerEntity caster) {
        return CollisionShapes.getSingleTarget(caster, 3.0); // 3 block range, single target
    }

    @Override
    protected void applyDamage(PlayerEntity caster, LivingEntity target) {
        float damage = baseDamage;

        // 30% chance for critical hit (double damage)
        if (Math.random() < 0.3) {
            damage *= 2.0f;
            // TODO: Add crit particle effects
        }

        target.damage(caster.getDamageSources().playerAttack(caster), damage);
    }
}
