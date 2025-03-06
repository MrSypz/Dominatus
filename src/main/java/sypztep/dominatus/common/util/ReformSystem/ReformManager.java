package sypztep.dominatus.common.util.ReformSystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModItems;
import sypztep.hawsamoot.common.data.RarityBorder;
import sypztep.hawsamoot.common.util.RarityHelper;

import java.util.HashMap;
import java.util.Map;

public class ReformManager {
    public record Reform(ReformType type) {
        public static final Codec<Reform> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf("type").xmap(
                                type -> {
                                    try {
                                        return ReformType.valueOf(type);
                                    } catch (IllegalArgumentException e) {
                                        return ReformType.NONE;
                                    }
                                },
                                ReformType::name
                        ).forGetter(Reform::type)
                ).apply(instance, Reform::new)
        );

        public boolean hasEffect() {
            return type != ReformType.NONE;
        }
    }

    public enum ReformType {
        // Weapon Reforms with Black Desert stats
        CRIMSON_FLAME("Crimson Flame", Formatting.RED, ReformCategory.WEAPON,
                new ReformStats(0, 0, 0, 0, 0, 0.2f, 0)),

        DESTRUCTION("Destruction", Formatting.DARK_RED, ReformCategory.WEAPON,
                new ReformStats(0, 0, 1.0f, 0, 0, 0, 0)),

        TEMPTATION("Temptation", Formatting.LIGHT_PURPLE, ReformCategory.WEAPON,
                new ReformStats(0, 0, 0, 0, 0.5f, 0, 0.15f)),

        // Armor Reforms with Black Desert stats
        IRON_WALL("Iron Wall", Formatting.GRAY, ReformCategory.ARMOR,
                new ReformStats(0, 0, 0, 2, 0, 0, 0)),

        AGILITY("Agility", Formatting.AQUA, ReformCategory.ARMOR,
                new ReformStats(5, 2, 0, 0, 0, 0, 0)),

        INTIMIDATION("Intimidation", Formatting.DARK_PURPLE, ReformCategory.ARMOR,
                new ReformStats(0, 0, 0, 2, 0, 0, 0)),

        SACRIFICE("Sacrifice", Formatting.GOLD, ReformCategory.ARMOR,
                new ReformStats(0, 0, 0, 1, 0, 0.05f, 0)),

        ULTIMATE("Ultimate", Formatting.YELLOW, ReformCategory.BOTH,
                new ReformStats(2, 0, 1.0f, 2, 0, 0, 0)),

        NONE("", Formatting.WHITE, ReformCategory.BOTH,
                new ReformStats(0, 0, 0, 0, 0, 0, 0)); // No effects

        private final String prefix;
        private final Formatting formatting;
        private final ReformCategory category;
        private final ReformStats stats;

        ReformType(String prefix, Formatting formatting, ReformCategory category, ReformStats stats) {
            this.prefix = prefix;
            this.formatting = formatting;
            this.category = category;
            this.stats = stats;
        }

        public String getPrefix() {
            return prefix;
        }

        public Formatting getFormatting() {
            return formatting;
        }

        public ReformCategory getCategory() {
            return category;
        }

        public ReformStats getStats() {
            return stats;
        }

        // Check if this reform type has an effect (not NONE)
        public boolean hasEffect() {
            return this != NONE;
        }
    }

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
            } else if (stack.isOf(ModItems.REFORM_STONE_GRADE_LOW)) {
                return LOW;
            }
            return null; // Not a reform stone
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

        // Get item category and stone grade
        ReformCategory itemCategory = getItemCategory(item);
        ReformStoneGrade stoneGrade = ReformStoneGrade.fromItem(reformStone);

        // Roll for reform type based on stone grade and item category
        ReformType reform = rollReform(stoneGrade, itemCategory);

        // Apply the reform
        if (reform == ReformType.NONE) {
            // Clear any existing reform
            clearReform(item);
        } else {
            setReform(item, reform, stoneGrade);
        }

        return true;
    }

    // Roll for reform type based on stone grade and item category
    // Roll for reform type based on stone grade and item category
    private static ReformType rollReform(ReformStoneGrade stoneGrade, ReformCategory category) {
        float roll = (float) Math.random();

        // Define chances based on stone grade - similar to Black Desert rates
        switch (stoneGrade) {
            case LOW:
                // Low grade stones (Green)
                if (roll < 0.35f) return ReformType.NONE; // 35% chance of no effect
                else if (category == ReformCategory.ARMOR) {
                    if (roll < 0.60f) return ReformType.IRON_WALL;      // 25% chance
                    else if (roll < 0.85f) return ReformType.AGILITY;   // 25% chance
                    else return ReformType.INTIMIDATION;                // 15% chance
                } else { // WEAPON
                    if (roll < 0.60f) return ReformType.TEMPTATION;     // 25% chance
                    else if (roll < 0.85f) return ReformType.DESTRUCTION; // 25% chance
                    else return ReformType.CRIMSON_FLAME;               // 15% chance
                }

            case MID:
                // Mid grade stones (Blue)
                if (roll < 0.005f) return ReformType.ULTIMATE;         // 0.5% chance for ultimate
                else if (roll < 0.25f) return ReformType.NONE;         // 24.5% chance for no effect
                else if (category == ReformCategory.ARMOR) {
                    if (roll < 0.50f) return ReformType.IRON_WALL;      // 25% chance
                    else if (roll < 0.75f) return ReformType.AGILITY;   // 25% chance
                    else return ReformType.INTIMIDATION;                // 25% chance
                } else { // WEAPON
                    if (roll < 0.50f) return ReformType.TEMPTATION;     // 25% chance
                    else if (roll < 0.75f) return ReformType.DESTRUCTION; // 25% chance
                    else return ReformType.CRIMSON_FLAME;               // 25% chance
                }

            case HIGH:
                // High grade stones (Yellow)
                if (roll < 0.02f) return ReformType.ULTIMATE;          // 2% chance for ultimate
                else if (roll < 0.17f) return ReformType.NONE;         // 15% chance for no effect
                else if (category == ReformCategory.ARMOR) {
                    if (roll < 0.37f) return ReformType.SACRIFICE;      // 20% chance
                    else if (roll < 0.57f) return ReformType.IRON_WALL; // 20% chance
                    else if (roll < 0.77f) return ReformType.AGILITY;   // 20% chance
                    else return ReformType.INTIMIDATION;                // 23% chance
                } else { // WEAPON
                    if (roll < 0.47f) return ReformType.TEMPTATION;     // 30% chance
                    else if (roll < 0.77f) return ReformType.DESTRUCTION; // 30% chance
                    else return ReformType.CRIMSON_FLAME;               // 23% chance
                }

            default:
                return ReformType.NONE;
        }
    }

    // Set reform on an item
    private static void setReform(ItemStack stack, ReformType reform, ReformStoneGrade stoneGrade) {
        // Apply rarity based on stone grade
        RarityHelper.setRarity(stack, stoneGrade.getRarityBorder());

        // Set reform data component
        stack.set(ModDataComponents.REFORM, new Reform(reform));

        if (reform.hasEffect()) {
            // Rename item with prefix
            renameWithPrefix(stack, reform);

            // Apply stat bonuses
            applyReformAttributes(stack, reform);
        }
    }

    // Apply attribute bonuses from reform
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

    // Get appropriate slot for attribute modifiers
    private static AttributeModifierSlot getAppropriateSlot(ItemStack stack) {
        if (stack.contains(DataComponentTypes.EQUIPPABLE)) {
            return AttributeModifierSlot.forEquipmentSlot(stack.get(DataComponentTypes.EQUIPPABLE).slot());
        }

        if (stack.getItem() instanceof ArmorItem) return AttributeModifierSlot.ARMOR;
        return AttributeModifierSlot.MAINHAND;
    }
    // Clear reform from an item
    public static void clearReform(ItemStack stack) {
        ReformType currentReform = getReform(stack).type();

        // Skip if there's no reform to clear
        if (currentReform == ReformType.NONE) {
            return;
        }

        // Remove reform component if present
        if (stack.contains(ModDataComponents.REFORM)) {
            stack.remove(ModDataComponents.REFORM);
        }

        // Reset rarity to common
        RarityHelper.setRarity(stack, RarityBorder.COMMON);

        // Remove any custom name related to reform
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

        // Remove reform attribute modifiers
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