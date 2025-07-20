package sypztep.dominatus.common.system.stat;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Interface for player-specific stat behaviors (point tracking, cost calculation)
 * Extends StatUI to provide display capabilities
 */
public interface PlayerStatBehavior extends StatUI , StatEffect {

    int getTotalPointsSpent();

    /**
     * Calculate cost per point at current level (Ragnarok-style scaling)
     */
    int getPointCost();

    /**
     * Calculate total cost to increase by specified points
     */
    int calculateCost(int points);

    /**
     * Increase stat by spending benefit points (player only)
     */
    boolean increaseWithPoints(ServerPlayerEntity player, int points);

    /**
     * Reset stat to base value and refund spent points (player only)
     */
    void resetWithRefund(ServerPlayerEntity player);

    /**
     * Core NBT key
     */
    void writeToNbt(NbtCompound tag);
    void readFromNbt(NbtCompound tag);
    /**
     * Enhanced NBT methods for point tracking
     */
    void writePlayerDataToNbt(NbtCompound tag);
    void readPlayerDataFromNbt(NbtCompound tag);

    /**
     * Get effect description showing cost and changes
     */
    List<Text> getEffectDescriptionWithCost(int additionalPoints);
}