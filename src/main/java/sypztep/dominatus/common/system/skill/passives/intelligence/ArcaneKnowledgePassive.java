package sypztep.dominatus.common.system.skill.passives.intelligence;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class ArcaneKnowledgePassive extends BaseAttributePassive {
    public ArcaneKnowledgePassive() {
        super(Dominatus.id("arcane_knowledge"), Text.literal("§9Arcane Knowledge"), Text.literal("+30% Magic Damage"), "intelligence", 35, 3);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.MAGIC_ATTACK_DAMAGE, Dominatus.id("passive_arcane"), 0.30, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.MAGIC_ATTACK_DAMAGE, Dominatus.id("passive_arcane"));
    }
}
