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
 * Warrior skill: Strike enemies in a cone with increased damage
 */
public class WarriorCleave extends ActiveSkill {

    public WarriorCleave() {
        super(
                Dominatus.id("warrior_cleave"),
                "Powerful Cleave",
                "Strike all enemies in front of you with increased damage",
                PlayerClass.WARRIOR,
                5, // Required class level
                3, // Class points to learn
                ResourceType.RAGE,
                5f, // Resource cost
                15, // 3 second cooldown
                2f, // Base damage
                3, // Single hit
                2 // DElay
        );
    }

    @Override
    protected List<LivingEntity> getTargets(PlayerEntity caster) {
        return CollisionShapes.getCircleTargets(caster, 4.0); // 4 block range, 90 degree cone
    }

    @Override
    protected void applyDamageBypass(PlayerEntity caster, LivingEntity target) {
        // Warrior cleave does 120% normal damage
        float damage = baseDamage * 1.2f;

        // Reset immunity and apply damage
        target.timeUntilRegen = 0;
        target.hurtTime = 0;

        target.damage(caster.getDamageSources().playerAttack(caster), damage);
    }
}

