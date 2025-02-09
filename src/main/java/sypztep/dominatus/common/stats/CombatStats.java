package sypztep.dominatus.common.stats;

public class CombatStats {
    private int baseValue;
    private int bonusValue;

    public CombatStats(int baseValue) {
        this.baseValue = baseValue;
        this.bonusValue = 0;
    }

    public int getTotal() {
        return baseValue + bonusValue;
    }

    public int getBase() {
        return baseValue;
    }

    public int getBonus() {
        return bonusValue;
    }

    public void setBase(int value) {
        this.baseValue = value;
    }

    public void addBonus(int value) {
        this.bonusValue += value;
    }

    public void setBonus(int value) {
        this.bonusValue = value;
    }

    public void reset() {
        this.bonusValue = 0;
    }
}