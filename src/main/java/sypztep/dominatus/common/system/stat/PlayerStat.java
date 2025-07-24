package sypztep.dominatus.common.system.stat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.client.payload.SendToastPayloadS2C;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.level.core.LevelData;

import java.util.List;

public abstract class PlayerStat<T extends Stat> implements PlayerStatBehavior {
    private int totalPointsSpent = 0;
    protected final T coreStat;
    private final String statDisplayName;

    public PlayerStat(T coreStat, String statDisplayName) {
        this.coreStat = coreStat;
        this.statDisplayName = statDisplayName;
    }

    // Delegate core stat methods
    public int getValue() { return coreStat.getValue(); }
    public int getBaseValue() { return coreStat.getBaseValue(); }
    public void setValue(int value) { coreStat.setValue(value); }

    // Delegate effect application
    public void applyPrimaryEffect(LivingEntity entity) { coreStat.applyPrimaryEffect(entity); }
    public void applySecondaryEffect(LivingEntity entity) { coreStat.applySecondaryEffect(entity); }

    // ====================
    // STAT UI IMPLEMENTATION
    // ====================
    @Override
    public String getStatName() { return statDisplayName; }


    @Override
    public List<Text> getEffectDescriptionWithCost(int additionalPoints) {
        return List.of(Text.literal(String.format("§6%s: §f%d §7→ §f%d §7(Cost: %d)",
                statDisplayName.toUpperCase().substring(0, 3), getValue(), getValue() + additionalPoints, calculateCost(additionalPoints))));
    }

    // ====================
    // PLAYER STAT BEHAVIOR (same implementation as before)
    // ====================
    @Override
    public int getTotalPointsSpent() { return totalPointsSpent; }

    @Override
    public int getPointCost() { return 1 + (getValue() / 10); }

    @Override
    public int calculateCost(int points) {
        int totalCost = 0;
        int tempValue = getValue();
        for (int i = 0; i < points; i++) {
            totalCost += 1 + (tempValue / 10);
            tempValue++;
        }
        return totalCost;
    }

    @Override
    public boolean increaseWithPoints(ServerPlayerEntity player, int points) {
        if (points <= 0) return false;

        // Check if stat is already maxed
        if (coreStat.isMaxed()) {
            SendToastPayloadS2C.sendError(player, String.format("%s is already at maximum level (%d)!",
                    statDisplayName, Stat.MAX_STAT_VALUE));
            return false;
        }

        // Check if increasing would exceed the cap
        int currentValue = getValue();
        int maxIncrease = Stat.MAX_STAT_VALUE - currentValue;
        if (points > maxIncrease) {
            if (maxIncrease > 0) {
                SendToastPayloadS2C.sendError(player, String.format("Can only increase %s by %d more points (max: %d)!",
                        statDisplayName, maxIncrease, Stat.MAX_STAT_VALUE));
            } else {
                SendToastPayloadS2C.sendError(player, String.format("%s is already at maximum level (%d)!",
                        statDisplayName, Stat.MAX_STAT_VALUE));
            }
            return false;
        }

        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = levelComponent.getLevelData();

        int cost = calculateCost(points);
        if (levelData.getAvailableBenefits() < cost || !levelData.spendBenefits(cost)) {
            return false;
        }

        setValue(getValue() + points);
        this.totalPointsSpent += cost;

        applyPrimaryEffect(player);
        applySecondaryEffect(player);

        // Check for new passive skill unlocks
        String statType = getStatTypeFromDisplayName(statDisplayName);
        levelComponent.checkPassiveUnlocks(statType, getValue());

        return true;
    }

    /**
     * Convert display name to stat type for passive skill system
     */
    private String getStatTypeFromDisplayName(String displayName) {
        return switch (displayName.toLowerCase()) {
            case "strength" -> "strength";
            case "agility" -> "agility";
            case "vitality" -> "vitality";
            case "intelligence" -> "intelligence";
            case "dexterity" -> "dexterity";
            case "luck" -> "luck";
            default -> displayName.toLowerCase();
        };
    }

    @Override
    public void resetWithRefund(ServerPlayerEntity player) {
        LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);
        LevelData levelData = levelComponent.getLevelData();

        levelData.addBenefits(this.totalPointsSpent);
        setValue(getBaseValue());
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
    public void writeToNbt(NbtCompound tag) {
        writePlayerDataToNbt(tag);
        coreStat.writeToNbt(tag);
    }
    @Override
    public void readFromNbt(NbtCompound tag) {
        readPlayerDataFromNbt(tag);
        coreStat.readFromNbt(tag);
    }
}