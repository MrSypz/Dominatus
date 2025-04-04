package sypztep.dominatus.mixin.core.combat.critevasion;

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
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.client.init.ModParticle;
import sypztep.dominatus.client.payload.AddTextParticlesPayloadS2C;
import sypztep.dominatus.common.api.combat.CriticalOverhaul;
import sypztep.dominatus.common.util.combatsystem.EntityCombatAttributes;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.api.combat.MissingAccessor;

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
    private boolean isHit;
    @Unique
    protected LivingEntity target = (LivingEntity) (Object) this;

    @Shadow
    public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public boolean isMissing() {
        return !isHit;
    }

    @ModifyVariable(method = "applyDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float applyDamageFirst(float amount, DamageSource source) {
        if (!this.getWorld().isClient()) {
            Entity attacker = source.getAttacker();

            if (attacker instanceof CriticalOverhaul criticalAttacker) {
                Entity projectileSource = source.getSource();
                if (projectileSource instanceof PersistentProjectileEntity) {
                    criticalAttacker.storeCrit().setCritical(this.isCritical());
                    float critDamage = criticalAttacker.calCritDamage(amount);
                    boolean shouldCrit = critDamage > amount;
                    if (shouldCrit) {
                        applyCriticalParticle(this);
                        playCriticalSound(attacker);
                    }
                    return critDamage;
                }

                if (!(attacker instanceof PlayerEntity)) {
                    float critDamage = criticalAttacker.calCritDamage(amount);
                    boolean shouldCrit = critDamage != amount;
                    this.setCritical(shouldCrit);
                    if (shouldCrit) {
                        applyCriticalParticle(this);
                        playCriticalSound(attacker);
                    }
                    return critDamage;
                }
            }
        }
        return amount;
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void handleDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.getWorld().isClient()) return;

        Entity attacker = source.getAttacker();
        if (!(attacker instanceof LivingEntity livingAttacker)) {
            return;
        }

        isHit = calculateHit(livingAttacker);
        if (!isHit) {
            sendMissingParticles(livingAttacker);

            if (attacker instanceof CriticalOverhaul criticalAttacker) criticalAttacker.setCritical(false);

            cir.setReturnValue(false);
            return;
        }

        if (source.getSource() instanceof PersistentProjectileEntity projectile) {
            if (attacker instanceof CriticalOverhaul criticalAttacker) {
                criticalAttacker.setCritical(projectile.isCritical());
                if (projectile.isCritical()) playCriticalSound(attacker);
            }
        }
        // Handle melee crit effects
        else if (attacker instanceof CriticalOverhaul criticalAttacker && criticalAttacker.isCritical()) {
            applyCriticalParticle(this);
            playCriticalSound(attacker);
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void handleCrit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!this.getWorld().isClient() && source.getAttacker() instanceof CriticalOverhaul criticalAttacker) {
            if (this.isCritical()) applyCriticalParticle(this);
            criticalAttacker.setCritical(false);
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
        return (float) this.getAttributeValue(ModEntityAttributes.CRIT_DAMAGE);
    }

    @Override
    public float getCritRate() {
        return (float) this.getAttributeValue(ModEntityAttributes.CRIT_CHANCE);
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
    public void applyCriticalParticle(Entity target) {
        if (!isHit) {
            return;
        }
        if (target != null) {
            PlayerLookup.tracking((ServerWorld) target.getWorld(), target.getChunkPos())
                    .forEach(foundPlayer -> AddTextParticlesPayloadS2C.send(
                            foundPlayer, target.getId(),
                            ModParticle.CRITICAL
                    ));
        }
    }

    @Unique
    private boolean calculateHit(LivingEntity attacker) {
        EntityCombatAttributes attackerAttributes = new EntityCombatAttributes(attacker);
        EntityCombatAttributes defenderAttributes = new EntityCombatAttributes(target);
        return attackerAttributes.calculateHit(defenderAttributes);
    }

    @Unique
    private void sendMissingParticles(LivingEntity attacker) {
        PlayerLookup.tracking((ServerWorld) target.getWorld(), target.getChunkPos()).forEach(foundPlayer -> AddTextParticlesPayloadS2C.send(foundPlayer, this.getId(), ModParticle.MISSING));
        PlayerLookup.tracking((ServerWorld) attacker.getWorld(), attacker.getChunkPos()).forEach(foundPlayer -> AddTextParticlesPayloadS2C.send(foundPlayer, this.getId(), ModParticle.MISSING));
    }
}