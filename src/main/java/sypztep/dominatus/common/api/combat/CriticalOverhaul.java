package sypztep.dominatus.common.api.combat;

import java.util.Random;

public interface CriticalOverhaul {

    void setCritical(boolean setCrit);

    boolean isCritical();

    default float calCritDamage(float amount) {
        float totalCritRate = this.getTotalCritRate();
        float totalCritDMG = this.getTotalCritDamage();

        if (!this.storeCrit().isCritical() &&
                (!(totalCritDMG > 0.0F) || !(totalCritRate > 0.0F) ||
                        !(this.getRand().nextFloat() < totalCritRate))) {
            return amount;
        } else {
            this.storeCrit().setCritical(true);
            return amount * (1.0F + totalCritDMG);
        }
    }

    Random getRand();

    default CriticalOverhaul storeCrit() {
        return this;
    }

    default float getTotalCritRate() {
        return (this.getCritRate() + this.getCritRateFromEquipped());
    }

    default float getTotalCritDamage() {
        return (this.getCritDamage() + this.getCritDamageFromEquipped());
    }

    default float getCritRate() {
        return 0.0F;
    }

    default float getCritDamage() {
        return 0.0F;
    }

    default float getCritRateFromEquipped() {
        return 0.0F;
    }

    default float getCritDamageFromEquipped() {
        return 0.0F;
    }
}