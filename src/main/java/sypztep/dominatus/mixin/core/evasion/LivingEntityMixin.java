package sypztep.dominatus.mixin.core.evasion;

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
import sypztep.dominatus.common.attributes.EntityCombatAttributes;
import sypztep.knumber.client.particle.util.TextParticleProvider;
import sypztep.knumber.client.payload.AddTextParticlesPayload;

import java.awt.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    LivingEntity target = (LivingEntity) (Object) this;

    protected LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private final TextParticleProvider MISSING = TextParticleProvider.register(Text.translatable("dominatus.text.missing"),new Color(255,255,255),-0.045f,-1 );

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z", ordinal = 0), cancellable = true)
    private void handleMissing(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof LivingEntity livingAttacker) {

            EntityCombatAttributes attackerAttributes = new EntityCombatAttributes(target);
            EntityCombatAttributes defenderAttributes = new EntityCombatAttributes(livingAttacker);

            boolean hits = attackerAttributes.calculateHit(defenderAttributes);
            if (hits) {
                System.out.println("Hit");
            } else {
                System.out.println("Miss");
                PlayerLookup.tracking((ServerWorld) target.getWorld(), target.getChunkPos()).forEach(foundPlayer -> AddTextParticlesPayload.send(foundPlayer, this.getId(), MISSING)); //Who Take Damage
                PlayerLookup.tracking((ServerWorld) livingAttacker.getWorld(), livingAttacker.getChunkPos()).forEach(foundPlayer -> AddTextParticlesPayload.send(foundPlayer, this.getId(), MISSING)); // Attacker

                cir.setReturnValue(false);
            }
        }
    }
}
