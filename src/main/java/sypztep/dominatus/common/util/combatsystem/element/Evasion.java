package sypztep.dominatus.common.util.combatsystem.element;

import sypztep.dominatus.common.util.combatsystem.CombatAttribute;

public class Evasion extends CombatAttribute {
    private static final double BASE_EVASION_CHANCE = 0.08D; // (8% base evasion)
    private static final double EVASION_SCALING_FACTOR = 0.0015D; // (0.15% per point)

    public Evasion(double baseValue) {
        super(baseValue);
    }

    @Override
    public double calculateEffect() {
        return BASE_EVASION_CHANCE + (getTotalValue() * EVASION_SCALING_FACTOR);
    }
}