package sypztep.dominatus.common.util;

import net.minecraft.item.ItemStack;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.init.ModDataComponents;

public class RefinementUtil {
    public static void setRefinement(ItemStack stack, Refinement refinement) {
        stack.set(ModDataComponents.REFINEMENT, refinement);
    }
    public static void setRefinement(ItemStack stack, int refine, int accuracy, int evasion, int durability, float damage, int protection) {
        Refinement refinement = new Refinement(refine, accuracy, evasion, durability, damage, protection);
        stack.set(ModDataComponents.REFINEMENT, refinement);
    }

    public static Refinement getRefinement(ItemStack stack) {
        return stack.get(ModDataComponents.REFINEMENT);
    }
    //Helper methods
    public static int getRefineLvl(ItemStack stack) {
        return getRefinement(stack).refine();
    }
    public static int getAccuracy(ItemStack stack) {
        return getRefinement(stack).accuracy();
    }
    public static int getEvasion(ItemStack stack) {
        return getRefinement(stack).evasion();
    }
    public static int getDurability(ItemStack stack) {
        return getRefinement(stack).durability();
    }
    public static float getDamage(ItemStack stack) {
        return getRefinement(stack).damage();
    }
    public static int getProtection(ItemStack stack) {
        return getRefinement(stack).protection();
    }

    public static void updateRefinement(ItemStack stack, int refineLvl, int maxLvl, int startAcc, int endAcc, int startEva, int endEva, float startDmg, float endDmg, int startProt, int endProt) {
        Refinement oldRef = getRefinement(stack);
        int newAccuracy = refineValue(refineLvl, maxLvl, startAcc, endAcc);
        int newEvasion = refineValue(refineLvl, maxLvl, startEva, endEva);
        float newDamage = refineValue(refineLvl, maxLvl, startDmg, endDmg);
        int newProtection = refineValue(refineLvl, maxLvl, startProt, endProt);

        setRefinement(stack, refineLvl, newAccuracy, newEvasion, oldRef.durability(), newDamage, newProtection);
    }
    public static void updateRefinement(ItemStack stack, DominatusItemEntry entry ,int refineLvl) {
        Refinement oldRef = getRefinement(stack);
        int newAccuracy = refineValue(refineLvl, entry.maxLvl(), entry.startAccuracy(), entry.endAccuracy());
        int newEvasion = refineValue(refineLvl, entry.maxLvl(), entry.startEvasion(), entry.endAccuracy());
        float newDamage = refineValue(refineLvl, entry.maxLvl(), entry.starDamage(), entry.endDamage());
        int newProtection = refineValue(refineLvl, entry.maxLvl(), entry.startProtection(), entry.endProtection());

        setRefinement(stack, refineLvl, newAccuracy, newEvasion, oldRef.durability(), newDamage, newProtection);
    }

    public static int refineValue(int currentLvl, int maxLvl, int startValue, int endValue) {
        if (currentLvl < 0 || currentLvl > maxLvl)
            throw new IllegalArgumentException("Input value out of range");
        int outputRange = endValue - startValue;

        float normalizedInput = (float) (currentLvl) / maxLvl;
        float curvedInput = (float) Math.pow(normalizedInput, 1.725);

        return (int) (startValue + curvedInput * outputRange);
    }
    public static float refineValue(int currentLvl, int maxLvl, float startValue, float endValue) {
        if (currentLvl < 0 || currentLvl > maxLvl)
            throw new IllegalArgumentException("Input value out of range");
        float outputRange = endValue - startValue;

        float normalizedInput = (float) (currentLvl) / maxLvl;
        float curvedInput = (float) Math.pow(normalizedInput, 1.725);

        return startValue + curvedInput * outputRange;
    }
}
