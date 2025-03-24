package sypztep.dominatus.client.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import sypztep.dominatus.common.util.refinesystem.RefinementManager;

public record AddRefineSoundPayloadS2C(int entityId, int soundId) implements CustomPayload {
    public static final Id<AddRefineSoundPayloadS2C> ID = CustomPayload.id("add_refinesound");
    public static final PacketCodec<PacketByteBuf, AddRefineSoundPayloadS2C> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            AddRefineSoundPayloadS2C::entityId,
            PacketCodecs.VAR_INT,
            AddRefineSoundPayloadS2C::soundId,
            AddRefineSoundPayloadS2C::new
    );

    public static void send(ServerPlayerEntity player, int entityId, RefinementManager.RefineSound sound) {
        ServerPlayNetworking.send(player, new AddRefineSoundPayloadS2C(entityId, sound.getId()));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<AddRefineSoundPayloadS2C> {
        @Override
        public void receive(AddRefineSoundPayloadS2C payload, ClientPlayNetworking.Context context) {
            Entity entity = context.player().getWorld().getEntityById(payload.entityId());
            if (entity != null) {
                RefinementManager.RefineSound sound = RefinementManager.RefineSound.byId(payload.soundId());
                entity.playSound(sound.getSound(), sound.getVolume(), sound.getPitch());
            }
        }
    }
}