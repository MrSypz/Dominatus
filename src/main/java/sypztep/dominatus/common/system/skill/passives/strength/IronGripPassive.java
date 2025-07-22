package sypztep.dominatus.common.system.skill.passives.strength;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

// ====================
// STRENGTH PASSIVES
// ====================

/**
 * STR 10 - Iron Grip: +1 Attack Damage
 */
public class IronGripPassive extends BaseAttributePassive {
    private static final Identifier MODIFIER_ID = Dominatus.id("passive_iron_grip");

    public IronGripPassive() {
        super(
                Dominatus.id("iron_grip"),
                Text.literal("§6Iron Grip"),
                Text.literal("Your strong grip increases attack damage by 1"),
                "strength",
                10,
                1
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, EntityAttributes.GENERIC_ATTACK_DAMAGE, MODIFIER_ID, 1.0, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.GENERIC_ATTACK_DAMAGE, MODIFIER_ID);
    }
}

