package sypztep.dominatus.common.system.stat.elements.player;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.system.stat.elements.core.VitalityStat;

import java.util.List;

public class PlayerVitalityStat extends PlayerStat<VitalityStat> {
    public PlayerVitalityStat() {
        super(new VitalityStat(),"Vitality");
    }

    // ====================
    // STAT UI
    // ====================

    @Override
    public List<Text> getEffectDescriptionWithCost(int additionalPoints) {
        int futureValue = getValue() + additionalPoints;

        double currentMaxHealth = VitalityStat.calculateMaxHealthBonus(getValue(), getBaseValue()) * 100;
        double futureMaxHealth = VitalityStat.calculateMaxHealthBonus(futureValue, getBaseValue()) * 100;
        double maxHealthIncrease = futureMaxHealth - currentMaxHealth;

        double currentHealthRegen = VitalityStat.calculateHealthRegenBonus(getValue(), getBaseValue()) * 100;
        double futureHealthRegen = VitalityStat.calculateHealthRegenBonus(futureValue, getBaseValue()) * 100;
        double healthRegenIncrease = futureHealthRegen - currentHealthRegen;

        double currentHealEffective = VitalityStat.calculateHealEffectiveBonus(getValue(), getBaseValue()) * 100;
        double futureHealEffective = VitalityStat.calculateHealEffectiveBonus(futureValue, getBaseValue()) * 100;
        double healEffectiveIncrease = futureHealEffective - currentHealEffective;

        return List.of(
                Text.literal("VITALITY").formatted(Formatting.WHITE, Formatting.BOLD)
                        .append(Text.literal(" " + getValue()).formatted(Formatting.GRAY))
                        .append(Text.literal(" → ").formatted(Formatting.DARK_GRAY))
                        .append(Text.literal(String.valueOf(futureValue)).formatted(Formatting.WHITE)),

                Text.literal("Cost: ").formatted(Formatting.GRAY)
                        .append(Text.literal(calculateCost(additionalPoints) + " Benefit Points").formatted(Formatting.YELLOW)),

                Text.literal(""),

                Text.literal("Primary Effects").formatted(Formatting.GOLD),
                Text.literal("  Max Health: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", maxHealthIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentMaxHealth, futureMaxHealth)).formatted(Formatting.DARK_GRAY)),

                Text.literal(""),

                Text.literal("Secondary Effects").formatted(Formatting.GOLD),
                Text.literal("  Health Regen: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", healthRegenIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentHealthRegen, futureHealthRegen)).formatted(Formatting.DARK_GRAY)),

                Text.literal("  Heal Effective: ").formatted(Formatting.GRAY)
                        .append(Text.literal(String.format("+%.1f%%", healEffectiveIncrease)).formatted(Formatting.GREEN))
                        .append(Text.literal(String.format(" (%.1f%% → %.1f%%)", currentHealEffective, futureHealEffective)).formatted(Formatting.DARK_GRAY))
        );
    }
}
