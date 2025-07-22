package sypztep.dominatus.common.system.skill.passives.dexterity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class PerfectAimPassive extends BaseAttributePassive {
    public PerfectAimPassive() {
        super(Dominatus.id("perfect_aim"), Text.literal("§e§lPerfect Aim"), Text.literal("+25 Accuracy, +10% Crit Chance"), "dexterity", 99, 6);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.ACCURACY, Dominatus.id("passive_perfect_aim_acc"), 25.0, EntityAttributeModifier.Operation.ADD_VALUE);
        applyAttributeModifier(entity, ModEntityAttributes.CRIT_CHANCE, Dominatus.id("passive_perfect_aim_crit"), 0.10, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.ACCURACY, Dominatus.id("passive_perfect_aim_acc"));
        removeAttributeModifier(entity, ModEntityAttributes.CRIT_CHANCE, Dominatus.id("passive_perfect_aim_crit"));
    }
}
