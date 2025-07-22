package sypztep.dominatus.common.system.skill.passives.dexterity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class LethalPrecisionPassive extends BaseAttributePassive {
    public LethalPrecisionPassive() {
        super(Dominatus.id("lethal_precision"), Text.literal("§eLethal Precision"), Text.literal("+5% Critical Chance"), "dexterity", 75, 5);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.CRIT_CHANCE, Dominatus.id("passive_lethal_precision"), 0.05, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.CRIT_CHANCE, Dominatus.id("passive_lethal_precision"));
    }
}
