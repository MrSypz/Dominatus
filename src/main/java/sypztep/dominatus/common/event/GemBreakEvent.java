package sypztep.dominatus.common.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.client.payload.GemBreakPayloadS2C;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.init.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GemBreakEvent {
    private static final float BREAK_CHANCE = 0.50f; // 10% chance per gem
    private static final Random RANDOM = new Random();

    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity player && isMonsterAttack(damageSource)) {
                GemDataComponent gemData = GemDataComponent.get(player);
                Map<Identifier, GemComponent> presets = gemData.getGemPresets();

                if (!presets.isEmpty()) {
                    List<Text> brokenGemMessages = new ArrayList<>();

                    // Check each equipped gem independently
                    List<Identifier> equippedSlots = new ArrayList<>(presets.keySet());
                    equippedSlots.removeIf(slot -> presets.get(slot) == null); // Filter out empty slots

                    for (Identifier slot : equippedSlots) {
                        if (RANDOM.nextFloat() < BREAK_CHANCE) {
                            GemComponent brokenGem = presets.get(slot);
                            gemData.deleteGemFromPreset(slot);

                            // Create message for this gem
                            Text breakMessage = Text.literal(" - ")
                                    .append(Text.translatable(String.valueOf(brokenGem.type())))
                                    .formatted(Formatting.RED);
                            brokenGemMessages.add(breakMessage);
                        }
                    }

                    if (!brokenGemMessages.isEmpty()) {
                        Text fullMessage = Text.literal("The following gems have shattered upon death:")
                                .formatted(Formatting.RED);
                        for (Text gemMessage : brokenGemMessages) {
                            fullMessage = fullMessage.copy().append("\n").append(gemMessage);
                        }

                        // Send the packet to the client for death screen display
                        GemBreakPayloadS2C.send(player, brokenGemMessages);
                    }
                }
            }
        });
    }

    // Check if the death was caused by a monster attack
    private static boolean isMonsterAttack(DamageSource damageSource) {
        return damageSource.getAttacker() != null &&
                (damageSource.getAttacker() instanceof MobEntity || damageSource.isOf(DamageTypes.MOB_ATTACK)) &&
                !damageSource.isOf(DamageTypes.PLAYER_ATTACK); // Exclude player attacks
    }
}