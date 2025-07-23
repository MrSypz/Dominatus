package sypztep.dominatus.common.system.skill.passives.agility;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

/**
 * AGI 99 - Phantom Speed: +50% Movement Speed and 2 More time Jump
 */
public class PhantomSpeedPassive extends BaseAttributePassive {
    private static final Identifier MODIFIER_ID = Dominatus.id("passive_phantom_speed");

    public PhantomSpeedPassive() {
        super(
                Dominatus.id("phantom_speed"),
                Text.literal("§a§lPhantom Speed"),
                Text.literal("Your legendary agility grants +50% movement speed, And 2 more time Jump"),
                "agility",
                99,
                6
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, EntityAttributes.GENERIC_MOVEMENT_SPEED, MODIFIER_ID, 0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.GENERIC_MOVEMENT_SPEED, MODIFIER_ID);
    }

    @Override
    public Identifier getIcon() {
        return Dominatus.id("textures/passive/phantom_speed.png");
    }
}
