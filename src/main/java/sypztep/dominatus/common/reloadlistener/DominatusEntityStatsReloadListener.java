package sypztep.dominatus.common.reloadlistener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.data.DominatusEntityEntry;

import java.io.InputStream;
import java.io.InputStreamReader;

public class DominatusEntityStatsReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = Dominatus.id("entity_stats");

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        DominatusEntityEntry.BASEMOBSTATS_MAP.clear();
        manager.findAllResources("entity_stats", path -> path.getPath().endsWith(".json")).forEach((identifier, resources) -> {
            for (Resource resource : resources) {
                try (InputStream stream = resource.getInputStream()) {
                    JsonObject object = JsonParser.parseReader(new JsonReader(new InputStreamReader(stream))).getAsJsonObject();

                    // Parse the entity ID from the file path
                    String filePath = identifier.getPath();
                    String entityIdStr = filePath.substring(filePath.indexOf("/") + 1, filePath.length() - 5).replace("/", ":");
                    Identifier entityId = Identifier.of(entityIdStr);
                    EntityType<?> entityType = Registries.ENTITY_TYPE.get(entityId);

                    if (entityType == Registries.ENTITY_TYPE.get(Registries.ENTITY_TYPE.getDefaultId()) && !entityId.equals(Registries.ENTITY_TYPE.getDefaultId())) {
                        continue;
                    }

                    // Parse the combat stats
                    JsonObject stats = object.getAsJsonObject("combat_stats");

                    double accuracy = getDoubleOrDefault(stats, "accuracy", 0.0);
                    double evasion = getDoubleOrDefault(stats, "evasion", 0.0);
                    double critChance = getDoubleOrDefault(stats, "crit_chance", 0.0);
                    double critDamage = getDoubleOrDefault(stats, "crit_damage", 0.0);
                    double backAttack = getDoubleOrDefault(stats, "back_attack", 0.0);
                    double airAttack = getDoubleOrDefault(stats, "air_attack", 0.0);
                    double downAttack = getDoubleOrDefault(stats, "down_attack", 0.0);

                    // Create and store the entry
                    DominatusEntityEntry entry = new DominatusEntityEntry(
                            accuracy, evasion, critChance, critDamage, backAttack, airAttack, downAttack);

                    DominatusEntityEntry.BASEMOBSTATS_MAP.put(entityType, entry);

                    Dominatus.LOGGER.info("Loaded combat stats for entity: {}", entityId);

                } catch (Exception e) {
                    Dominatus.LOGGER.error("Failed to load entity stats from '{}': {}", identifier, e.getMessage());
                    Dominatus.LOGGER.error("Exception details: ", e);
                }
            }
        });

        Dominatus.LOGGER.info("Loaded combat stats for {} entity types", DominatusEntityEntry.BASEMOBSTATS_MAP.size());
    }

    /**
     * Helper method to safely get a double value from JSON with a default fallback
     */
    private double getDoubleOrDefault(JsonObject json, String key, double defaultValue) {
        return json.has(key) ? json.get(key).getAsDouble() : defaultValue;
    }
}