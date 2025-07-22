package sypztep.dominatus.common.system.skill.passives.luck;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class LegendaryLuckPassive extends BaseAttributePassive {
    public LegendaryLuckPassive() {
        super(Dominatus.id("legendary_luck"), Text.literal("§d§lLegendary Luck"), Text.literal("+15% Critical Chance, massive bonuses"), "luck", 99, 6);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.CRIT_CHANCE, Dominatus.id("passive_legendary_luck"), 0.15, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.CRIT_CHANCE, Dominatus.id("passive_legendary_luck"));
    }
}
