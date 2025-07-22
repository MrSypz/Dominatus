package sypztep.dominatus.common.system.stat.elements.player;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.system.stat.PlayerStat;
import sypztep.dominatus.common.system.stat.elements.core.DexterityStat;

import java.util.List;

public class PlayerDexterityStat extends PlayerStat<DexterityStat> {
    public PlayerDexterityStat() {
        super(new DexterityStat(), "Dexterity");
    }

    // ====================
    // STAT UI
    // ====================

    // PlayerDexterityStat - Dark Theme Version
    @Override
    public List<Text> getEffectDescriptionWithCost(int additionalPoints) {
        int futureValue = getValue() + additionalPoints;

        int currentAccuracy = Math.max(0, DexterityStat.calculateAccuracyBonus(getValue(), getBaseValue()));
        int futureAccuracy = Math.max(0, DexterityStat.calculateAccuracyBonus(futureValue, getBaseValue()));
        int accuracyIncrease = futureAccuracy - currentAccuracy;

        double currentProjectileDamage = Math.max(0, DexterityStat.calculateProjectileDamageBonus(getValue(), getBaseValue()) * 100);
        double futureProjectileDamage = Math.max(0, DexterityStat.calculateProjectileDamageBonus(futureValue, getBaseValue()) * 100);
        double projectileDamageIncrease = futureProjectileDamage - currentProjectileDamage;

        double currentAttackSpeed = Math.max(0, DexterityStat.calculateAttackSpeedBonus(getValue(), getBaseValue()) * 100);
        double futureAttackSpeed = Math.max(0, DexterityStat.calculateAttackSpeedBonus(futureValue, getBaseValue()) * 100);
        double attackSpeedIncrease = futureAttackSpeed - currentAttackSpeed;

        return List.of(
                Text.literal("DEXTERITY").formatted(Formatting.WHITE, Formatting.BOLD)
                        .append(Text.literal(" " + getValue()).formatted(Formatting.GRAY))
                        .append(Text.literal(" → ").formatted(Formatting.DARK_GRAY))
                        .append(Text.literal(String.valueOf(futureValue)).formatted(Formatting.WHITE)),

                Text.literal("Cost: ").formatted(Formatting.GRAY)
                        .append(Text.literal(calculateCost(additionalPoints) + " Benefit Points").formatted(Formatting.YELLOW)),

                Text.literal(""),

                Text.literal("Primary Effects").formatted(Formatting.GOLD),
                Text.literal("  Accuracy: ").formatted(Formatting.GRAY)
                        .append(Text.literal("+" + accuracyIncrease).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%d → %d)", currentAccuracy, futureAccuracy)).formatted(Formatting.DARK_GRAY)),

                Text.literal(""),

                Text.literal("Secondary Effects").formatted(Formatting.GOLD),
                Text.literal("  Projectile Damage: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", projectileDamageIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentProjectileDamage, futureProjectileDamage)).formatted(Formatting.DARK_GRAY)),

                Text.literal("  Attack Speed: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", attackSpeedIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentAttackSpeed, futureAttackSpeed)).formatted(Formatting.DARK_GRAY))
        );
    }
}