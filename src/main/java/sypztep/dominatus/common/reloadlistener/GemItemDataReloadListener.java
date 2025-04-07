package sypztep.dominatus.common.reloadlistener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.data.GemComponent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class GemItemDataReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final Map<Identifier, GemComponent> GEM_TYPES = new HashMap<>();
    private static final Identifier ID = Dominatus.id("gem_data");

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        GEM_TYPES.clear();

        Dominatus.LOGGER.info("=== Loading Gem Data ===");

        manager.findResources("gems", path -> path.getPath().endsWith(".json")).forEach((identifier, resources) -> {
            try (InputStream stream = resources.getInputStream()) {
                JsonObject json = JsonParser.parseReader(new JsonReader(new InputStreamReader(stream))).getAsJsonObject();

                String gemPath = identifier.getPath().substring("gems/".length()).replace(".json", "");
                Identifier gemType = Identifier.of(identifier.getNamespace(), gemPath);

                GemComponent.CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(error ->
                        Dominatus.LOGGER.error("Failed to parse gem {}: {}", identifier, error)).ifPresent(gemComponent -> {
                    Dominatus.LOGGER.info("Loaded gem type: {} with texture: {}",
                            gemType, gemComponent.texture().orElse(null));
                    GEM_TYPES.put(gemType, gemComponent);
                });

            } catch (Exception e) {
                Dominatus.LOGGER.error("Failed to load gem data from '{}': {}", identifier, e.getMessage());
            }
        });

        Dominatus.LOGGER.info("Loaded {} gem types", GEM_TYPES.size());
    }

    public static Optional<GemComponent> getGemType(Identifier type) {
        return Optional.ofNullable(GEM_TYPES.get(type));
    }

    public static Collection<Identifier> getGemTypes() {
        return GEM_TYPES.keySet();
    }
}