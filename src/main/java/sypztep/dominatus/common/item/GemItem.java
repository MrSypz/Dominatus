package sypztep.dominatus.common.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.util.gemsystem.GemManager;

import java.util.List;
import java.util.Optional;


public final class GemItem extends Item {
    public GemItem() {
        super(new Item.Settings().maxCount(1));
    }

    @Override
    public Text getName(ItemStack stack) {
        return GemComponent.fromStack(stack)
                .map(gem -> Text.translatable("item.dominatus.gem." + gem.type().getPath())
                        .formatted(Formatting.GOLD)) // Match the gold color from UI
                .orElseGet(() -> (net.minecraft.text.MutableText) super.getName(stack));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Optional<GemComponent> gemComponent = GemComponent.fromStack(stack);

        if (gemComponent.isPresent()) {
            GemComponent gem = gemComponent.get();

            // Add gem type/rarity if applicable
            tooltip.add(Text.empty());

            // Add inventory information if player is holding
            if (type.isAdvanced() && MinecraftClient.getInstance().player != null) {
                PlayerEntity player = MinecraftClient.getInstance().player;
                GemDataComponent gemData = GemDataComponent.get(player);

                // Show inventory count
                int inventoryCount = (int) gemData.getGemInventory().stream()
                        .filter(g -> g.type().equals(gem.type()))
                        .count();
                tooltip.add(Text.literal("In Inventory:")
                        .formatted(Formatting.GRAY).append(Text.literal(" " + inventoryCount).formatted(Formatting.GREEN)));

                // Show equipped count
                int equippedCount = (int) gemData.getGemPresets().values().stream()
                        .filter(g -> g != null && g.type().equals(gem.type()))
                        .count();
                int maxPresets = gem.maxPresets();
                Formatting countColor = equippedCount >= maxPresets ? Formatting.RED : Formatting.GREEN;
                tooltip.add(Text.literal("Equipped: ")
                        .formatted(Formatting.GRAY)
                        .append(Text.literal(equippedCount + "/" + maxPresets)
                                .formatted(countColor)));
            }

            tooltip.add(Text.literal("✧ ")
                    .formatted(Formatting.GOLD)
                    .append(Text.translatable("item.dominatus.gem.effects")
                            .formatted(Formatting.YELLOW))
                    .append(" ✧")
                    .formatted(Formatting.GOLD));

            gem.attributeModifiers().forEach((attributeId, modifier) -> {
                EntityAttribute attribute = Registries.ATTRIBUTE.get(attributeId);
                if (attribute != null) {
                    String operation = switch (modifier.operation()) {
                        case ADD_VALUE -> "➕";
                        case ADD_MULTIPLIED_BASE -> "✕";
                        case ADD_MULTIPLIED_TOTAL -> "⚝";
                    };

                    MutableText effectText = Text.literal(operation + " ")
                            .formatted(Formatting.AQUA)
                            .append(Text.literal(String.format("%.1f", modifier.value()))
                                    .formatted(Formatting.GREEN))
                            .append(" ")
                            .append(Text.translatable(attribute.getTranslationKey())
                                    .formatted(Formatting.WHITE));

                    tooltip.add(effectText);
                }
            });

            // Add usage instructions
            tooltip.add(Text.empty());
            tooltip.add(Text.literal("Right-click")
                    .formatted(Formatting.YELLOW)
                    .append(Text.literal(" to add to inventory")
                            .formatted(Formatting.GRAY)));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) {
            if (!GemDataComponent.isInventoryFull(user)) {
                user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F);
            } else {
                user.sendMessage(Text.literal("Gem Inventory is full!").formatted(Formatting.RED), true);
            }
            return TypedActionResult.success(stack, true);
        }

        if (!GemDataComponent.isInventoryFull(user)) {
            if (GemManager.consumeGem(user, stack)) {
                return TypedActionResult.success(stack, false);
            }
        }

        return TypedActionResult.fail(stack);
    }
}
