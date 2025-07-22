package sypztep.dominatus.common.system.skill.passives.intelligence;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class ArcmagePassive extends BaseAttributePassive {
    public ArcmagePassive() {
        super(Dominatus.id("arcmage"), Text.literal("§9§lArchmage"), Text.literal("+100% Magic Damage"), "intelligence", 99, 6);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.MAGIC_ATTACK_DAMAGE, Dominatus.id("passive_arcmage"), 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.MAGIC_ATTACK_DAMAGE, Dominatus.id("passive_arcmage"));
    }
}
