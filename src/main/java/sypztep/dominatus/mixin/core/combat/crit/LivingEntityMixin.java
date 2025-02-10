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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.common.api.combat.CriticalOverhaul;
import sypztep.dominatus.common.api.combat.MissingAccessor;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.knumber.client.particle.util.TextParticleProvider;
import sypztep.knumber.client.payload.AddTextParticlesPayload;

import java.awt.*;
import java.util.Random;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CriticalOverhaul, MissingAccessor {
    @Unique
    private static final float SOUND_VOLUME = 1.0F;
    @Unique
    private static final float SOUND_PITCH = 1.0F;
    @Unique
    private final Random critRandom = new Random();
    @Unique
    private boolean isCrit;
    @Unique
    public boolean mobisCrit;
    @Unique
    protected volatile boolean isHit = true; // Initialize to true


    @Unique
    protected TextParticleProvider CRITICAL = TextParticleProvider.register(Text.translatable("dominatus.text.critical"), new Color(ModConfig.critDamageColor), -0.055f, -0.045F,()-> ModConfig.damageCritIndicator);

    @Shadow
    public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    @Override
    public boolean isHit() {
        return isHit;
    }

    @Override
    public void setHit(boolean hit) {
        isHit = hit;
    }

    @ModifyVariable(method = "applyDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float applyDamageFirst(float amount, ServerWorld world, DamageSource source, float originalAmount) {
        if (world.isClient()) {
            return amount;
        }

        Entity attacker = source.getAttacker();
        if (!(attacker instanceof CriticalOverhaul criticalAttacker)) {
            return amount;
        }

        // Handle projectile critical hits
        Entity projectileSource = source.getSource();
        if (projectileSource instanceof PersistentProjectileEntity) {
            criticalAttacker.storeCrit().setCritical(this.isCritical());
            return criticalAttacker.calCritDamage(amount);
        }

        // Handle non-player entity critical hits
        if (!(attacker instanceof PlayerEntity)) {
            float criticalDamage = criticalAttacker.calCritDamage(amount);
            mobisCrit = criticalDamage != amount;

            if (mobisCrit) {
                applyParticle(this);
                playCriticalSound(attacker);
            }
            return criticalDamage;
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
            criticalOverhaul.setCritical(false);
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

    @Unique
    private void playCriticalSound(Entity attacker) {
        attacker.getWorld().playSound(
                this,
                this.getBlockPos(),
                SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
                SoundCategory.HOSTILE,
                SOUND_VOLUME,
                SOUND_PITCH
        );
    }

    @Unique
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