package sypztep.dominatus.mixin.core.registry.attribute.apply;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sypztep.dominatus.common.data.DominatusEntityEntry;
import sypztep.dominatus.common.init.ModEntityAttributes;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Inject(method = "spawnEntity", at = @At("HEAD"))
    private void applyMobStats(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof LivingEntity living) {
            applyEntityAttributes(living);
        }
    }

    private static void applyEntityAttributes(LivingEntity entity) {
        DominatusEntityEntry entry = DominatusEntityEntry.BASEMOBSTATS_MAP.get(entity.getType());
        if (entry == null) {
            return;
        }

        applyAttributeIfPresent(entity, ModEntityAttributes.ACCURACY, entry.accuracy());
        applyAttributeIfPresent(entity, ModEntityAttributes.EVASION, entry.evasion());
        applyAttributeIfPresent(entity, ModEntityAttributes.CRIT_CHANCE, entry.critChance());
        applyAttributeIfPresent(entity, ModEntityAttributes.CRIT_DAMAGE, entry.critDamage());
        applyAttributeIfPresent(entity, ModEntityAttributes.BACK_ATTACK, entry.backAttack());
        applyAttributeIfPresent(entity, ModEntityAttributes.AIR_ATTACK, entry.airAttack());
        applyAttributeIfPresent(entity, ModEntityAttributes.DOWN_ATTACK, entry.downAttack());
    }

    private static void applyAttributeIfPresent(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, double value) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        if (instance != null && value != 0) {
            instance.setBaseValue(value);
        }
    }
}
