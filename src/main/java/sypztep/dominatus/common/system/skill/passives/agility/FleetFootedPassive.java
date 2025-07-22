package sypztep.dominatus.common.system.skill.passives.agility;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

/**
 * AGI 35 - Fleet Footed: +20% Attack Speed
 */
public class FleetFootedPassive extends BaseAttributePassive {
    private static final Identifier MODIFIER_ID = Dominatus.id("passive_fleet_footed");

    public FleetFootedPassive() {
        super(
                Dominatus.id("fleet_footed"),
                Text.literal("§aFleet Footed"),
                Text.literal("Your agility grants 20% attack speed"),
                "agility",
                35,
                3
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, EntityAttributes.GENERIC_ATTACK_SPEED, MODIFIER_ID, 0.2, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.GENERIC_ATTACK_SPEED, MODIFIER_ID);
    }
}
