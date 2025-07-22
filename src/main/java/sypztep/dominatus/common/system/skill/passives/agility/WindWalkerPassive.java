package sypztep.dominatus.common.system.skill.passives.agility;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

/**
 * AGI 75 - Wind Walker: No fall damage + Jump Boost
 */
public class WindWalkerPassive extends BaseAttributePassive {
    public WindWalkerPassive() {
        super(
                Dominatus.id("wind_walker"),
                Text.literal("§bWind Walker"),
                Text.literal("Immunity to fall damage and increased jump height"),
                "agility",
                75,
                5
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        // Apply jump boost and safe fall
        applyAttributeModifier(entity, EntityAttributes.GENERIC_JUMP_STRENGTH, Dominatus.id("passive_wind_walker_jump"), 0.3, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        applyAttributeModifier(entity, EntityAttributes.GENERIC_SAFE_FALL_DISTANCE, Dominatus.id("passive_wind_walker_fall"), 999.0, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.GENERIC_JUMP_STRENGTH, Dominatus.id("passive_wind_walker_jump"));
        removeAttributeModifier(entity, EntityAttributes.GENERIC_SAFE_FALL_DISTANCE, Dominatus.id("passive_wind_walker_fall"));
    }
}
