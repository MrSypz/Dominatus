package sypztep.dominatus.common.util;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import sypztep.dominatus.common.system.skill.active.ActiveSkill;

public class SkillDamageSource extends DamageSource {
    private final ActiveSkill skill;
    private final PlayerEntity caster;

    public SkillDamageSource(DamageSource baseDamageSource, PlayerEntity caster, ActiveSkill skill) {
        super(baseDamageSource.getTypeRegistryEntry(), baseDamageSource.getSource(), baseDamageSource.getAttacker());
        this.caster = caster;
        this.skill = skill;
    }

    public ActiveSkill getSkill() {
        return skill;
    }

    public PlayerEntity getCaster() {
        return caster;
    }

    @Override
    public boolean isScaledWithDifficulty() {
        return false; // Skill damage isn't affected by difficulty
    }
}