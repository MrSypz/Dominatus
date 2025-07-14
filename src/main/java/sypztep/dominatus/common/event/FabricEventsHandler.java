package sypztep.dominatus.common.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.DamageTrackerComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.util.stats.ExpUtil;

import java.util.Map;
import java.util.UUID;

public class FabricEventsHandler {

    public static void register() {
        // Register attack event to track damage
        AttackEntityCallback.EVENT.register(FabricEventsHandler::onPlayerAttackEntity);

        // Register death event to distribute experience
        ServerLivingEntityEvents.AFTER_DEATH.register(FabricEventsHandler::onEntityDeath);

        // Register entity load event to initialize max health
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register(FabricEventsHandler::onEntityLoad);
    }

    /**
     * Initialize damage tracker when entity loads
     */
    private static void onEntityLoad(net.minecraft.entity.Entity entity, net.minecraft.server.world.ServerWorld world) {
        // Only handle living entities (not players)
        if (!(entity instanceof LivingEntity livingEntity) || livingEntity instanceof PlayerEntity) {
            return;
        }

        DamageTrackerComponent tracker = ModEntityComponents.DAMAGETRACKER.getNullable(livingEntity);
        if (tracker != null) {
            // Initialize max health when entity is loaded (attributes should be ready now)
            tracker.setMaxHealth(livingEntity.getMaxHealth());
        }
    }

    /**
     * Track damage when player attacks an entity
     */
    private static ActionResult onPlayerAttackEntity(PlayerEntity player, World world, Hand hand,
                                                     net.minecraft.entity.Entity entity, EntityHitResult hitResult) {
        // Only handle on server side
        if (world.isClient()) {
            return ActionResult.PASS;
        }

        // Only track damage to living entities (not players)
        if (!(entity instanceof LivingEntity target) || target instanceof PlayerEntity) {
            return ActionResult.PASS;
        }

        DamageTrackerComponent tracker = ModEntityComponents.DAMAGETRACKER.getNullable(target);
        if (tracker == null) {
            return ActionResult.PASS;
        }

        // Calculate damage that would be dealt
        // This is a simplified calculation - you might want to make it more accurate
        float damage = calculatePlayerDamage(player, target);

        if (damage > 0) {
            // Only track actual damage that will be applied
            float actualDamage = Math.min(damage, target.getHealth());
            tracker.addDamage(player, actualDamage);

            Dominatus.LOGGER.debug("Player {} dealt {} damage to {}",
                    player.getName().getString(), actualDamage, target.getType().getName().getString());
        }

        return ActionResult.PASS;
    }

    /**
     * Calculate damage that a player would deal to a target
     * This is a simplified calculation based on attack damage attribute
     */
    private static float calculatePlayerDamage(PlayerEntity player, LivingEntity target) {
        // Get player's attack damage
        double attackDamage = player.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE);

        // Apply weapon damage if holding a weapon
        if (player.getMainHandStack() != null) {
            // This is simplified - in reality you'd need to check weapon damage more thoroughly
            return (float) attackDamage;
        }

        return (float) attackDamage;
    }

    /**
     * Handle entity death and distribute experience
     */
    private static void onEntityDeath(LivingEntity entity, DamageSource damageSource) {
        // Only handle non-player living entities on server side
        if (entity instanceof PlayerEntity || entity.getWorld().isClient()) {
            return;
        }

        DamageTrackerComponent tracker = ModEntityComponents.DAMAGETRACKER.getNullable(entity);
        if (tracker == null) {
            return;
        }

        Map<UUID, Float> damageMap = tracker.getAllDamage();
        if (damageMap.isEmpty()) {
            return;
        }

        ServerWorld world = (ServerWorld) entity.getWorld();
        String entityName = entity.getType().getName().getString();

        Dominatus.LOGGER.debug("Distributing exp for {} to {} players", entityName, damageMap.size());

        // Distribute experience to all players who dealt damage
        for (Map.Entry<UUID, Float> entry : damageMap.entrySet()) {
            UUID playerId = entry.getKey();
            float damageDealt = entry.getValue();

            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(playerId);
            if (player == null) {
                continue; // Player is offline
            }

            float damagePercentage = tracker.getDamagePercentage(player);
            if (damagePercentage <= 0) {
                continue;
            }

            int expReward = ExpUtil.calculateExpReward(player, entity, damagePercentage);

            if (expReward > 0) {
                // Award experience using utility method
                float percentage = damagePercentage * 100f;
                String source = String.format("%.1f%% damage to %s", percentage, entityName);

                ExpUtil.awardExperience(player, expReward, source);

                Dominatus.LOGGER.debug("Player {} gained {} exp from {} ({}% damage)",
                        player.getName().getString(), expReward, entityName, percentage);
            }
        }

        // Clear damage tracker
        tracker.clearDamage();
    }
}