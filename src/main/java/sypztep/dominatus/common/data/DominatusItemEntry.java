package sypztep.dominatus.common.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.util.refinesystem.StatRange;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record DominatusItemEntry(
        int maxLvl,
        StatRange<Integer> accuracy,
        StatRange<Integer> evasion,
        int maxDurability,
        StatRange<Float> damage,
        StatRange<Integer> protection,
        StatRange<Integer> damageReduction,
        int repairpoint
) {
    public static final Map<RegistryEntry<Item>, DominatusItemEntry> DOMINATUS_ITEM_ENTRY_MAP = new HashMap<>();

    public static Optional<DominatusItemEntry> getDominatusItemData(ItemStack stack) {
        RegistryEntry<Item> itemEntry = Registries.ITEM.getEntry(stack.getItem());
        return Optional.ofNullable(DOMINATUS_ITEM_ENTRY_MAP.get(itemEntry));
    }

    public static Optional<DominatusItemEntry> getDominatusItemData(String itemID) {
        Identifier itemIdentifier = Identifier.of(itemID);
        RegistryEntry<Item> itemEntry = Registries.ITEM.getEntry(itemIdentifier).orElse(null);
        return Optional.ofNullable(DOMINATUS_ITEM_ENTRY_MAP.get(itemEntry));
    }

    public static String getItemId(ItemStack stack) {
        return Registries.ITEM.getId(stack.getItem()).toString();
    }
}