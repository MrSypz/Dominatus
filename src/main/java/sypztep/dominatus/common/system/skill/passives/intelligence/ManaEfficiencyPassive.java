package sypztep.dominatus.common.system.skill.passives.intelligence;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class ManaEfficiencyPassive extends BaseAttributePassive {
    public ManaEfficiencyPassive() {
        super(Dominatus.id("mana_efficiency"), Text.literal("§9Mana Efficiency"), Text.literal("+15% Magic Damage"), "intelligence", 10, 1);
    }
    @Override protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.MAGIC_ATTACK_DAMAGE, Dominatus.id("passive_mana_eff"), 0.15, EntityAttributeModifier.Operation.ADD_VALUE);
    }
    @Override protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.MAGIC_ATTACK_DAMAGE, Dominatus.id("passive_mana_eff"));
    }
}

