package sypztep.dominatus.common.event.applymobstats;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class LivingEntityEvent implements ServerEntityEvents.Load{
    @Override
    public void onLoad(Entity entity, ServerWorld serverWorld) {

    }
//    private <T> void applyStatsForMobType(LivingEntity living, EntityType<?> mobEntityType, Map<EntityType<?>, T> statsMap) {
//        T entry = statsMap.get(mobEntityType);
//        if (entry == null) return;
//
//        if (entry instanceof BaseMobStatsEntry baseMobStatsEntry) {
//            UniqueStatsComponent uniqueStatsComponent = ModEntityComponents.UNIQUESTATS.get(living);
//            LivingStats livingStats = uniqueStatsComponent.getLivingStats();
//
//            if (!(living.getWorld() instanceof ServerWorld serverWorld)) {
//                return;
//            }
//
//            List<PlayerEntity> players = PlayerEntityUtils.getPlayersInChunk(serverWorld, living.getChunkPos().x, living.getChunkPos().z, 1);
//            int totalLevel = 0;
//
//            int playerCount = 0;
//            for (PlayerEntity player : players) {
//                if (player instanceof ServerPlayerEntity serverPlayer) {
//                    UniqueStatsComponent playerStats = ModEntityComponents.UNIQUESTATS.get(serverPlayer);
//                    totalLevel += playerStats.getLevel();
//                    playerCount++;
//                }
//            }
//
//            int meanLevel = playerCount > 0 ? totalLevel / playerCount : 0;
//
//            Map<StatTypes, Integer> localStatsMap = Map.of(
//                    StatTypes.STRENGTH, baseMobStatsEntry.str(),
//                    StatTypes.AGILITY, baseMobStatsEntry.agi(),
//                    StatTypes.DEXTERITY, baseMobStatsEntry.dex(),
//                    StatTypes.VITALITY, baseMobStatsEntry.vit(),
//                    StatTypes.INTELLIGENCE, baseMobStatsEntry.anint(),
//                    StatTypes.LUCK, baseMobStatsEntry.luk()
//            );
//
//            List<StatTypes> statTypes = new ArrayList<>(localStatsMap.keySet());
//
//            for (StatTypes statType : statTypes) {
//                Stat stat = livingStats.getStat(statType);
//                stat.setPoints(localStatsMap.get(statType) + meanLevel);
//                stat.applyPrimaryEffect(living);
//                stat.applySecondaryEffect(living);
//            }
//
//            living.setHealth(living.getMaxHealth());
//            livingStats.getLevelSystem().setLevel(baseMobStatsEntry.lvl() + meanLevel);
//        }
//    }
}
