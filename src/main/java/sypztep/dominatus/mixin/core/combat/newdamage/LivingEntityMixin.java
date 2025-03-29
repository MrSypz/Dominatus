package sypztep.dominatus.mixin.core.combat.newdamage;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    /**
     * Override the armor damage calculation
     */
    @Inject(
            method = "applyArmorToDamage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void applyImprovedArmorToDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity)(Object)this;

        // Skip if damage bypasses armor
        if (source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
            cir.setReturnValue(amount);
            return;
        }

        // Get armor and toughness values
        float armor = self.getArmor();
        float armorToughness = (float)self.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR_TOUGHNESS);

        // Calculate with improved formula
        float damageAfterArmor = calculateImprovedArmorDamage(self, amount, source, armor, armorToughness);

        // Return the modified damage
        cir.setReturnValue(damageAfterArmor);
    }

    /**
     * Improved armor damage calculation with better scaling for high armor values
     */
    private float calculateImprovedArmorDamage(LivingEntity entity, float damage,
                                               DamageSource source, float armor, float armorToughness) {
        // Improved base formula variables with scaling for high armor
        float toughnessContribution = 2.0F + armorToughness / 4.0F + (armor > 20 ? (armor - 20) / 40.0F : 0);

        // No artificial cap on armor
        // Calculate damage reduction from armor
        float armorProtection = MathHelper.clamp(
                armor - damage / toughnessContribution,
                armor * 0.15F, // Minimum 15% of armor is always effective
                armor
        );

        // Convert to damage reduction factor with strong high-end scaling
        float damageReductionRatio;
        if (armorProtection <= 20.0F) {
            // Vanilla-like for low armor
            damageReductionRatio = armorProtection / 25.0F; // 80% at armor 20
        } else {
            // Strong linear progression for high armor
            damageReductionRatio = 0.8F + (armorProtection - 20.0F) / 200.0F; // Each point over 20 adds 0.5% reduction
        }

        // Cap at 95% for balance
        damageReductionRatio = Math.min(damageReductionRatio, 0.95F);

        // Calculate armor penetration based on weapon and enchantments
        float armorPenetration = calculateArmorPenetration(entity, source, damageReductionRatio);

        // Final protection factor (accounting for penetration)
        float protectionFactor = 1.0F - armorPenetration;

        // Return the amount of damage that gets through
        return damage * protectionFactor;
    }

    /**
     * Calculate armor penetration based on weapon enchantments
     */
    private float calculateArmorPenetration(LivingEntity target, DamageSource source, float baseReduction) {
        // Get attacker if available
        if (source.getAttacker() instanceof LivingEntity attacker) {
            // Get weapon stack
            ItemStack weaponStack = attacker.getMainHandStack();

            // Check if weapon exists and we're in a server world
            if (!weaponStack.isEmpty() && target.getWorld() instanceof ServerWorld serverWorld) {
                // Get the armor penetration factor from enchantments
                // In vanilla, this would be influenced by enchantments like Sharpness
                // The baseReduction parameter represents how much damage would be reduced by armor
                float armorPenetrationFactor = EnchantmentHelper.getArmorEffectiveness(
                        serverWorld,
                        weaponStack,
                        target,
                        source,
                        baseReduction
                );

                // Clamp the value between 0 and the original reduction amount
                // This prevents negative values or exceeding the base reduction
                return MathHelper.clamp(armorPenetrationFactor, 0.0F, baseReduction);
            }
        }

        // If no attacker or no weapon, use the base reduction amount
        return baseReduction;
    }

    /**
     * Override the protection enchantment calculation
     */
    @Redirect(
            method = "modifyAppliedDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/DamageUtil;getInflictedDamage(FF)F"
            )
    )
    private float getImprovedInflictedDamage(float damageDealt, float protection) {
        // No artificial cap on protection values
        // Convert to damage reduction percentage with improved scaling
        float damageReduction;
        if (protection <= 20.0F) {
            // Vanilla-like for low protection
            damageReduction = protection / 25.0F;
        } else {
            // Improved scaling for high protection
            damageReduction = 0.8F + (protection - 20.0F) / 300.0F;
        }

        // Cap at 95% for balance
        damageReduction = Math.min(damageReduction, 0.95F);

        return damageDealt * (1.0F - damageReduction);
    }
}