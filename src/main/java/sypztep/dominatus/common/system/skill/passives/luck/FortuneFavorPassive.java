package sypztep.dominatus.common.system.skill.passives.luck;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class FortuneFavorPassive extends BaseAttributePassive {
    public FortuneFavorPassive() {
        super(Dominatus.id("fortune_favor"), Text.literal("§dFortune's Favor"), Text.literal("+10% All attributes effectiveness"), "luck", 75, 5);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
    }
}
