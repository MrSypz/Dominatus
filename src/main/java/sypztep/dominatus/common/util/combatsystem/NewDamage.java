package sypztep.dominatus.common.util.combatsystem;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;

public class NewDamage {
    private static final float VANILLA_MAX_DR = 12.0F;  // Full Netherite toughness (fixed cap)

    // DP brackets: every 7 DP up to 157, inspired by BDO tiers
    private static final float[] DP_BRACKETS = {
            0, 7, 14, 21, 28, 35, 42, 49, 56, 63, 70, 77, 84, 91, 98, 105, 112, 119, 126, 133, 140, 157
    };
    private static final float[] DR_PER_BRACKET = {
            0.00F, 0.01F, 0.02F, 0.03F, 0.04F, 0.05F, 0.06F, 0.07F, 0.08F, 0.09F,
            0.10F, 0.11F, 0.12F, 0.13F, 0.14F, 0.15F, 0.16F, 0.17F, 0.18F, 0.19F,
            0.20F, 0.21F
    }; // DR% per tier, up to 21% at 157 DP

    /**
     * Calculate damage after applying armor-based reduction.
     * @return Reduced damage amount
     */
    public static float applyArmorToDamage(LivingEntity entity, float damage) {
        float drPercent = getDamageReductionPercent(entity);
        return damage * (1.0F - drPercent);
    }

    /**
     * Get the current damage reduction percentage based on DP and DR.
     * @return DR percentage (0.0 to 1.0)
     */
    public static float getDamageReductionPercent(LivingEntity entity) {
        float totalDP = (float) entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR); // DP from evasion
        float totalDR = Math.min((float) entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS), VANILLA_MAX_DR); // DR capped at 12
        return calculateDamageReduction(totalDP, totalDR);
    }

    /**
     * Calculate DR% using DP brackets and a small DR bonus, BDO-style.
     * @return DR percentage (0.0 to 1.0)
     */
    private static float calculateDamageReduction(float dp, float dr) {
        // DP bracket calculation
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
        // If DP exceeds max bracket (157), use highest tier
        if (dp > DP_BRACKETS[DP_BRACKETS.length - 1]) {
            dpBaseDR = DR_PER_BRACKET[DP_BRACKETS.length - 1];
        }

        // DR contribution: Small bonus, tuned for ~2-3% at 12 DR
        float drK = 60.0F; // 12 / (12 + 60) ≈ 0.167
        float drContribution = dr / (dr + drK); // 12 DR → ~0.167
        float drScaled = drContribution * 0.15F; // ~2.5% at 12 DR

        // Total DR capped at 30%, aiming for ~20% at (157 DP, 12 DR)
        return Math.min(dpBaseDR + drScaled, 0.30F);
    }
}