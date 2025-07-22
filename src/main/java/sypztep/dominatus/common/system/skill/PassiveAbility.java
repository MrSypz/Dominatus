package sypztep.dominatus.common.system.skill;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Represents a passive ability that can be unlocked through stat progression
 */
public interface PassiveAbility {

    /**
     * @return Unique identifier for this passive ability
     */
    Identifier getId();

    /**
     * @return Display name of the passive ability
     */
    Text getName();

    /**
     * @return Description of what this passive does
     */
    Text getDescription();

    /**
     * @return Detailed tooltip lines explaining the ability
     */
    List<Text> getTooltip();

    /**
     * @return The stat this passive is associated with
     */
    String getStatType();

    /**
     * @return The stat value required to unlock this passive
     */
    int getRequiredStatValue();

    /**
     * @return The tier/level of this passive (for ordering/progression)
     */
    int getTier();

    /**
     * Called when the passive ability is first unlocked
     * @param entity The entity that unlocked this passive
     */
    void onUnlock(LivingEntity entity);

    /**
     * Called when the passive ability is applied/activated
     * @param entity The entity this passive applies to
     */
    void applyEffect(LivingEntity entity);

    /**
     * Called when the passive ability should be removed
     * @param entity The entity to remove the passive from
     */
    void removeEffect(LivingEntity entity);

    /**
     * @return Whether this passive is currently active/applied
     */
    boolean isActive();

    /**
     * @return Icon identifier for UI display (optional)
     */
    default Identifier getIcon() {
        return null;
    }

    /**
     * @return Whether this passive can stack (be upgraded)
     */
    default boolean canStack() {
        return false;
    }

    /**
     * @return Maximum stack level if stackable
     */
    default int getMaxStacks() {
        return 1;
    }
}