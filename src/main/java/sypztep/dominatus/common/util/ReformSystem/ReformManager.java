package sypztep.dominatus.common.util.ReformSystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModItems;
import sypztep.hawsamoot.common.data.RarityBorder;
import sypztep.hawsamoot.common.util.RarityHelper;

public class ReformManager {
    // Define a Reform record to store in item data components
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
        CRIMSON_FLAME("Crimson Flame", Formatting.RED, ReformCategory.WEAPON),
        DESTRUCTION("Destruction", Formatting.DARK_RED, ReformCategory.WEAPON),
        TEMPTATION("Temptation", Formatting.LIGHT_PURPLE, ReformCategory.WEAPON),

        IRON_WALL("Iron Wall", Formatting.GRAY, ReformCategory.ARMOR),
        AGILITY("Agility", Formatting.AQUA, ReformCategory.ARMOR),
        INTIMIDATION("Intimidation", Formatting.DARK_PURPLE, ReformCategory.ARMOR),
        SACRIFICE("Sacrifice", Formatting.GOLD, ReformCategory.ARMOR),

        ULTIMATE("Ultimate", Formatting.YELLOW, ReformCategory.BOTH),

        NONE("", Formatting.WHITE, ReformCategory.BOTH); // Green Grade is actually no reform applied

        private final String prefix;
        private final Formatting formatting;
        private final ReformCategory category;

        ReformType(String prefix, Formatting formatting, ReformCategory category) {
            this.prefix = prefix;
            this.formatting = formatting;
            this.category = category;
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

        // Check if this reform type has an effect (not NONE)
        public boolean hasEffect() {
            return this != NONE;
        }
    }

    // Reform categories
    public enum ReformCategory {
        WEAPON, ARMOR, BOTH
    }

    // Reform stone grades
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

        // Get stone grade from ItemStack
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

    // Item category detection
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

    // Check if an item can be reformed with a specific stone
    public static boolean canReform(ItemStack item, ItemStack reformStone) {
        // Get the item category
        ReformCategory itemCategory = getItemCategory(item);
        if (itemCategory == null) {
            return false; // Item is not reformable
        }

        // Get the reform stone grade
        ReformStoneGrade stoneGrade = ReformStoneGrade.fromItem(reformStone);
        if (stoneGrade == null) {
            return false; // Not a valid reform stone
        }

        // All other checks (already has ultimate, etc.)
        ReformType currentReform = getReform(item).type();
        if (currentReform == ReformType.ULTIMATE) {
            return false; // Already has ultimate reform
        }

        return true;
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
    private static ReformType rollReform(ReformStoneGrade stoneGrade, ReformCategory category) {
        float roll = (float) Math.random();

        // Define chances based on stone grade
        switch (stoneGrade) {
            case LOW:
                if (roll < 0.2f) return ReformType.NONE; // Green Grade (no effect)
                else if (category == ReformCategory.ARMOR) {
                    if (roll < 0.5f) return ReformType.IRON_WALL;
                    else if (roll < 0.8f) return ReformType.AGILITY;
                    else return ReformType.INTIMIDATION;
                } else { // WEAPON
                    if (roll < 0.5f) return ReformType.TEMPTATION;
                    else if (roll < 0.8f) return ReformType.DESTRUCTION;
                    else return ReformType.CRIMSON_FLAME;
                }

            case MID:
                if (roll < 0.01f) return ReformType.ULTIMATE;
                else if (roll < 0.11f) return ReformType.NONE; // Green Grade (no effect)
                else if (category == ReformCategory.ARMOR) {
                    if (roll < 0.44f) return ReformType.IRON_WALL;
                    else if (roll < 0.77f) return ReformType.AGILITY;
                    else return ReformType.INTIMIDATION;
                } else { // WEAPON
                    if (roll < 0.44f) return ReformType.TEMPTATION;
                    else if (roll < 0.77f) return ReformType.DESTRUCTION;
                    else return ReformType.CRIMSON_FLAME;
                }

            case HIGH:
                if (roll < 0.05f) return ReformType.ULTIMATE;
                else if (roll < 0.15f) return ReformType.NONE; // Green Grade (no effect)
                else if (category == ReformCategory.ARMOR) {
                    if (roll < 0.35f) return ReformType.SACRIFICE;
                    else if (roll < 0.55f) return ReformType.IRON_WALL;
                    else if (roll < 0.75f) return ReformType.AGILITY;
                    else return ReformType.INTIMIDATION;
                } else { // WEAPON
                    if (roll < 0.45f) return ReformType.TEMPTATION;
                    else if (roll < 0.65f) return ReformType.DESTRUCTION;
                    else return ReformType.CRIMSON_FLAME;
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
            renameWithPrefix(stack, reform);
        }
    }

    // Clear reform from an item
    public static void clearReform(ItemStack stack) {
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

    // Check if an item has an active reform (not NONE)
    public static boolean hasActiveReform(ItemStack item) {
        return getReform(item).hasEffect();
    }
}