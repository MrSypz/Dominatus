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
import sypztep.dominatus.common.system.stat.elements.core.LuckStat;

import java.util.List;

public class PlayerLuckStat extends LuckStat implements PlayerStatBehavior, StatUI {

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
        return "Luck";
    }

    @Override
    public List<Text> getEffectDescription() {
        double critChanceBonus = calculateCritChanceBonus() * 100;
        double magicDamageBonus = calculateMagicDamageBonus() * 100;
        double attackSpeedBonus = calculateAttackSpeedBonus() * 100;
        int accuracyBonus = calculateAccuracyBonus();
        int evasionBonus = calculateEvasionBonus();

        return List.of(
                Text.literal(String.format("§6LUK: §f%d §7(Points spent: %d)", currentValue, totalPointsSpent)),
                Text.literal(String.format("§7Crit Chance: §f+%.2f%%", critChanceBonus)),
                Text.literal(String.format("§7Magic Damage: §f+%.1f%%", magicDamageBonus)),
                Text.literal(String.format("§7Attack Speed: §f+%.1f%%", attackSpeedBonus)),
                Text.literal(String.format("§7Bonus Accuracy: §f+%d", accuracyBonus)),
                Text.literal(String.format("§7Bonus Evasion: §f+%d", evasionBonus))
        );
    }

    @Override
    public List<Text> getEffectDescriptionWithCost(int additionalPoints) {
        int futureValue = currentValue + additionalPoints;

        double currentCritChance = calculateCritChanceBonus() * 100;
        double futureCritChance = (futureValue - baseValue) * CRIT_CHANCE_SCALING * 100;
        double critChanceIncrease = futureCritChance - currentCritChance;

        double currentMagicDamage = calculateMagicDamageBonus() * 100;
        double futureMagicDamage = (futureValue - baseValue) * MAGIC_DAMAGE_SCALING * 100;
        double magicDamageIncrease = futureMagicDamage - currentMagicDamage;

        double currentAttackSpeed = calculateAttackSpeedBonus() * 100;
        double futureAttackSpeed = (futureValue - baseValue) * ATTACK_SPEED_SCALING * 100;
        double attackSpeedIncrease = futureAttackSpeed - currentAttackSpeed;

        int currentAccuracy = calculateAccuracyBonus();
        int futureAccuracy = Math.max(0, (futureValue - baseValue) / 3);
        int accuracyIncrease = futureAccuracy - currentAccuracy;

        int currentEvasion = calculateEvasionBonus();
        int futureEvasion = Math.max(0, (futureValue - baseValue) / 5);
        int evasionIncrease = futureEvasion - currentEvasion;

        return List.of(
                Text.literal(String.format("§6LUK: §f%d §7→ §f%d", currentValue, futureValue)),
                Text.literal(String.format("§7Cost: §f%d §7benefit points", calculateCost(additionalPoints))),
                Text.literal(""),
                Text.literal("§6Primary Effects:").formatted(Formatting.GOLD),
                Text.literal(String.format("  §7Crit Chance: §f+%.2f%% §7(§f%.2f%% §7→ §f%.2f%%§7)",
                        critChanceIncrease, currentCritChance, futureCritChance)),
                Text.literal(""),
                Text.literal("§6Secondary Effects:").formatted(Formatting.GOLD),
                Text.literal(String.format("  §7Magic Damage: §f+%.1f%% §7(§f%.1f%% §7→ §f%.1f%%§7)",
                        magicDamageIncrease, currentMagicDamage, futureMagicDamage)),
                Text.literal(String.format("  §7Attack Speed: §f+%.1f%% §7(§f%.1f%% §7→ §f%.1f%%§7)",
                        attackSpeedIncrease, currentAttackSpeed, futureAttackSpeed)),
                Text.literal(String.format("  §7Bonus Accuracy: §f+%d §7(§f%d §7→ §f%d§7)",
                        accuracyIncrease, currentAccuracy, futureAccuracy)),
                Text.literal(String.format("  §7Bonus Evasion: §f+%d §7(§f%d §7→ §f%d§7)",
                        evasionIncrease, currentEvasion, futureEvasion))
        );
    }
}
