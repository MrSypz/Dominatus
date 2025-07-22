package sypztep.dominatus.common.system.skill.passives.dexterity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class MarksmanPassive extends BaseAttributePassive {
    public MarksmanPassive() {
        super(Dominatus.id("marksman"), Text.literal("§eMarksman"), Text.literal("+25% Projectile Damage"), "dexterity", 50, 4);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.PROJECTILE_ATTACK_DAMAGE, Dominatus.id("passive_marksman"), 0.25, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.PROJECTILE_ATTACK_DAMAGE, Dominatus.id("passive_marksman"));
    }
}
