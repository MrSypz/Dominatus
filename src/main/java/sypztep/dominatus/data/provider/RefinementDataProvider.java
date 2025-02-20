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

        // Swords (Like Liverto/Rosar variants)
        // Format: name, isVanilla, maxLvl, startAcc, endAcc, startEva(0), endEva(0), durability, startDmg, endDmg, startProt(0), endProt(0), repair
        futures.add(generateWeaponData(writer, Items.WOODEN_SWORD.toString(), true, 15, 15, 45, 0, 0, 100, 8, 25, 0, 0, 10));
        futures.add(generateWeaponData(writer, Items.STONE_SWORD.toString(), true, 15, 18, 48, 0, 0, 100, 12, 32, 0, 0, 12));
        futures.add(generateWeaponData(writer, Items.IRON_SWORD.toString(), true, 15, 22, 52, 0, 0, 100, 16, 38, 0, 0, 15));
        futures.add(generateWeaponData(writer, Items.GOLDEN_SWORD.toString(), true, 15, 28, 58, 0, 0, 100, 14, 35, 0, 0, 20)); // High accuracy, lower damage
        futures.add(generateWeaponData(writer, Items.DIAMOND_SWORD.toString(), true, 15, 25, 55, 0, 0, 100, 20, 45, 0, 0, 25)); // Like Liverto
        futures.add(generateWeaponData(writer, Items.NETHERITE_SWORD.toString(), true, 15, 30, 62, 0, 0, 100, 25, 52, 0, 0, 30)); // Better Liverto

        // Axes (Higher damage, lower accuracy - like Iron/Steel axes in BDO)
        futures.add(generateWeaponData(writer, Items.WOODEN_AXE.toString(), true, 15, 12, 38, 0, 0, 100, 10, 28, 0, 0, 10));
        futures.add(generateWeaponData(writer, Items.STONE_AXE.toString(), true, 15, 15, 42, 0, 0, 100, 14, 35, 0, 0, 12));
        futures.add(generateWeaponData(writer, Items.IRON_AXE.toString(), true, 15, 18, 45, 0, 0, 100, 18, 42, 0, 0, 15));
        futures.add(generateWeaponData(writer, Items.DIAMOND_AXE.toString(), true, 15, 22, 48, 0, 0, 100, 24, 50, 0, 0, 20));
        futures.add(generateWeaponData(writer, Items.NETHERITE_AXE.toString(), true, 15, 25, 52, 0, 0, 100, 28, 58, 0, 0, 25));

        return futures;
    }

    // Armor sets rebalanced to focus on Protection and Evasion only
    private List<CompletableFuture<?>> generateLeatherArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Leather - Like Agerian/Talis (Evasion focused)
        // Format: name, isVanilla, maxLvl, startAcc(0), endAcc(0), startEva, endEva, durability, startDmg(0), endDmg(0), startProt, endProt, repair
        futures.add(generateWeaponData(writer, Items.LEATHER_HELMET.toString(), true, 15, 0, 0, 12, 32, 100, 0, 0, 4, 15, 10));
        futures.add(generateWeaponData(writer, Items.LEATHER_CHESTPLATE.toString(), true, 15, 0, 0, 15, 38, 100, 0, 0, 7, 22, 15));
        futures.add(generateWeaponData(writer, Items.LEATHER_LEGGINGS.toString(), true, 15, 0, 0, 14, 35, 100, 0, 0, 6, 18, 12));
        futures.add(generateWeaponData(writer, Items.LEATHER_BOOTS.toString(), true, 15, 0, 0, 16, 40, 100, 0, 0, 4, 15, 10));
        return futures;
    }

    private List<CompletableFuture<?>> generateChainmailArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Chainmail - Like Fortuna/Hercules (Balanced)
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_HELMET.toString(), true, 15, 0, 0, 10, 28, 100, 0, 0, 6, 18, 12));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_CHESTPLATE.toString(), true, 15, 0, 0, 12, 32, 100, 0, 0, 10, 26, 18));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_LEGGINGS.toString(), true, 15, 0, 0, 11, 30, 100, 0, 0, 8, 22, 15));
        futures.add(generateWeaponData(writer, Items.CHAINMAIL_BOOTS.toString(), true, 15, 0, 0, 13, 34, 100, 0, 0, 6, 18, 12));
        return futures;
    }

    private List<CompletableFuture<?>> generateIronArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Iron - Like Grunil (Protection focused)
        futures.add(generateWeaponData(writer, Items.IRON_HELMET.toString(), true, 15, 0, 0, 8, 25, 100, 0, 0, 8, 22, 15));
        futures.add(generateWeaponData(writer, Items.IRON_CHESTPLATE.toString(), true, 15, 0, 0, 10, 28, 100, 0, 0, 12, 32, 20));
        futures.add(generateWeaponData(writer, Items.IRON_LEGGINGS.toString(), true, 15, 0, 0, 9, 26, 100, 0, 0, 10, 28, 18));
        futures.add(generateWeaponData(writer, Items.IRON_BOOTS.toString(), true, 15, 0, 0, 11, 30, 100, 0, 0, 8, 22, 15));
        return futures;
    }

    private List<CompletableFuture<?>> generateGoldenArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Golden - Like Rocaba (High evasion)
        futures.add(generateWeaponData(writer, Items.GOLDEN_HELMET.toString(), true, 15, 0, 0, 14, 35, 100, 0, 0, 5, 18, 20));
        futures.add(generateWeaponData(writer, Items.GOLDEN_CHESTPLATE.toString(), true, 15, 0, 0, 18, 42, 100, 0, 0, 8, 25, 25));
        futures.add(generateWeaponData(writer, Items.GOLDEN_LEGGINGS.toString(), true, 15, 0, 0, 16, 38, 100, 0, 0, 7, 22, 22));
        futures.add(generateWeaponData(writer, Items.GOLDEN_BOOTS.toString(), true, 15, 0, 0, 15, 36, 100, 0, 0, 5, 18, 20));
        return futures;
    }

    private List<CompletableFuture<?>> generateDiamondArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Diamond - Like Heve (Better balanced)
        futures.add(generateWeaponData(writer, Items.DIAMOND_HELMET.toString(), true, 15, 0, 0, 12, 32, 100, 0, 0, 10, 28, 22));
        futures.add(generateWeaponData(writer, Items.DIAMOND_CHESTPLATE.toString(), true, 15, 0, 0, 15, 38, 100, 0, 0, 15, 35, 28));
        futures.add(generateWeaponData(writer, Items.DIAMOND_LEGGINGS.toString(), true, 15, 0, 0, 14, 35, 100, 0, 0, 12, 32, 25));
        futures.add(generateWeaponData(writer, Items.DIAMOND_BOOTS.toString(), true, 15, 0, 0, 13, 34, 100, 0, 0, 10, 28, 22));
        return futures;
    }

    private List<CompletableFuture<?>> generateNetheriteArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // Netherite - Like Ultimate Armor (Pre-boss)
        futures.add(generateWeaponData(writer, Items.NETHERITE_HELMET.toString(), true, 15, 0, 0, 15, 38, 100, 0, 0, 12, 32, 25));
        futures.add(generateWeaponData(writer, Items.NETHERITE_CHESTPLATE.toString(), true, 15, 0, 0, 18, 42, 100, 0, 0, 18, 42, 32));
        futures.add(generateWeaponData(writer, Items.NETHERITE_LEGGINGS.toString(), true, 15, 0, 0, 16, 40, 100, 0, 0, 15, 38, 28));
        futures.add(generateWeaponData(writer, Items.NETHERITE_BOOTS.toString(), true, 15, 0, 0, 15, 38, 100, 0, 0, 12, 32, 25));
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