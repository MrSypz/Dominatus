package sypztep.dominatus.common.system.skill.passives.luck;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class SerendipityPassive extends BaseAttributePassive {
    public SerendipityPassive() {
        super(Dominatus.id("serendipity"), Text.literal("§dSerendipity"), Text.literal("+5% All damage types"), "luck", 35, 3);
    }

    @Override
    protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, ModEntityAttributes.MELEE_ATTACK_DAMAGE, Dominatus.id("passive_serendipity_melee"), 0.05, EntityAttributeModifier.Operation.ADD_VALUE);
        applyAttributeModifier(entity, ModEntityAttributes.MAGIC_ATTACK_DAMAGE, Dominatus.id("passive_serendipity_magic"), 0.05, EntityAttributeModifier.Operation.ADD_VALUE);
        applyAttributeModifier(entity, ModEntityAttributes.PROJECTILE_ATTACK_DAMAGE, Dominatus.id("passive_serendipity_proj"), 0.05, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, ModEntityAttributes.MELEE_ATTACK_DAMAGE, Dominatus.id("passive_serendipity_melee"));
        removeAttributeModifier(entity, ModEntityAttributes.MAGIC_ATTACK_DAMAGE, Dominatus.id("passive_serendipity_magic"));
        removeAttributeModifier(entity, ModEntityAttributes.PROJECTILE_ATTACK_DAMAGE, Dominatus.id("passive_serendipity_proj"));
    }
}
