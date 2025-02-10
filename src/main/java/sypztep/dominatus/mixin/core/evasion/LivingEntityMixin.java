package sypztep.dominatus.mixin.core.evasion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.common.attributes.EntityCombatAttributes;
import sypztep.dominatus.common.init.ModEntityAttributes;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity{
    @Unique
    LivingEntity target = (LivingEntity) (Object) this;

    protected LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z", ordinal = 0), cancellable = true)
    private void handleMissing(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof LivingEntity livingAttacker) {

            EntityCombatAttributes attackerAttributes = new EntityCombatAttributes(target, target.getAttributeValue(ModEntityAttributes.GENERIC_ACCURACY), target.getAttributeValue(ModEntityAttributes.GENERIC_EVASION));
            EntityCombatAttributes defenderAttributes = new EntityCombatAttributes(livingAttacker, 80.0, 570.0);

            boolean hits = attackerAttributes.calculateHit(defenderAttributes);
            if (hits) {
                System.out.println("Hit");
            } else {
                System.out.println("Miss");
                cir.setReturnValue(false);
            }
        }
    }
}
