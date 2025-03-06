package test.java.sypztep.dominatus;

import sypztep.dominatus.common.combat.element.Accuracy;
import sypztep.dominatus.common.combat.element.Evasion;

public class CombatVisualizerTest {

    public void visualizeHitChances() {
        System.out.println("Hit Chance Visualization (%)");
        System.out.println("============================");

        // Header
        System.out.printf("%4s ", "EVA→");
        for (int eva = 0; eva <= 500; eva += 100) {
            System.out.printf("%6d ", eva);
        }
        System.out.printf("%n%4s ", "ACC↓");
        System.out.println("-".repeat(42));

        // Data
        for (int acc = 0; acc <= 500; acc += 100) {
            System.out.printf("%4d │", acc);
            for (int eva = 0; eva <= 500; eva += 100) {
                Accuracy accuracy = new Accuracy(acc);
                Evasion evasion = new Evasion(eva);
                double hitChance = accuracy.calculateHitChance(evasion);
                System.out.printf(" %5.1f ", hitChance * 100);
            }
            System.out.println();
        }
    }

    public void testProgression() {
        System.out.println("\nStat Progression Test");
        System.out.println("====================");

        // Test progression of stats from 0 to 500
        System.out.printf("%-6s | %-15s | %-15s%n", "Value", "Accuracy Effect", "Evasion Effect");
        System.out.println("-".repeat(40));

        for (int value : new int[]{0, 50, 100, 200, 300, 400, 500}) {
            Accuracy acc = new Accuracy(value);
            Evasion eva = new Evasion(value);

            System.out.printf("%-6d | %-14.2f%% | %-14.2f%%%n",
                    value,
                    acc.calculateEffect() * 100,
                    eva.calculateEffect() * 100);
        }
    }
}