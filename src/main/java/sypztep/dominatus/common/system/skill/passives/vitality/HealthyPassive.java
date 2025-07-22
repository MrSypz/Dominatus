package sypztep.dominatus.common.system.skill.passives.vitality;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.BaseAttributePassive;

public class HealthyPassive extends BaseAttributePassive {
    public HealthyPassive() {
        super(Dominatus.id("healthy"), Text.literal("§cHealthy"), Text.literal("+20 Max Health"), "vitality", 10, 1);
    }
    @Override protected void applyAttributeModifiers(LivingEntity entity) {
        applyAttributeModifier(entity, EntityAttributes.GENERIC_MAX_HEALTH, Dominatus.id("passive_healthy"), 20.0, EntityAttributeModifier.Operation.ADD_VALUE);
    }
    @Override protected void removeAttributeModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.GENERIC_MAX_HEALTH, Dominatus.id("passive_healthy"));
    }
}

