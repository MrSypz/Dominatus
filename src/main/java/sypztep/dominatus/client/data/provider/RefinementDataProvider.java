package sypztep.dominatus.client.data.provider;

import net.minecraft.item.Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import com.google.gson.JsonObject;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import java.util.List;

public class RefinementDataProvider implements DataProvider {
    private final FabricDataOutput output;

    public RefinementDataProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Weapons
        futures.addAll(generateWeapons(writer));

        // Armor Sets
        futures.addAll(generateLeatherArmor(writer));
        futures.addAll(generateChainmailArmor(writer));
        futures.addAll(generateIronArmor(writer));
        futures.addAll(generateGoldenArmor(writer));
        futures.addAll(generateDiamondArmor(writer));
        futures.addAll(generateNetheriteArmor(writer));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private List<CompletableFuture<?>> generateWeapons(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(generateWeaponData(writer, Items.WOODEN_SWORD.toString(), true, 20, 42, 84, 0, 0, 100, 2, 8, 0, 0, 10));  // Green Grade
        futures.add(generateWeaponData(writer, Items.STONE_SWORD.toString(), true, 20, 45, 86, 0, 0, 100, 3, 10, 0, 0, 10));   // Green Grade
        futures.add(generateWeaponData(writer, Items.IRON_SWORD.toString(), true, 20, 48, 89, 0, 0, 100, 4, 12, 0, 0, 5));     // Blue Grade
        futures.add(generateWeaponData(writer, Items.GOLDEN_SWORD.toString(), true, 20, 54, 96, 0, 0, 100, 2, 8, 0, 0, 5));   // Blue Grade
        futures.add(generateWeaponData(writer, Items.DIAMOND_SWORD.toString(), true, 20, 68, 115, 0, 0, 100, 5, 14, 0, 0, 2)); // Yellow Grade
        futures.add(generateWeaponData(writer, Items.NETHERITE_SWORD.toString(), true, 20, 72, 122, 0, 0, 100, 6, 16, 0, 0, 1)); // Boss Grade

        futures.add(generateWeaponData(writer, Items.WOODEN_AXE.toString(), true, 20, 38, 78, 0, 0, 100, 5, 14, 0, 0, 10));    // Green Grade
        futures.add(generateWeaponData(writer, Items.STONE_AXE.toString(), true, 20, 42, 82, 0, 0, 100, 7, 18, 0, 0, 10));     // Green Grade
        futures.add(generateWeaponData(writer, Items.IRON_AXE.toString(), true, 20, 45, 85, 0, 0, 100, 7, 18, 0, 0, 5));       // Blue Grade
        futures.add(generateWeaponData(writer, Items.DIAMOND_AXE.toString(), true, 20, 62, 108, 0, 0, 100, 7, 18, 0, 0, 2));   // Yellow Grade
        futures.add(generateWeaponData(writer, Items.NETHERITE_AXE.toString(), true, 20, 68, 115, 0, 0, 100, 8, 20, 0, 0, 1)); // Boss Grade

        return futures;
    }

    private List<CompletableFuture<?>> generateLeatherArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Leather - Green Grade
        futures.add(generateWeaponData(writer, Items.LEATHER_HELMET.toString(), true, 20, 0, 0, 18, 38, 100, 0, 0, 14, 29, 10));
        futures.add(generateWeaponData(writer, Items.LEATHER_CHESTPLATE.toString(), true, 20, 0, 0, 22, 42, 100, 0, 0, 28, 43, 10));
        futures.add(generateWeaponData(writer, Items.LEATHER_LEGGINGS.toString(), true, 20, 0, 0, 16, 36, 100, 0, 0, 12, 27, 10));
        futures.add(generateWeaponData(writer, Items.LEATHER_BOOTS.toString(), true, 20, 0, 0, 17, 37, 100, 0, 0, 13, 28, 10));
        return futures;
    }

    private List<CompletableFuture<?>> generateChainmailArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Chainmail - Blue Grade
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_HELMET.toString(), true, 20, 0, 0, 15, 35, 100, 0, 0, 17, 32, 5));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_CHESTPLATE.toString(), true, 20, 0, 0, 17, 37, 100, 0, 0, 32, 47, 5));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_LEGGINGS.toString(), true, 20, 0, 0, 14, 34, 100, 0, 0, 15, 30, 5));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_BOOTS.toString(), true, 20, 0, 0, 15, 35, 100, 0, 0, 16, 31, 5));
        return futures;
    }

    private List<CompletableFuture<?>> generateIronArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Iron - Blue Grade
        futures.add(generateWeaponData(writer, Items.IRON_HELMET.toString(), true, 20, 0, 0, 16, 36, 100, 0, 0, 18, 33, 5));
        futures.add(generateWeaponData(writer, Items.IRON_CHESTPLATE.toString(), true, 20, 0, 0, 18, 38, 100, 0, 0, 34, 49, 5));
        futures.add(generateWeaponData(writer, Items.IRON_LEGGINGS.toString(), true, 20, 0, 0, 15, 35, 100, 0, 0, 16, 31, 5));
        futures.add(generateWeaponData(writer, Items.IRON_BOOTS.toString(), true, 20, 0, 0, 16, 36, 100, 0, 0, 17, 32, 5));
        return futures;
    }

    private List<CompletableFuture<?>> generateGoldenArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Golden - Yellow Grade (like Rocaba)
        futures.add(generateWeaponData(writer, Items.GOLDEN_HELMET.toString(), true, 20, 0, 0, 20, 40, 100, 0, 0, 16, 31, 2));
        futures.add(generateWeaponData(writer, Items.GOLDEN_CHESTPLATE.toString(), true, 20, 0, 0, 24, 44, 100, 0, 0, 30, 45, 2));
        futures.add(generateWeaponData(writer, Items.GOLDEN_LEGGINGS.toString(), true, 20, 0, 0, 22, 42, 100, 0, 0, 14, 29, 2));
        futures.add(generateWeaponData(writer, Items.GOLDEN_BOOTS.toString(), true, 20, 0, 0, 21, 41, 100, 0, 0, 15, 30, 2));
        return futures;
    }

    private List<CompletableFuture<?>> generateDiamondArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Diamond - Yellow Grade (like Akum)
        futures.add(generateWeaponData(writer, Items.DIAMOND_HELMET.toString(), true, 20, 0, 0, 20, 40, 100, 0, 0, 22, 37, 2));
        futures.add(generateWeaponData(writer, Items.DIAMOND_CHESTPLATE.toString(), true, 20, 0, 0, 24, 44, 100, 0, 0, 38, 53, 2));
        futures.add(generateWeaponData(writer, Items.DIAMOND_LEGGINGS.toString(), true, 20, 0, 0, 18, 38, 100, 0, 0, 20, 35, 2));
        futures.add(generateWeaponData(writer, Items.DIAMOND_BOOTS.toString(), true, 20, 0, 0, 19, 39, 100, 0, 0, 21, 36, 2));
        return futures;
    }

    private List<CompletableFuture<?>> generateNetheriteArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Netherite - Boss Grade
        futures.add(generateWeaponData(writer, Items.NETHERITE_HELMET.toString(), true, 20, 0, 0, 22, 42, 100, 0, 0, 25, 40, 1));
        futures.add(generateWeaponData(writer, Items.NETHERITE_CHESTPLATE.toString(), true, 20, 0, 0, 26, 46, 100, 0, 0, 41, 56, 1));
        futures.add(generateWeaponData(writer, Items.NETHERITE_LEGGINGS.toString(), true, 20, 0, 0, 20, 40, 100, 0, 0, 23, 38, 1));
        futures.add(generateWeaponData(writer, Items.NETHERITE_BOOTS.toString(), true, 20, 0, 0, 28, 48, 100, 0, 0, 24, 39, 1));
        return futures;
    }

    private CompletableFuture<?> generateWeaponData(DataWriter writer, String itemName, boolean isVanilla,
                                                    int maxLvl, int startAccuracy, int endAccuracy,
                                                    int startEvasion, int endEvasion, int maxDurability,
                                                    int startDamage, int endDamage,
                                                    int startProtection, int endProtection,
                                                    int repairPoint) {
        JsonObject json = new JsonObject();

        JsonObject arg = new JsonObject();
        arg.addProperty("vanilla", isVanilla);
        json.add("arg", arg);

        JsonObject itemProperties = new JsonObject();
        itemProperties.addProperty("maxLvl", maxLvl);
        itemProperties.addProperty("startAccuracy", startAccuracy);
        itemProperties.addProperty("endAccuracy", endAccuracy);
        itemProperties.addProperty("startEvasion", startEvasion);
        itemProperties.addProperty("endEvasion", endEvasion);
        itemProperties.addProperty("maxDurability", maxDurability);
        itemProperties.addProperty("starDamage", startDamage);
        itemProperties.addProperty("endDamage", endDamage);
        itemProperties.addProperty("startProtection", startProtection);
        itemProperties.addProperty("endProtection", endProtection);
        itemProperties.addProperty("repairpoint", repairPoint);
        json.add("itemProperties", itemProperties);

        String cleanItemName = itemName.replace("minecraft:", "");
        Identifier id = Identifier.of(output.getModId(), "refine/" + cleanItemName);
        return DataProvider.writeToPath(writer, json, output.getPath().resolve("data/" + id.getNamespace() + "/" + id.getPath() + ".json"));
    }

    @Override
    public String getName() {
        return "Refinement Data";
    }
}