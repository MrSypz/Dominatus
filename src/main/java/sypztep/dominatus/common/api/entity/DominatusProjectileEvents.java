package sypztep.dominatus.common.api.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;

public final class DominatusProjectileEvents {

    /**
     * Called when a projectile is about to hit an entity.
     * Return false to cancel the hit (make projectile clip through).
     */
    public static final Event<AllowProjectileHit> ALLOW_PROJECTILE_HIT =
            EventFactory.createArrayBacked(AllowProjectileHit.class, (listeners) ->
                    (projectile, target, hitResult) -> {
                        for (AllowProjectileHit listener : listeners) {
                            if (!listener.allowHit(projectile, target, hitResult)) {
                                return false; // Cancel if any listener says no
                            }
                        }
                        return true; // Allow hit if all listeners agree
                    });

    @FunctionalInterface
    public interface AllowProjectileHit {
        /**
         * @param projectile The projectile entity hitting
         * @param target The entity being hit
         * @param hitResult The hit result
         * @return false to cancel hit (clip through), true to allow hit
         */
        boolean allowHit(ProjectileEntity projectile, Entity target, EntityHitResult hitResult);
    }
}