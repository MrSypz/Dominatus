package sypztep.dominatus.common.util.combatsystem;

/**
 * Base abstract class for all combat-related attributes
 */
public abstract class CombatAttribute {
    protected double baseValue;
    protected double bonusValue;

    public CombatAttribute(double baseValue) {
        this.baseValue = baseValue;
        this.bonusValue = 0.0;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(double baseValue) {
        this.baseValue = baseValue;
    }

    public double getBonusValue() {
        return bonusValue;
    }

    public void setBonusValue(double bonusValue) {
        this.bonusValue = bonusValue;
    }

    public double getTotalValue() {
        return baseValue + bonusValue;
    }

    public abstract double calculateEffect();
}
