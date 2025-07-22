package sypztep.dominatus.common.system.skill.passives.vitality;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class IronSkinPassive extends BaseAttributePassive {
    public IronSkinPassive() {
        super(Dominatus.id("iron_skin"), Text.literal("§7Iron Skin"), Text.literal("+2 Armor"), "vitality", 35, 3);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, EntityAttributes.GENERIC_ARMOR, Dominatus.id("passive_iron_skin"), 2.0, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.GENERIC_ARMOR, Dominatus.id("passive_iron_skin"));
    }
}
