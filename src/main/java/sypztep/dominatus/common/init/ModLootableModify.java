package sypztep.dominatus.common.init;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;

import java.util.List;

public class ModLootableModify {
    public static void init() {
        LootTableEvents.MODIFY.register((id, tableBuilder, source, registries) -> {
            if (source.isBuiltin()) {
                LootPool.Builder lootPool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(1,10));
                LootPool.Builder builder = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(1,3));
                if (LootTables.TRIAL_CHAMBERS_REWARD_CHEST.equals(id)) {
                    lootPool.with(ItemEntry.builder(ModItems.MOONLIGHT_CRESCENT));
                    tableBuilder.pool(lootPool);
                    builder.with(ItemEntry.builder(ModItems.LOSS_FRAGMENT));
                    builder.with(ItemEntry.builder(ModItems.LAHAV_FRAGMENT));
                    tableBuilder.pool(builder);
                }
            }
            if (source.isBuiltin() && isHostileMobLootTable(id)) {
                LootPool.Builder refine_weapon = LootPool.builder()
                        .conditionally(RandomChanceLootCondition.builder(0.05f)) // 5% chance this pool activates
                        .rolls(UniformLootNumberProvider.create(1, 3)) // If activated, drops 1-3 items
                        .conditionally(KilledByPlayerLootCondition.builder())
                        .with(ItemEntry.builder(ModItems.REFINE_WEAPON_STONE));
                LootPool.Builder refine_armor = LootPool.builder()
                        .conditionally(RandomChanceLootCondition.builder(0.05f)) // 5% chance this pool activates
                        .rolls(UniformLootNumberProvider.create(1, 3)) // If activated, drops 1-3 items
                        .conditionally(KilledByPlayerLootCondition.builder())
                        .with(ItemEntry.builder(ModItems.REFINE_ARMOR_STONE));

                tableBuilder.pool(refine_weapon);
                tableBuilder.pool(refine_armor);
            }
        });
    }
    private static final List<EntityType<?>> HOSTILE_MOBS = List.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.CREEPER,
            EntityType.SPIDER,
            EntityType.ENDERMAN,
            EntityType.BLAZE,
            EntityType.WITHER_SKELETON,
            EntityType.WITCH,
            EntityType.HUSK,
            EntityType.STRAY,
            EntityType.PHANTOM,
            EntityType.DROWNED,
            EntityType.PILLAGER,
            EntityType.VINDICATOR,
            EntityType.EVOKER,
            EntityType.VEX,
            EntityType.RAVAGER,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.GHAST,
            EntityType.HOGLIN,
            EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.MAGMA_CUBE,
            EntityType.SLIME,
            EntityType.GUARDIAN,
            EntityType.ELDER_GUARDIAN,
            EntityType.SILVERFISH,
            EntityType.SHULKER,
            EntityType.ENDERMITE,
            EntityType.WITHER,
            EntityType.ENDER_DRAGON
    );

    private static boolean isHostileMobLootTable(RegistryKey<LootTable> id) {
        for (EntityType<?> entityType : HOSTILE_MOBS) {
            if (entityType.getLootTableId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
