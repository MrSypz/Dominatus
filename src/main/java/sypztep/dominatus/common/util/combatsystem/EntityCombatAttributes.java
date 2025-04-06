package sypztep.dominatus.common.util.combatsystem;

import net.minecraft.entity.LivingEntity;
import sypztep.dominatus.common.util.combatsystem.element.Accuracy;
import sypztep.dominatus.common.util.combatsystem.element.Evasion;
import sypztep.dominatus.common.init.ModEntityAttributes;

public final class EntityCombatAttributes {
    private final LivingEntity entity;
    private final Accuracy accuracy;
    private final Evasion evasion;

    public EntityCombatAttributes(LivingEntity entity) {
        this.entity = entity;
        this.accuracy = new Accuracy(entity.getAttributeValue(ModEntityAttributes.ACCURACY));
        this.evasion = new Evasion(entity.getAttributeValue(ModEntityAttributes.EVASION));
    }

    public Accuracy getAccuracy() {
        return accuracy;
    }

    public Evasion getEvasion() {
        return evasion;
    }

    public boolean calculateHit(EntityCombatAttributes defender) {
        double hitChance = getAccuracy().calculateHitChance(defender.getEvasion(), defender.entity.getArmor());
        return Math.random() < hitChance;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}