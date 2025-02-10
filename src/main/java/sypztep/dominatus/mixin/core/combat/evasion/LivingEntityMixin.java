package sypztep.dominatus.mixin.core.combat.evasion;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.common.api.combat.CriticalOverhaul;
import sypztep.dominatus.common.api.combat.MissingAccessor;
import sypztep.dominatus.common.attributes.EntityCombatAttributes;
import sypztep.knumber.client.particle.util.TextParticleProvider;
import sypztep.knumber.client.payload.AddTextParticlesPayload;

import java.awt.Color;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    protected LivingEntity target = (LivingEntity) (Object) this;

    @Unique
    private static final TextParticleProvider MISSING = TextParticleProvider.register(
            Text.translatable("dominatus.text.missing"),
            new Color(255, 255, 255),
            -0.045f,
            -1
    );

    protected LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "damage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z", ordinal = 0),
            cancellable = true
    )
    private void handleMissing(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();
        if (!(attacker instanceof LivingEntity livingAttacker)) {
            return;
        }

        EntityCombatAttributes attackerAttributes = new EntityCombatAttributes(target);
        EntityCombatAttributes defenderAttributes = new EntityCombatAttributes(livingAttacker);

        boolean hits = attackerAttributes.calculateHit(defenderAttributes);

        // Update attacker's hit state through the interface
        if (livingAttacker instanceof MissingAccessor attackerAccessor) {
            attackerAccessor.setHit(hits);
            System.out.println("isHit Evasion Class: " + hits);
        }

        if (!hits) {
            showMissParticles(livingAttacker);

            if (livingAttacker instanceof CriticalOverhaul criticalAttacker) {
                criticalAttacker.setCritical(false);
            }

            cir.setReturnValue(false);
        }
    }

    @Unique
    private void showMissParticles(LivingEntity attacker) {
        if (!(target.getWorld() instanceof ServerWorld)) {
            return;
        }

        PlayerLookup.tracking((ServerWorld) target.getWorld(), target.getChunkPos())
                .forEach(player -> AddTextParticlesPayload.send(player, getId(), MISSING));

        PlayerLookup.tracking((ServerWorld) attacker.getWorld(), attacker.getChunkPos())
                .forEach(player -> AddTextParticlesPayload.send(player, getId(), MISSING));
    }
}