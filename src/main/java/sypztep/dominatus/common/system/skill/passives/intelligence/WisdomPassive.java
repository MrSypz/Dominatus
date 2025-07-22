package sypztep.dominatus.common.system.skill.passives.intelligence;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class WisdomPassive extends BaseAttributePassive {
    public WisdomPassive() {
        super(Dominatus.id("wisdom"), Text.literal("§9Wisdom"), Text.literal("+25% Magic Resistance"), "intelligence", 20, 2);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.MAGIC_RESISTANCE, Dominatus.id("passive_wisdom"), 0.25, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.MAGIC_RESISTANCE, Dominatus.id("passive_wisdom"));
    }
}
