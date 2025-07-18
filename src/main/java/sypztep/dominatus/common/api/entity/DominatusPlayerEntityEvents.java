package sypztep.dominatus.common.api.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

public final class DominatusPlayerEntityEvents {
    public static final Event<ModifyAttackDamage> MODIFY_ATTACK_DAMAGE = EventFactory.createArrayBacked(ModifyAttackDamage.class, (listeners) -> (player, originalDamage) -> {
        for (ModifyAttackDamage listener : listeners)
            originalDamage = listener.modifyDamage(player, originalDamage);
        return originalDamage;
    });

    public static final Event<ModifyAttackCondition> MODIFY_ATTACK_CONDITION = EventFactory.createArrayBacked(ModifyAttackCondition.class, (listeners) -> (player, target, vanillaCrit) -> {
        for (ModifyAttackCondition listener : listeners)
            vanillaCrit = listener.modifyCondition(player, target, vanillaCrit);
        return vanillaCrit;
    });
    public static final Event<AllowAttack> ALLOW_ATTACK = EventFactory.createArrayBacked(AllowAttack.class, (listeners) -> (player, target) -> {
        for (AllowAttack listener : listeners)
            return listener.allowAttack(player, target);
        return true;
    });
    public static final Event<DamageDealt> DAMAGE_DEALT = EventFactory.createArrayBacked(DamageDealt.class, (listeners) -> (entity, source, finalDamage) -> {
        for (DamageDealt listener : listeners) {
            listener.onDamageDealt(entity, source, finalDamage);
        }
    });

    @FunctionalInterface
    public interface ModifyAttackDamage {
        float modifyDamage(PlayerEntity player, float originalDamage);
    }

    @FunctionalInterface
    public interface ModifyAttackCondition {
        boolean modifyCondition(PlayerEntity player, Entity target, boolean vanillaCrit);
    }

    @FunctionalInterface
    public interface AllowAttack {
        boolean allowAttack(PlayerEntity player, Entity target);
    }

    @FunctionalInterface
    public interface DamageDealt {
        /**
         * Called when damage is being dealt to an entity.
         * This is called after all damage calculations are complete.
         *
         * @param entity      The entity taking damage
         * @param source      The damage source
         * @param finalDamage The final damage amount that will be applied
         */
        void onDamageDealt(LivingEntity entity, DamageSource source, float finalDamage);
    }
}
