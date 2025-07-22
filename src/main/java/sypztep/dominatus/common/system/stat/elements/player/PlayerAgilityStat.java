package sypztep.dominatus.common.system.stat.elements.player;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.system.stat.PlayerStat;
import sypztep.dominatus.common.system.stat.elements.core.AgilityStat;

import java.util.List;

public class PlayerAgilityStat extends PlayerStat<AgilityStat> {
    public PlayerAgilityStat() {
        super(new AgilityStat(), "Agility");
    }

    // ====================
    // STAT UI
    // ====================

    @Override
    public List<Text> getEffectDescriptionWithCost(int additionalPoints) {
        int futureValue = getValue() + additionalPoints;

        double currentAttackSpeed = Math.max(0, AgilityStat.calculateAttackSpeedBonus(getValue(), getBaseValue()) * 100);
        double futureAttackSpeed = Math.max(0, AgilityStat.calculateAttackSpeedBonus(futureValue, getBaseValue()) * 100);
        double attackSpeedIncrease = futureAttackSpeed - currentAttackSpeed;

        int currentEvasion = Math.max(0, AgilityStat.calculateEvasionBonus(getValue(), getBaseValue()));
        int futureEvasion = Math.max(0, AgilityStat.calculateEvasionBonus(futureValue, getBaseValue()));
        int evasionIncrease = futureEvasion - currentEvasion;

        return List.of(
                Text.literal("AGILITY").formatted(Formatting.WHITE, Formatting.BOLD)
                        .append(Text.literal(" " + getValue()).formatted(Formatting.GRAY))
                        .append(Text.literal(" → ").formatted(Formatting.DARK_GRAY))
                        .append(Text.literal(String.valueOf(futureValue)).formatted(Formatting.WHITE)),

                Text.literal("Cost: ").formatted(Formatting.GRAY)
                        .append(Text.literal(calculateCost(additionalPoints) + " Benefit Points").formatted(Formatting.YELLOW)),

                Text.literal(""),

                Text.literal("Primary Effects").formatted(Formatting.GOLD),
                Text.literal("  Attack Speed: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", attackSpeedIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentAttackSpeed, futureAttackSpeed)).formatted(Formatting.DARK_GRAY)),

                Text.literal(""),

                Text.literal("Secondary Effects").formatted(Formatting.GOLD),
                Text.literal("  Evasion: ").formatted(Formatting.GRAY)
                        .append(Text.literal("+" + evasionIncrease).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%d → %d)", currentEvasion, futureEvasion)).formatted(Formatting.DARK_GRAY))
        );
    }
}