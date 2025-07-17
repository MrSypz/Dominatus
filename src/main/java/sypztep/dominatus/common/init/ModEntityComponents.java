package sypztep.dominatus.common.init;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.living.DamageTrackerComponent;
import sypztep.dominatus.common.component.living.LivingLevelComponent;

public final class ModEntityComponents implements EntityComponentInitializer {
    public ModEntityComponents() {}
    public static final ComponentKey<LivingLevelComponent> LIVINGLEVEL = ComponentRegistry.getOrCreate(Dominatus.id("livinglevel"), LivingLevelComponent.class);
    public static final ComponentKey<DamageTrackerComponent> DAMAGETRACKER = ComponentRegistry.getOrCreate(Dominatus.id("dmgtracker"), DamageTrackerComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(LivingEntity.class, LIVINGLEVEL).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(LivingLevelComponent::new);
        registry.registerFor(MobEntity.class, DAMAGETRACKER, entity -> new DamageTrackerComponent());
    }
}
