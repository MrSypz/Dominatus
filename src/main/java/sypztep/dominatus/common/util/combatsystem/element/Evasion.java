package sypztep.dominatus.common.util.combatsystem.element;

import sypztep.dominatus.common.util.combatsystem.CombatAttribute;

public final class Evasion extends CombatAttribute {
    private static final double BASE_EVASION = 0.0;
    private static final double EVASION_SCALING = 0.005;

    public Evasion(double baseValue) {
        super(baseValue);
    }

    @Override
    public double calculateEffect() {
        return BASE_EVASION + (getTotalValue() * EVASION_SCALING);
    }
}