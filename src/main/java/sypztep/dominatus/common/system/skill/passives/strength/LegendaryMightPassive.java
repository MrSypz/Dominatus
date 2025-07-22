package sypztep.dominatus.common.system.skill.passives.strength;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

/**
 * STR 99 - Legendary Might: +2 Base Attack Damage
 */
public class LegendaryMightPassive extends BaseAttributePassive {
    private static final Identifier MODIFIER_ID = Dominatus.id("passive_legendary_might");

    public LegendaryMightPassive() {
        super(
                Dominatus.id("legendary_might"),
                Text.literal("§6§lLegendary Might"),
                Text.literal("Your legendary strength grants +2 base attack damage"),
                "strength",
                99,
                6
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, EntityAttributes.GENERIC_ATTACK_DAMAGE, MODIFIER_ID, 2.0, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.GENERIC_ATTACK_DAMAGE, MODIFIER_ID);
    }

    @Override
    public Identifier getIcon() {
        return Dominatus.id("textures/passive/legendary_might.png");
    }
}
