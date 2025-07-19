package sypztep.dominatus.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for finding and working with players in the world
 */
public class PlayerEntityUtils {

    /**
     * Get all players in a specified chunk radius around a given chunk position
     *
     * @param world The server world
     * @param chunkX Center chunk X coordinate
     * @param chunkZ Center chunk Z coordinate
     * @param radius Radius in chunks (1 = 3x3 area, 2 = 5x5 area, etc.)
     * @return List of players found in the area
     */
    public static List<PlayerEntity> getPlayersInChunk(ServerWorld world, int chunkX, int chunkZ, int radius) {
        List<PlayerEntity> players = new ArrayList<>();

        // Search in a radius around the given chunk
        for (int x = chunkX - radius; x <= chunkX + radius; x++) {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                ChunkPos chunkPos = new ChunkPos(x, z);

                // Check if chunk is loaded
                if (world.isChunkLoaded(x, z)) {
                    WorldChunk chunk = world.getChunk(x, z);

                    // Get all players in this chunk
                    for (ServerPlayerEntity player : world.getPlayers()) {
                        ChunkPos playerChunk = player.getChunkPos();
                        if (playerChunk.equals(chunkPos)) {
                            players.add(player);
                        }
                    }
                }
            }
        }

        return players;
    }

    /**
     * Get all players within a block radius of a position
     *
     * @param world The server world
     * @param x Center X coordinate
     * @param y Center Y coordinate
     * @param z Center Z coordinate
     * @param radius Radius in blocks
     * @return List of players found in the area
     */
    public static List<PlayerEntity> getPlayersInRadius(ServerWorld world, double x, double y, double z, double radius) {
        List<PlayerEntity> players = new ArrayList<>();
        double radiusSquared = radius * radius;

        for (ServerPlayerEntity player : world.getPlayers()) {
            double distanceSquared = player.squaredDistanceTo(x, y, z);
            if (distanceSquared <= radiusSquared) {
                players.add(player);
            }
        }

        return players;
    }

    /**
     * Get the closest player to a specific position
     *
     * @param world The server world
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param maxDistance Maximum search distance (or -1 for unlimited)
     * @return Closest player or null if none found
     */
    public static PlayerEntity getClosestPlayer(ServerWorld world, double x, double y, double z, double maxDistance) {
        PlayerEntity closestPlayer = null;
        double closestDistance = maxDistance > 0 ? maxDistance * maxDistance : Double.MAX_VALUE;

        for (ServerPlayerEntity player : world.getPlayers()) {
            double distance = player.squaredDistanceTo(x, y, z);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPlayer = player;
            }
        }

        return closestPlayer;
    }

    /**
     * Calculate average level of players in a list
     *
     * @param players List of players
     * @return Average level, or 0 if no players
     */
    public static int calculateAverageLevel(List<PlayerEntity> players) {
        if (players.isEmpty()) {
            return 0;
        }

        int totalLevel = 0;
        int validPlayers = 0;

        for (PlayerEntity player : players) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                // You can adapt this to use your level component
                totalLevel += serverPlayer.experienceLevel; // Fallback to vanilla level
                validPlayers++;
            }
        }

        return validPlayers > 0 ? totalLevel / validPlayers : 0;
    }

    /**
     * Get all players in the same dimension as the given world
     *
     * @param world The server world
     * @return List of all players in this dimension
     */
    public static List<PlayerEntity> getPlayersInDimension(ServerWorld world) {
        List<PlayerEntity> players = new ArrayList<>();

        for (ServerPlayerEntity player : world.getPlayers()) {
            players.add(player);
        }

        return players;
    }

    /**
     * Check if any players are nearby a position
     *
     * @param world The server world
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param radius Search radius
     * @return True if at least one player is within radius
     */
    public static boolean hasPlayersNearby(ServerWorld world, double x, double y, double z, double radius) {
        return !getPlayersInRadius(world, x, y, z, radius).isEmpty();
    }

    /**
     * Get player count in chunk area
     *
     * @param world The server world
     * @param chunkX Center chunk X
     * @param chunkZ Center chunk Z
     * @param radius Chunk radius
     * @return Number of players in the area
     */
    public static int getPlayerCountInChunk(ServerWorld world, int chunkX, int chunkZ, int radius) {
        return getPlayersInChunk(world, chunkX, chunkZ, radius).size();
    }
}