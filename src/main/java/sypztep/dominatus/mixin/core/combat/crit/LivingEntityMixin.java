package sypztep.dominatus.mixin.core.combat.crit;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.common.api.combat.CriticalOverhaul;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.knumber.client.particle.util.TextParticleProvider;
import sypztep.knumber.client.payload.AddTextParticlesPayload;

import java.awt.*;
import java.util.Random;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CriticalOverhaul {
    @Unique
    private boolean isCrit;
    @Unique
    public boolean mobisCrit;
    @Unique
    private final Random critRandom = new Random();
    @Unique
    protected TextParticleProvider CRITICAL = TextParticleProvider.register(Text.translatable("dominatus.text.critical"), new Color(ModConfig.critDamageColor), -0.055f, -0.045F,()-> ModConfig.damageCritIndicator);

    @Shadow
    public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyVariable(
            method = "applyDamage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;modifyAppliedDamage(Lnet/minecraft/entity/damage/DamageSource;F)F",
                    shift = At.Shift.AFTER
            ),
            ordinal = 0,
            argsOnly = true)
    private float applyDamageFirst(float amount, ServerWorld world, DamageSource source, float originalAmount) {
        if (!this.getWorld().isClient()) {
            Entity attacker;
            attacker = source.getAttacker();

            if (attacker instanceof CriticalOverhaul invoker) {
                Entity projectileSource = source.getSource();
                if (projectileSource instanceof PersistentProjectileEntity) {
                    invoker.storeCrit().setCritical(this.isCritical());
                    return invoker.calCritDamage(amount);
                }
            }
            if (!(source.getAttacker() instanceof PlayerEntity)) {
                if (attacker instanceof CriticalOverhaul invoker) {
                    float critDamage = invoker.calCritDamage(amount);
                    mobisCrit = amount - critDamage != 0;
                    amount = critDamage;
                }
            }
        }
        return amount;
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void damageFirst(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof CriticalOverhaul criticalOverhaul && source.getSource() instanceof PersistentProjectileEntity projectile) {
            applyParticle(source.getSource());
            criticalOverhaul.setCritical(projectile.isCritical());
        }
    }


    @Inject(method = "damage", at = @At("RETURN"))
    private void handleCrit(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof CriticalOverhaul criticalOverhaul) {
            if (!this.getWorld().isClient() && this.isCritical())
                applyParticle(this);
            criticalOverhaul.setCritical(false); //return the flag
        }
    }

    @Override
    public void setCritical(boolean setCrit) {
        if (this.getWorld().isClient()) return;
        this.isCrit = setCrit;
    }

    @Override
    public Random getRand() {
        return this.critRandom;
    }

    @Override
    public boolean isCritical() {
        return this.isCrit;
    }

    @Override
    public float getCritDamage() {
        return (float) this.getAttributeValue(ModEntityAttributes.GENERIC_CRIT_DAMAGE);
    }

    @Override
    public float getCritRate() {
        return (float) this.getAttributeValue(ModEntityAttributes.GENERIC_CRIT_CHANCE);
    }

    public void applyParticle(Entity target) {
        if (target != null) {
            PlayerLookup.tracking((ServerWorld) target.getWorld(), target.getChunkPos())
                    .forEach(foundPlayer -> AddTextParticlesPayload.send(
                            foundPlayer, target.getId(),
                            CRITICAL
                    ));
        }
    }
}