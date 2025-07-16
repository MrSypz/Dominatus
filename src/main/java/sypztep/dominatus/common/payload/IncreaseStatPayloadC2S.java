package sypztep.dominatus.common.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.stat.PlayerStatBehavior;
import sypztep.dominatus.common.system.stat.PlayerStatManager;

public record IncreaseStatPayloadC2S(String statName, int points) implements CustomPayload {
    public static final Id<IncreaseStatPayloadC2S> ID = new Id<>(Dominatus.id("increase_stat"));
    public static final PacketCodec<PacketByteBuf, IncreaseStatPayloadC2S> CODEC = PacketCodec.tuple(PacketCodecs.STRING, IncreaseStatPayloadC2S::statName, PacketCodecs.VAR_INT, IncreaseStatPayloadC2S::points, IncreaseStatPayloadC2S::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send(String statName, int points) {
        ClientPlayNetworking.send(new IncreaseStatPayloadC2S(statName, points));
    }

    public static void sendStrength(int points) {
        send("strength", points);
    }

    public static void sendAgility(int points) {
        send("agility", points);
    }

    public static void sendVitality(int points) {
        send("vitality", points);
    }

    public static void sendIntelligence(int points) {
        send("intelligence", points);
    }

    public static void sendDexterity(int points) {
        send("dexterity", points);
    }

    public static void sendLuck(int points) {
        send("luck", points);
    }

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<IncreaseStatPayloadC2S> {
        @Override
        public void receive(IncreaseStatPayloadC2S payload, ServerPlayNetworking.Context context) {
            ServerPlayerEntity player = context.player();

            // Validate inputs
            if (payload.points <= 0) {
                Dominatus.LOGGER.warn("Player {} tried to increase stat with invalid points: {}", player.getName().getString(), payload.points);
                return;
            }

            // Valid stat names
            String[] validStats = {"strength", "agility", "vitality", "intelligence", "dexterity", "luck"};
            boolean isValidStat = false;
            for (String validStat : validStats) {
                if (validStat.equals(payload.statName)) {
                    isValidStat = true;
                    break;
                }
            }

            if (!isValidStat) {
                Dominatus.LOGGER.warn("Player {} tried to increase invalid stat: {}", player.getName().getString(), payload.statName);
                return;
            }

            // Get the player's level component
            LivingLevelComponent levelComponent = ModEntityComponents.LIVINGLEVEL.get(player);

            // Ensure it's a player (should always be true for players, but safety check)
            if (!levelComponent.isPlayer()) {
                Dominatus.LOGGER.error("Received stat increase request for non-player entity: {}", player.getName().getString());
                return;
            }

            // Get the player stat manager
            PlayerStatManager statManager = levelComponent.getPlayerStatManager();
            if (statManager == null) {
                Dominatus.LOGGER.error("PlayerStatManager is null for player: {}", player.getName().getString());
                return;
            }

            // Get the specific stat
            PlayerStatBehavior stat = statManager.getStat(payload.statName);
            if (stat == null) {
                Dominatus.LOGGER.error("Stat '{}' not found for player: {}", payload.statName, player.getName().getString());
                return;
            }

            // Try to increase the stat
            boolean success = stat.increaseWithPoints(player, payload.points);

            if (success) {
                levelComponent.applyAllStatEffects();

                levelComponent.sync();

                Dominatus.LOGGER.debug("Player {} increased {} by {} points", player.getName().getString(), payload.statName, payload.points);
            } else {
                Dominatus.LOGGER.debug("Player {} failed to increase {} by {} points (insufficient benefits)", player.getName().getString(), payload.statName, payload.points);
            }
        }
    }
}