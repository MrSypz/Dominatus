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
        futures.addAll(generateOffhandItems(writer));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private List<CompletableFuture<?>> generateWeapons(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Base values: accuracy 50, evasion 20
        // Swords - Balanced weapons
        futures.add(generateWeaponData(writer, Items.WOODEN_SWORD.toString(), 20, 50, 120, 0, 0, 100, 2, 12, 0, 0, 10));  // Green Grade
        futures.add(generateWeaponData(writer, Items.STONE_SWORD.toString(), 20, 54, 128, 0, 0, 100, 3, 15, 0, 0, 10));   // Green Grade
        futures.add(generateWeaponData(writer, Items.IRON_SWORD.toString(), 20, 60, 138, 0, 0, 100, 4, 18, 0, 0, 5));     // Blue Grade
        futures.add(generateWeaponData(writer, Items.GOLDEN_SWORD.toString(), 20, 65, 150, 0, 0, 100, 2, 12, 0, 0, 5));   // Blue Grade
        futures.add(generateWeaponData(writer, Items.DIAMOND_SWORD.toString(), 20, 78, 185, 0, 0, 100, 5, 22, 0, 0, 2));  // Yellow Grade
        futures.add(generateWeaponData(writer, Items.NETHERITE_SWORD.toString(), 20, 90, 220, 0, 0, 100, 6, 28, 0, 0, 1)); // Boss Grade

        futures.add(generateWeaponData(writer, Items.CROSSBOW.toString(), 20, 60, 185, 0, 0, 100, 0, 0, 0, 0, 5));     // Blue Grade
        futures.add(generateWeaponData(writer, Items.BOW.toString(), 20, 60, 185, 0, 0, 100, 0, 0, 0, 0, 5));     // Blue Grade

        futures.add(generateWeaponData(writer, Items.MACE.toString(), 20, 78, 185, 0, 0, 100, 5, 22, 0, 0, 1));  // Yellow Grade
        futures.add(generateWeaponData(writer, Items.TRIDENT.toString(), 20, 78, 185, 0, 0, 100, 5, 22, 0, 0, 1));  // Yellow Grade
        // Axes - Higher damage, lower accuracy
        futures.add(generateWeaponData(writer, Items.WOODEN_AXE.toString(), 20, 45, 110, 0, 0, 100, 5, 18, 0, 0, 10));    // Green Grade
        futures.add(generateWeaponData(writer, Items.STONE_AXE.toString(), 20, 48, 115, 0, 0, 100, 7, 24, 0, 0, 10));     // Green Grade
        futures.add(generateWeaponData(writer, Items.IRON_AXE.toString(), 20, 52, 125, 0, 0, 100, 7, 28, 0, 0, 5));       // Blue Grade
        futures.add(generateWeaponData(writer, Items.DIAMOND_AXE.toString(), 20, 70, 165, 0, 0, 100, 7, 32, 0, 0, 2));    // Yellow Grade
        futures.add(generateWeaponData(writer, Items.NETHERITE_AXE.toString(), 20, 78, 190, 0, 0, 100, 8, 38, 0, 0, 1));  // Boss Grade

        return futures;
    }
    private List<CompletableFuture<?>> generateLeatherArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Leather - Green Grade (Evasion focused)
        futures.add(generateWeaponData(writer, Items.LEATHER_HELMET.toString(), 20, 0, 0, 20, 65, 100, 0, 0, 12, 25, 10));
        futures.add(generateWeaponData(writer, Items.LEATHER_CHESTPLATE.toString(), 20, 0, 0, 28, 85, 100, 0, 0, 22, 38, 10));
        futures.add(generateWeaponData(writer, Items.LEATHER_LEGGINGS.toString(), 20, 0, 0, 22, 62, 100, 0, 0, 10, 22, 10));
        futures.add(generateWeaponData(writer, Items.LEATHER_BOOTS.toString(), 20, 0, 0, 24, 68, 100, 0, 0, 11, 23, 10));
        return futures;
    }

    private List<CompletableFuture<?>> generateChainmailArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Chainmail - Blue Grade (Mix of evasion and DR)
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_HELMET.toString(), 20, 0, 0, 20, 58, 100, 0, 0, 20, 42, 5));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_CHESTPLATE.toString(), 20, 0, 0, 24, 65, 100, 0, 0, 38, 68, 5));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_LEGGINGS.toString(), 20, 0, 0, 18, 55, 100, 0, 0, 18, 38, 5));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_BOOTS.toString(), 20, 0, 0, 20, 58, 100, 0, 0, 19, 40, 5));
        return futures;
    }

    private List<CompletableFuture<?>> generateIronArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Iron - Blue Grade (DR focused)
        futures.add(generateWeaponData(writer, Items.IRON_HELMET.toString(), 20, 0, 0, 15, 38, 100, 0, 0, 22, 48, 5));
        futures.add(generateWeaponData(writer, Items.IRON_CHESTPLATE.toString(), 20, 0, 0, 18, 42, 100, 0, 0, 42, 78, 5));
        futures.add(generateWeaponData(writer, Items.IRON_LEGGINGS.toString(), 20, 0, 0, 14, 36, 100, 0, 0, 20, 45, 5));
        futures.add(generateWeaponData(writer, Items.IRON_BOOTS.toString(), 20, 0, 0, 15, 38, 100, 0, 0, 22, 48, 5));
        return futures;
    }

    private List<CompletableFuture<?>> generateGoldenArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Golden - Yellow Grade (High evasion like Rocaba)
        futures.add(generateWeaponData(writer, Items.GOLDEN_HELMET.toString(), 20, 0, 0, 28, 75, 100, 0, 0, 18, 38, 2));
        futures.add(generateWeaponData(writer, Items.GOLDEN_CHESTPLATE.toString(), 20, 0, 0, 35, 90, 100, 0, 0, 32, 58, 2));
        futures.add(generateWeaponData(writer, Items.GOLDEN_LEGGINGS.toString(), 20, 0, 0, 30, 80, 100, 0, 0, 16, 35, 2));
        futures.add(generateWeaponData(writer, Items.GOLDEN_BOOTS.toString(), 20, 0, 0, 28, 75, 100, 0, 0, 17, 36, 2));
        return futures;
    }

    private List<CompletableFuture<?>> generateDiamondArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Diamond - Yellow Grade (Like Akum - balanced)
        futures.add(generateWeaponData(writer, Items.DIAMOND_HELMET.toString(), 20, 0, 0, 22, 62, 100, 0, 0, 24, 58, 2));
        futures.add(generateWeaponData(writer, Items.DIAMOND_CHESTPLATE.toString(), 20, 0, 0, 30, 72, 100, 0, 0, 46, 95, 2));
        futures.add(generateWeaponData(writer, Items.DIAMOND_LEGGINGS.toString(), 20, 0, 0, 20, 58, 100, 0, 0, 22, 52, 2));
        futures.add(generateWeaponData(writer, Items.DIAMOND_BOOTS.toString(), 20, 0, 0, 22, 60, 100, 0, 0, 23, 54, 2));
        return futures;
    }

    private List<CompletableFuture<?>> generateNetheriteArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Netherite - Boss Grade (DR focused with good evasion)
        futures.add(generateWeaponData(writer, Items.NETHERITE_HELMET.toString(), 20, 0, 0, 25, 70, 100, 0, 0, 28, 68, 1));
        futures.add(generateWeaponData(writer, Items.NETHERITE_CHESTPLATE.toString(), 20, 0, 0, 35, 85, 100, 0, 0, 52, 115, 1));
        futures.add(generateWeaponData(writer, Items.NETHERITE_LEGGINGS.toString(), 20, 0, 0, 24, 65, 100, 0, 0, 26, 65, 1));
        futures.add(generateWeaponData(writer, Items.NETHERITE_BOOTS.toString(), 20, 0, 0, 34, 80, 100, 0, 0, 28, 68, 1));
        return futures;
    }
    // Add this method to your data generator class
    private List<CompletableFuture<?>> generateOffhandItems(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Yuria Bracket - Blue Grade (Balanced stats)
        // Good starting offhand with balanced accuracy/evasion
        futures.add(generateWeaponData(writer, ModItems.YURIA_BRACKET.toString(),
                20,      // maxLvl
                45,      // startAccuracy
                92,      // endAccuracy
                40,      // startEvasion
                82,      // endEvasion
                100,     // maxDurability
                0,       // startDamage
                0,       // endDamage
                14,      // startProtection
                28,      // endProtection
                5));     // repairPoint (Blue grade)

        // Kutum Bracket - Boss Grade (Accuracy and DR focused)
        // Mimicking Kutum's AP, accuracy, and monster damage bonuses
        futures.add(generateWeaponData(writer, ModItems.KUTUM_BRACKET.toString(),
                20,      // maxLvl
                85,      // startAccuracy - high accuracy
                210,     // endAccuracy - very high at TET/PEN
                25,      // startEvasion - lower evasion
                65,      // endEvasion - moderate at high enhance
                100,     // maxDurability
                3,       // startDamage - small damage bonus
                12,      // endDamage - decent at TET/PEN
                18,      // startProtection - good DR
                42,      // endProtection - high DR at TET/PEN
                1));     // repairPoint (Boss grade)

        // Nouver Bracket - Boss Grade (Evasion and Damage focused)
        // Mimicking Nouver's high AP but lower defensive stats
        futures.add(generateWeaponData(writer, ModItems.NOUVER_BRACKET.toString(),
                20,      // maxLvl
                38,      // startAccuracy - lower accuracy
                85,      // endAccuracy - moderate at high enhance
                75,      // startEvasion - high evasion
                185,     // endEvasion - very high at TET/PEN
                100,     // maxDurability
                5,       // startDamage - higher damage
                18,      // endDamage - high damage at TET/PEN
                8,       // startProtection - lower DR
                22,      // endProtection - moderate at TET/PEN
                1));     // repairPoint (Boss grade)

        return futures;
    }

    private CompletableFuture<?> generateWeaponData(DataWriter writer, String itemName,
                                                    int maxLvl, int startAccuracy, int endAccuracy,
                                                    int startEvasion, int endEvasion, int maxDurability,
                                                    int startDamage, int endDamage,
                                                    int startProtection, int endProtection,
                                                    int repairPoint) {
        JsonObject json = new JsonObject();

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

        // Parse the item identifier to get namespace and path
        Identifier itemId = Identifier.tryParse(itemName);
        if (itemId == null) {
            // If itemName doesn't have a namespace, assume it's minecraft
            itemId = Identifier.ofVanilla(itemName);
        }

        // Get namespace and path from itemId
        String namespace = itemId.getNamespace();
        String path = itemId.getPath();

        // Create the output path using Identifier
        Identifier outputId = Dominatus.id("refine/" + namespace + "/" + path);

        // Write the JSON file
        return DataProvider.writeToPath(writer, json,
                output.getPath().resolve("data/" + outputId.getNamespace() + "/" + outputId.getPath() + ".json"));
    }

    @Override
    public String getName() {
        return "Refinement Data";
    }
}