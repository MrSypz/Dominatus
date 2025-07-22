package sypztep.dominatus.common.system.skill.passives.luck;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class LadyLuckPassive extends BaseAttributePassive {
    public LadyLuckPassive() {
        super(Dominatus.id("lady_luck"), Text.literal("§dLady Luck"), Text.literal("+7% Critical Chance"), "luck", 50, 4);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.CRIT_CHANCE, Dominatus.id("passive_lady_luck"), 0.07, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.CRIT_CHANCE, Dominatus.id("passive_lady_luck"));
    }
}
