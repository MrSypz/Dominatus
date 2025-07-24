package sypztep.dominatus.common.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.living.PlayerSkillComponent;
import sypztep.dominatus.common.init.ModEntityComponents;

import java.util.Optional;

public record AssignSkillPayloadC2S(int slotIndex, Optional<Identifier> skillId) implements CustomPayload {
    public static final Id<AssignSkillPayloadC2S> ID = new Id<>(Dominatus.id("assign_skill"));
    public static final PacketCodec<PacketByteBuf, AssignSkillPayloadC2S> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, AssignSkillPayloadC2S::slotIndex,
            PacketCodecs.optional(Identifier.PACKET_CODEC), AssignSkillPayloadC2S::skillId,
            AssignSkillPayloadC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send(int slotIndex, Identifier skillId) {
        ClientPlayNetworking.send(new AssignSkillPayloadC2S(slotIndex, Optional.ofNullable(skillId)));
    }

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<AssignSkillPayloadC2S> {
        @Override
        public void receive(AssignSkillPayloadC2S payload, ServerPlayNetworking.Context context) {
            ServerPlayerEntity player = context.player();
            PlayerSkillComponent skillComp = ModEntityComponents.PLAYERSKILL.get(player);

            skillComp.assignSkillToSlot(payload.slotIndex(), payload.skillId().orElse(null));
        }
    }
}