package sypztep.dominatus.common.system.stat.elements.player;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.LevelData;
import sypztep.dominatus.common.system.stat.PlayerStatBehavior;
import sypztep.dominatus.common.system.stat.StatUI;
import sypztep.dominatus.common.system.stat.elements.core.VitalityStat;

import java.util.List;

public class PlayerVitalityStat extends VitalityStat implements PlayerStatBehavior, StatUI {

    private int totalPointsSpent = 0;

    // ====================
    // PLAYER STAT BEHAVIOR
    // ====================

    @Override
    public int getTotalPointsSpent() {
        return totalPointsSpent;
    }

    @Override
    public int getPointCost() {
        return 1 + (currentValue / 10);
    }

    @Override
    public int calculateCost(int points) {
        int totalCost = 0;
        int tempValue = currentValue;
        for (int i = 0; i < points; i++) {
            totalCost += 1 + (tempValue / 10);
            tempValue++;
        }
        return totalCost;
    }

    @Override
    public boolean increaseWithPoints(ServerPlayerEntity player, int points) {
        if (points <= 0) return false;

        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = levelComponent.getLevelData();

        int cost = calculateCost(points);
        if (levelData.getAvailableBenefits() < cost || !levelData.spendBenefits(cost)) {
            return false;
        }

        this.currentValue += points;
        this.totalPointsSpent += cost;

        applyPrimaryEffect(player);
        applySecondaryEffect(player);

        return true;
    }

    @Override
    public void resetWithRefund(ServerPlayerEntity player) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = levelComponent.getLevelData();

        levelData.addBenefits(this.totalPointsSpent);
        this.currentValue = baseValue;
        this.totalPointsSpent = 0;

        applyPrimaryEffect(player);
        applySecondaryEffect(player);
    }

    @Override
    public void writePlayerDataToNbt(NbtCompound tag) {
        tag.putInt("TotalPointsSpent", this.totalPointsSpent);
    }

    @Override
    public void readPlayerDataFromNbt(NbtCompound tag) {
        this.totalPointsSpent = tag.getInt("TotalPointsSpent");
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        super.readFromNbt(tag);
        readPlayerDataFromNbt(tag);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        super.writeToNbt(tag);
        writePlayerDataToNbt(tag);
    }

    // ====================
    // STAT UI
    // ====================

    @Override
    public String getStatName() {
        return "Vitality";
    }

    @Override
    public List<Text> getEffectDescription() {
        double maxHealthBonus = (currentValue - baseValue) * MAX_HEALTH_SCALING * 100;
        double healthRegenBonus = calculateHealthRegenBonus() * 100;

        return List.of(
                Text.literal(String.format("§6VIT: §f%d §7(Points spent: %d)", currentValue, totalPointsSpent)),
                Text.literal(String.format("§7Max Health: §f+%.1f%%", maxHealthBonus)),
                Text.literal(String.format("§7Health Regen: §f+%.1f%%", healthRegenBonus))
        );
    }

    @Override
    public List<Text> getEffectDescriptionWithCost(int additionalPoints) {
        int futureValue = currentValue + additionalPoints;

        double currentMaxHealth = calculateMaxHealthBonus(20) * 100;
        double futureMaxHealth = (futureValue - baseValue) * MAX_HEALTH_SCALING * 100;
        double maxHealthIncrease = futureMaxHealth - currentMaxHealth;

        double currentHealthRegen = calculateHealthRegenBonus() * 100;
        double futureHealthRegen = (futureValue - baseValue) * HEALTH_REGEN_SCALING * 100;
        double healthRegenIncrease = futureHealthRegen - currentHealthRegen;


        return List.of(
                Text.literal(String.format("§6VIT: §f%d §7→ §f%d", currentValue, futureValue)),
                Text.literal(String.format("§7Cost: §f%d §7benefit points", calculateCost(additionalPoints))),
                Text.literal(""),
                Text.literal("§6Primary Effects:").formatted(Formatting.GOLD),
                Text.literal(String.format("  §7Max Health: §f+%.1f%% §7(§f%.1f%% §7→ §f%.1f%%§7)",
                        maxHealthIncrease, currentMaxHealth, futureMaxHealth)),
                Text.literal(""),
                Text.literal("§6Secondary Effects:").formatted(Formatting.GOLD),
                Text.literal(String.format("  §7Health Regen: §f+%.1f%% §7(§f%.1f%% §7→ §f%.1f%%§7)",
                        healthRegenIncrease, currentHealthRegen, futureHealthRegen))
        );
    }
}
