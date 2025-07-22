package sypztep.dominatus.common.system.skill.passives.strength;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

/**
 * STR 75 - Titan Strength: +50% Block Break Speed
 */
public class TitanStrengthPassive extends BaseAttributePassive {
    private static final Identifier MODIFIER_ID = Dominatus.id("passive_titan_strength");

    public TitanStrengthPassive() {
        super(
                Dominatus.id("titan_strength"),
                Text.literal("§6Titan Strength"),
                Text.literal("Your immense strength breaks blocks 50% faster"),
                "strength",
                75,
                5
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, EntityAttributes.PLAYER_BLOCK_BREAK_SPEED, MODIFIER_ID, 0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.PLAYER_BLOCK_BREAK_SPEED, MODIFIER_ID);
    }
}
