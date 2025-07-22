package sypztep.dominatus.common.system.skill.passives.agility;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

/**
 * AGI 50 - Shadow Step: 15% chance to avoid damage completely
 */
public class ShadowStepPassive extends BaseAttributePassive {
    public ShadowStepPassive() {
        super(
                Dominatus.id("shadow_step"),
                Text.literal("§8Shadow Step"),
                Text.literal("15% chance to completely avoid incoming damage"),
                "agility",
                50,
                4
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        // This would need special handling in damage events
        // For now, we'll add evasion as a representation
        applyAttributeModifier(entity, ModEntityAttributes.EVASION, Dominatus.id("passive_shadow_step"), 15.0, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.EVASION, Dominatus.id("passive_shadow_step"));
    }
}
