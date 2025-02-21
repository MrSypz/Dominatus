package sypztep.dominatus.common.init;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.item.RefinementStoneItem;

public class ModItems {
    public static Item REFINE_WEAPON_STONE;
    public static Item REFINE_ARMOR_STONE;
    public static Item LOSS_FRAGMENT;
    public static Item LAHAV_FRAGMENT;
    public static Item REFINE_WEAPONENFORGE_STONE;
    public static Item REFINE_ARMORENFORGE_STONE;
    public static Item MAHILNANT;
    public static Item MOONLIGHT_CRESCENT;

    public static void init() {
        REFINE_WEAPON_STONE = register("refine_weapon_stone", new RefinementStoneItem(new Item.Settings().rarity(Rarity.RARE).registryKey(RegistryKey.of(RegistryKeys.ITEM, Dominatus.id("refine_weapon_stone")))));
        REFINE_ARMOR_STONE = register("refine_armor_stone", new RefinementStoneItem(new Item.Settings().rarity(Rarity.RARE).registryKey(RegistryKey.of(RegistryKeys.ITEM, Dominatus.id("refine_armor_stone")))));
        LOSS_FRAGMENT = register("loss_fragment", new RefinementStoneItem(new Item.Settings().rarity(Rarity.UNCOMMON).registryKey(RegistryKey.of(RegistryKeys.ITEM, Dominatus.id("loss_fragment")))));
        LAHAV_FRAGMENT = register("lahav_fragment", new RefinementStoneItem(new Item.Settings().rarity(Rarity.UNCOMMON).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, Dominatus.id("lahav_fragment")))));
        REFINE_WEAPONENFORGE_STONE = register("refine_weapon_enforge_stone", new RefinementStoneItem(new Item.Settings().rarity(Rarity.EPIC).registryKey(RegistryKey.of(RegistryKeys.ITEM, Dominatus.id("refine_weapon_enforge_stone")))));
        REFINE_ARMORENFORGE_STONE = register("refine_armor_enforge_stone", new RefinementStoneItem(new Item.Settings().rarity(Rarity.EPIC).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, Dominatus.id("refine_armor_enforge_stone")))));
        MAHILNANT = register("mahilnant", new RefinementStoneItem(new Item.Settings().rarity(Rarity.RARE).registryKey(RegistryKey.of(RegistryKeys.ITEM, Dominatus.id("mahilnant")))));
        MOONLIGHT_CRESCENT = register("moonlight_crescent", new RefinementStoneItem(new Item.Settings().rarity(Rarity.RARE).registryKey(RegistryKey.of(RegistryKeys.ITEM, Dominatus.id("moonlight_crescent")))));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.addAfter(Items.DISC_FRAGMENT_5, ModItems.REFINE_ARMOR_STONE);
            content.addAfter(ModItems.REFINE_ARMOR_STONE, ModItems.REFINE_WEAPON_STONE);
            content.addAfter(ModItems.REFINE_WEAPON_STONE, ModItems.LOSS_FRAGMENT);
            content.addAfter(ModItems.LOSS_FRAGMENT, ModItems.LAHAV_FRAGMENT);
            content.addAfter(ModItems.LAHAV_FRAGMENT, ModItems.REFINE_ARMORENFORGE_STONE);
            content.addAfter(ModItems.REFINE_ARMORENFORGE_STONE, ModItems.REFINE_WEAPONENFORGE_STONE);
            content.addAfter(ModItems.REFINE_WEAPONENFORGE_STONE, ModItems.MOONLIGHT_CRESCENT);
            content.addAfter(ModItems.MOONLIGHT_CRESCENT, ModItems.MAHILNANT);
        });
    }

    public static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, RegistryKey.of(RegistryKeys.ITEM, Dominatus.id(name)).getValue(), item);
    }
}
