package sypztep.dominatus.common.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.util.gemsystem.GemManagerHelper;

public record GemActionPayloadC2S(Identifier action, Identifier slot, int inventoryIndex) implements CustomPayload {
    public static final CustomPayload.Id<GemActionPayloadC2S> ID = CustomPayload.id("gem_action");

    public static final PacketCodec<RegistryByteBuf, GemActionPayloadC2S> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, GemActionPayloadC2S::action,           // Codec for Identifier
            Identifier.PACKET_CODEC, GemActionPayloadC2S::slot,             // Codec for Identifier
            PacketCodecs.INTEGER, GemActionPayloadC2S::inventoryIndex,      // Codec for Integer
            GemActionPayloadC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    // Static method to send equip action
    public static void sendEquipGem(Identifier slot, int inventoryIndex) {
        ClientPlayNetworking.send(new GemActionPayloadC2S(Dominatus.id("equip_gem"), slot, inventoryIndex));
    }

    // Static method to send unequip action
    public static void sendUnequipGem(Identifier slot) {
        ClientPlayNetworking.send(new GemActionPayloadC2S(Dominatus.id("unequip_gem"), slot, -1));
    }
    public static void sendRemoveGem(int inventoryIndex) {
        ClientPlayNetworking.send(new GemActionPayloadC2S(Dominatus.id("remove_gem"), Dominatus.id("none"), inventoryIndex));
    }

    // Receiver class for server-side handling
    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<GemActionPayloadC2S> {
        @Override
        public void receive(GemActionPayloadC2S payload, ServerPlayNetworking.Context context) {
            if (context.player() != null) {

                GemDataComponent gemData = GemDataComponent.get(context.player());
                Identifier action = payload.action();
                Identifier slot = payload.slot();
                int inventoryIndex = payload.inventoryIndex();

                if (action.equals(Dominatus.id("equip_gem")) && inventoryIndex >= 0) {
                    if (inventoryIndex < gemData.getGemInventory().size()) {
                        GemComponent gem = gemData.getGemInventory().get(inventoryIndex);
                        if (gemData.canAddGemToPresets(gem) && gemData.isPresetSlotValid(slot)) {
                            gemData.setPresetSlot(slot, gem);
                            gemData.removeFromInventory(inventoryIndex);
                            GemManagerHelper.updateEntityStats(context.player());
                        }
                    }
                } else if (action.equals(Dominatus.id("unequip_gem"))) {
                    GemComponent gem = gemData.getPresetSlot(slot).orElse(null);
                    if (gem != null && !gemData.isInventoryFull()) {
                        gemData.addToInventory(gem);
                        gemData.setPresetSlot(slot, null);
                        GemManagerHelper.updateEntityStats(context.player());
                    }
                } else if (action.equals(Dominatus.id("remove_gem")) && inventoryIndex >= 0) {
                    if (inventoryIndex < gemData.getGemInventory().size()) gemData.removeFromInventory(inventoryIndex);
                }
                GemDataComponent.sync(context.player());
            }
        }
    }
}