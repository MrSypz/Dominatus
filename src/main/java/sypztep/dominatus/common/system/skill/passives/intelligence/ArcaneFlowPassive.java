package sypztep.dominatus.common.system.skill.passives.intelligence;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class ArcaneFlowPassive extends BaseAttributePassive {
    public ArcaneFlowPassive() {
        super(Dominatus.id("arcane_flow"), Text.literal("§9Arcane Flow"), Text.literal("Spell effects last longer"), "intelligence", 75, 5);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
    }
}
