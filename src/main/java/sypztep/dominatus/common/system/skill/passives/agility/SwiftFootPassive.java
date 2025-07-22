package sypztep.dominatus.common.system.skill.passives.agility;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

// ====================
// AGILITY PASSIVES
// ====================

/**
 * AGI 10 - Swift Foot: +10% Movement Speed
 */
public class SwiftFootPassive extends BaseAttributePassive {
    private static final Identifier MODIFIER_ID = Dominatus.id("passive_swift_foot");

    public SwiftFootPassive() {
        super(
                Dominatus.id("swift_foot"),
                Text.literal("§aSwift Foot"),
                Text.literal("Your quick feet grant 10% movement speed"),
                "agility",
                10,
                1
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, EntityAttributes.GENERIC_MOVEMENT_SPEED, MODIFIER_ID, 0.1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.GENERIC_MOVEMENT_SPEED, MODIFIER_ID);
    }
}

