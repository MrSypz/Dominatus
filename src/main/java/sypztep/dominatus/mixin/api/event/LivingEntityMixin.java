package sypztep.dominatus.mixin.api.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import sypztep.dominatus.common.api.entity.DominatusLivingEntityEvents;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * Modify damage before armor calculation.
     */
    @ModifyVariable(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"), argsOnly = true)
    private float applyPreArmorDamageModification(float amount, DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return DominatusLivingEntityEvents.PRE_ARMOR_DAMAGE.invoker().modifyDamage(entity, source, amount);
    }

    /**
     * Modify damage after armor calculation but before resistance effects and enchantments.
     */
    @ModifyVariable(method = "modifyAppliedDamage", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/damage/DamageSource;)F"), argsOnly = true)
    private float applyPostArmorDamageModification(float amount, DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return DominatusLivingEntityEvents.POST_ARMOR_DAMAGE.invoker().modifyDamage(entity, source, amount);
    }
}
