package sypztep.dominatus.client.data.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import com.google.gson.JsonObject;
import sypztep.dominatus.Dominatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EntityAttributesDataProvider implements DataProvider {
    private final FabricDataOutput output;

    public EntityAttributesDataProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.addAll(generateHostileMobs(writer));
        futures.addAll(generateBossMobs(writer));
        futures.addAll(generateNeutralMobs(writer));
        futures.addAll(generatePassiveMobs(writer));
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private List<CompletableFuture<?>> generateHostileMobs(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Early Game (Green Grade Zone - Refine 0-5)
        // Player accuracy: 20-120, so mobs need ~150 accuracy to maintain advantage
        futures.add(generateEntityData(writer, EntityType.ZOMBIE, 150.0, 35.0, 0.05, 1.5, 0.35, 0.0, 0.0));       // More reliable hits
        futures.add(generateEntityData(writer, EntityType.SKELETON, 160.0, 40.0, 0.15, 2.0, 0.0, 0.0, 0.0));      // Archer accuracy
        futures.add(generateEntityData(writer, EntityType.SPIDER, 145.0, 45.0, 0.08, 1.5, 0.20, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.CREEPER, 140.0, 50.0, 0.0, 1.0, 0.75, 0.0, 0.0));

        // Basic Variants (Slightly stronger)
        futures.add(generateEntityData(writer, EntityType.HUSK, 155.0, 38.0, 0.06, 1.6, 0.40, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.DROWNED, 155.0, 42.0, 0.07, 1.7, 0.30, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.ZOMBIE_VILLAGER, 150.0, 35.0, 0.05, 1.5, 0.35, 0.0, 0.0));

        // Mid Game (Blue Grade Zone - Refine 7-15)
        // Player accuracy: 60-138, so mobs need ~200 accuracy
        futures.add(generateEntityData(writer, EntityType.STRAY, 200.0, 65.0, 0.18, 2.2, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.CAVE_SPIDER, 190.0, 70.0, 0.10, 1.6, 0.25, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.SLIME, 180.0, 60.0, 0.05, 1.2, 0.0, 0.0, 0.35));
        futures.add(generateEntityData(writer, EntityType.MAGMA_CUBE, 185.0, 75.0, 0.08, 1.5, 0.0, 0.0, 0.35));

        // Nether Mobs (Higher accuracy for harder area)
        futures.add(generateEntityData(writer, EntityType.BLAZE, 220.0, 80.0, 0.12, 1.8, 0.0, 0.45, 0.0));
        futures.add(generateEntityData(writer, EntityType.GHAST, 210.0, 70.0, 0.10, 1.7, 0.0, 0.50, 0.0));
        futures.add(generateEntityData(writer, EntityType.PIGLIN, 215.0, 85.0, 0.12, 1.8, 0.30, 0.0, 0.0));

        // Late Game (Yellow Grade Zone - Refine 16-18)
        // Player accuracy: 78-185, so mobs need ~250-300 accuracy
        futures.add(generateEntityData(writer, EntityType.WITHER_SKELETON, 280.0, 110.0, 0.20, 2.5, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.HOGLIN, 270.0, 115.0, 0.15, 2.0, 0.45, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.ZOGLIN, 275.0, 120.0, 0.18, 2.2, 0.50, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.PIGLIN_BRUTE, 290.0, 125.0, 0.20, 2.5, 0.40, 0.0, 0.0));

        // Illagers (Elite fighters)
        futures.add(generateEntityData(writer, EntityType.PILLAGER, 275.0, 115.0, 0.15, 2.0, 0.25, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.VINDICATOR, 285.0, 120.0, 0.18, 2.2, 0.35, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.EVOKER, 290.0, 125.0, 0.20, 2.5, 0.30, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.ILLUSIONER, 295.0, 130.0, 0.22, 2.8, 0.25, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.RAVAGER, 300.0, 135.0, 0.15, 2.5, 0.40, 0.0, 0.30));

        // Water Guardians
        futures.add(generateEntityData(writer, EntityType.GUARDIAN, 260.0, 100.0, 0.15, 2.0, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.ELDER_GUARDIAN, 320.0, 140.0, 0.25, 3.0, 0.0, 0.0, 0.0));

        // Trial Chamber Mobs (1.21) - Late Game Zone
        // Breeze - Wind-based ranged attacker
        futures.add(generateEntityData(writer, EntityType.BREEZE, 285.0, 125.0, 0.18, 2.3, 0.0, 0.55, 0.0));
        // Bogged - ranged attacker
        futures.add(generateEntityData(writer, EntityType.BOGGED, 295.0, 120.0, 0.22, 2.5, 0.30, 0.0, 0.0));

        return futures;
    }

    private List<CompletableFuture<?>> generateBossMobs(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Endgame Bosses (Boss Grade Zone - Refine 18+)
        // Player accuracy: 90-220+, so bosses need 400+ accuracy
        futures.add(generateEntityData(writer, EntityType.ENDER_DRAGON, 450.0, 180.0, 0.30, 3.5, 0.40, 0.60, 0.0)); // Guaranteed hits on most players
        futures.add(generateEntityData(writer, EntityType.WITHER, 425.0, 170.0, 0.35, 3.8, 0.35, 0.55, 0.0));
        futures.add(generateEntityData(writer, EntityType.WARDEN, 500.0, 190.0, 0.40, 4.0, 0.50, 0.0, 0.35));      // Highest accuracy

        return futures;
    }

    private List<CompletableFuture<?>> generateNeutralMobs(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Mid-Late Game Neutral Mobs
        futures.add(generateEntityData(writer, EntityType.ENDERMAN, 300.0, 130.0, 0.20, 2.5, 0.45, 0.0, 0.0));   // Elite mob level accuracy

        // Pack Hunters (Mid Game)
        futures.add(generateEntityData(writer, EntityType.WOLF, 200.0, 75.0, 0.12, 1.8, 0.40, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.POLAR_BEAR, 210.0, 80.0, 0.15, 2.0, 0.35, 0.0, 0.0));

        // Defensive Mobs
        futures.add(generateEntityData(writer, EntityType.BEE, 180.0, 85.0, 0.08, 1.5, 0.25, 0.30, 0.0));
        futures.add(generateEntityData(writer, EntityType.GOAT, 185.0, 70.0, 0.15, 2.0, 0.40, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.LLAMA, 175.0, 65.0, 0.10, 1.6, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.PANDA, 170.0, 60.0, 0.12, 1.8, 0.30, 0.0, 0.0));

        // Constructs
        futures.add(generateEntityData(writer, EntityType.IRON_GOLEM, 250.0, 100.0, 0.15, 2.2, 0.35, 0.0, 0.25));
        futures.add(generateEntityData(writer, EntityType.SNOW_GOLEM, 160.0, 55.0, 0.08, 1.5, 0.0, 0.0, 0.0));

        return futures;
    }

    private List<CompletableFuture<?>> generatePassiveMobs(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Basic Land Animals (Low Evasion)
        futures.add(generateEntityData(writer, EntityType.COW, 50.0, 25.0, 0.0, 0.0, 0.0, 0.0, 0.0));          // Hit chance: 55-83%
        futures.add(generateEntityData(writer, EntityType.SHEEP, 50.0, 25.0, 0.0, 0.0, 0.0, 0.0, 0.0));        // Hit chance: 55-83%
        futures.add(generateEntityData(writer, EntityType.PIG, 50.0, 30.0, 0.0, 0.0, 0.0, 0.0, 0.0));          // Hit chance: 52-80%

        // More Agile Animals
        futures.add(generateEntityData(writer, EntityType.HORSE, 50.0, 45.0, 0.0, 0.0, 0.0, 0.0, 0.0));        // Hit chance: 47-73%
        futures.add(generateEntityData(writer, EntityType.DONKEY, 50.0, 35.0, 0.0, 0.0, 0.0, 0.0, 0.0));       // Hit chance: 50-77%
        futures.add(generateEntityData(writer, EntityType.MULE, 50.0, 35.0, 0.0, 0.0, 0.0, 0.0, 0.0));         // Hit chance: 50-77%
        futures.add(generateEntityData(writer, EntityType.RABBIT, 50.0, 55.0, 0.0, 0.0, 0.0, 0.0, 0.0));       // Hit chance: 42-69%

        // Small/Flying Animals (Higher Evasion)
        futures.add(generateEntityData(writer, EntityType.CHICKEN, 50.0, 40.0, 0.0, 0.0, 0.0, 0.0, 0.0));      // Hit chance: 48-75%
        futures.add(generateEntityData(writer, EntityType.PARROT, 50.0, 50.0, 0.0, 0.0, 0.0, 0.15, 0.0));      // Hit chance: 44-71%
        futures.add(generateEntityData(writer, EntityType.BAT, 50.0, 55.0, 0.0, 0.0, 0.0, 0.20, 0.0));         // Hit chance: 42-69%

        // Aquatic Animals
        futures.add(generateEntityData(writer, EntityType.SQUID, 50.0, 45.0, 0.0, 0.0, 0.0, 0.0, 0.0));        // Hit chance: 47-73%
        futures.add(generateEntityData(writer, EntityType.GLOW_SQUID, 50.0, 45.0, 0.0, 0.0, 0.0, 0.0, 0.0));   // Hit chance: 47-73%
        futures.add(generateEntityData(writer, EntityType.COD, 50.0, 50.0, 0.0, 0.0, 0.0, 0.0, 0.0));          // Hit chance: 44-71%
        futures.add(generateEntityData(writer, EntityType.SALMON, 50.0, 55.0, 0.0, 0.0, 0.0, 0.0, 0.0));       // Hit chance: 42-69%
        futures.add(generateEntityData(writer, EntityType.TROPICAL_FISH, 50.0, 60.0, 0.0, 0.0, 0.0, 0.0, 0.0)); // Hit chance: 40-67%
        futures.add(generateEntityData(writer, EntityType.PUFFERFISH, 50.0, 45.0, 0.0, 0.0, 0.0, 0.0, 0.0));   // Hit chance: 47-73%
        futures.add(generateEntityData(writer, EntityType.TURTLE, 50.0, 35.0, 0.0, 0.0, 0.0, 0.0, 0.0));       // Hit chance: 50-77%
        futures.add(generateEntityData(writer, EntityType.DOLPHIN, 50.0, 65.0, 0.0, 0.0, 0.0, 0.0, 0.0));      // Hit chance: 38-65%

        // NPCs
        futures.add(generateEntityData(writer, EntityType.VILLAGER, 50.0, 30.0, 0.0, 0.0, 0.0, 0.0, 0.0));     // Hit chance: 52-80%
        futures.add(generateEntityData(writer, EntityType.WANDERING_TRADER, 50.0, 45.0, 0.0, 0.0, 0.0, 0.0, 0.0)); // Hit chance: 47-73%

        return futures;
    }

    private CompletableFuture<?> generateEntityData(DataWriter writer, EntityType<?> entityType, double accuracy, double evasion, double critChance, double critDamage, double backAttack, double airAttack, double downAttack) {
        JsonObject json = new JsonObject();

        // Create the combat_stats object
        JsonObject combatStats = new JsonObject();
        combatStats.addProperty("accuracy", accuracy);
        combatStats.addProperty("evasion", evasion);
        combatStats.addProperty("crit_chance", critChance);
        combatStats.addProperty("crit_damage", critDamage);
        combatStats.addProperty("back_attack", backAttack);
        combatStats.addProperty("air_attack", airAttack);
        combatStats.addProperty("down_attack", downAttack);

        // Add the combat_stats to the main JSON
        json.add("combat_stats", combatStats);

        // Get the entity's registry key
        Identifier entityId = Registries.ENTITY_TYPE.getId(entityType);
        String namespace = entityId.getNamespace();
        String path = entityId.getPath();

        // Create the output path for this entity
        Identifier outputId = Dominatus.id("entity_stats/" + namespace + "/" + path);

        // Write the JSON file
        return DataProvider.writeToPath(writer, json, output.getPath().resolve("data/" + outputId.getNamespace() + "/" + outputId.getPath() + ".json"));
    }

    @Override
    public String getName() {
        return "Entity Combat Attributes";
    }
}