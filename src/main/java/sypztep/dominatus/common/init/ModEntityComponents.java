package sypztep.dominatus.common.init;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.PassiveEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.DamageTrackerComponent;
import sypztep.dominatus.common.component.LivingLevelComponent;

public class ModEntityComponents implements EntityComponentInitializer {
    public ModEntityComponents() {}
    public static final ComponentKey<LivingLevelComponent> LIVINGLEVEL = ComponentRegistry.getOrCreate(Dominatus.id("livinglevel"), LivingLevelComponent.class);
    public static final ComponentKey<DamageTrackerComponent> DAMAGETRACKER = ComponentRegistry.getOrCreate(Dominatus.id("dmgtracker"), DamageTrackerComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(LivingEntity.class, LIVINGLEVEL).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(LivingLevelComponent::new);
        registry.registerFor(MobEntity.class, DAMAGETRACKER, DamageTrackerComponent::new);
//        registry.registerFor(PassiveEntity.class, DAMAGETRACKER, DamageTrackerComponent::new);
//        registry.registerFor(AmbientEntity.class, DAMAGETRACKER, DamageTrackerComponent::new);
//        registry.registerFor(WaterCreatureEntity.class, DAMAGETRACKER, DamageTrackerComponent::new);
    }
}
