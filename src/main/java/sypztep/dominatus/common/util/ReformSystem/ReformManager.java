package sypztep.dominatus.common.util.ReformSystem;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.data.Reform;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModItems;
import sypztep.hawsamoot.common.data.RarityBorder;
import sypztep.hawsamoot.common.util.RarityHelper;

import java.util.HashMap;
import java.util.Map;

public class ReformManager {
    public record ReformStats(
            int accuracy,
            int evasion,
            float damage,
            int protection,
            float attackSpeed,
            float critChance,
            float critDamage
    ) {}

    public enum ReformCategory {
        WEAPON, ARMOR, BOTH
    }

    public enum ReformStoneGrade {
        LOW(1, RarityBorder.COMMON),
        MID(2, RarityBorder.RARE),
        HIGH(3, RarityBorder.LEGENDARY);

        private final int grade;
        private final RarityBorder rarityBorder;

        ReformStoneGrade(int grade, RarityBorder rarityBorder) {
            this.grade = grade;
            this.rarityBorder = rarityBorder;
        }

        public int getGrade() {
            return grade;
        }

        public RarityBorder getRarityBorder() {
            return rarityBorder;
        }

        public static ReformStoneGrade fromItem(ItemStack stack) {
            if (stack.isOf(ModItems.REFORM_STONE_GRADE_HIGH)) {
                return HIGH;
            } else if (stack.isOf(ModItems.REFORM_STONE_GRADE_MID)) {
                return MID;
            } else
                return LOW;
        }
    }

    public static ReformCategory getItemCategory(ItemStack stack) {
        // Implement logic to detect if an item is a weapon or armor
        if (stack.getItem() instanceof net.minecraft.item.ArmorItem) {
            return ReformCategory.ARMOR;
        } else if (stack.getItem() instanceof net.minecraft.item.SwordItem ||
                stack.getItem() instanceof net.minecraft.item.AxeItem ||
                stack.getItem() instanceof net.minecraft.item.BowItem) {
            return ReformCategory.WEAPON;
        }
        return null; // Not reformable
    }

    public static boolean canReform(ItemStack item, ItemStack reformStone) {
        ReformCategory itemCategory = getItemCategory(item);
        if (itemCategory == null) {
            return false; // Item is not reformable
        }

        ReformStoneGrade stoneGrade = ReformStoneGrade.fromItem(reformStone);
        if (stoneGrade == null) {
            return false; // Not a valid reform stone
        }

        // All other checks (already has ultimate, etc.)
        ReformType currentReform = getReform(item).type();
        return currentReform != ReformType.ULTIMATE; // Already has ultimate reform
    }

    // Apply reform to an item
    public static boolean applyReform(ItemStack item, ItemStack reformStone) {
        // Check if reform is possible
        if (!canReform(item, reformStone)) {
            return false;
        }

        ReformCategory itemCategory = getItemCategory(item);
        ReformStoneGrade stoneGrade = ReformStoneGrade.fromItem(reformStone);

        ReformType reform = rollReform(stoneGrade, itemCategory);

        // Apply the reform
        if (reform == ReformType.NONE) {
            clearReform(item);
        } else {
            setReform(item, reform, stoneGrade);
        }

        return true;
    }

    private static ReformType rollReform(ReformStoneGrade stoneGrade, ReformCategory category) {
        float roll = (float) Math.random();

        // First determine outcome based on stone grade
        switch (stoneGrade) {
            case LOW: // Grade 1
                if (category == ReformCategory.ARMOR) {
                    if (roll < 0.20f) return ReformType.IRON_WALL;         // 20%
                    else if (roll < 0.50f) return ReformType.AGILITY;      // 30%
                    else if (roll < 0.80f) return ReformType.INTIMIDATION; // 30%
                    else return ReformType.NONE;                           // 20% Green Grade
                } else { // WEAPON
                    if (roll < 0.20f) return ReformType.TEMPTATION;        // 20%
                    else if (roll < 0.50f) return ReformType.DESTRUCTION;  // 30%
                    else if (roll < 0.80f) return ReformType.CRIMSON_FLAME;// 30%
                    else return ReformType.NONE;                           // 20% Green Grade
                }

            case MID: // Grade 2
                if (roll < 0.01f) return ReformType.ULTIMATE;             // 1%
                if (category == ReformCategory.ARMOR) {
                    if (roll < 0.21f) return ReformType.IRON_WALL;        // 20%
                    else if (roll < 0.41f) return ReformType.AGILITY;     // 20%
                    else if (roll < 0.61f) return ReformType.INTIMIDATION;// 20%
                    else if (roll < 0.71f) return ReformType.NONE;        // 10% Green Grade
                    else return ReformType.NONE;                          // 29% Fail
                } else { // WEAPON
                    if (roll < 0.21f) return ReformType.TEMPTATION;       // 20%
                    else if (roll < 0.41f) return ReformType.DESTRUCTION; // 20%
                    else if (roll < 0.61f) return ReformType.CRIMSON_FLAME;// 20%
                    else if (roll < 0.71f) return ReformType.NONE;        // 10% Green Grade
                    else return ReformType.NONE;                          // 29% Fail
                }

            case HIGH: // Grade 3
                if (roll < 0.05f) return ReformType.ULTIMATE;             // 5%
                if (category == ReformCategory.ARMOR) {
                    if (roll < 0.35f) return ReformType.IRON_WALL;        // 30%
                    else if (roll < 0.65f) return ReformType.AGILITY;     // 30%
                    else if (roll < 0.85f) return ReformType.INTIMIDATION;// 20%
                    else if (roll < 0.95f) return ReformType.NONE;        // 10% Green Grade
                    else return ReformType.NONE;                          // 5% Fail
                } else { // WEAPON
                    if (roll < 0.35f) return ReformType.TEMPTATION;       // 30%
                    else if (roll < 0.65f) return ReformType.DESTRUCTION; // 30%
                    else if (roll < 0.85f) return ReformType.CRIMSON_FLAME;// 20%
                    else if (roll < 0.95f) return ReformType.NONE;        // 10% Green Grade
                    else return ReformType.NONE;                          // 5% Fail
                }
        }
        return ReformType.NONE;
    }
    private static void setReform(ItemStack stack, ReformType reform, ReformStoneGrade stoneGrade) {
        // Set rarity based on reform type and stone grade
        RarityBorder newRarity;

        if (reform == ReformType.ULTIMATE) {
            // Ultimate reforms always get legendary rarity
            newRarity = RarityBorder.LEGENDARY;
        } else if (reform == ReformType.NONE) {
            // Failed reforms get common rarity
            newRarity = RarityBorder.COMMON;
        } else {
            // Other successful reforms get rarity based on stone grade
            switch (stoneGrade) {
                case HIGH -> newRarity = RarityBorder.EPIC;      // Yellow stone successful reforms
                case MID -> newRarity = RarityBorder.RARE;       // Blue stone successful reforms
                case LOW -> newRarity = RarityBorder.UNCOMMON;   // Green stone successful reforms
                default -> newRarity = RarityBorder.COMMON;
            }
        }

        // Update rarity border
        RarityHelper.setRarity(stack, newRarity);

        // Set reform data
        stack.set(ModDataComponents.REFORM, new Reform(reform));

        if (reform.hasEffect()) {
            renameWithPrefix(stack, reform);
            applyReformAttributes(stack, reform);
        }
    }

    // Apply attribute bonuses from reform
    private static void applyReformAttributes(ItemStack stack, ReformType reform) {
        // Get reform stats
        ReformStats stats = reform.getStats();

        // Get appropriate slot for this item
        AttributeModifierSlot slot = getAppropriateSlot(stack);

        // Get current attribute modifiers or default
        AttributeModifiersComponent attributeModifiers = stack.getOrDefault(
                DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();

        // Define our reform attribute IDs and values
        Map<RegistryEntry<EntityAttribute>, Pair<Identifier, Number>> reformAttributes = new HashMap<>();

        // Determine item category
        ReformCategory itemCategory = getItemCategory(stack);

        // Apply attributes based on item type and reform type
        if (itemCategory == ReformCategory.WEAPON) {
            // Apply weapon-appropriate stats
            if (stats.damage() > 0)
                reformAttributes.put(EntityAttributes.ATTACK_DAMAGE, new Pair<>(Dominatus.id("reform.damage"), stats.damage()));

            // Only apply these attributes to weapons
            if (stats.critChance() > 0)
                reformAttributes.put(ModEntityAttributes.CRIT_CHANCE, new Pair<>(Dominatus.id("reform.crit_chance"), stats.critChance()));

            if (stats.critDamage() > 0)
                reformAttributes.put(ModEntityAttributes.CRIT_DAMAGE, new Pair<>(Dominatus.id("reform.crit_damage"), stats.critDamage()));

            if (stats.attackSpeed() > 0 && reform == ReformType.ULTIMATE)
                reformAttributes.put(EntityAttributes.ATTACK_SPEED, new Pair<>(Dominatus.id("reform.attack_speed"), stats.attackSpeed()));

            if (stats.accuracy() > 0 && reform == ReformType.ULTIMATE)
                reformAttributes.put(ModEntityAttributes.ACCURACY, new Pair<>(Dominatus.id("reform.accuracy"), stats.accuracy()));
        }
        else if (itemCategory == ReformCategory.ARMOR) {
            // Apply armor-appropriate stats
            if (stats.protection() > 0)
                reformAttributes.put(EntityAttributes.ARMOR, new Pair<>(Dominatus.id("reform.armor"), stats.protection()));

            // Only apply these attributes to armor
            if (stats.evasion() > 0)
                reformAttributes.put(ModEntityAttributes.EVASION, new Pair<>(Dominatus.id("reform.evasion"), stats.evasion()));

            if (stats.attackSpeed() > 0 && (reform == ReformType.INTIMIDATION || reform == ReformType.SACRIFICE || reform == ReformType.ULTIMATE))
                reformAttributes.put(EntityAttributes.ATTACK_SPEED, new Pair<>(Dominatus.id("reform.attack_speed"), stats.attackSpeed()));

            if (stats.accuracy() > 0 && (reform == ReformType.AGILITY || reform == ReformType.ULTIMATE))
                reformAttributes.put(ModEntityAttributes.ACCURACY, new Pair<>(Dominatus.id("reform.accuracy"), stats.accuracy()));

            if (stats.critChance() > 0 && (reform == ReformType.SACRIFICE || reform == ReformType.ULTIMATE))
                reformAttributes.put(ModEntityAttributes.CRIT_CHANCE, new Pair<>(Dominatus.id("reform.crit_chance"), stats.critChance()));
        }

        // Copy non-conflicting attributes
        for (AttributeModifiersComponent.Entry entry : attributeModifiers.modifiers()) {
            Identifier modifierId = entry.modifier().id();
            boolean isOurReformAttribute = modifierId.getNamespace().equals(Dominatus.MODID) &&
                    modifierId.getPath().startsWith("reform.");

            // Skip our own reform attributes, will add new ones below
            if (!isOurReformAttribute) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
        }

        // Add our reform attributes
        reformAttributes.forEach((attribute, pair) ->
                builder.add(
                        attribute,
                        new EntityAttributeModifier(
                                pair.getLeft(),
                                pair.getRight().doubleValue(),
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        slot
                )
        );

        stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
    }

    private static AttributeModifierSlot getAppropriateSlot(ItemStack stack) {
        if (stack.contains(DataComponentTypes.EQUIPPABLE)) {
            return AttributeModifierSlot.forEquipmentSlot(stack.get(DataComponentTypes.EQUIPPABLE).slot());
        }

        if (stack.getItem() instanceof ArmorItem) return AttributeModifierSlot.ARMOR;
        return AttributeModifierSlot.MAINHAND;
    }
    public static void clearReform(ItemStack stack) {
        ReformType currentReform = getReform(stack).type();

        if (currentReform == ReformType.NONE) {
            return;
        }

        // Remove reform data
        if (stack.contains(ModDataComponents.REFORM)) {
            stack.remove(ModDataComponents.REFORM);
        }

        // Reset rarity to default
        RarityHelper.setRarity(stack, getDefaultRarity(stack));

        // Clear custom name if it has reform prefix
        if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
            String currentName = stack.getName().getString();
            boolean hasPrefix = false;

            for (ReformType type : ReformType.values()) {
                if (type != ReformType.NONE && currentName.startsWith(type.getPrefix())) {
                    hasPrefix = true;
                    break;
                }
            }

            if (hasPrefix) {
                stack.remove(DataComponentTypes.CUSTOM_NAME);
            }
        }

        // Remove reform attributes
        if (stack.contains(DataComponentTypes.ATTRIBUTE_MODIFIERS)) {
            AttributeModifiersComponent attributeModifiers = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();

            // Copy only non-reform attributes
            for (AttributeModifiersComponent.Entry entry : attributeModifiers.modifiers()) {
                Identifier modifierId = entry.modifier().id();
                boolean isReformAttribute = modifierId.getNamespace().equals(Dominatus.MODID) &&
                        modifierId.getPath().startsWith("reform.");

                if (!isReformAttribute) {
                    builder.add(entry.attribute(), entry.modifier(), entry.slot());
                }
            }

            // Apply the filtered attributes or remove if empty
            if (builder.build().modifiers().isEmpty()) {
                stack.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            } else {
                stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
            }
        }
    }

    // Helper method to get default rarity for items
    private static RarityBorder getDefaultRarity(ItemStack stack) {
        // You might want to customize this based on your item's default rarity
        return switch (stack.getRarity()) {
            case COMMON -> RarityBorder.COMMON;
            case UNCOMMON -> RarityBorder.UNCOMMON;
            case RARE -> RarityBorder.RARE;
            case EPIC -> RarityBorder.EPIC;
        };
    }

    // Get current reform of an item
    public static Reform getReform(ItemStack item) {
        if (item.contains(ModDataComponents.REFORM)) {
            return item.get(ModDataComponents.REFORM);
        }
        return new Reform(ReformType.NONE);
    }

    // Rename item with reform prefix
    private static void renameWithPrefix(ItemStack stack, ReformType reform) {
        if (reform == ReformType.NONE) return;

        Text originalName;

        // Get original name without any reform prefix
        if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
            String currentName = stack.getName().getString();
            // Check if the name already has a reform prefix and remove it
            for (ReformType type : ReformType.values()) {
                if (type != ReformType.NONE && currentName.startsWith(type.getPrefix())) {
                    currentName = currentName.substring(type.getPrefix().length() + 1); // +1 for the space
                    break;
                }
            }
            originalName = Text.literal(currentName);
        } else {
            originalName = stack.getName();
        }

        // Create new name with prefix and formatting
        MutableText newName = Text.literal(reform.getPrefix() + " ")
                .setStyle(Style.EMPTY.withColor(reform.getFormatting()))
                .append(originalName);

        stack.set(DataComponentTypes.CUSTOM_NAME, newName);
    }
}