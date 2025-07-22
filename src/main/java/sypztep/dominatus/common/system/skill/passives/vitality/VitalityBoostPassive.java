package sypztep.dominatus.common.system.skill.passives.vitality;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class VitalityBoostPassive extends BaseAttributePassive {
    public VitalityBoostPassive() {
        super(Dominatus.id("vitality_boost"), Text.literal("§cVitality Boost"), Text.literal("+40% Max Health"), "vitality", 50, 4);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, EntityAttributes.GENERIC_MAX_HEALTH, Dominatus.id("passive_vitality_boost"), 0.4, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.GENERIC_MAX_HEALTH, Dominatus.id("passive_vitality_boost"));
    }
}
