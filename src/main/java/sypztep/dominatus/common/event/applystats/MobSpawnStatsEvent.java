package sypztep.dominatus.common.event.applystats;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.data.MobExpEntry;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.stat.EntityStatManager;
import sypztep.dominatus.common.util.PlayerEntityUtils;

import java.util.List;
import java.util.Set;

public class MobSpawnStatsEvent implements ServerEntityEvents.Load {

    private static final MobSpawnStatsEvent INSTANCE = new MobSpawnStatsEvent();

    // Boss entity types (these get special treatment)
    private static final Set<EntityType<?>> BOSS_ENTITIES = Set.of(
            EntityType.ENDER_DRAGON,
            EntityType.WITHER,
            EntityType.WARDEN,
            EntityType.ELDER_GUARDIAN
    );

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register(INSTANCE);
    }

    @Override
    public void onLoad(Entity entity, ServerWorld world) {
        // Only apply to living entities that are not players
        if (!(entity instanceof LivingEntity livingEntity) || entity instanceof PlayerEntity) {
            return;
        }

        // Check if we have mob data for this entity type
        MobExpEntry mobEntry = MobExpEntry.getEntry(livingEntity.getType());
        if (mobEntry == null) {
            return;
        }

        // Check config settings
        if (!ModConfig.enableDynamicMobScaling) {
            applyStaticMobStatsAndLevel(livingEntity, mobEntry);
            return;
        }

        // Determine mob type for scaling rules
        boolean isBoss = isBossEntity(livingEntity);
        boolean isPassive = isPassiveEntity(livingEntity);

        // Check if we should scale this mob type
        if (!ModConfig.shouldScaleMob(isBoss, isPassive)) {
            applyStaticMobStatsAndLevel(livingEntity, mobEntry);
            return;
        }

        try {
            applyDynamicMobStatsAndLevel(livingEntity, mobEntry, world, isBoss, isPassive);
        } catch (Exception e) {
            Dominatus.LOGGER.error("Failed to apply dynamic stats to mob {}: {}",
                    livingEntity.getType().getName().getString(), e.getMessage());
            // Fallback to static stats
            applyStaticMobStatsAndLevel(livingEntity, mobEntry);
        }
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
        levelComponent.setLevel(Math.max(ModConfig.minMobLevel, mobEntry.baseLevel()));

        // Apply static stats
        if (!levelComponent.isPlayer()) {
            EntityStatManager statManager = levelComponent.getEntityStatManager();
            if (statManager != null) {
                applyStatsToManager(statManager, mobEntry.stats(), 0, entity);
                levelComponent.applyAllStatEffects();
                entity.setHealth(entity.getMaxHealth());
            }
        }

        levelComponent.sync();
    }

    /**
     * Apply dynamic stats with player-based scaling
     */
    private void applyDynamicMobStatsAndLevel(LivingEntity entity, MobExpEntry mobEntry,
                                              ServerWorld world, boolean isBoss, boolean isPassive) {

        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(entity);
        if (levelComponent == null) {
            return;
        }

        // Calculate dynamic level scaling based on nearby players
        int levelBonus = calculatePlayerBasedLevelBonus(entity, world, isBoss, isPassive);

        // Apply dynamic level (base level + player scaling)
        int finalLevel = Math.max(ModConfig.minMobLevel, mobEntry.baseLevel() + levelBonus);
        levelComponent.setLevel(finalLevel);

        // Apply dynamic stats if this is not a player
        if (!levelComponent.isPlayer()) {
            EntityStatManager statManager = levelComponent.getEntityStatManager();
            if (statManager != null) {
                applyStatsToManager(statManager, mobEntry.stats(), levelBonus, entity);

                // Apply all effects after setting stats
                levelComponent.applyAllStatEffects();

                // Set mob to full health after stat changes
                entity.setHealth(entity.getMaxHealth());

                Dominatus.LOGGER.debug("Applied dynamic stats to {} (Level: {} + {} bonus): {}",
                        entity.getType().getName().getString(), mobEntry.baseLevel(), levelBonus, mobEntry.stats());
            }
        }

        levelComponent.sync();
    }

    /**
     * Calculate level bonus based on nearby players with configurable parameters
     */
    private int calculatePlayerBasedLevelBonus(LivingEntity entity, ServerWorld world,
                                               boolean isBoss, boolean isPassive) {

        List<PlayerEntity> nearbyPlayers;

        // Use either chunk-based or radius-based player detection
        if (ModConfig.mobScalingBlockRadius > 0) {
            nearbyPlayers = PlayerEntityUtils.getPlayersInRadius(
                    world, entity.getX(), entity.getY(), entity.getZ(), ModConfig.mobScalingBlockRadius);
        } else {
            nearbyPlayers = PlayerEntityUtils.getPlayersInChunk(
                    world, entity.getChunkPos().x, entity.getChunkPos().z, ModConfig.mobScalingChunkRadius);
        }

        if (nearbyPlayers.isEmpty()) {
            return 0; // No players nearby, no bonus
        }

        int totalLevel = 0;
        int playerCount = 0;

        for (PlayerEntity player : nearbyPlayers) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                LivingLevelComponent playerLevelComponent = ModEntityComponents.LIVINGLEVEL.getNullable(serverPlayer);
                if (playerLevelComponent != null) {
                    totalLevel += playerLevelComponent.getLevel();
                    playerCount++;
                }
            }
        }

        if (playerCount == 0) {
            return 0;
        }

        // Calculate mean player level
        int meanPlayerLevel = totalLevel / playerCount;

        // Apply scaling factor from config
        float scalingFactor = ModConfig.getMobScalingFactor();

        // Apply different scaling for different mob types
        if (isBoss) {
            scalingFactor *= 0.8f; // Bosses scale slightly less aggressively
        } else if (isPassive) {
            scalingFactor *= 0.3f; // Passive mobs scale much less
        }

        // Calculate and cap the bonus
        int levelBonus = Math.round(meanPlayerLevel * scalingFactor);
        levelBonus = Math.min(levelBonus, ModConfig.maxMobLevelBonus);

        Dominatus.LOGGER.debug("Calculated level bonus for {} ({}): {} players nearby (avg level {}), factor: {}, bonus: {}",
                entity.getType().getName().getString(),
                isBoss ? "boss" : isPassive ? "passive" : "hostile",
                playerCount, meanPlayerLevel, scalingFactor, levelBonus);

        return levelBonus;
    }

    /**
     * Apply stats with level bonus to stat manager
     */
    private void applyStatsToManager(EntityStatManager statManager, MobExpEntry.MobStats baseStats,
                                     int levelBonus, LivingEntity entity) {

        // Apply each stat with level bonus
        if (statManager.getStrength() != null) {
            int finalStr = Math.max(1, baseStats.strength + levelBonus);
            statManager.getStrength().setValue(finalStr);
        }

        if (statManager.getAgility() != null) {
            int finalAgi = Math.max(1, baseStats.agility + levelBonus);
            statManager.getAgility().setValue(finalAgi);
        }

        if (statManager.getVitality() != null) {
            int finalVit = Math.max(1, baseStats.vitality + levelBonus);
            statManager.getVitality().setValue(finalVit);
        }

        if (statManager.getIntelligence() != null) {
            int finalInt = Math.max(1, baseStats.intelligence + levelBonus);
            statManager.getIntelligence().setValue(finalInt);
        }

        if (statManager.getDexterity() != null) {
            int finalDex = Math.max(1, baseStats.dexterity + levelBonus);
            statManager.getDexterity().setValue(finalDex);
        }

        if (statManager.getLuck() != null) {
            int finalLuk = Math.max(1, baseStats.luck + levelBonus);
            statManager.getLuck().setValue(finalLuk);
        }

        if (levelBonus > 0) {
            Dominatus.LOGGER.debug("Dynamic stats applied to {}: STR={}, AGI={}, VIT={}, INT={}, DEX={}, LUK={} (base + {} bonus)",
                    entity.getType().getName().getString(),
                    baseStats.strength + levelBonus, baseStats.agility + levelBonus, baseStats.vitality + levelBonus,
                    baseStats.intelligence + levelBonus, baseStats.dexterity + levelBonus, baseStats.luck + levelBonus,
                    levelBonus);
        }
    }

    /**
     * Check if entity is a boss mob
     */
    private boolean isBossEntity(LivingEntity entity) {
        return BOSS_ENTITIES.contains(entity.getType());
    }

    /**
     * Check if entity is a passive mob
     */
    private boolean isPassiveEntity(LivingEntity entity) {
        return !(entity instanceof HostileEntity) && entity instanceof MobEntity;
    }
}