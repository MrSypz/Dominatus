package sypztep.dominatus.common.event.applylivingstats;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.data.MobExpEntry;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.stat.EntityStatManager;

public class MobSpawnStatsEvent implements ServerEntityEvents.Load {

    private static final MobSpawnStatsEvent INSTANCE = new MobSpawnStatsEvent();

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register(INSTANCE);
    }

    @Override
    public void onLoad(Entity entity, ServerWorld world) {
        if (!(entity instanceof LivingEntity livingEntity) || entity instanceof PlayerEntity) return;

        MobExpEntry mobEntry = MobExpEntry.getEntry(livingEntity.getType());
        if (mobEntry == null) return;

        applyStaticMobStatsAndLevel(livingEntity, mobEntry);
    }

    /**
     * Apply static stats without dynamic scaling
     */
    private void applyStaticMobStatsAndLevel(LivingEntity entity, MobExpEntry mobEntry) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(entity);
        if (levelComponent == null) {
            return;
        }

        // Apply base level only
        levelComponent.setLevel(mobEntry.baseLevel());

        if (!levelComponent.isPlayer()) {
            EntityStatManager statManager = levelComponent.getEntityStatManager();
            if (statManager != null) {
                applyStatsToManager(statManager, mobEntry.stats());
                levelComponent.applyAllStatEffects();
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    /**
     * Apply stats with level bonus to stat manager
     */
    private void applyStatsToManager(EntityStatManager statManager, MobExpEntry.MobStats baseStats) {

        // Apply each stat with level bonus
        if (statManager.getStrength() != null) {
            int finalStr = Math.max(1, baseStats.strength);
            statManager.getStrength().setValue(finalStr);
        }

        if (statManager.getAgility() != null) {
            int finalAgi = Math.max(1, baseStats.agility);
            statManager.getAgility().setValue(finalAgi);
        }

        if (statManager.getVitality() != null) {
            int finalVit = Math.max(1, baseStats.vitality);
            statManager.getVitality().setValue(finalVit);
        }

        if (statManager.getIntelligence() != null) {
            int finalInt = Math.max(1, baseStats.intelligence);
            statManager.getIntelligence().setValue(finalInt);
        }

        if (statManager.getDexterity() != null) {
            int finalDex = Math.max(1, baseStats.dexterity);
            statManager.getDexterity().setValue(finalDex);
        }

        if (statManager.getLuck() != null) {
            int finalLuk = Math.max(1, baseStats.luck);
            statManager.getLuck().setValue(finalLuk);
        }
    }
}