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
        MobExpEntry.MOBEXP_MAP.clear();

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

                            // Parse expReward field directly
                            if (!object.has("expReward")) {
                                Dominatus.LOGGER.error("Missing 'expReward' field in file '{}'", identifier);
                                errorCount.getAndIncrement();
                                continue;
                            }

                            int exp = object.get("expReward").getAsInt();

                            // Create and store the entry
                            MobExpEntry entry = new MobExpEntry(exp);
                            MobExpEntry.MOBEXP_MAP.put(entityType, entry);

                            loadedCount.getAndIncrement();

                            Dominatus.LOGGER.debug("Loaded exp for entity '{}': exp={}",
                                    entityIdStr, exp);

                        } catch (NumberFormatException e) {
                            errorCount.getAndIncrement();
                            Dominatus.LOGGER.error("Invalid number format in file '{}': {}", identifier, e.getMessage());
                        } catch (Exception e) {
                            errorCount.getAndIncrement();
                            Dominatus.LOGGER.error("Failed to load mob exp from '{}': {}", identifier, e.getMessage());
                            Dominatus.LOGGER.error("Exception details: ", e);
                        }
                    }
                });

        // Log summary
        Dominatus.LOGGER.info("Successfully loaded {} mob exp entries", loadedCount);

        // Log some examples for debugging
        if (Dominatus.LOGGER.isDebugEnabled()) {
            MobExpEntry.MOBEXP_MAP.entrySet().stream()
                    .limit(5)
                    .forEach(entry -> {
                        Identifier id = Registries.ENTITY_TYPE.getId(entry.getKey());
                        MobExpEntry stats = entry.getValue();
                        Dominatus.LOGGER.debug("Loaded: {} -> {}", id, stats);
                    });
        }
    }
}