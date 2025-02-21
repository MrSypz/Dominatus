package sypztep.dominatus.mixin.core.combat.critevasion;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
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
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.init.ModParticle;
import sypztep.dominatus.client.payload.AddTextParticlesPayload;
import sypztep.dominatus.common.api.combat.CriticalOverhaul;
import sypztep.dominatus.common.attributes.EntityCombatAttributes;
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
    private float applyDamageFirst(float amount, ServerWorld world, DamageSource source, float originalAmount) {
        if (world.isClient()) {
            return amount;
        }

        Entity attacker = source.getAttacker();
        if (!(attacker instanceof CriticalOverhaul criticalAttacker)) {
            return amount;
        }

        boolean shouldCrit;

        Entity projectileSource = source.getSource();
        if (projectileSource instanceof PersistentProjectileEntity) {
            shouldCrit = criticalAttacker.calCritDamage(amount) > amount;

            criticalAttacker.storeCrit().setCritical(shouldCrit);
            this.setCritical(shouldCrit);

            if (shouldCrit) {
                Dominatus.LOGGER.info("Applying projectile critical hit!");
                amount = criticalAttacker.calCritDamage(amount);

                playCriticalSound(attacker);
            }
        }
        else {
            float criticalDamage = criticalAttacker.calCritDamage(amount);
            shouldCrit = criticalDamage > amount;
            amount = criticalDamage;

            // Apply effects for non-player critical hits
            if (shouldCrit) {
                applyCriticalParticle(this);
                playCriticalSound(attacker);
            }
        }

        Dominatus.LOGGER.info("Final damage: {}, isCrit = {}", amount, shouldCrit);

        return amount;
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void handleDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();
        if (!(attacker instanceof LivingEntity livingAttacker)) {
            return;
        }

        //Check if the attack should miss
        isHit = calculateHit(livingAttacker);
        if (!isHit) {
            sendMissingParticles(livingAttacker);

            if (attacker instanceof CriticalOverhaul criticalAttacker) {
                criticalAttacker.setCritical(false);
            }

            cir.setReturnValue(false);
            return;
        }

        //Handle projectile crit effects (works for skeletons and players)
        if (source.getSource() instanceof PersistentProjectileEntity projectile) {
            if (projectile instanceof CriticalOverhaul projectileCritical && projectileCritical.isCritical()) {
                applyCriticalParticle(source.getSource());
                ((CriticalOverhaul) attacker).setCritical(true);
            }
        }
        //Handle melee crit effects (monsters, players)
        else if (attacker instanceof CriticalOverhaul criticalOverhaul && criticalOverhaul.isCritical()) {
            applyCriticalParticle(this);
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void handleCrit(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof CriticalOverhaul criticalOverhaul) {
            if (!this.getWorld().isClient() && this.isCritical()) {
                applyCriticalParticle(this);
            }
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
                    .forEach(foundPlayer -> AddTextParticlesPayload.send(
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
        PlayerLookup.tracking((ServerWorld) target.getWorld(), target.getChunkPos()).forEach(foundPlayer -> AddTextParticlesPayload.send(foundPlayer, this.getId(), ModParticle.MISSING));
        PlayerLookup.tracking((ServerWorld) attacker.getWorld(), attacker.getChunkPos()).forEach(foundPlayer -> AddTextParticlesPayload.send(foundPlayer, this.getId(), ModParticle.MISSING));
    }
}