package sypztep.dominatus.common.system.stat;

import net.minecraft.entity.LivingEntity;

public interface StatEffect {
    void applyPrimaryEffect(LivingEntity entity);
    void applySecondaryEffect(LivingEntity entity);
}
