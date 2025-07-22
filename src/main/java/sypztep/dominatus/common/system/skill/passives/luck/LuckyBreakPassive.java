package sypztep.dominatus.common.system.skill.passives.luck;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class LuckyBreakPassive extends BaseAttributePassive {
    public LuckyBreakPassive() {
        super(Dominatus.id("lucky_break"), Text.literal("§dLucky Break"), Text.literal("+3 Accuracy, +3 Evasion"), "luck", 20, 2);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.ACCURACY, Dominatus.id("passive_lucky_break_acc"), 3.0, EntityAttributeModifier.Operation.ADD_VALUE);
        applyAttributeModifier(entity, ModEntityAttributes.EVASION, Dominatus.id("passive_lucky_break_eva"), 3.0, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.ACCURACY, Dominatus.id("passive_lucky_break_acc"));
        removeAttributeModifier(entity, ModEntityAttributes.EVASION, Dominatus.id("passive_lucky_break_eva"));
    }
}
