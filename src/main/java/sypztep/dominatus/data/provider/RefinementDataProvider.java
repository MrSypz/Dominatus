package sypztep.dominatus.data.provider;

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
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

    public RefinementDataProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        this.output = output;
        this.registriesFuture = registriesFuture;
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

        // Swords
        futures.add(generateWeaponData(writer, Items.WOODEN_SWORD.toString(), true, 20, 15, 65, 8, 45, 100, 8, 25, 3, 12, 10));
        futures.add(generateWeaponData(writer, Items.STONE_SWORD.toString(), true, 20, 18, 70, 10, 48, 100, 10, 28, 4, 14, 12));
        futures.add(generateWeaponData(writer, Items.IRON_SWORD.toString(), true, 20, 22, 75, 12, 52, 100, 12, 32, 5, 16, 15));
        futures.add(generateWeaponData(writer, Items.GOLDEN_SWORD.toString(), true, 20, 25, 78, 15, 55, 100, 15, 35, 6, 18, 20));
        futures.add(generateWeaponData(writer, Items.DIAMOND_SWORD.toString(), true, 20, 28, 82, 18, 58, 100, 18, 40, 8, 22, 25));
        futures.add(generateWeaponData(writer, Items.NETHERITE_SWORD.toString(), true, 20, 32, 88, 20, 62, 100, 22, 45, 10, 25, 30));

        // Axes
        futures.add(generateWeaponData(writer, Items.WOODEN_AXE.toString(), true, 20, 12, 60, 5, 40, 90, 9, 26, 2, 10, 10));
        futures.add(generateWeaponData(writer, Items.STONE_AXE.toString(), true, 20, 15, 65, 6, 42, 110, 11, 29, 3, 12, 12));
        futures.add(generateWeaponData(writer, Items.IRON_AXE.toString(), true, 20, 18, 70, 8, 45, 140, 13, 33, 4, 14, 15));
        futures.add(generateWeaponData(writer, Items.DIAMOND_AXE.toString(), true, 20, 25, 78, 12, 50, 180, 16, 38, 6, 18, 20));
        futures.add(generateWeaponData(writer, Items.NETHERITE_AXE.toString(), true, 20, 28, 82, 15, 55, 220, 20, 42, 8, 22, 25));

        return futures;
    }

    private List<CompletableFuture<?>> generateLeatherArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Leather - Light armor, high evasion, low protection
        futures.add(generateWeaponData(writer, Items.LEATHER_HELMET.toString(), true, 20, 3, 35, 12, 45, 80, 0, 8, 5, 18, 10));
        futures.add(generateWeaponData(writer, Items.LEATHER_CHESTPLATE.toString(), true, 20, 5, 45, 15, 55, 120, 0, 10, 8, 25, 15));
        futures.add(generateWeaponData(writer, Items.LEATHER_LEGGINGS.toString(), true, 20, 4, 40, 14, 50, 100, 0, 9, 7, 22, 12));
        futures.add(generateWeaponData(writer, Items.LEATHER_BOOTS.toString(), true, 20, 3, 35, 16, 58, 90, 0, 8, 4, 16, 10));
        return futures;
    }

    private List<CompletableFuture<?>> generateChainmailArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Chainmail - Medium armor, balanced stats
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_HELMET.toString(), true, 20, 5, 38, 10, 42, 100, 0, 10, 8, 22, 12));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_CHESTPLATE.toString(), true, 20, 8, 48, 12, 52, 150, 0, 12, 12, 30, 18));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_LEGGINGS.toString(), true, 20, 6, 42, 11, 45, 120, 0, 11, 10, 26, 15));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_BOOTS.toString(), true, 20, 5, 38, 13, 48, 110, 0, 10, 7, 20, 12));
        return futures;
    }

    private List<CompletableFuture<?>> generateIronArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Iron - Heavy armor, high protection, lower evasion
        futures.add(generateWeaponData(writer, Items.IRON_HELMET.toString(), true, 20, 8, 42, 8, 40, 120, 0, 12, 12, 28, 15));
        futures.add(generateWeaponData(writer, Items.IRON_CHESTPLATE.toString(), true, 20, 10, 52, 10, 50, 180, 0, 15, 15, 35, 20));
        futures.add(generateWeaponData(writer, Items.IRON_LEGGINGS.toString(), true, 20, 9, 45, 9, 45, 150, 0, 13, 14, 32, 18));
        futures.add(generateWeaponData(writer, Items.IRON_BOOTS.toString(), true, 20, 8, 42, 11, 46, 130, 0, 12, 11, 26, 15));
        return futures;
    }

    private List<CompletableFuture<?>> generateGoldenArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Golden - Special armor, high accuracy bonus but medium protection
        futures.add(generateWeaponData(writer, Items.GOLDEN_HELMET.toString(), true, 20, 10, 45, 7, 38, 110, 0, 14, 14, 30, 20));
        futures.add(generateWeaponData(writer, Items.GOLDEN_CHESTPLATE.toString(), true, 20, 12, 55, 8, 48, 160, 0, 18, 18, 38, 25));
        futures.add(generateWeaponData(writer, Items.GOLDEN_LEGGINGS.toString(), true, 20, 11, 50, 8, 42, 140, 0, 16, 16, 34, 22));
        futures.add(generateWeaponData(writer, Items.GOLDEN_BOOTS.toString(), true, 20, 10, 45, 9, 44, 120, 0, 14, 13, 28, 20));
        return futures;
    }

    private List<CompletableFuture<?>> generateDiamondArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Diamond - Superior armor, high all stats
        futures.add(generateWeaponData(writer, Items.DIAMOND_HELMET.toString(), true, 20, 12, 52, 5, 42, 180, 0, 16, 18, 36, 22));
        futures.add(generateWeaponData(writer, Items.DIAMOND_CHESTPLATE.toString(), true, 20, 15, 58, 6, 45, 220, 0, 20, 22, 42, 28));
        futures.add(generateWeaponData(writer, Items.DIAMOND_LEGGINGS.toString(), true, 20, 13, 55, 6, 44, 200, 0, 18, 20, 40, 25));
        futures.add(generateWeaponData(writer, Items.DIAMOND_BOOTS.toString(), true, 20, 12, 52, 7, 46, 190, 0, 16, 17, 34, 22));
        return futures;
    }

    private List<CompletableFuture<?>> generateNetheriteArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Netherite - Boss-tier armor, highest stats
        futures.add(generateWeaponData(writer, Items.NETHERITE_HELMET.toString(), true, 20, 15, 58, 4, 40, 240, 0, 20, 22, 42, 25));
        futures.add(generateWeaponData(writer, Items.NETHERITE_CHESTPLATE.toString(), true, 20, 18, 62, 5, 42, 280, 0, 25, 25, 48, 32));
        futures.add(generateWeaponData(writer, Items.NETHERITE_LEGGINGS.toString(), true, 20, 16, 60, 5, 41, 260, 0, 22, 24, 45, 28));
        futures.add(generateWeaponData(writer, Items.NETHERITE_BOOTS.toString(), true, 20, 15, 58, 6, 44, 250, 0, 20, 21, 40, 25));
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