package sypztep.dominatus.common.system.skill.passives.dexterity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class PrecisionPassive extends BaseAttributePassive {
    public PrecisionPassive() {
        super(Dominatus.id("precision"), Text.literal("§ePrecision"), Text.literal("+15% Projectile Damage"), "dexterity", 20, 2);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.PROJECTILE_ATTACK_DAMAGE, Dominatus.id("passive_precision"), 0.15, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.PROJECTILE_ATTACK_DAMAGE, Dominatus.id("passive_precision"));
    }
}
