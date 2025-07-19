package sypztep.dominatus.common.api.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public final class DominatusLivingEntityEvents {

    public static final Event<PreArmorDamage> PRE_ARMOR_DAMAGE = EventFactory.createArrayBacked(PreArmorDamage.class, callbacks -> (entity, source, amount) -> {
        for (PreArmorDamage callback : callbacks) {
            amount = callback.preModifyDamage(entity, source, amount);
            if (amount <= 0.0f) return 0.0f;
        }
        return amount;
    });

    public static final Event<PostArmorDamage> POST_ARMOR_DAMAGE = EventFactory.createArrayBacked(PostArmorDamage.class, callbacks -> (entity, source, amount) -> {
        for (PostArmorDamage callback : callbacks) {
            amount = callback.postModifyDamage(entity, source, amount);
            if (amount <= 0.0f) return 0.0f;
        }
        return amount;
    });

    @FunctionalInterface
    public interface PreArmorDamage {
        /**
         * Modify damage before armor calculation. only if targe thave armor
         *
         * @param entity The entity taking damage
         * @param source The damage source
         * @param amount The current damage amount
         * @return The modified damage amount (return 0 or negative to cancel damage)
         */
        float preModifyDamage(LivingEntity entity, DamageSource source, float amount);
    }

    @FunctionalInterface
    public interface PostArmorDamage {
        /**
         * Modify damage after armor calculation but before resistance/enchantments.
         *
         * @param entity The entity taking damage
         * @param source The damage source
         * @param amount The current damage amount (after armor reduction)
         * @return The modified damage amount (return 0 or negative to cancel damage)
         */
        float postModifyDamage(LivingEntity entity, DamageSource source, float amount);
    }

}