package sypztep.dominatus.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.init.ModDataComponents;

import java.util.Random;

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
    public static void updateRefinement(ItemStack stack, DominatusItemEntry entry, int refineLvl) {
        Refinement oldRef = getRefinement(stack);  // Get current refinement
        int newAccuracy = refineValue(refineLvl, entry.maxLvl(), entry.startAccuracy(), entry.endAccuracy());
        int newEvasion = refineValue(refineLvl, entry.maxLvl(), entry.startEvasion(), entry.endAccuracy());
        float newDamage = refineValue(refineLvl, entry.maxLvl(), entry.starDamage(), entry.endDamage());
        int newProtection = refineValue(refineLvl, entry.maxLvl(), entry.startProtection(), entry.endProtection());

        // Debug output to confirm refineLvl change
        System.out.println("Updating refinement - New refineLvl: " + refineLvl);

        setRefinement(stack, refineLvl, newAccuracy, newEvasion, oldRef.durability(), newDamage, newProtection);
    }


    public static boolean handleRefine(ItemStack slotOutput, int failStack) {
        double successRate = FailStackUtil.calculateSuccessRate(slotOutput, failStack);
        Random random = new Random();
        double randomValue = random.nextDouble();
        System.out.println("Success Rate: " + successRate + ", Random Value: " + randomValue);  // Debugging line
        return randomValue < successRate;
    }

    public static void processRefinement(ItemStack slotOutput, int failStack, int currentRefineLvl,DominatusItemEntry entry,  PlayerEntity player) {
        if (handleRefine(slotOutput, failStack)) {
            handleSuccess(slotOutput, currentRefineLvl, entry, player);
        } else {
            handleFailure(slotOutput, failStack, currentRefineLvl, entry, player);
        }
    }
    public static void handleSuccess(ItemStack slotOutput, int currentRefineLvl, DominatusItemEntry entry, PlayerEntity player) {
        int newRefineLvl = currentRefineLvl + 1;
        System.out.println(newRefineLvl);
        updateRefinement(slotOutput,entry,newRefineLvl);
        FailStackUtil.successRefine(player);
//        AddRefineSoundPayloadS2C.send(serverPlayer, player.getId(), RefineUtil.RefineSound.SUCCESS.select());
    }

    public static void handleFailure(ItemStack slotOutput, int failStack, int currentRefineLvl,  DominatusItemEntry entry, PlayerEntity player) {
        if (currentRefineLvl > 16) { // 17 - 20
            int newRefineLvl = Math.max(currentRefineLvl - 1, 0);
            updateRefinement(slotOutput, entry,newRefineLvl);
//            RefineUtil.setRefineLvl(slotOutput, newRefineLvl);
//            updateStats(slotOutput, newRefineLvl, maxLvl, startAccuracy, endAccuracy, startEvasion, endEvasion, startDamage, endDamage, startProtect, endProtect);
        }
//        int newDurability = Math.max(RefineUtil.getDurability(slotOutput) - 10, 0);
//        RefineUtil.setDurability(slotOutput, newDurability);
        FailStackUtil.failRefine(player, failStack);
//        AddRefineSoundPayloadS2C.send(serverPlayer, player.getId(), RefineUtil.RefineSound.FAIL.select());
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
