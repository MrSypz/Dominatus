package sypztep.dominatus.common.system.skill.passives.strength;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

/**
 * STR 20 - Power Strike: +25% Critical Damage
 */
public class PowerStrikePassive extends BaseAttributePassive {
    private static final Identifier MODIFIER_ID = Dominatus.id("passive_power_strike");

    public PowerStrikePassive() {
        super(
                Dominatus.id("power_strike"),
                Text.literal("§6Power Strike"),
                Text.literal("Your powerful strikes deal 25% more critical damage"),
                "strength",
                20,
                2
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.CRIT_DAMAGE, MODIFIER_ID, 0.25, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.CRIT_DAMAGE, MODIFIER_ID);
    }
}
