package sypztep.dominatus.common.system.skill.passives.luck;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class FortunePassive extends BaseAttributePassive {
    public FortunePassive() {
        super(Dominatus.id("fortune"), Text.literal("§dFortune"), Text.literal("+2% Critical Chance"), "luck", 10, 1);
    }
    @Override protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.CRIT_CHANCE, Dominatus.id("passive_fortune"), 0.02, EntityAttributeModifier.Operation.ADD_VALUE);
    }
    @Override protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.CRIT_CHANCE, Dominatus.id("passive_fortune"));
    }
}

