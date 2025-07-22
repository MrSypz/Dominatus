package sypztep.dominatus.common.system.skill.passives.vitality;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class RegenerationPassive extends BaseAttributePassive {
    public RegenerationPassive() {
        super(Dominatus.id("regeneration"), Text.literal("§cRegeneration"), Text.literal("+50% Health Regen"), "vitality", 20, 2);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.HEALTH_REGEN, Dominatus.id("passive_regen"), 0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.HEALTH_REGEN, Dominatus.id("passive_regen"));
    }
}
