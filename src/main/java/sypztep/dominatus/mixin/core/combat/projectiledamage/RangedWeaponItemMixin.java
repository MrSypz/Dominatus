package sypztep.dominatus.mixin.core.combat.projectiledamage;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.RangedWeaponItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sypztep.dominatus.common.init.ModEntityAttributes;

@Mixin(RangedWeaponItem.class)
public class RangedWeaponItemMixin {
    @WrapOperation(
            method = "shootAll",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/RangedWeaponItem;shoot(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/projectile/ProjectileEntity;IFFFLnet/minecraft/entity/LivingEntity;)V"))
    private void additionArrowDamage(
            RangedWeaponItem instance, LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target,
            Operation<Void> original) {
        original.call(instance, shooter, projectile, index, speed, divergence, yaw, target);

        if (projectile instanceof PersistentProjectileEntity projectileEntity) {
            double accuracy = shooter.getAttributeValue(ModEntityAttributes.ACCURACY);
            projectileEntity.setDamage(projectileEntity.getDamage() + accuracy * 0.2);
        }
    }
}
