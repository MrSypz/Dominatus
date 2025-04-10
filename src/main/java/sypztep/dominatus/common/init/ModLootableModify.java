package sypztep.dominatus.common.init;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.loot.RandomGemComponentLootFunction;
import sypztep.dominatus.common.loot.SpecificGemComponentLootFunction;

import java.util.List;

public final class ModLootableModify {
    public static final LootFunctionType<RandomGemComponentLootFunction> RANDOM_GEM_FUNCTION_TYPE =
            Registry.register(
                    Registries.LOOT_FUNCTION_TYPE,
                    Dominatus.id("random_gem_component"),
                    new LootFunctionType<>(RandomGemComponentLootFunction.CODEC)
            );
    public static final LootFunctionType<SpecificGemComponentLootFunction> SPECIFIC_GEM_FUNCTION_TYPE =
            Registry.register(
                    Registries.LOOT_FUNCTION_TYPE,
                    Dominatus.id("specific_gem_component"),
                    new LootFunctionType<>(SpecificGemComponentLootFunction.CODEC)
            );

    public static void init() {
        LootTableEvents.MODIFY.register((id, tableBuilder, source, registries) -> {
            if (source.isBuiltin()) {
                // TRIAL_CHAMBERS_REWARD_CHEST (Normal chest - lowest tier)
                if (LootTables.TRIAL_CHAMBERS_REWARD_CHEST.equals(id)) {
                    // Moonlight Crescent: 1% chance, 2-4 items
                    LootPool.Builder moonlightPool = LootPool.builder()
                            .conditionally(RandomChanceLootCondition.builder(0.01f))
                            .rolls(UniformLootNumberProvider.create(2, 4))
                            .with(ItemEntry.builder(ModItems.MOONLIGHT_CRESCENT));
                    tableBuilder.pool(moonlightPool);

                    // Loss & Lahav Fragments: 2% chance, 1-5 items each
                    LootPool.Builder fragmentsPool = LootPool.builder()
                            .conditionally(RandomChanceLootCondition.builder(0.02f))
                            .rolls(UniformLootNumberProvider.create(1, 5))
                            .with(ItemEntry.builder(ModItems.LOSS_FRAGMENT))
                            .with(ItemEntry.builder(ModItems.LAHAV_FRAGMENT));
                    tableBuilder.pool(fragmentsPool);
                }

                // COMMON CHESTS
                else if (LootTables.TRIAL_CHAMBERS_REWARD_COMMON_CHEST.equals(id) ||
                        LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON_CHEST.equals(id)) {
                    // Moonlight Crescent: 10% chance, 3-8 items
                    LootPool.Builder moonlightPool = LootPool.builder()
                            .conditionally(RandomChanceLootCondition.builder(0.10f))
                            .rolls(UniformLootNumberProvider.create(3, 8))
                            .with(ItemEntry.builder(ModItems.MOONLIGHT_CRESCENT));
                    tableBuilder.pool(moonlightPool);

                    // Loss & Lahav Fragments: 5% chance, 5-13 items each
                    LootPool.Builder fragmentsPool = LootPool.builder()
                            .conditionally(RandomChanceLootCondition.builder(0.05f))
                            .rolls(UniformLootNumberProvider.create(5, 13))
                            .with(ItemEntry.builder(ModItems.LOSS_FRAGMENT))
                            .with(ItemEntry.builder(ModItems.LAHAV_FRAGMENT));
                    tableBuilder.pool(fragmentsPool);
                }

                // RARE CHESTS
                else if (LootTables.TRIAL_CHAMBERS_REWARD_RARE_CHEST.equals(id) ||
                        LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE_CHEST.equals(id)) {
                    // Moonlight Crescent: 50% chance, 4-16 items
                    LootPool.Builder moonlightPool = LootPool.builder()
                            .conditionally(RandomChanceLootCondition.builder(0.50f))
                            .rolls(UniformLootNumberProvider.create(4, 16))
                            .with(ItemEntry.builder(ModItems.MOONLIGHT_CRESCENT));
                    tableBuilder.pool(moonlightPool);

                    // Loss & Lahav Fragments: 7.5% chance, 10-19 items each
                    LootPool.Builder fragmentsPool = LootPool.builder()
                            .conditionally(RandomChanceLootCondition.builder(0.075f))
                            .rolls(UniformLootNumberProvider.create(10, 19))
                            .with(ItemEntry.builder(ModItems.LOSS_FRAGMENT))
                            .with(ItemEntry.builder(ModItems.LAHAV_FRAGMENT));
                    tableBuilder.pool(fragmentsPool);
                }

                // UNIQUE CHESTS
                else if (LootTables.TRIAL_CHAMBERS_REWARD_UNIQUE_CHEST.equals(id) ||
                        LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE_CHEST.equals(id)) {
                    // Moonlight Crescent: 100% chance (guaranteed), 5-32 items
                    LootPool.Builder moonlightPool = LootPool.builder()
                            .rolls(UniformLootNumberProvider.create(5, 32))
                            .with(ItemEntry.builder(ModItems.MOONLIGHT_CRESCENT));
                    tableBuilder.pool(moonlightPool);

                    // Loss & Lahav Fragments: 15% chance, 18-26 items each
                    LootPool.Builder fragmentsPool = LootPool.builder()
                            .conditionally(RandomChanceLootCondition.builder(0.15f))
                            .rolls(UniformLootNumberProvider.create(18, 26))
                            .with(ItemEntry.builder(ModItems.LOSS_FRAGMENT))
                            .with(ItemEntry.builder(ModItems.LAHAV_FRAGMENT));
                    tableBuilder.pool(fragmentsPool);
                }
                if (isHostileMobLootTable(id)) {
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
                // Define chest-specific gem types and chances
                if (LootTables.PILLAGER_OUTPOST_CHEST.equals(id) || LootTables.DESERT_PYRAMID_CHEST.equals(id)) {
                    String[] gemTypes = {"accuracy", "evasion"};
                    tierMap(tableBuilder, gemTypes);
                } else if (LootTables.BASTION_TREASURE_CHEST.equals(id)) {
                    String[] gemTypes = {"goliath"};
                    rareTierMap(tableBuilder, gemTypes);
                } else if (LootTables.ABANDONED_MINESHAFT_CHEST.equals(id) || LootTables.SPAWN_BONUS_CHEST.equals(id)) {
                    String[] gemTypes = {"miner"};
                    tierMap(tableBuilder, gemTypes);
                } else if (LootTables.ANCIENT_CITY_CHEST.equals(id)) {
                    String[] gemTypes = {"accuracy", "evasion", "goliath"}; // Added evasion and goliath
                    rareTierMap(tableBuilder, gemTypes);
                }
            }
        });
    }

    private static void tierMap(LootTable.Builder tableBuilder, String[] gemTypes) {
        String[] tiers = {"pri", "duo", "tri"};
        float[] chances = {0.08f, 0.04f, 0.02f}; // 8%, 4%, 2%

        getGemType(tableBuilder, gemTypes, tiers, chances);
    }

    private static void rareTierMap(LootTable.Builder tableBuilder, String[] gemTypes) {
        String[] tiers = {"pri", "duo", "tri"};
        float[] chances = {0.12f, 0.08f, 0.04f}; // 12%, 8%, 4% (higher rates like Bastion)

        getGemType(tableBuilder, gemTypes, tiers, chances);
    }

    private static void getGemType(LootTable.Builder tableBuilder, String[] gemTypes, String[] tiers, float[] chances) {
        for (String gemType : gemTypes) {
            for (int i = 0; i < tiers.length; i++) {
                LootPool.Builder pool = LootPool.builder()
                        .conditionally(RandomChanceLootCondition.builder(chances[i]))
                        .rolls(UniformLootNumberProvider.create(1, 1))
                        .with(ItemEntry.builder(ModItems.GEM)
                                .apply(new SpecificGemComponentLootFunction.Builder(
                                        Dominatus.id(tiers[i] + "_" + gemType))));
                tableBuilder.pool(pool);
            }
        }
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
