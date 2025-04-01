package sypztep.dominatus.mixin.core.combat.unlimitarmorcap;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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

        if (source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
            cir.setReturnValue(amount);
            return;
        }

        float armor = self.getArmor();
        float armorToughness = (float)self.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR_TOUGHNESS);

        float damageAfterArmor = calculateImprovedArmorDamage(self, amount, source, armor, armorToughness);

        cir.setReturnValue(damageAfterArmor);
    }

    /**
     * Improved armor damage calculation with better scaling for high armor values
     */
    @Unique
    private float calculateImprovedArmorDamage(LivingEntity entity, float damage,
                                               DamageSource source, float armor, float armorToughness) {
        float toughnessContribution = 2.0F + armorToughness / 4.0F + (armor > 20 ? (armor - 20) / 40.0F : 0);

        float armorProtection = MathHelper.clamp(
                armor - damage / toughnessContribution,
                armor * 0.15F, // Minimum 15% of armor is always effective
                armor
        );
        float damageReductionRatio;
        if (armorProtection <= 20.0F) damageReductionRatio = armorProtection / 25.0F; // 80% at armor 20
         else damageReductionRatio = 0.8F + (armorProtection - 20.0F) / 200.0F; // Each point over 20 adds 0.5% reduction

        damageReductionRatio = Math.min(damageReductionRatio, 0.95F);

        float armorPenetration = calculateArmorPenetration(entity, source, damageReductionRatio);

        float protectionFactor = 1.0F - armorPenetration;

        return damage * protectionFactor;
    }

    /**
     * Calculate armor penetration based on weapon enchantments
     */
    @Unique
    private float calculateArmorPenetration(LivingEntity target, DamageSource source, float baseReduction) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            ItemStack weaponStack = attacker.getMainHandStack();

            if (!weaponStack.isEmpty() && target.getWorld() instanceof ServerWorld serverWorld) {
                float armorPenetrationFactor = EnchantmentHelper.getArmorEffectiveness(
                        serverWorld,
                        weaponStack,
                        target,
                        source,
                        baseReduction
                );

                return MathHelper.clamp(armorPenetrationFactor, 0.0F, baseReduction);
            }
        }

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
        float damageReduction;
        if (protection <= 20.0F) damageReduction = protection / 25.0F;
         else damageReduction = 0.8F + (protection - 20.0F) / 300.0F;
        damageReduction = Math.min(damageReduction, 0.95F);

        return damageDealt * (1.0F - damageReduction);
    }
}