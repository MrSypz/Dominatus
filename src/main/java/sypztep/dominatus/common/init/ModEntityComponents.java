package sypztep.dominatus.common.init;

import net.minecraft.entity.player.PlayerEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.FailStackComponent;

public class ModEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<FailStackComponent> FAILSTACK_COMPONENT = ComponentRegistry.getOrCreate(Dominatus.id("failstack"), FailStackComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, FAILSTACK_COMPONENT).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(FailStackComponent::new);
    }
}
