package test;

import test.java.sypztep.dominatus.CombatAttributeTest;
import test.java.sypztep.dominatus.CombatVisualizerTest;

public class main {
    public static void main(String[] args) {
        CombatAttributeTest combatTest = new CombatAttributeTest();
        CombatVisualizerTest visualizer = new CombatVisualizerTest();

        // Run all tests
        combatTest.testHitChances();
        combatTest.testExtremeCases();
        visualizer.visualizeHitChances();
        visualizer.testProgression();
    }
}
