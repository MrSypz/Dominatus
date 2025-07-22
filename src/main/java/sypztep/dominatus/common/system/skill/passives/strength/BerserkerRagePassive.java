package sypztep.dominatus.common.system.skill.passives.strength;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

/**
 * STR 50 - Berserker Rage: +5% Attack Speed per missing 10% HP
 */
public class BerserkerRagePassive extends BaseAttributePassive {
    public BerserkerRagePassive() {
        super(
                Dominatus.id("berserker_rage"),
                Text.literal("§cBerserker Rage"),
                Text.literal("Gain 5% attack speed for every 10% health missing"),
                "strength",
                50,
                4
        );
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        // This would need special handling in a tick event or health change event
        // For now, we'll apply a base bonus
        updateBerserkerBonus(entity);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.GENERIC_ATTACK_SPEED, Dominatus.id("passive_berserker_rage"));
    }

    private void updateBerserkerBonus(LivingEntity entity) {
        float healthPercentage = entity.getHealth() / entity.getMaxHealth();
        float missingHealthPercentage = 1.0f - healthPercentage;
        int bonusStacks = (int) (missingHealthPercentage * 10); // Every 10% missing = 1 stack
        double attackSpeedBonus = bonusStacks * 0.05; // 5% per stack

        applyAttributeModifier(entity, EntityAttributes.GENERIC_ATTACK_SPEED,
                Dominatus.id("passive_berserker_rage"), attackSpeedBonus, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }
}
