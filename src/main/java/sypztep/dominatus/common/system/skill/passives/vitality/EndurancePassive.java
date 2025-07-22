package sypztep.dominatus.common.system.skill.passives.vitality;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class EndurancePassive extends BaseAttributePassive {
    public EndurancePassive() {
        super(Dominatus.id("endurance"), Text.literal("§cEndurance"), Text.literal("+100% Health Regen"), "vitality", 75, 5);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.HEALTH_REGEN, Dominatus.id("passive_endurance"), 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.HEALTH_REGEN, Dominatus.id("passive_endurance"));
    }
}
