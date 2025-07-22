package sypztep.dominatus.common.system.stat.elements.player;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.system.stat.PlayerStat;
import sypztep.dominatus.common.system.stat.elements.core.LuckStat;

import java.util.List;

public class PlayerLuckStat extends PlayerStat<LuckStat> {

    public PlayerLuckStat() {
        super(new LuckStat(), "Luck");
    }

    @Override
    public List<Text> getEffectDescriptionWithCost(int additionalPoints) {
        int futureValue = getValue() + additionalPoints;

        double currentCritChance = LuckStat.calculateCritChanceBonus(getValue(), getBaseValue()) * 100;
        double futureCritChance = LuckStat.calculateCritChanceBonus(futureValue, getBaseValue()) * 100;
        double critChanceIncrease = futureCritChance - currentCritChance;

        double currentMagicDamage = LuckStat.calculateMagicDamageBonus(getValue(), getBaseValue()) * 100;
        double futureMagicDamage = LuckStat.calculateMagicDamageBonus(futureValue, getBaseValue()) * 100;
        double magicDamageIncrease = futureMagicDamage - currentMagicDamage;

        double currentAttackSpeed = LuckStat.calculateAttackSpeedBonus(getValue(), getBaseValue()) * 100;
        double futureAttackSpeed = LuckStat.calculateAttackSpeedBonus(futureValue, getBaseValue()) * 100;
        double attackSpeedIncrease = futureAttackSpeed - currentAttackSpeed;

        int currentAccuracy = LuckStat.calculateAccuracyBonus(getValue(), getBaseValue());
        int futureAccuracy = LuckStat.calculateAccuracyBonus(futureValue, getBaseValue());
        int accuracyIncrease = futureAccuracy - currentAccuracy;

        int currentEvasion = LuckStat.calculateEvasionBonus(getValue(), getBaseValue());
        int futureEvasion = LuckStat.calculateEvasionBonus(futureValue, getBaseValue());
        int evasionIncrease = futureEvasion - currentEvasion;

        return List.of(
                Text.literal("LUCK").formatted(Formatting.WHITE, Formatting.BOLD)
                        .append(Text.literal(" " + getValue()).formatted(Formatting.GRAY))
                        .append(Text.literal(" → ").formatted(Formatting.DARK_GRAY))
                        .append(Text.literal(String.valueOf(futureValue)).formatted(Formatting.WHITE)),

                Text.literal("Cost: ").formatted(Formatting.GRAY)
                        .append(Text.literal(calculateCost(additionalPoints) + " Benefit Points").formatted(Formatting.YELLOW)),

                Text.literal(""),

                Text.literal("Primary Effects").formatted(Formatting.GOLD),
                Text.literal("  Critical Chance: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.2f%%", critChanceIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.2f%% → %.2f%%)", currentCritChance, futureCritChance)).formatted(Formatting.DARK_GRAY)),

                Text.literal(""),

                Text.literal("Secondary Effects").formatted(Formatting.GOLD),
                Text.literal("  Magic Damage: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", magicDamageIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentMagicDamage, futureMagicDamage)).formatted(Formatting.DARK_GRAY)),

                Text.literal("  Attack Speed: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", attackSpeedIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentAttackSpeed, futureAttackSpeed)).formatted(Formatting.DARK_GRAY)),

                Text.literal("  Accuracy: ").formatted(Formatting.GRAY)
                        .append(Text.literal("+" + accuracyIncrease).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%d → %d)", currentAccuracy, futureAccuracy)).formatted(Formatting.DARK_GRAY)),

                Text.literal("  Evasion: ").formatted(Formatting.GRAY)
                        .append(Text.literal("+" + evasionIncrease).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%d → %d)", currentEvasion, futureEvasion)).formatted(Formatting.DARK_GRAY))
        );
    }
}
