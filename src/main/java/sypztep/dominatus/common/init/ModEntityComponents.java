package sypztep.dominatus.common.init;

import net.minecraft.entity.player.PlayerEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.FailStackComponent;
import sypztep.dominatus.common.component.GemDataComponent;

public final class ModEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<FailStackComponent> FAILSTACK_COMPONENT = ComponentRegistry.getOrCreate(Dominatus.id("failstack"), FailStackComponent.class);
    public static final ComponentKey<GemDataComponent> GEM_DATA_COMPONENT = ComponentRegistry.getOrCreate(Dominatus.id("gempresent"), GemDataComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, FAILSTACK_COMPONENT).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(FailStackComponent::new);
        registry.beginRegistration(PlayerEntity.class, GEM_DATA_COMPONENT).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(GemDataComponent::new);
    }
}
