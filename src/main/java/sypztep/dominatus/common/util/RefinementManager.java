package sypztep.dominatus.common.util;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModItems;

public class RefinementManager {
    public static final int MAX_NORMAL_LEVEL = 15;
    public static final int MAX_ENHANCED_LEVEL = 20;
    public static final int BASE_DURABILITY_LOSS = 10;

    public static final int ENHANCED_FAILSTACK_INCREASE = 2;
    public static final int NORMAL_FAILSTACK_INCREASE = 1;
    public static final int ENHANCED_DURABILITY_MULTIPLIER = 2;

    public record RefinementResult(
            boolean success,
            int newLevel,
            int newDurability,
            int newFailStack,
            boolean consumeMaterial
    ) {}
    public static void initializeRefinement(ItemStack stack) {
        new RefinementBuilder().applyTo(stack);
    }

    private static Refinement getRefinement(ItemStack stack) {
        return stack.get(ModDataComponents.REFINEMENT);
    }

    public static void applyRefinement(ItemStack stack, DominatusItemEntry entry, int newLevel) {
        Refinement oldRef = getRefinement(stack);

        new RefinementBuilder()
                .fromExisting(oldRef)
                .withRefine(newLevel)
                .withAccuracy(RefinementCalculator.calculateStatValue(
                        newLevel, entry.maxLvl(), entry.startAccuracy(), entry.endAccuracy()))
                .withEvasion(RefinementCalculator.calculateStatValue(
                        newLevel, entry.maxLvl(), entry.startEvasion(), entry.endEvasion()))
                .withDamage(RefinementCalculator.calculateStatValue(
                        newLevel, entry.maxLvl(), entry.starDamage(), entry.endDamage()))
                .withProtection(RefinementCalculator.calculateStatValue(
                        newLevel, entry.maxLvl(), entry.startProtection(), entry.endProtection()))
                .applyTo(stack);
    }

    public static RefinementResult processRefinement(ItemStack item, ItemStack material, int failStack) {
        Refinement currentRef = getRefinement(item);
        if (currentRef == null) {
            initializeRefinement(item);
            currentRef = getRefinement(item);
        }

        // Check if this is a repair attempt
        if (MaterialValidator.isRepairMaterial(material)) {
            return handleRepair(item, failStack);
        }

        // Validate material for refinement
        if (!MaterialValidator.isValidMaterial(material, item, currentRef.refine())) {
            return new RefinementResult(false, currentRef.refine(), currentRef.durability(), failStack, false);
        }

        // Normal refinement process
        int currentLevel = currentRef.refine();
        double successRate = RefinementCalculator.calculateSuccessRate(currentLevel, failStack);
        boolean success = Math.random() < successRate;

        if (success) {
            return handleSuccess(item, currentLevel);
        } else {
            return handleFailure(item, currentLevel, failStack);
        }
    }

    private static RefinementResult handleFailure(ItemStack item, int currentLevel, int failStack) {
        Refinement current = getRefinement(item);

        int newLevel;
        if (currentLevel > MAX_NORMAL_LEVEL && currentLevel != 16)  // Only degrade if above PRI
            newLevel = currentLevel - 1;
         else
            newLevel = currentLevel; // Stay at current level for +15 and PRI

        // Calculate durability loss based on enhancement level
        int durabilityLoss = BASE_DURABILITY_LOSS;
        if (currentLevel >= MAX_NORMAL_LEVEL) {
            durabilityLoss *= ENHANCED_DURABILITY_MULTIPLIER; // Double durability loss after +15
        }
        int newDurability = Math.max(current.durability() - durabilityLoss, 0);

        int failstackIncrease = currentLevel >= MAX_NORMAL_LEVEL ?
                ENHANCED_FAILSTACK_INCREASE : // +2 failstacks for +15 to +20
                NORMAL_FAILSTACK_INCREASE;    // +1 failstack for +1 to +14
        int newFailStack = failStack + failstackIncrease;

        new RefinementBuilder()
                .fromExisting(current)
                .withRefine(newLevel)
                .withDurability(newDurability)
                .applyTo(item);

        return new RefinementResult(false, newLevel, newDurability, newFailStack, true);
    }

    private static RefinementResult handleSuccess(ItemStack item, int currentLevel) {
        int newLevel = currentLevel + 1;
        applyRefinement(item, getDominatusEntry(item), newLevel);
        return new RefinementResult(true, newLevel, getRefinement(item).durability(), 0, true);
    }

    private static RefinementResult handleRepair(ItemStack item,int failStack) {
        Refinement current = getRefinement(item);
        DominatusItemEntry entry = getDominatusEntry(item);

        if (current.durability() >= entry.maxDurability()) {
            return new RefinementResult(
                    false,
                    current.refine(),
                    current.durability(),
                    failStack,
                    false
            );
        }

        int repairAmount = entry.repairpoint();
        int newDurability = Math.min(current.durability() + repairAmount, entry.maxDurability());

        new RefinementBuilder()
                .fromExisting(current)
                .withDurability(newDurability)
                .applyTo(item);

        return new RefinementResult(
                true,
                current.refine(),
                newDurability,
                failStack,
                true
        );
    }

    private static DominatusItemEntry getDominatusEntry(ItemStack item) {
        return DominatusItemEntry.getDominatusItemData(DominatusItemEntry.getItemId(item))
                .orElseThrow(() -> new IllegalStateException("Invalid item for refinement"));
    }
    public static class MaterialValidator {
        public static boolean isValidMaterial(ItemStack material, ItemStack target, int currentLevel) {
            boolean isArmor = target.getItem() instanceof ArmorItem;

            if (currentLevel < MAX_NORMAL_LEVEL) {
                return isArmor ?
                        material.isOf(ModItems.REFINE_ARMOR_STONE) :
                        material.isOf(ModItems.REFINE_WEAPON_STONE);
            } else if (currentLevel < MAX_ENHANCED_LEVEL) {
                return isArmor ?
                        material.isOf(ModItems.REFINE_ARMORENFORGE_STONE) :
                        material.isOf(ModItems.REFINE_WEAPONENFORGE_STONE);
            }
            return false;
        }
        public static boolean isRepairMaterial(ItemStack material) {
            return material.isOf(ModItems.MOONLIGHT_CRESCENT);
        }
    }
}