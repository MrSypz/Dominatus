package sypztep.dominatus.client.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.toast.ToastManager;
import sypztep.dominatus.client.toast.ToastNotification;

public record SendToastPayloadS2C(String message, int toastTypeOrdinal) implements CustomPayload {
    public static final Id<SendToastPayloadS2C> ID = new Id<>(Dominatus.id("show_toast"));
    public static final PacketCodec<PacketByteBuf, SendToastPayloadS2C> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            SendToastPayloadS2C::message,
            PacketCodecs.VAR_INT,
            SendToastPayloadS2C::toastTypeOrdinal,
            SendToastPayloadS2C::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    // Static methods to send different types of toasts
    public static void sendExperience(ServerPlayerEntity player, long amount, String source) {
        String message = source != null ?
                String.format("§6+%s EXP §7(%s)", formatNumber(amount), source) :
                String.format("§6+%s EXP", formatNumber(amount));

        send(player, message, ToastNotification.ToastType.EXPERIENCE);
    }

    public static void sendLevelUp(ServerPlayerEntity player, int oldLevel, int newLevel) {
        String message = String.format("§l§bLEVEL UP!§r§7 %d → §b%d", oldLevel, newLevel);
        send(player, message, ToastNotification.ToastType.LEVEL_UP);
    }

    public static void sendDeathPenalty(ServerPlayerEntity player, long expLost, String killerName) {
        String formattedPenalty = formatNumber(expLost);
        String message = killerName != null ?
                String.format("§l§cDEATH PENALTY!§r§7 Lost %s experience from %s",
                        formattedPenalty, killerName) :
                String.format("§l§cDEATH PENALTY!§r§7 Lost %s experience", formattedPenalty);

        send(player, message, ToastNotification.ToastType.DEATH_PENALTY);
    }

    public static void sendStatIncrease(ServerPlayerEntity player, String statName, int points, int cost) {
        String message = String.format("§l§e%s Increased!§r§7 +%d points (Cost: %d benefits)",
                statName, points, cost);
        send(player, message, ToastNotification.ToastType.INFO);
    }

    public static void sendBenefitsGained(ServerPlayerEntity player, int amount, String reason) {
        String message = reason != null ?
                String.format("§l§dBenefit Points!§r§7 +%d from %s", amount, reason) :
                String.format("§l§dBenefit Points!§r§7 +%d points gained", amount);

        send(player, message, ToastNotification.ToastType.INFO);
    }

    public static void sendInfo(ServerPlayerEntity player, String message) {
        send(player, message, ToastNotification.ToastType.INFO);
    }

    public static void sendWarning(ServerPlayerEntity player, String message) {
        send(player, message, ToastNotification.ToastType.WARNING);
    }

    public static void sendError(ServerPlayerEntity player, String message) {
        send(player, message, ToastNotification.ToastType.ERROR);
    }

    // Generic send method
    private static void send(ServerPlayerEntity player, String message, ToastNotification.ToastType type) {
        ServerPlayNetworking.send(player, new SendToastPayloadS2C(message, type.ordinal()));
    }

    // Utility method to format numbers
    private static String formatNumber(long number) {
        if (number >= 1_000_000_000L) {
            return String.format("%.1fB", number / 1_000_000_000.0);
        } else if (number >= 1_000_000L) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000L) {
            return String.format("%.1fK", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }

    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<SendToastPayloadS2C> {
        @Override
        public void receive(SendToastPayloadS2C payload, ClientPlayNetworking.Context context) {
            ToastNotification.ToastType[] types = ToastNotification.ToastType.values();
            if (payload.toastTypeOrdinal < 0 || payload.toastTypeOrdinal >= types.length) {
                Dominatus.LOGGER.warn("Received invalid toast type ordinal: {}", payload.toastTypeOrdinal);
                return;
            }

            ToastNotification.ToastType type = types[payload.toastTypeOrdinal];
            Text message = Text.literal(payload.message);

            // Add toast to manager
            ToastManager.getInstance().addToast(new ToastNotification(message, type));
        }
    }
}