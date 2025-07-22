package sypztep.dominatus.common.system.skill.passives.strength;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

/**
 * STR 35 - Mighty Blow: +15% Melee Damage
 */
public class MightyBlowPassive extends BaseAttributePassive {
    private static final Identifier MODIFIER_ID = Dominatus.id("passive_mighty_blow");

    public MightyBlowPassive() {
        super(
                Dominatus.id("mighty_blow"),
                Text.literal("§6Mighty Blow"),
                Text.literal("Your melee attacks deal 15% more damage"),
                "strength",
                35,
                3
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.MELEE_ATTACK_DAMAGE, MODIFIER_ID, 0.15, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.MELEE_ATTACK_DAMAGE, MODIFIER_ID);
    }
}
