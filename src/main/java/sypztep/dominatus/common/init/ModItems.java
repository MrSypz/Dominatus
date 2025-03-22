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
    public static Item MOONLIGHT_CRESCENT;
    public static Item MAHILNANT;


    public static void init() {
        REFINE_WEAPON_STONE = registerItem("refine_weapon_stone", new RefinementStoneItem(new Item.Settings().maxCount(99).rarity(Rarity.RARE)));
        REFINE_ARMOR_STONE = registerItem("refine_armor_stone", new RefinementStoneItem(new Item.Settings().maxCount(99).rarity(Rarity.RARE)));
        LOSS_FRAGMENT = registerItem("loss_fragment", new RefinementStoneItem(new Item.Settings().maxCount(99).rarity(Rarity.UNCOMMON)));
        LAHAV_FRAGMENT = registerItem("lahav_fragment", new RefinementStoneItem(new Item.Settings().maxCount(99).rarity(Rarity.UNCOMMON).fireproof()));
        REFINE_WEAPONENFORGE_STONE = registerItem("refine_weapon_enforge_stone", new RefinementStoneItem(new Item.Settings().maxCount(99).rarity(Rarity.EPIC)));
        REFINE_ARMORENFORGE_STONE = registerItem("refine_armor_enforge_stone", new RefinementStoneItem(new Item.Settings().maxCount(99).rarity(Rarity.EPIC).fireproof()));
        MAHILNANT = registerItem("mahilnant", new RefinementStoneItem(new Item.Settings().maxCount(99).rarity(Rarity.RARE)));
        MOONLIGHT_CRESCENT = registerItem("moonlight_crescent", new RefinementStoneItem(new Item.Settings().maxCount(99).rarity(Rarity.RARE)));


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

    public static <T extends Item> T registerItem(String name, T item) {
        Registry.register(Registries.ITEM, Dominatus.id(name), item);
        return item;
    }
}
