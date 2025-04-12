package sypztep.dominatus.common.util.refinesystem;

import net.minecraft.util.math.MathHelper;

public final class RefinementCalculator {
    // Base success rates for each enhancement level (0-20), made harder for 15-20
    private static final double[] SUCCESS_RATES = {
            1.0,    // 0
            0.95,   // 1 - First breakpoint
            0.9,    // 2
            0.85,   // 3
            0.8,    // 4
            0.75,   // 5
            0.7,    // 6
            0.6,    // 7 - Second breakpoint
            0.5,    // 8
            0.45,   // 9
            0.40,   // 10
            0.35,   // 11
            0.30,   // 12
            0.25,   // 13
            0.20,   // 14
            0.10,   // 15 - Third breakpoint (was 0.15)
            0.08,   // PRI (16, was 0.125)
            0.06,   // DUO (17, was 0.10)
            0.04,   // TRI (18, was 0.075)
            0.03,   // TET (19, was 0.05)
            0.015   // PEN (20, was 0.025)
    };

    // Constants for enhancement system
    private static final double SUCCESS_RATE_CAP = 0.90; // Maximum 90% success rate
    private static final double[] FAILSTACK_MULTIPLIERS = {
            0.01,   // 0-7 (+1% per failstack)
            0.015,  // 8-14 (+1.5% per failstack)
            0.02,   // 15 PRI (+2% per failstack)
            0.015,  // 16-17 DUO (unchanged)
            0.01,   // 18 TRI (unchanged)
            0.006,  // 19 TET (was 0.0075)
            0.004   // 20 PEN (was 0.005)
    };

    private static final int[] FAILSTACK_CAPS = {
            20,   // 0-7
            35,   // 8-14
            45,   // 15 PRI (was 50)
            50,   // 16-17 DUO (was 55)
            55,   // 18 TRI (was 60)
            90,   // 19 TET (was 100)
            140   // 20 PEN (was 150)
    };

    // Function to map enhancement level to the appropriate array index
    private static int getMultiplierIndex(int level) {
        if (level <= 7) return 0;
        if (level <= 14) return 1;
        if (level == 15) return 2;
        if (level == 16) return 3;
        if (level == 17) return 3;
        if (level == 18) return 4;
        if (level == 19) return 5;
        return 6; // level 20
    }

    public static double calculateSuccessRate(int currentLevel, int failStack) {
        if (currentLevel >= SUCCESS_RATES.length) return 0.0;

        double baseRate = SUCCESS_RATES[currentLevel];

        int multiplierIndex = getMultiplierIndex(currentLevel);
        double multiplier = FAILSTACK_MULTIPLIERS[multiplierIndex];
        int cap = FAILSTACK_CAPS[multiplierIndex];

        int effectiveFailstack = Math.min(failStack, cap);
        double failStackBonus = effectiveFailstack * multiplier;

        // Return capped success rate
        return Math.min(baseRate + failStackBonus, SUCCESS_RATE_CAP);
    }

    public static int calculateStatValue(int currentLvl, int maxLvl, int startValue, int endValue) {
        if (currentLvl < 0 || currentLvl > maxLvl) {
            currentLvl = MathHelper.clamp(currentLvl, 0, maxLvl);
        }
        // Add 15 to endValue at max refinement
        int adjustedEndValue = endValue + (int) (15 * ((double) currentLvl / maxLvl));
        return calculateValue(currentLvl, maxLvl, startValue, adjustedEndValue).intValue();
    }

    public static float calculateStatValue(int currentLvl, int maxLvl, float startValue, float endValue) {
        if (currentLvl < 0 || currentLvl > maxLvl) {
            currentLvl = MathHelper.clamp(currentLvl, 0, maxLvl);
        }
        // Add 15 to endValue at max refinement
        float adjustedEndValue = endValue + (15 * ((float) currentLvl / maxLvl));
        return calculateValue(currentLvl, maxLvl, startValue, adjustedEndValue).floatValue();
    }

    private static Number calculateValue(int currentLvl, int maxLvl, Number startValue, Number endValue) {
        currentLvl = MathHelper.clamp(currentLvl, 0, maxLvl);

        double relativePosition = (double) currentLvl / maxLvl;

        double ratio;
        double maxLevel20Ratio = 20.0 / maxLvl;

        if (currentLvl == 0) {
            ratio = 0.0;
        } else if (relativePosition <= 0.05 * maxLevel20Ratio) {
            ratio = 0.10 * (relativePosition / (0.05 * maxLevel20Ratio));
        } else if (relativePosition <= 0.35 * maxLevel20Ratio) {
            double normalizedPosition = (relativePosition - 0.05 * maxLevel20Ratio) / (0.3 * maxLevel20Ratio);
            ratio = 0.10 + 0.30 * normalizedPosition;
        } else if (relativePosition <= 0.75 * maxLevel20Ratio) {
            double normalizedPosition = (relativePosition - 0.35 * maxLevel20Ratio) / (0.4 * maxLevel20Ratio);
            ratio = 0.40 + 0.35 * normalizedPosition;
        } else {
            double normalizedPosition = (relativePosition - 0.75 * maxLevel20Ratio) / (0.25 * maxLevel20Ratio);
            ratio = 0.75 + 0.25 * Math.min(normalizedPosition, 1.0);
        }

        // Adjusted exponent to make curve slightly steeper for higher levels
        ratio = Math.pow(ratio, 1.2);

        if (startValue instanceof Integer) {
            int range = ((Integer) endValue) - ((Integer) startValue);
            return ((Integer) startValue) + (int) (ratio * range);
        } else {
            float range = ((Float) endValue) - ((Float) startValue);
            return ((Float) startValue) + (float) (ratio * range);
        }
    }
}