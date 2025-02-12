package sypztep.dominatus.common.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record DominatusItemEntry(
        int maxLvl,
        int startAccuracy,
        int endAccuracy,
        int startEvasion,
        int endEvasion,
        int maxDurability,
        float starDamage,
        float endDamage,
        int startProtection,
        int endProtection,
        int repairpoint
)
{
    public static final Map<RegistryEntry<Item>, DominatusItemEntry> PENOMIOR_ITEM_ENTRY_MAP = new HashMap<>();

    public static Optional<DominatusItemEntry> getDominatusItemData(ItemStack stack) {
        RegistryEntry<Item> itemEntry = Registries.ITEM.getEntry(stack.getItem());
        return Optional.ofNullable(PENOMIOR_ITEM_ENTRY_MAP.get(itemEntry));
    }
    public static Optional<DominatusItemEntry> getDominatusItemData(String itemID) {
        Identifier itemIdentifier = Identifier.of(itemID);
        RegistryEntry<Item> itemEntry = Registries.ITEM.getEntry(itemIdentifier).orElse(null);
        return Optional.ofNullable(PENOMIOR_ITEM_ENTRY_MAP.get(itemEntry));
    }
    public static String getItemId(ItemStack stack) {
        return Registries.ITEM.getId(stack.getItem()).toString();
    }
}
