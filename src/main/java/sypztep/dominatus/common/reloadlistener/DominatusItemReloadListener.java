package sypztep.dominatus.common.reloadlistener;

import com.google.gson.JsonElement;
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

import java.io.InputStream;
import java.io.InputStreamReader;

public class DominatusItemReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = Dominatus.id("penomioritemdata");
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

                    JsonObject arg = object.getAsJsonObject("arg");
                    boolean isVanilla = arg.has("vanilla") && arg.get("vanilla").getAsBoolean();

                    String namespace = identifier.getNamespace(); // e.g., "minecraft" or "pointblank"
                    String pathStr = identifier.getPath().substring(identifier.getPath().indexOf("/") + 1, identifier.getPath().length() - 5).replace("/", ":");
                    Identifier itemId = Identifier.of(namespace,pathStr);
                    if (isVanilla) { // if true it will use the vanilla namespace
                        itemId = Identifier.ofVanilla(itemId.getPath());
                    }
                    Item item = Registries.ITEM.get(itemId);

                    // Log the processing
                    Dominatus.LOGGER.info("Processing item: {}", itemId);

                    if (item == Registries.ITEM.get(Registries.ITEM.getDefaultId()) && !itemId.equals(Registries.ITEM.getDefaultId())) {
                        Dominatus.LOGGER.warn("Item with ID {} could not be found, skipping.", itemId);
                        continue;
                    }

                    JsonObject itemProperties = object.getAsJsonObject("itemProperties");

                    int maxLvl = itemProperties.get("maxLvl").getAsInt();
                    int startAccuracy = itemProperties.get("startAccuracy").getAsInt();
                    int endAccuracy = itemProperties.get("endAccuracy").getAsInt();
                    int startEvasion = itemProperties.get("startEvasion").getAsInt();
                    int endEvasion = itemProperties.get("endEvasion").getAsInt();
                    int maxDurability = itemProperties.get("maxDurability").getAsInt();
                    int starDamage = itemProperties.get("starDamage").getAsInt();
                    int endDamage = itemProperties.get("endDamage").getAsInt();
                    int startProtection = itemProperties.get("startProtection").getAsInt();
                    int endProtection = itemProperties.get("endProtection").getAsInt();
                    int repairpoint = itemProperties.get("repairpoint").getAsInt();

                    DominatusItemEntry entry = new DominatusItemEntry(
                            maxLvl,
                            startAccuracy,
                            endAccuracy,
                            startEvasion,
                            endEvasion,
                            maxDurability,
                            starDamage,
                            endDamage,
                            startProtection,
                            endProtection,
                            repairpoint
                    );

                    DominatusItemEntry.DOMINATUS_ITEM_ENTRY_MAP.put(Registries.ITEM.getEntry(item), entry);

                } catch (Exception ignored) {
                }
            }
        });
    }
}
