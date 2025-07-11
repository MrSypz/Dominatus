package sypztep.dominatus.common.api.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public final class DominatusPlayerEntityEvents {
    public static final Event<ModifyAttackDamage> MODIFY_ATTACK_DAMAGE = EventFactory.createArrayBacked(
            ModifyAttackDamage.class,
            (listeners) -> (player, damage) -> {
                for (ModifyAttackDamage listener : listeners) {
                    damage = listener.modifyDamage(player,  damage);
                }
                return damage;
            }
    );

    public static final Event<ModifyAttackCondition> MODIFY_ATTACK_CONDITION = EventFactory.createArrayBacked(
            ModifyAttackCondition.class,
            (listeners) -> (player, target, vanillaCrit) -> {
                for (ModifyAttackCondition listener : listeners)
                    vanillaCrit = listener.modifyCondition(player, target, vanillaCrit);
                return vanillaCrit;
            }
    );

    @FunctionalInterface
    public interface ModifyAttackDamage {
        float modifyDamage(PlayerEntity player, float damage);
    }

    @FunctionalInterface
    public interface ModifyAttackCondition {
        boolean modifyCondition(PlayerEntity player, Entity target, boolean vanillaCrit);
    }
}
