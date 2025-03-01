package sypztep.dominatus.common.util.RefineSystem;

import net.minecraft.util.math.MathHelper;

public class RefinementCalculator {
    // Base success rates for each enhancement level (0-20)
    private static final double[] SUCCESS_RATES = {
            1.0, 0.95, 0.90, 0.80, 0.70, 0.60, 0.50, 0.40, 0.30, 0.20, 0.10,
            0.09, 0.08, 0.07, 0.06, 0.05, // Levels 1-15
            0.25, // PRI (16)
            0.175, // DUO (17)
            0.125, // TRI (18)
            0.075, // TET (19)
            0.025  // PEN (20)
    };

    // Constants for enhancement system
    private static final double SUCCESS_RATE_CAP = 0.90; // Maximum 90% success rate
    private static final double[] FAILSTACK_MULTIPLIERS = {
            0.02,  // 1-15 (+2% per failstack)
            0.03,  // PRI (+3% per failstack)
            0.03,  // DUO
            0.02,  // TRI
            0.015, // TET
            0.01   // PEN (+1% per failstack)
    };

    private static final int[] FAILSTACK_CAPS = {
            25,  // 1-15 (cap at 25 stacks)
            50,  // PRI
            40,  // DUO
            44,  // TRI
            90,  // TET
            124  // PEN
    };

    public static double calculateSuccessRate(int currentLevel, int failStack) {
        if (currentLevel >= SUCCESS_RATES.length) return 0.0;

        double baseRate = SUCCESS_RATES[currentLevel];

        // Get appropriate multiplier based on enhancement level
        double multiplier = getFailstackMultiplier(currentLevel);

        // Get appropriate failstack cap
        int effectiveFailstack = Math.min(failStack, getFailstackCap(currentLevel));

        // Calculate bonus from failstacks
        double failStackBonus = effectiveFailstack * multiplier;

        // Return capped success rate
        return Math.min(baseRate + failStackBonus, SUCCESS_RATE_CAP);
    }

    private static double getFailstackMultiplier(int level) {
        if (level < 15) return FAILSTACK_MULTIPLIERS[0];
        return FAILSTACK_MULTIPLIERS[level - 14]; // 15->1, 16->2, etc.
    }

    private static int getFailstackCap(int level) {
        if (level < 15) return FAILSTACK_CAPS[0];
        return FAILSTACK_CAPS[level - 14];
    }

    public static int calculateStatValue(int currentLvl, int maxLvl, int startValue, int endValue) {
        if (currentLvl < 0 || currentLvl > maxLvl) {
            currentLvl = MathHelper.clamp(currentLvl, 0, maxLvl);
        }
        return calculateValue(currentLvl, maxLvl, startValue, endValue).intValue();
    }

    public static float calculateStatValue(int currentLvl, int maxLvl, float startValue, float endValue) {
        if (currentLvl < 0 || currentLvl > maxLvl) {
            currentLvl = MathHelper.clamp(currentLvl, 0, maxLvl);
        }
        return calculateValue(currentLvl, maxLvl, startValue, endValue).floatValue();
    }

    private static Number calculateValue(int currentLvl, int maxLvl, Number startValue, Number endValue) {
        float normalizedInput = (float) currentLvl / maxLvl;
        float curvedInput = (float) Math.pow(normalizedInput, 1.725);

        if (startValue instanceof Integer) {
            int range = ((Integer)endValue) - ((Integer)startValue);
            return ((Integer)startValue) + (int)(curvedInput * range);
        } else {
            float range = ((Float)endValue) - ((Float)startValue);
            return ((Float)startValue) + curvedInput * range;
        }
    }
}