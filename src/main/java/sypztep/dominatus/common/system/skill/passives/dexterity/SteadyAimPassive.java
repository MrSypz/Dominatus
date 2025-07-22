package sypztep.dominatus.common.system.skill.passives.dexterity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class SteadyAimPassive extends BaseAttributePassive {
    public SteadyAimPassive() {
        super(Dominatus.id("steady_aim"), Text.literal("§eSteady Aim"), Text.literal("+10 Accuracy"), "dexterity", 10, 1);
    }
    @Override protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.ACCURACY, Dominatus.id("passive_steady_aim"), 10.0, EntityAttributeModifier.Operation.ADD_VALUE);
    }
    @Override protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.ACCURACY, Dominatus.id("passive_steady_aim"));
    }
}

