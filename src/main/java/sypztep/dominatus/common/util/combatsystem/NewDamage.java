package sypztep.dominatus.common.util.combatsystem;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import sypztep.dominatus.common.init.ModEntityAttributes;

public final class NewDamage {
    private static final float MAX_DR_PERCENT = 0.40F;

    private static final float[] DP_BRACKETS = {
            0, 7, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77, 84, 91, 98, 105, 112, 119, 126, 133,
            140, 157, 175, 200, 225, 250, 275, 300
    };
    private static final float[] DR_PER_BRACKET = {
            0.00F, 0.01F, 0.02F, 0.03F, 0.04F, 0.05F, 0.06F, 0.07F, 0.08F, 0.09F,
            0.10F, 0.11F, 0.12F, 0.13F, 0.14F, 0.15F, 0.16F, 0.17F, 0.18F, 0.19F,
            0.20F, 0.21F, 0.22F, 0.23F, 0.24F, 0.25F, 0.26F, 0.27F
    }; // DR% per tier, up to 27% at 300 DP

    /**
     * Calculate damage after applying armor-based reduction.
     * @return Reduced damage amount
     */
    public static float applyArmorToDamage(LivingEntity entity, float damage) {
        float drPercent = getDamageReductionPercent(entity);
        return Math.max(damage * (1.0F - drPercent), 0.0F);
    }

    /**
     * Get the current damage reduction percentage based on DP and DR.
     * @return DR percentage (0.0 to 1.0)
     */
    public static float getDamageReductionPercent(LivingEntity entity) {
        float totalDP = (float) entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR); // DP from armor
        float totalDR = (float) entity.getAttributeValue(ModEntityAttributes.DAMAGE_REDUCTION); // DR from vanilla + refinement
        return calculateDamageReduction(totalDP, totalDR);
    }

    /**
     * Calculate DR% using DP brackets and DR contribution.
     * @return DR percentage (0.0 to 1.0)
     */
    private static float calculateDamageReduction(float dp, float dr) {
        // DP-based DR
        float dpBaseDR = 0.0F;
        for (int i = 0; i < DP_BRACKETS.length - 1; i++) {
            if (dp <= DP_BRACKETS[i]) {
                dpBaseDR = DR_PER_BRACKET[i];
                break;
            } else if (dp <= DP_BRACKETS[i + 1]) {
                // Linear interpolation between brackets
                float dpRange = DP_BRACKETS[i + 1] - DP_BRACKETS[i];
                float drRange = DR_PER_BRACKET[i + 1] - DR_PER_BRACKET[i];
                float dpProgress = (dp - DP_BRACKETS[i]) / dpRange;
                dpBaseDR = DR_PER_BRACKET[i] + (drRange * dpProgress);
                break;
            }
        }
        // If DP exceeds max bracket (300), apply diminishing returns
        if (dp > DP_BRACKETS[DP_BRACKETS.length - 1]) {
            float excessDP = dp - DP_BRACKETS[DP_BRACKETS.length - 1];
            dpBaseDR = DR_PER_BRACKET[DP_BRACKETS.length - 1] + (float) (0.05F * Math.log1p(excessDP / 50.0F));
        }

        // DR contribution: scaled for vanilla (8-16) and refinement values (up to 30)
        float drContribution = Math.min(dr / 50.0F, 0.12F); // Cap DR contribution at 12% (60 DR)

        // Total DR capped at 40%
        return Math.min(dpBaseDR + drContribution, MAX_DR_PERCENT);
    }
}