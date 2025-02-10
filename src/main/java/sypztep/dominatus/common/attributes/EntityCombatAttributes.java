package sypztep.dominatus.common.attributes;

import net.minecraft.entity.LivingEntity;
import sypztep.dominatus.common.attributes.element.Accuracy;
import sypztep.dominatus.common.attributes.element.Evasion;

/**
 * Container class to manage combat attributes for an entity
 */
public class EntityCombatAttributes {
    private final LivingEntity entity;
    private final Accuracy accuracy;
    private final Evasion evasion;

    public EntityCombatAttributes(LivingEntity entity, double baseAccuracy, double baseEvasion) {
        this.entity = entity;
        this.accuracy = new Accuracy(baseAccuracy);
        this.evasion = new Evasion(baseEvasion);
    }

    public Accuracy getAccuracy() {
        return accuracy;
    }

    public Evasion getEvasion() {
        return evasion;
    }

    /**
     * Calculate if an attack hits based on attacker's accuracy and defender's evasion
     * @param defender The defending entity's combat attributes
     * @return true if the attack hits, false if it misses
     */
    public boolean calculateHit(EntityCombatAttributes defender) {
        double hitChance = this.accuracy.calculateHitChance(defender.getEvasion());
        return Math.random() < hitChance;
    }
}