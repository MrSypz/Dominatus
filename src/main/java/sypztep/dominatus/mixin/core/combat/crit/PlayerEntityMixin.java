package sypztep.dominatus.mixin.core.combat.crit;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {
    @Unique
    private boolean alreadyCalculated;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    private float storedamage(float original) {
        float modifiedDamage = this.calCritDamage(original);
        this.alreadyCalculated = original != modifiedDamage;
        return modifiedDamage;
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "CONSTANT", args = "floatValue=1.5"))
    private float applyCritDmg(float original) {
        float modifiedCritDamage = this.alreadyCalculated ? 1.0F : (this.storeCrit().isCritical() ? (1.0F + this.getTotalCritDamage()) : original);
        this.alreadyCalculated = false;
        return modifiedCritDamage;
    }
//    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 3)
//    private boolean critCanSweepAttack(boolean bl4, @Local(ordinal = 0) boolean bl, @Local(ordinal = 1) boolean bl2, @Local(ordinal = 2) boolean bl3) {
//        double d = this.horizontalSpeed - this.prevHorizontalSpeed;
//        return bl && !bl2 && this.isOnGround() && d < this.getMovementSpeed() && this.getStackInHand(Hand.MAIN_HAND).getItem() instanceof SwordItem;
//    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 2)
    private boolean doCrit(boolean original, Entity target) {
        if (!this.getWorld().isClient()) {
            boolean iscrit = this.isCritical();
            if (iscrit) {
                applyParticle(target);
                this.setCritical(true);
                return true;
            }
        }
        return false;
    }
}
