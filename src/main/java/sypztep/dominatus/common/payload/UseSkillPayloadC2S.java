package sypztep.dominatus.common.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.living.PlayerSkillComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.skill.active.SkillResult;

public record UseSkillPayloadC2S(int slotIndex) implements CustomPayload {
    public static final Id<UseSkillPayloadC2S> ID = new Id<>(Dominatus.id("use_skill"));
    public static final PacketCodec<PacketByteBuf, UseSkillPayloadC2S> CODEC =
            PacketCodec.tuple(PacketCodecs.VAR_INT, UseSkillPayloadC2S::slotIndex, UseSkillPayloadC2S::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send(int slotIndex) {
        ClientPlayNetworking.send(new UseSkillPayloadC2S(slotIndex));
    }

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<UseSkillPayloadC2S> {
        @Override
        public void receive(UseSkillPayloadC2S payload, ServerPlayNetworking.Context context) {
            ServerPlayerEntity player = context.player();
            PlayerSkillComponent skillComp = ModEntityComponents.PLAYERSKILL.get(player);

            SkillResult result = skillComp.useSkillInSlot(payload.slotIndex());

            // Send feedback to player
            if (result.success()) {
                player.sendMessage(Text.literal("✓ " + result.message()).formatted(net.minecraft.util.Formatting.GREEN), true);
            } else {
                player.sendMessage(Text.literal("✗ " + result.message()).formatted(net.minecraft.util.Formatting.RED), true);
            }
        }
    }
}
