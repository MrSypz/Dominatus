package sypztep.dominatus.common.compat;

import net.minecraft.entity.LivingEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import sypztep.dominatus.common.compat.provider.EntityComponentProvider;

public final class JadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(EntityComponentProvider.INSTANCE, LivingEntity.class);

    }
}
