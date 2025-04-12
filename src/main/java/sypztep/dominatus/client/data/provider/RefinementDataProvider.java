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
        futures.addAll(generateSimpleSword(writer));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private List<CompletableFuture<?>> generateWeapons(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Swords - Balanced weapons
        futures.add(generateWeaponData(writer, Items.WOODEN_SWORD.toString(), 15, 45, 100, 4, 12, 0, 0, 10));
        futures.add(generateWeaponData(writer, Items.STONE_SWORD.toString(), 20, 60, 100, 5, 15, 0, 0, 10));
        futures.add(generateWeaponData(writer, Items.IRON_SWORD.toString(), 30, 85, 100, 6, 18, 0, 0, 5));
        futures.add(generateWeaponData(writer, Items.DIAMOND_SWORD.toString(), 50, 120, 100, 8, 24, 0, 0, 5));
        futures.add(generateWeaponData(writer, Items.GOLDEN_SWORD.toString(), 40, 100, 100, 10, 30, 0, 0, 2));
        futures.add(generateWeaponData(writer, Items.NETHERITE_SWORD.toString(), 65, 150, 100, 12, 40, 0, 0, 1));

        // Axes - Higher damage, lower accuracy
        futures.add(generateWeaponData(writer, Items.WOODEN_AXE.toString(), 10, 35, 100, 6, 18, 0, 0, 10));
        futures.add(generateWeaponData(writer, Items.STONE_AXE.toString(), 15, 45, 100, 8, 21, 0, 0, 10));
        futures.add(generateWeaponData(writer, Items.IRON_AXE.toString(), 25, 75, 100, 10, 27, 0, 0, 5));
        futures.add(generateWeaponData(writer, Items.GOLDEN_AXE.toString(), 30, 85, 100, 10, 27, 0, 0, 5));
        futures.add(generateWeaponData(writer, Items.DIAMOND_AXE.toString(), 40, 100, 100, 12, 33, 0, 0, 2));
        futures.add(generateWeaponData(writer, Items.NETHERITE_AXE.toString(), 55, 130, 100, 14, 42, 0, 0, 1));

        // Ranged weapons
        futures.add(generateWeaponData(writer, Items.CROSSBOW.toString(), 45, 115, 100, 0, 0, 0, 0, 5));
        futures.add(generateWeaponData(writer, Items.BOW.toString(), 40, 110, 100, 0, 0, 0, 0, 5));

        // Special weapons
        futures.add(generateItemData(writer, Items.MACE.toString(), 55, 130, 0, 0, 100, 5, 18, 0, 0, 0, 0, 1));
        futures.add(generateItemData(writer, Items.TRIDENT.toString(), 55, 130, 0, 0, 100, 5, 18, 0, 0, 0, 0, 1));
        futures.add(generateItemData(writer, Items.SHIELD.toString(), 5, 40, 15, 50, 100, 0, 0, 8, 20, 0, 0, 5));

        return futures;
    }

    private List<CompletableFuture<?>> generateLeatherArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateArmorData(writer, Items.LEATHER_HELMET.toString(), 8, 25, 100, 4, 10, 0, 0, 10));
        futures.add(generateArmorData(writer, Items.LEATHER_CHESTPLATE.toString(), 12, 35, 100, 6, 16, 0, 0, 10));
        futures.add(generateArmorData(writer, Items.LEATHER_LEGGINGS.toString(), 10, 30, 100, 5, 12, 0, 0, 10));
        futures.add(generateArmorData(writer, Items.LEATHER_BOOTS.toString(), 8, 25, 100, 4, 10, 0, 0, 10));
        return futures;
    }

    private List<CompletableFuture<?>> generateChainmailArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateArmorData(writer, Items.CHAINMAIL_HELMET.toString(), 6, 20, 100, 6, 15, 0, 0, 5));
        futures.add(generateArmorData(writer, Items.CHAINMAIL_CHESTPLATE.toString(), 10, 28, 100, 10, 24, 0, 0, 5));
        futures.add(generateArmorData(writer, Items.CHAINMAIL_LEGGINGS.toString(), 8, 24, 100, 8, 20, 0, 0, 5));
        futures.add(generateArmorData(writer, Items.CHAINMAIL_BOOTS.toString(), 6, 20, 100, 6, 15, 0, 0, 5));
        return futures;
    }

    private List<CompletableFuture<?>> generateIronArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateArmorData(writer, Items.IRON_HELMET.toString(), 4, 15, 100, 8, 20, 0, 0, 5));
        futures.add(generateArmorData(writer, Items.IRON_CHESTPLATE.toString(), 6, 18, 100, 12, 28, 0, 0, 5));
        futures.add(generateArmorData(writer, Items.IRON_LEGGINGS.toString(), 5, 16, 100, 10, 24, 0, 0, 5));
        futures.add(generateArmorData(writer, Items.IRON_BOOTS.toString(), 4, 15, 100, 8, 20, 0, 0, 5));
        return futures;
    }

    private List<CompletableFuture<?>> generateGoldenArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateArmorData(writer, Items.GOLDEN_HELMET.toString(), 10, 30, 100, 6, 16, 0, 0, 2));
        futures.add(generateArmorData(writer, Items.GOLDEN_CHESTPLATE.toString(), 15, 40, 100, 10, 24, 0, 0, 2));
        futures.add(generateArmorData(writer, Items.GOLDEN_LEGGINGS.toString(), 12, 35, 100, 8, 20, 0, 0, 2));
        futures.add(generateArmorData(writer, Items.GOLDEN_BOOTS.toString(), 10, 30, 100, 6, 16, 0, 0, 2));
        return futures;
    }

    private List<CompletableFuture<?>> generateDiamondArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateArmorData(writer, Items.DIAMOND_HELMET.toString(), 8, 25, 100, 10, 24, 0, 0, 2));
        futures.add(generateArmorData(writer, Items.DIAMOND_CHESTPLATE.toString(), 12, 32, 100, 16, 36, 0, 0, 2));
        futures.add(generateArmorData(writer, Items.DIAMOND_LEGGINGS.toString(), 10, 28, 100, 12, 30, 0, 0, 2));
        futures.add(generateArmorData(writer, Items.DIAMOND_BOOTS.toString(), 8, 25, 100, 10, 24, 0, 0, 2));
        return futures;
    }

    private List<CompletableFuture<?>> generateNetheriteArmor(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateArmorData(writer, Items.NETHERITE_HELMET.toString(), 10, 30, 100, 12, 28, 0, 0, 1));
        futures.add(generateArmorData(writer, Items.NETHERITE_CHESTPLATE.toString(), 15, 40, 100, 20, 45, 0, 0, 1));
        futures.add(generateArmorData(writer, Items.NETHERITE_LEGGINGS.toString(), 12, 35, 100, 16, 36, 0, 0, 1));
        futures.add(generateArmorData(writer, Items.NETHERITE_BOOTS.toString(), 10, 30, 100, 12, 28, 0, 0, 1));
        return futures;
    }

    private List<CompletableFuture<?>> generateWristItems(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateItemData(writer, ModItems.YURIA_BRACKET.toString(), 15, 45, 8, 25, 100, 0, 0, 2, 10, 0, 0, 5));
        return futures;
    }

    private List<CompletableFuture<?>> generateSimpleSword(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(generateWeaponData(writer, "simplyswords:iron_longsword", 30, 85, 100, 6, 18, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_twinblade", 30, 85, 100, 7, 20, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_rapier", 35, 105, 100, 4, 15, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_katana", 30, 85, 100, 6, 18, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_sai", 20, 85, 100, 2, 14, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_spear", 30, 85, 100, 6, 18, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_glaive", 40, 105, 100, 8, 20, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_warglaive", 40, 105, 100, 6, 18, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_cutlass", 30, 85, 100, 7, 20, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_claymore", 35, 95, 100, 8, 24, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_greathammer", 20, 85, 100, 10, 24, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_greataxe", 20, 85, 100, 10, 26, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_chakram", 35, 110, 100, 4, 18, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_scythe", 35, 95, 100, 6, 20, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:iron_halberd", 20, 85, 100, 10, 24, 0, 0, 5));

        futures.add(generateWeaponData(writer, "simplyswords:gold_longsword", 40, 100, 100, 10, 30, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_twinblade", 40, 100, 100, 11, 32, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_rapier", 45, 120, 100, 8, 26, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_katana", 40, 100, 100, 10, 30, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_sai", 30, 100, 100, 6, 24, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_spear", 40, 100, 100, 10, 30, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_glaive", 50, 120, 100, 12, 32, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_warglaive", 50, 120, 100, 10, 30, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_cutlass", 40, 100, 100, 11, 32, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_claymore", 45, 110, 100, 12, 34, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_greathammer", 30, 100, 100, 14, 34, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_greataxe", 30, 100, 100, 14, 36, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_chakram", 45, 125, 100, 8, 28, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_scythe", 45, 110, 100, 10, 30, 0, 0, 2));
        futures.add(generateWeaponData(writer, "simplyswords:gold_halberd", 30, 100, 100, 14, 34, 0, 0, 2));

        futures.add(generateWeaponData(writer, "simplyswords:diamond_longsword", 50, 120, 100, 8, 24, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_twinblade", 50, 120, 100, 9, 26, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_rapier", 55, 140, 100, 6, 20, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_katana", 50, 120, 100, 8, 24, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_sai", 40, 120, 100, 5, 18, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_spear", 50, 120, 100, 8, 24, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_glaive", 60, 140, 100, 10, 26, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_warglaive", 60, 140, 100, 8, 24, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_cutlass", 50, 120, 100, 9, 26, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_claymore", 55, 130, 100, 10, 30, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_greathammer", 40, 120, 100, 12, 30, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_greataxe", 40, 120, 100, 12, 32, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_chakram", 55, 145, 100, 6, 22, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_scythe", 55, 130, 100, 8, 26, 0, 0, 5));
        futures.add(generateWeaponData(writer, "simplyswords:diamond_halberd", 40, 120, 100, 12, 30, 0, 0, 5));

        futures.add(generateWeaponData(writer, "simplyswords:netherite_longsword", 65, 150, 100, 12, 40, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_twinblade", 65, 150, 100, 13, 42, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_rapier", 70, 170, 100, 10, 36, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_katana", 65, 150, 100, 12, 40, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_sai", 55, 150, 100, 9, 34, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_spear", 65, 150, 100, 12, 40, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_glaive", 75, 170, 100, 14, 42, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_warglaive", 75, 170, 100, 12, 40, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_cutlass", 65, 150, 100, 13, 42, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_claymore", 70, 160, 100, 14, 46, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_greathammer", 55, 150, 100, 16, 46, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_greataxe", 55, 150, 100, 16, 48, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_chakram", 70, 175, 100, 10, 38, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_scythe", 70, 160, 100, 12, 42, 0, 0, 1));
        futures.add(generateWeaponData(writer, "simplyswords:netherite_halberd", 55, 150, 100, 16, 46, 0, 0, 1));

        futures.add(generateWeaponData(writer, "simplyswords:runic_longsword", 85, 190, 100, 14, 52, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_twinblade", 85, 190, 100, 15, 54, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_rapier", 90, 215, 100, 12, 46, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_katana", 85, 190, 100, 14, 52, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_sai", 75, 190, 100, 11, 44, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_spear", 85, 190, 100, 14, 52, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_glaive", 95, 215, 100, 16, 54, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_warglaive", 95, 215, 100, 14, 52, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_cutlass", 85, 190, 100, 15, 54, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_claymore", 90, 200, 100, 16, 60, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_greathammer", 75, 190, 100, 18, 60, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_greataxe", 75, 190, 100, 18, 62, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_chakram", 90, 225, 100, 12, 50, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_scythe", 90, 200, 100, 14, 56, 0, 0, 0));
        futures.add(generateWeaponData(writer, "simplyswords:runic_halberd", 75, 190, 100, 18, 60, 0, 0, 0));

        return futures;
    }

    private CompletableFuture<?> generateWeaponData(DataWriter writer, String itemName, int startAccuracy, int endAccuracy, int maxDurability, int startDamage, int endDamage, int startDamageReduction, int endDamageReduction, int repairPoint) {
        return generateItemData(writer, itemName, startAccuracy, endAccuracy, 0, 0, maxDurability, startDamage, endDamage, 0, 0, startDamageReduction, endDamageReduction, repairPoint);
    }

    private CompletableFuture<?> generateArmorData(DataWriter writer, String itemName, int startEvasion, int endEvasion, int maxDurability, int startProtection, int endProtection, int startDamageReduction, int endDamageReduction, int repairPoint) {
        return generateItemData(writer, itemName, 0, 0, startEvasion, endEvasion, maxDurability, 0, 0, startProtection, endProtection, startDamageReduction, endDamageReduction, repairPoint);
    }

    private CompletableFuture<?> generateItemData(DataWriter writer, String itemName, int startAccuracy, int endAccuracy, int startEvasion, int endEvasion, int maxDurability, int startDamage, int endDamage, int startProtection, int endProtection, int startDamageReduction, int endDamageReduction, int repairPoint) {
        JsonObject json = new JsonObject();
        JsonObject itemProperties = new JsonObject();
        JsonObject modifier = new JsonObject();

        itemProperties.addProperty("maxLvl", 20);

        JsonObject accuracy = new JsonObject();
        accuracy.addProperty("start", startAccuracy);
        accuracy.addProperty("end", endAccuracy);
        modifier.add("accuracy", accuracy);

        JsonObject evasion = new JsonObject();
        evasion.addProperty("start", startEvasion);
        evasion.addProperty("end", endEvasion);
        modifier.add("evasion", evasion);

        JsonObject damage = new JsonObject();
        damage.addProperty("start", startDamage);
        damage.addProperty("end", endDamage);
        modifier.add("damage", damage);

        JsonObject protection = new JsonObject();
        protection.addProperty("start", startProtection);
        protection.addProperty("end", endProtection);
        modifier.add("protection", protection);

        JsonObject damageReduction = new JsonObject();
        damageReduction.addProperty("start", startDamageReduction);
        damageReduction.addProperty("end", endDamageReduction);
        modifier.add("damageReduction", damageReduction);

        itemProperties.add("modifier", modifier);
        itemProperties.addProperty("maxDurability", maxDurability);
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