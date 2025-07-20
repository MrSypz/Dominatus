package sypztep.dominatus.common.system.stat.elements.player;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.system.stat.elements.core.IntelligenceStat;

import java.util.List;

public class PlayerIntelligenceStat extends PlayerStat<IntelligenceStat> {

    public PlayerIntelligenceStat() {
        super(new IntelligenceStat(), "Intelligence");
    }

    @Override
    public List<Text> getEffectDescriptionWithCost(int additionalPoints) {
        int futureValue = getValue() + additionalPoints;

        double currentMagicDamage = IntelligenceStat.calculateMagicDamageBonus(getValue(), getBaseValue()) * 100;
        double futureMagicDamage = IntelligenceStat.calculateMagicDamageBonus(futureValue, getBaseValue()) * 100;
        double magicDamageIncrease = futureMagicDamage - currentMagicDamage;

        double currentMagicRes = IntelligenceStat.calculateMagicResistanceBonus(getValue(), getBaseValue()) * 100;
        double futureMagicRes = IntelligenceStat.calculateMagicResistanceBonus(futureValue, getBaseValue()) * 100;
        double magicResIncrease = futureMagicRes - currentMagicRes;

        return List.of(
                Text.literal("INTELLIGENCE").formatted(Formatting.WHITE, Formatting.BOLD)
                        .append(Text.literal(" " + getValue()).formatted(Formatting.GRAY))
                        .append(Text.literal(" → ").formatted(Formatting.DARK_GRAY))
                        .append(Text.literal(String.valueOf(futureValue)).formatted(Formatting.WHITE)),

                Text.literal("Cost: ").formatted(Formatting.GRAY)
                        .append(Text.literal(calculateCost(additionalPoints) + " Benefit Points").formatted(Formatting.YELLOW)),

                Text.literal(""),

                Text.literal("Primary Effects").formatted(Formatting.GOLD),
                Text.literal("  Magic Damage: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", magicDamageIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentMagicDamage, futureMagicDamage)).formatted(Formatting.DARK_GRAY)),

                Text.literal(""),

                Text.literal("Secondary Effects").formatted(Formatting.GOLD),
                Text.literal("  Magic Resistance: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", magicResIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentMagicRes, futureMagicRes)).formatted(Formatting.DARK_GRAY))
        );
    }
}
