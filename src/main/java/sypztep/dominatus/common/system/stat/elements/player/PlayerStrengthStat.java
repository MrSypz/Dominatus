package sypztep.dominatus.common.system.stat.elements.player;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.system.stat.elements.core.StrengthStat;

import java.util.List;

public class PlayerStrengthStat extends PlayerStat<StrengthStat> {

    public PlayerStrengthStat() {
        super(new StrengthStat(), "Strength");
    }

    @Override
    public List<Text> getEffectDescriptionWithCost(int additionalPoints) {
        int futureValue = getValue() + additionalPoints;

        double currentMeleeDamage = Math.max(0, StrengthStat.calculateMeleeDamageBonus(getValue(), getBaseValue()) * 100);
        double futureMeleeDamage = Math.max(0, StrengthStat.calculateMeleeDamageBonus(futureValue, getBaseValue()) * 100);
        double meleeDamageIncrease = futureMeleeDamage - currentMeleeDamage;

        double currentBlockBreakSpeed = Math.max(0, StrengthStat.calculateBlockBreakSpeedBonus(getValue(), getBaseValue()) * 100);
        double futureBlockBreakSpeed = Math.max(0, StrengthStat.calculateBlockBreakSpeedBonus(futureValue, getBaseValue()) * 100);
        double blockBreakSpeedIncrease = futureBlockBreakSpeed - currentBlockBreakSpeed;

        double currentAttackSpeed = Math.max(0, StrengthStat.calculateAttackSpeedBonus(getValue(), getBaseValue()) * 100);
        double futureAttackSpeed = Math.max(0, StrengthStat.calculateAttackSpeedBonus(futureValue, getBaseValue()) * 100);
        double attackSpeedIncrease = futureAttackSpeed - currentAttackSpeed;

        return List.of(
                Text.literal("STRENGTH").formatted(Formatting.WHITE, Formatting.BOLD)
                        .append(Text.literal(" " + getValue()).formatted(Formatting.GRAY))
                        .append(Text.literal(" → ").formatted(Formatting.DARK_GRAY))
                        .append(Text.literal(String.valueOf(futureValue)).formatted(Formatting.WHITE)),

                Text.literal("Cost: ").formatted(Formatting.GRAY)
                        .append(Text.literal(calculateCost(additionalPoints) + " Benefit Points").formatted(Formatting.YELLOW)),

                Text.literal(""),

                Text.literal("Primary Effects").formatted(Formatting.GOLD),
                Text.literal("  Melee Damage: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", meleeDamageIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentMeleeDamage, futureMeleeDamage)).formatted(Formatting.DARK_GRAY)),

                Text.literal(""),

                Text.literal("Secondary Effects").formatted(Formatting.GOLD),
                Text.literal("  Block Break Speed: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", blockBreakSpeedIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentBlockBreakSpeed, futureBlockBreakSpeed)).formatted(Formatting.DARK_GRAY)),

                Text.literal("  Attack Speed: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", attackSpeedIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentAttackSpeed, futureAttackSpeed)).formatted(Formatting.DARK_GRAY))
        );
    }
}