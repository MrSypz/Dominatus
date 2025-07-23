package sypztep.dominatus.common.system.skill.passives.agility;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.skill.PassiveSkillManager;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

/**
 * AGI 50 - Shadow Step: foodstep is now gone
 */
public class ShadowStepPassive extends BaseAttributePassive {
    public ShadowStepPassive() {
        super(
                Dominatus.id("shadow_step"),
                Text.literal("§8Shadow Step"),
                Text.literal("No longer have footstep sound"),
                "agility",
                50,
                4
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.EVASION, Dominatus.id("passive_shadow_step"), 15.0, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.EVASION, Dominatus.id("passive_shadow_step"));
    }
    public static boolean hasShadowStep(PlayerEntity entity) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(entity);
        PassiveSkillManager passiveManager = levelComponent.getPassiveSkillManager();

        if (passiveManager == null) return false;
        return passiveManager.isUnlocked(Dominatus.id("shadow_step"));
    }
}
