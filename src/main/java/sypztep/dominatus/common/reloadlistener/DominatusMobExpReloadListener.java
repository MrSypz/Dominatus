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
import sypztep.dominatus.common.data.MobExpEntry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

public class DominatusMobExpReloadListener implements SimpleSynchronousResourceReloadListener {

    private static final Identifier ID = Dominatus.id("mobexp");

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        // Clear existing data
        MobExpEntry.clearAll();

        AtomicInteger loadedCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();

        // Find all mobexp JSON files
        manager.findAllResources("mobexp", path -> path.getPath().endsWith(".json"))
                .forEach((identifier, resources) -> {
                    for (Resource resource : resources) {
                        try (InputStream stream = resource.getInputStream()) {
                            JsonObject object = JsonParser.parseReader(
                                    new JsonReader(new InputStreamReader(stream))
                            ).getAsJsonObject();

                            // Extract entity ID from file path
                            String filePath = identifier.getPath();
                            String entityIdStr = filePath.substring(
                                    filePath.indexOf("/") + 1,
                                    filePath.length() - 5
                            ).replace("/", ":");

                            Identifier entityId = Identifier.of(entityIdStr);
                            EntityType<?> entityType = Registries.ENTITY_TYPE.get(entityId);

                            // Skip if entity type doesn't exist
                            if (entityType == Registries.ENTITY_TYPE.get(Registries.ENTITY_TYPE.getDefaultId()) &&
                                    !entityId.equals(Registries.ENTITY_TYPE.getDefaultId())) {
                                Dominatus.LOGGER.warn("Unknown entity type '{}' in file '{}'", entityIdStr, identifier);
                                errorCount.getAndIncrement();
                                continue;
                            }

                            if (!object.has("expReward")) {
                                Dominatus.LOGGER.error("Missing 'expReward' field in file '{}'", identifier);
                                errorCount.getAndIncrement();
                                continue;
                            }

                            int expReward = object.get("expReward").getAsInt();

                            // Parse optional baseLevel field (default to 1)
                            int baseLevel = object.has("baseLevel") ? object.get("baseLevel").getAsInt() : 1;

                            // Parse optional stats object
                            MobExpEntry.MobStats stats = parseStats(object, entityIdStr, baseLevel);

                            // Create and store the entry
                            MobExpEntry entry = new MobExpEntry(expReward, baseLevel, stats);
                            MobExpEntry.addEntry(entityType, entry);

                            loadedCount.getAndIncrement();

                            Dominatus.LOGGER.debug("Loaded mob data for entity '{}': expReward={}, baseLevel={}, stats={}",
                                    entityIdStr, expReward, baseLevel, stats);

                        } catch (NumberFormatException e) {
                            errorCount.getAndIncrement();
                            Dominatus.LOGGER.error("Invalid number format in file '{}': {}", identifier, e.getMessage());
                        } catch (Exception e) {
                            errorCount.getAndIncrement();
                            Dominatus.LOGGER.error("Failed to load mob data from '{}': {}", identifier, e.getMessage());
                            Dominatus.LOGGER.error("Exception details: ", e);
                        }
                    }
                });

        // Log summary
        Dominatus.LOGGER.info("Successfully loaded {} mob entries with stats and levels", loadedCount.get());
        if (errorCount.get() > 0) {
            Dominatus.LOGGER.warn("Failed to load {} mob entries due to errors", errorCount.get());
        }

        // Log some examples for debugging
        if (Dominatus.LOGGER.isDebugEnabled()) {
            MobExpEntry.MOBEXP_MAP.entrySet().stream()
                    .limit(5)
                    .forEach(entry -> {
                        Identifier id = Registries.ENTITY_TYPE.getId(entry.getKey());
                        MobExpEntry mobEntry = entry.getValue();
                        Dominatus.LOGGER.debug("Loaded: {} -> {}", id, mobEntry);
                    });
        }
    }

    private MobExpEntry.MobStats parseStats(JsonObject object, String entityIdStr, int baseLevel) {
        // If no stats object, use default stats based on level
        if (!object.has("stats")) {
            Dominatus.LOGGER.debug("No stats found for '{}', using default level-based stats", entityIdStr);
            return MobExpEntry.MobStats.forLevel(Math.max(1, baseLevel));
        }

        JsonObject statsObject = object.getAsJsonObject("stats");

        try {
            int strength = getStatValue(statsObject, "strength", baseLevel);
            int agility = getStatValue(statsObject, "agility", baseLevel);
            int vitality = getStatValue(statsObject, "vitality", baseLevel);
            int intelligence = getStatValue(statsObject, "intelligence", baseLevel);
            int dexterity = getStatValue(statsObject, "dexterity", baseLevel);
            int luck = getStatValue(statsObject, "luck", baseLevel);

            return new MobExpEntry.MobStats(strength, agility, vitality, intelligence, dexterity, luck);

        } catch (Exception e) {
            Dominatus.LOGGER.warn("Error parsing stats for '{}', using default stats: {}", entityIdStr, e.getMessage());
            return MobExpEntry.MobStats.forLevel(Math.max(1, baseLevel));
        }
    }

    private int getStatValue(JsonObject statsObject, String statName, int defaultValue) {
        if (statsObject.has(statName)) {
            return Math.max(1, statsObject.get(statName).getAsInt()); // Minimum stat value of 1
        }
        return Math.max(1, defaultValue); // Default to base level or minimum 1
    }
}