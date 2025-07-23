package sypztep.dominatus.common.system.skill.passives.agility;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

/**
 * AGI 20 - Quick Reflex: +15 Evasion
 */
public class QuickReflexPassive extends BaseAttributePassive {
    private static final Identifier MODIFIER_ID = Dominatus.id("passive_quick_reflex");

    public QuickReflexPassive() {
        super(
                Dominatus.id("quick_reflex"),
                Text.literal("§aQuick Reflex"),
                Text.literal("Your quick reflexes increase evasion by 15"),
                "agility",
                20,
                2
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.EVASION, MODIFIER_ID, 15.0, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.EVASION, MODIFIER_ID);
    }
}
