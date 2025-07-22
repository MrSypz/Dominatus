package sypztep.dominatus.mixin.vanilla.vitalitybonus;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.dominatus.common.init.ModEntityAttributes;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    @Shadow public abstract void heal(float amount);

    @Shadow @Final private DamageTracker damageTracker;

    @Inject(method = "tickMovement", at = @At("TAIL")) //TODO: for some reason when first join the work it not work util player got hit by monster to init damagetracker.. 21/7/2025
    private void natureHealthRegen(CallbackInfo ci) {
        float natureHealthRegen = (float) this.getAttributeValue(ModEntityAttributes.HEALTH_REGEN);
        if (natureHealthRegen > 0.00f && this.age % 60 == 0 && this.damageTracker.getTimeSinceLastAttack() > 300) {
            this.heal(natureHealthRegen);
        }
    }
}
