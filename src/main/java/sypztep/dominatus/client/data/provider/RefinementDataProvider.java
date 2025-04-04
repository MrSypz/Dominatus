package sypztep.dominatus.client.data.provider;

import net.minecraft.item.Items;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import com.google.gson.JsonObject;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModItems;

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
        futures.addAll(generateWristItems(writer));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private List<CompletableFuture<?>> generateWeapons(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Swords - Balanced weapons (ADJUSTED)
        futures.add(generateWeaponData(writer, Items.WOODEN_SWORD.toString(), 15, 45, 0, 0, 100, 4, 12, 0, 0, 10));    // Green
        futures.add(generateWeaponData(writer, Items.STONE_SWORD.toString(), 20, 60, 0, 0, 100, 5, 15, 0, 0, 10));    // Green
        futures.add(generateWeaponData(writer, Items.IRON_SWORD.toString(), 30, 85, 0, 0, 100, 6, 18, 0, 0, 5));     // Blue
        futures.add(generateWeaponData(writer, Items.DIAMOND_SWORD.toString(), 50, 120, 0, 0, 100, 8, 24, 0, 0, 5));   // Blue
        futures.add(generateWeaponData(writer, Items.GOLDEN_SWORD.toString(), 40, 100, 0, 0, 100, 10, 30, 0, 0, 2));  // Yellow
        futures.add(generateWeaponData(writer, Items.NETHERITE_SWORD.toString(), 65, 150, 0, 0, 100, 12, 40, 0, 0, 1)); // Boss

// Axes - Higher damage, lower accuracy (ADJUSTED)
        futures.add(generateWeaponData(writer, Items.WOODEN_AXE.toString(), 10, 35, 0, 0, 100, 6, 18, 0, 0, 10));     // Green
        futures.add(generateWeaponData(writer, Items.STONE_AXE.toString(), 15, 45, 0, 0, 100, 8, 21, 0, 0, 10));      // Green
        futures.add(generateWeaponData(writer, Items.IRON_AXE.toString(), 25, 70, 0, 0, 100, 10, 27, 0, 0, 5));       // Blue
        futures.add(generateWeaponData(writer, Items.GOLDEN_AXE.toString(), 30, 85, 0, 0, 100, 10, 27, 0, 0, 5));       // Blue
        futures.add(generateWeaponData(writer, Items.DIAMOND_AXE.toString(), 40, 100, 0, 0, 100, 12, 33, 0, 0, 2));   // Yellow
        futures.add(generateWeaponData(writer, Items.NETHERITE_AXE.toString(), 55, 130, 0, 0, 100, 14, 42, 0, 0, 1)); // Boss
// Ranged weapons
        futures.add(generateWeaponData(writer, Items.CROSSBOW.toString(), 45, 115, 0, 0, 100, 0, 0, 0, 0, 5));        // Blue
        futures.add(generateWeaponData(writer, Items.BOW.toString(), 40, 110, 0, 0, 100, 0, 0, 0, 0, 5));             // Blue

// Special weapons
        futures.add(generateWeaponData(writer, Items.MACE.toString(), 55, 130, 0, 0, 100, 5, 18, 0, 0, 1));           // Yellow
        futures.add(generateWeaponData(writer, Items.TRIDENT.toString(), 55, 130, 0, 0, 100, 5, 18, 0, 0, 1));        // Yellow
        futures.add(generateWeaponData(writer, Items.SHIELD.toString(), 5, 40, 15, 50, 100, 0, 0, 8, 20, 5));          // Defensive

        return futures;
    }

    private List<CompletableFuture<?>> generateLeatherArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateWeaponData(writer, Items.LEATHER_HELMET.toString(), 0, 0, 8, 25, 100, 0, 0, 4, 10, 10));
        futures.add(generateWeaponData(writer, Items.LEATHER_CHESTPLATE.toString(), 0, 0, 12, 35, 100, 0, 0, 6, 16, 10));
        futures.add(generateWeaponData(writer, Items.LEATHER_LEGGINGS.toString(), 0, 0, 10, 30, 100, 0, 0, 5, 12, 10));
        futures.add(generateWeaponData(writer, Items.LEATHER_BOOTS.toString(), 0, 0, 8, 25, 100, 0, 0, 4, 10, 10));
        return futures;
    }

    private List<CompletableFuture<?>> generateChainmailArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_HELMET.toString(), 0, 0, 6, 20, 100, 0, 0, 6, 15, 5));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_CHESTPLATE.toString(), 0, 0, 10, 28, 100, 0, 0, 10, 24, 5));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_LEGGINGS.toString(), 0, 0, 8, 24, 100, 0, 0, 8, 20, 5));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_BOOTS.toString(), 0, 0, 6, 20, 100, 0, 0, 6, 15, 5));
        return futures;
    }

    private List<CompletableFuture<?>> generateIronArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateWeaponData(writer, Items.IRON_HELMET.toString(), 0, 0, 4, 15, 100, 0, 0, 8, 20, 5));
        futures.add(generateWeaponData(writer, Items.IRON_CHESTPLATE.toString(), 0, 0, 6, 18, 100, 0, 0, 12, 28, 5));
        futures.add(generateWeaponData(writer, Items.IRON_LEGGINGS.toString(), 0, 0, 5, 16, 100, 0, 0, 10, 24, 5));
        futures.add(generateWeaponData(writer, Items.IRON_BOOTS.toString(), 0, 0, 4, 15, 100, 0, 0, 8, 20, 5));
        return futures;
    }

    private List<CompletableFuture<?>> generateGoldenArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateWeaponData(writer, Items.GOLDEN_HELMET.toString(), 0, 0, 10, 30, 100, 0, 0, 6, 16, 2));
        futures.add(generateWeaponData(writer, Items.GOLDEN_CHESTPLATE.toString(), 0, 0, 15, 40, 100, 0, 0, 10, 24, 2));
        futures.add(generateWeaponData(writer, Items.GOLDEN_LEGGINGS.toString(), 0, 0, 12, 35, 100, 0, 0, 8, 20, 2));
        futures.add(generateWeaponData(writer, Items.GOLDEN_BOOTS.toString(), 0, 0, 10, 30, 100, 0, 0, 6, 16, 2));
        return futures;
    }

    private List<CompletableFuture<?>> generateDiamondArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateWeaponData(writer, Items.DIAMOND_HELMET.toString(), 0, 0, 8, 25, 100, 0, 0, 10, 24, 2));
        futures.add(generateWeaponData(writer, Items.DIAMOND_CHESTPLATE.toString(), 0, 0, 12, 32, 100, 0, 0, 16, 36, 2));
        futures.add(generateWeaponData(writer, Items.DIAMOND_LEGGINGS.toString(), 0, 0, 10, 28, 100, 0, 0, 12, 30, 2));
        futures.add(generateWeaponData(writer, Items.DIAMOND_BOOTS.toString(), 0, 0, 8, 25, 100, 0, 0, 10, 24, 2));
        return futures;
    }

    private List<CompletableFuture<?>> generateNetheriteArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateWeaponData(writer, Items.NETHERITE_HELMET.toString(), 0, 0, 10, 30, 100, 0, 0, 12, 28, 1));
        futures.add(generateWeaponData(writer, Items.NETHERITE_CHESTPLATE.toString(), 0, 0, 15, 40, 100, 0, 0, 20, 45, 1));
        futures.add(generateWeaponData(writer, Items.NETHERITE_LEGGINGS.toString(), 0, 0, 12, 35, 100, 0, 0, 16, 36, 1));
        futures.add(generateWeaponData(writer, Items.NETHERITE_BOOTS.toString(), 0, 0, 10, 30, 100, 0, 0, 12, 28, 1));
        return futures;
    }

    // Add this method to your data generator class
    private List<CompletableFuture<?>> generateWristItems(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateWeaponData(writer, ModItems.YURIA_BRACKET.toString(), 15, 45, 8, 25, 100, 0, 0, 2, 10, 5));
        return futures;
    }

    private CompletableFuture<?> generateWeaponData(DataWriter writer, String itemName, int startAccuracy, int endAccuracy, int startEvasion, int endEvasion, int maxDurability, int startDamage, int endDamage, int startProtection, int endProtection, int repairPoint) {
        JsonObject json = new JsonObject();

        JsonObject itemProperties = new JsonObject();
        itemProperties.addProperty("maxLvl", 20);
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

        Identifier itemId = Identifier.tryParse(itemName);
        if (itemId == null) itemId = Identifier.ofVanilla(itemName);
        String namespace = itemId.getNamespace();
        String path = itemId.getPath();

        Identifier outputId = Dominatus.id("refine/" + namespace + "/" + path);

        return DataProvider.writeToPath(writer, json, output.getPath().resolve("data/" + outputId.getNamespace() + "/" + outputId.getPath() + ".json"));
    }

    @Override
    public String getName() {
        return "Refinement Data";
    }
}