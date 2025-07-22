package sypztep.dominatus.client.data.provider;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.data.MobExpEntry;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ModMobDataGenerator implements DataProvider {

    private final FabricDataOutput output;

    public ModMobDataGenerator(FabricDataOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return generateMobExp(writer);
    }

    private CompletableFuture<?> generateMobExp(DataWriter writer) {
        CompletableFuture<?>[] futures = new CompletableFuture[]{
                // === HOSTILE MOBS (Level 5-15) ===
                // Basic undead (warrior type)
                createMobExpFile(writer, EntityType.ZOMBIE, MobExpEntry.warrior(50, 5)),
                createMobExpFile(writer, EntityType.ZOMBIE_VILLAGER, MobExpEntry.warrior(50, 5)),
                createMobExpFile(writer, EntityType.HUSK, MobExpEntry.warrior(120, 15)),
                createMobExpFile(writer, EntityType.DROWNED, MobExpEntry.warrior(70, 12)),

                // Ranged attackers (archer type)
                createMobExpFile(writer, EntityType.SKELETON, MobExpEntry.archer(220, 15)),
                createMobExpFile(writer, EntityType.STRAY, MobExpEntry.archer(250, 18)),

                // Agile mobs
                createMobExpFile(writer, EntityType.SPIDER, MobExpEntry.archer(150, 8)),
                createMobExpFile(writer, EntityType.CAVE_SPIDER, MobExpEntry.archer(120, 6)),

                // Explosive/special
                createMobExpFile(writer, EntityType.CREEPER, MobExpEntry.withLevelStats(350, 20)),

                // Magic users
                createMobExpEntry(writer, EntityType.WITCH, 1100, 30,5,15,30,45,40,20),
                createMobExpEntry(writer, EntityType.ENDERMAN, 1750, 15, 18, 25, 15, 30, 18, 22), // Teleporting entity

                // === NETHER MOBS (Level 15-25) ===
                createMobExpFile(writer, EntityType.ZOMBIFIED_PIGLIN, MobExpEntry.warrior(8, 15)),
                createMobExpFile(writer, EntityType.PIGLIN, MobExpEntry.warrior(10, 16)),
                createMobExpFile(writer, EntityType.PIGLIN_BRUTE, MobExpEntry.tank(15, 20)),
                createMobExpFile(writer, EntityType.BLAZE, MobExpEntry.mage(12, 18)),
                createMobExpFile(writer, EntityType.GHAST, MobExpEntry.mage(15, 20)),
                createMobExpFile(writer, EntityType.WITHER_SKELETON, MobExpEntry.warrior(12, 20)),
                createMobExpEntry(writer, EntityType.MAGMA_CUBE, 8, 15, 12, 8, 20, 8, 8, 10), // Tank-like
                createMobExpFile(writer, EntityType.HOGLIN, MobExpEntry.tank(10, 18)),
                createMobExpFile(writer, EntityType.ZOGLIN, MobExpEntry.tank(12, 20)),
                createMobExpFile(writer, EntityType.STRIDER, MobExpEntry.withLevelStats(3, 5)),

                // === END MOBS (Level 20-30) ===
                createMobExpFile(writer, EntityType.ENDERMITE, MobExpEntry.archer(5, 8)),
                createMobExpFile(writer, EntityType.SHULKER, MobExpEntry.tank(15, 25)),

                // === OCEAN MOBS (Level 10-20) ===
                createMobExpFile(writer, EntityType.GUARDIAN, MobExpEntry.tank(15, 18)),
                createMobExpFile(writer, EntityType.ELDER_GUARDIAN, MobExpEntry.boss(50, 35)),

                // === ILLAGERS (Level 12-25) ===
                createMobExpFile(writer, EntityType.PILLAGER, MobExpEntry.archer(8, 12)),
                createMobExpFile(writer, EntityType.VINDICATOR, MobExpEntry.warrior(10, 15)),
                createMobExpFile(writer, EntityType.EVOKER, MobExpEntry.mage(20, 25)),
                createMobExpFile(writer, EntityType.VEX, MobExpEntry.archer(5, 8)),
                createMobExpFile(writer, EntityType.RAVAGER, MobExpEntry.tank(25, 30)),
                createMobExpFile(writer, EntityType.ILLUSIONER, MobExpEntry.mage(18, 22)),

                // === SLIMES (Variable) ===
                createMobExpEntry(writer, EntityType.SLIME, 4, 6, 8, 4, 12, 4, 4, 6), // Tank-like

                // === BOSSES (Level 40-80) ===
                createMobExpFile(writer, EntityType.WITHER, MobExpEntry.boss(500, 60)),
                createMobExpEntry(writer, EntityType.ENDER_DRAGON, 1000, 80, 45, 35, 50, 40, 35, 40), // Unique boss
                createMobExpFile(writer, EntityType.WARDEN, MobExpEntry.boss(200, 70)),

                // === PASSIVE MOBS (Level 1-3) ===
                createMobExpFile(writer, EntityType.COW, MobExpEntry.withLevelStats(1, 2)),
                createMobExpFile(writer, EntityType.PIG, MobExpEntry.withLevelStats(1, 2)),
                createMobExpFile(writer, EntityType.SHEEP, MobExpEntry.withLevelStats(1, 2)),
                createMobExpFile(writer, EntityType.CHICKEN, MobExpEntry.withLevelStats(1, 1)),
                createMobExpFile(writer, EntityType.RABBIT, MobExpEntry.archer(1, 1)), // Fast
                createMobExpFile(writer, EntityType.HORSE, MobExpEntry.withLevelStats(2, 3)),
                createMobExpFile(writer, EntityType.DONKEY, MobExpEntry.withLevelStats(2, 3)),
                createMobExpFile(writer, EntityType.MULE, MobExpEntry.withLevelStats(2, 3)),
                createMobExpFile(writer, EntityType.LLAMA, MobExpEntry.withLevelStats(2, 3)),
                createMobExpFile(writer, EntityType.TRADER_LLAMA, MobExpEntry.withLevelStats(2, 3)),
                createMobExpFile(writer, EntityType.VILLAGER, MobExpEntry.withLevelStats(0, 1)),
                createMobExpFile(writer, EntityType.WANDERING_TRADER, MobExpEntry.withLevelStats(0, 2)),

                // === NEUTRAL MOBS (Level 2-8) ===
                createMobExpFile(writer, EntityType.WOLF, MobExpEntry.archer(3, 5)),
                createMobExpFile(writer, EntityType.POLAR_BEAR, MobExpEntry.tank(5, 8)),
                createMobExpFile(writer, EntityType.PANDA, MobExpEntry.tank(3, 6)),
                createMobExpFile(writer, EntityType.BEE, MobExpEntry.archer(2, 3)),
                createMobExpFile(writer, EntityType.GOAT, MobExpEntry.withLevelStats(3, 4)),
                createMobExpFile(writer, EntityType.AXOLOTL, MobExpEntry.withLevelStats(2, 3)),
                createMobExpFile(writer, EntityType.GLOW_SQUID, MobExpEntry.withLevelStats(2, 3)),
                createMobExpFile(writer, EntityType.SQUID, MobExpEntry.withLevelStats(2, 3)),

                // === 1.19+ MOBS ===
                createMobExpFile(writer, EntityType.ALLAY, MobExpEntry.mage(0, 5)),
                createMobExpFile(writer, EntityType.FROG, MobExpEntry.archer(2, 3)),
                createMobExpFile(writer, EntityType.TADPOLE, MobExpEntry.withLevelStats(1, 1)),

                // === 1.20+ MOBS ===
                createMobExpFile(writer, EntityType.CAMEL, MobExpEntry.withLevelStats(3, 5)),
                createMobExpFile(writer, EntityType.SNIFFER, MobExpEntry.withLevelStats(5, 8)),

                // === 1.21+ MOBS ===
                createMobExpFile(writer, EntityType.ARMADILLO, MobExpEntry.tank(2, 4)),
                createMobExpFile(writer, EntityType.BOGGED, MobExpEntry.archer(8, 12)),
                createMobExpFile(writer, EntityType.BREEZE, MobExpEntry.mage(15, 18))
        };

        return CompletableFuture.allOf(futures);
    }

    // Helper method for MobExpEntry
    private CompletableFuture<?> createMobExpFile(DataWriter writer, EntityType<?> entityType, MobExpEntry mobEntry) {
        return createMobExpFile(writer, entityType, mobEntry.expReward(), mobEntry.baseLevel(),
                mobEntry.stats().strength, mobEntry.stats().agility, mobEntry.stats().vitality,
                mobEntry.stats().intelligence, mobEntry.stats().dexterity, mobEntry.stats().luck);
    }

    // Custom stats method
    private CompletableFuture<?> createMobExpEntry(DataWriter writer, EntityType<?> entityType,
                                                   int expReward, int baseLevel, int str, int agi, int vit, int intel, int dex, int luck) {
        return createMobExpFile(writer, entityType, expReward, baseLevel, str, agi, vit, intel, dex, luck);
    }

    private CompletableFuture<?> createMobExpFile(DataWriter writer, EntityType<?> entityType,
                                                  int expReward, int baseLevel, int strength, int agility, int vitality,
                                                  int intelligence, int dexterity, int luck) {

        Identifier entityId = Registries.ENTITY_TYPE.getId(entityType);
        String namespace = entityId.getNamespace();
        String path = entityId.getPath();

        // Create the file path: data/dominatus/mobexp/namespace/path.json
        Path filePath = this.output.getPath()
                .resolve("data")
                .resolve(Dominatus.MODID)
                .resolve("mobexp")
                .resolve(namespace)
                .resolve(path + ".json");

        JsonObject jsonObject = createJsonObject(expReward, baseLevel, strength, agility, vitality, intelligence, dexterity, luck);

        return DataProvider.writeToPath(writer, jsonObject, filePath);
    }

    private JsonObject createJsonObject(int expReward, int baseLevel, int strength, int agility,
                                        int vitality, int intelligence, int dexterity, int luck) {
        JsonObject root = new JsonObject();
        root.addProperty("expReward", expReward);
        root.addProperty("baseLevel", baseLevel);

        JsonObject stats = new JsonObject();
        stats.addProperty("strength", strength);
        stats.addProperty("agility", agility);
        stats.addProperty("vitality", vitality);
        stats.addProperty("intelligence", intelligence);
        stats.addProperty("dexterity", dexterity);
        stats.addProperty("luck", luck);

        root.add("stats", stats);
        return root;
    }

    @Override
    public String getName() {
        return "Mob Exp and Stats Data";
    }
}