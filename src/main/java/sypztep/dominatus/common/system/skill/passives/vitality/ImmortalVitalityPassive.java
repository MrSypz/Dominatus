package sypztep.dominatus.common.system.skill.passives.vitality;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class ImmortalVitalityPassive extends BaseAttributePassive {
    public ImmortalVitalityPassive() {
        super(Dominatus.id("immortal_vitality"), Text.literal("§c§lImmortal Vitality"), Text.literal("10% chance to survive lethal damage"), "vitality", 99, 6);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
    }
}
