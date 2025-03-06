package test.java.sypztep.dominatus;

import sypztep.dominatus.common.combat.element.Accuracy;
import sypztep.dominatus.common.combat.element.Evasion;

import java.util.HashMap;
import java.util.Map;

public class CombatAttributeTest {
    private static final int SIMULATION_RUNS = 10000; // Number of attacks to simulate

    public void testHitChances() {
        // Test various combinations of Accuracy vs Evasion
        int[] accuracyValues = {10, 50, 100, 200, 500};
        int[] evasionValues = {10, 50, 100, 200, 500};

        System.out.println("Combat Balance Test Results");
        System.out.println("==========================");
        System.out.println("Simulations per combination: " + SIMULATION_RUNS);
        System.out.println();

        // Print header
        System.out.printf("%-8s | %-8s | %-12s | %-12s | %-12s%n",
                "ACC", "EVA", "Theo. Hit%", "Actual Hit%", "Hits/Total");
        System.out.println("-".repeat(60));

        for (int acc : accuracyValues) {
            for (int eva : evasionValues) {
                testCombination(acc, eva);
            }
            System.out.println("-".repeat(60));
        }
    }

    private void testCombination(int accuracyValue, int evasionValue) {
        Accuracy accuracy = new Accuracy(accuracyValue);
        Evasion evasion = new Evasion(evasionValue);

        // Calculate theoretical hit chance
        double theoreticalHitChance = accuracy.calculateHitChance(evasion);

        // Run simulation
        int hits = 0;
        for (int i = 0; i < SIMULATION_RUNS; i++) {
            if (calculateHit(evasion, accuracy)) {
                hits++;
            }
        }

        double actualHitChance = (double) hits / SIMULATION_RUNS;

        // Print results
        System.out.printf("%-8d | %-8d | %-11.2f%% | %-11.2f%% | %d/%-8d%n",
                accuracyValue,
                evasionValue,
                theoreticalHitChance * 100,
                actualHitChance * 100,
                hits,
                SIMULATION_RUNS);
    }

    private boolean calculateHit(Evasion evasion, Accuracy accuracy) {
        double hitChance = accuracy.calculateHitChance(evasion);
        return Math.random() < hitChance;
    }

    public void testExtremeCases() {
        System.out.println("\nExtreme Cases Test");
        System.out.println("=================");

        // Test cases
        Map<String, TestCase> cases = new HashMap<>();
        cases.put("Minimum Stats", new TestCase(0, 0));
        cases.put("Max Accuracy vs Min Evasion", new TestCase(1000, 0));
        cases.put("Min Accuracy vs Max Evasion", new TestCase(0, 1000));
        cases.put("Both Maximum", new TestCase(1000, 1000));
        cases.put("Balanced High", new TestCase(500, 500));

        for (Map.Entry<String, TestCase> entry : cases.entrySet()) {
            TestCase test = entry.getValue();
            Accuracy acc = new Accuracy(test.accuracy);
            Evasion eva = new Evasion(test.evasion);

            double hitChance = acc.calculateHitChance(eva);
            System.out.printf("\n%s:%n", entry.getKey());
            System.out.printf("Accuracy: %d, Evasion: %d%n", test.accuracy, test.evasion);
            System.out.printf("Hit Chance: %.2f%%%n", hitChance * 100);
        }
    }

    private record TestCase(int accuracy, int evasion) {
    }
}