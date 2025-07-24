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
 * Mage skill: Fireball that hits area
 */
public class MageFireball extends ActiveSkill {

    public MageFireball() {
        super(
                Dominatus.id("mage_fireball"),
                "Arcane Fireball",
                "Launch a fireball that explodes on impact",
                PlayerClass.MAGE,
                3, // Required class level
                2, // Class points to learn
                ResourceType.MANA,
                30f, // Resource cost
                80, // 4 second cooldown
                12f, // Base damage
                1 // Single hit
        );
    }

    @Override
    protected List<LivingEntity> getTargets(PlayerEntity caster) {
        // For now, just hit enemies around caster (simplified)
        // TODO: Implement projectile mechanics
        return CollisionShapes.getCircleTargets(caster, 3.0); // 3 block radius
    }

    @Override
    protected void applyDamage(PlayerEntity caster, LivingEntity target) {
        target.damage(caster.getDamageSources().magic(), baseDamage);
    }

    @Override
    protected void applyEffects(PlayerEntity caster, LivingEntity target) {
        // Set target on fire for 3 seconds
        target.setOnFireFor(3);
    }
}
