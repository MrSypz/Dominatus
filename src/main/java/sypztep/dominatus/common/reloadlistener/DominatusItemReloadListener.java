package sypztep.dominatus.common.reloadlistener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.util.refinesystem.StatRange;

import java.io.InputStream;
import java.io.InputStreamReader;

public final class DominatusItemReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = Dominatus.id("itemdata");

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        DominatusItemEntry.DOMINATUS_ITEM_ENTRY_MAP.clear();
        manager.findAllResources("refine", path -> path.getPath().endsWith(".json")).forEach((identifier, resources) -> {
            for (Resource resource : resources) {
                try (InputStream stream = resource.getInputStream()) {
                    JsonObject object = JsonParser.parseReader(new JsonReader(new InputStreamReader(stream))).getAsJsonObject();

                    String filePath = identifier.getPath();
                    String itemIdStr = filePath.substring(filePath.indexOf("/") + 1, filePath.length() - 5).replace("/", ":");
                    Identifier itemId = Identifier.of(itemIdStr);
                    Item item = Registries.ITEM.get(itemId);

                    Dominatus.LOGGER.info("Processing item: {}", itemId);

                    if (item == Registries.ITEM.get(Registries.ITEM.getDefaultId()) && !itemId.equals(Registries.ITEM.getDefaultId())) {
                        Dominatus.LOGGER.warn("Item with ID {} could not be found, skipping.", itemId);
                        continue;
                    }

                    JsonObject itemProperties = object.getAsJsonObject("itemProperties");
                    JsonObject modifier = itemProperties.getAsJsonObject("modifier");

                    int maxLvl = itemProperties.get("maxLvl").getAsInt();
                    StatRange<Integer> accuracy = new StatRange<>(
                            modifier.getAsJsonObject("accuracy").get("start").getAsInt(),
                            modifier.getAsJsonObject("accuracy").get("end").getAsInt()
                    );
                    StatRange<Integer> evasion = new StatRange<>(
                            modifier.getAsJsonObject("evasion").get("start").getAsInt(),
                            modifier.getAsJsonObject("evasion").get("end").getAsInt()
                    );
                    int maxDurability = itemProperties.get("maxDurability").getAsInt();
                    StatRange<Float> damage = new StatRange<>(
                            modifier.getAsJsonObject("damage").get("start").getAsFloat(),
                            modifier.getAsJsonObject("damage").get("end").getAsFloat()
                    );
                    StatRange<Integer> protection = new StatRange<>(
                            modifier.getAsJsonObject("protection").get("start").getAsInt(),
                            modifier.getAsJsonObject("protection").get("end").getAsInt()
                    );
                    StatRange<Integer> damageReduction = new StatRange<>(
                            modifier.getAsJsonObject("damageReduction").get("start").getAsInt(),
                            modifier.getAsJsonObject("damageReduction").get("end").getAsInt()
                    );
                    int repairpoint = itemProperties.get("repairpoint").getAsInt();

                    DominatusItemEntry entry = new DominatusItemEntry(
                            maxLvl,
                            accuracy,
                            evasion,
                            maxDurability,
                            damage,
                            protection,
                            damageReduction,
                            repairpoint
                    );

                    DominatusItemEntry.DOMINATUS_ITEM_ENTRY_MAP.put(Registries.ITEM.getEntry(item), entry);

                } catch (Exception e) {
                    Dominatus.LOGGER.error("Failed to load item data from '{}': {}", identifier, e.getMessage());
                    Dominatus.LOGGER.error("Exception details: ", e);
                }
            }
        });

        Dominatus.LOGGER.info("Loaded refinement data for {} items", DominatusItemEntry.DOMINATUS_ITEM_ENTRY_MAP.size());
    }
}