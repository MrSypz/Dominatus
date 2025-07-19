package sypztep.dominatus.common.system.stat.elements.player;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.LevelData;
import sypztep.dominatus.common.system.stat.PlayerStatBehavior;
import sypztep.dominatus.common.system.stat.Stat;
import sypztep.dominatus.common.system.stat.StatUI;

import java.util.List;

public class BasePlayerStat implements PlayerStatBehavior, StatUI {
    private final Stat coreStat;
    private final String statName;
    private int totalPointsSpent = 0;

    public BasePlayerStat(Stat coreStat, String statName) {
        this.coreStat = coreStat;
        this.statName = statName;
    }

    // ===== DELEGATE TO CORE STAT (ZERO DUPLICATION) =====
    public void applyPrimaryEffect(LivingEntity entity) { coreStat.applyPrimaryEffect(entity); }
    public void applySecondaryEffect(LivingEntity entity) { coreStat.applySecondaryEffect(entity); }
    public int getValue() { return coreStat.getValue(); }
    public void setValue(int value) { coreStat.setValue(value); }
    public int getBaseValue() { return coreStat.getBaseValue(); }
    public void readFromNbt(NbtCompound tag) {
        coreStat.readFromNbt(tag);
        totalPointsSpent = tag.getInt("TotalPointsSpent");
    }
    public void writeToNbt(NbtCompound tag) {
        coreStat.writeToNbt(tag);
        tag.putInt("TotalPointsSpent", totalPointsSpent);
    }

    // ===== COMMON PLAYER BEHAVIOR (WRITTEN ONCE) =====
    @Override public int getTotalPointsSpent() { return totalPointsSpent; }
    @Override public int getPointCost() { return 1 + (getValue() / 10); }

    @Override public int calculateCost(int points) {
        int totalCost = 0, tempValue = getValue();
        for (int i = 0; i < points; i++) {
            totalCost += 1 + (tempValue / 10);
            tempValue++;
        }
        return totalCost;
    }

    @Override public boolean increaseWithPoints(ServerPlayerEntity player, int points) {
        if (points <= 0) return false;
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = levelComponent.getLevelData();
        int cost = calculateCost(points);
        if (!levelData.spendBenefits(cost)) return false;
        setValue(getValue() + points);
        totalPointsSpent += cost;
        applyPrimaryEffect(player);
        applySecondaryEffect(player);
        return true;
    }

    @Override public void resetWithRefund(ServerPlayerEntity player) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        levelComponent.getLevelData().addBenefits(totalPointsSpent);
        setValue(getBaseValue());
        totalPointsSpent = 0;
        applyPrimaryEffect(player);
        applySecondaryEffect(player);
    }

    @Override public void writePlayerDataToNbt(NbtCompound tag) { writeToNbt(tag); }
    @Override public void readPlayerDataFromNbt(NbtCompound tag) { readFromNbt(tag); }

    // ===== BASIC UI =====
    @Override public String getStatName() { return statName; }
    @Override public List<Text> getEffectDescription() {
        return List.of(Text.literal(String.format("§6%s: §f%d §7(Points spent: %d)",
                statName.toUpperCase().substring(0, 3), getValue(), totalPointsSpent)));
    }
    @Override public List<Text> getEffectDescriptionWithCost(int points) {
        return List.of(Text.literal(String.format("§6%s: §f%d §7→ §f%d §7(Cost: %d)",
                statName.toUpperCase().substring(0, 3), getValue(), getValue() + points, calculateCost(points))));
    }
}