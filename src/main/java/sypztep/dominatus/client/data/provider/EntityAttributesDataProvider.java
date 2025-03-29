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

        // Basic Hostiles
        futures.add(generateEntityData(writer, EntityType.ZOMBIE, 0.0, 0.0, 0.01, 0.5, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.SKELETON, 100.0, 50.0, 0.01, 0.5, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.SPIDER, 0.0, 30.0, 0.01, 0.5, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.CREEPER, 0.0, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0));

        // Nether Mobs
        futures.add(generateEntityData(writer, EntityType.BLAZE, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.GHAST, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.MAGMA_CUBE, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.PIGLIN, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0));

        // Illagers
        futures.add(generateEntityData(writer, EntityType.PILLAGER, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.VINDICATOR, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.EVOKER, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0));

        return futures;
    }

    private List<CompletableFuture<?>> generateBossMobs(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Bosses
        futures.add(generateEntityData(writer, EntityType.ENDER_DRAGON, 0.0, 0.0, 0.2, 0.15, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.WITHER, 0.0, 0.0, 0.3, 0.15, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.ELDER_GUARDIAN, 0.0, 0.0, 0.2, 0.15, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.RAVAGER, 0.0, 0.0, 0.2, 0.15, 0.0, 0.0, 0.0));

        return futures;
    }

    private List<CompletableFuture<?>> generateNeutralMobs(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Neutral mobs
        futures.add(generateEntityData(writer, EntityType.ENDERMAN, 0.0, 0.0, 0.2, 0.5, 0.5, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.WOLF, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.BEE, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.IRON_GOLEM, 0.0, 0.0, 0.2, 0.5, 0.5, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.PIGLIN_BRUTE, 0.0, 0.0, 0.5, 0.5, 0.5, 0.0, 0.0));

        return futures;
    }

    private List<CompletableFuture<?>> generatePassiveMobs(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Passive mobs
        futures.add(generateEntityData(writer, EntityType.COW, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.PIG, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.CHICKEN, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.SHEEP, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        futures.add(generateEntityData(writer, EntityType.VILLAGER, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));

        return futures;
    }

    private CompletableFuture<?> generateEntityData(DataWriter writer, EntityType<?> entityType,
                                                    double accuracy, double evasion, double critChance,
                                                    double critDamage, double backAttack,
                                                    double airAttack, double downAttack) {
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
        return DataProvider.writeToPath(writer, json,
                output.getPath().resolve("data/" + outputId.getNamespace() + "/" + outputId.getPath() + ".json"));
    }

    @Override
    public String getName() {
        return "Entity Combat Attributes";
    }
}