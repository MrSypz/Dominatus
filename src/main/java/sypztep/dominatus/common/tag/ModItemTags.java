package sypztep.dominatus.common.tag;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import sypztep.dominatus.Dominatus;

public class ModItemTags {
    public static final TagKey<Item> REFORM_MATERIAL = TagKey.of(RegistryKeys.ITEM, Dominatus.id("reform_item"));
}
