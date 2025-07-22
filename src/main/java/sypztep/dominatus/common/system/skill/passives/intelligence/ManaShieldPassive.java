package sypztep.dominatus.common.system.skill.passives.intelligence;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class ManaShieldPassive extends BaseAttributePassive {
    public ManaShieldPassive() {
        super(Dominatus.id("mana_shield"), Text.literal("§9Mana Shield"), Text.literal("Magic resistance stacks"), "intelligence", 50, 4);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
    }
}
