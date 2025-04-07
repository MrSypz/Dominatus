package sypztep.dominatus.client.screen.panel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.tyrannus.client.screen.panel.ScrollablePanel;

import java.util.ArrayList;
import java.util.List;

public class GemInventoryPanel extends ScrollablePanel {
    private static final int ITEM_HEIGHT = 24;
    private static final int SHADOW_COLOR = 0xFF555555;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int HOVER_COLOR = 0x30FFFFFF; // Light white overlay for hover

    private final PlayerEntity player;
    private final GemPresetPanel presetPanel; // Reference to preset panel for slot selection
    private int hoveredIndex = -1; // Track hovered gem for tooltip

    public GemInventoryPanel(int x, int y, int width, int height, PlayerEntity player, GemPresetPanel presetPanel) {
        super(x, y, width, height, Text.translatable("panel.dominatus.gem_inventory"));
        this.player = player;
        this.presetPanel = presetPanel;
        updateContentHeight();
    }

    @Override
    protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {
        List<GemComponent> inventory = GemDataComponent.get(player).getGemInventory();
        int yOffset = -(int) scrollAmount;
        int contentX = getContentX();
        int contentWidth = getContentWidth() - scrollbarWidth;

        // Determine hovered gem
        hoveredIndex = -1;
        if (isMouseOver(mouseX, mouseY)) {
            int relativeY = (int) (mouseY - getContentY() + scrollAmount);
            if (relativeY >= 0) {
                hoveredIndex = relativeY / ITEM_HEIGHT;
                if (hoveredIndex >= inventory.size()) {
                    hoveredIndex = -1;
                }
            }
        }

        for (int i = 0; i < inventory.size(); i++) {
            GemComponent gem = inventory.get(i);
            int itemY = getContentY() + yOffset + (i * ITEM_HEIGHT);

            if (itemY + ITEM_HEIGHT >= getContentY() && itemY <= getContentY() + getContentHeight()) {
                // Background with hover effect
                int bgColor = (i == hoveredIndex) ? 0xFF3A3A3A : 0xFF2A2A2A;
                context.fill(contentX, itemY, contentX + contentWidth, itemY + ITEM_HEIGHT, bgColor);
                if (i == hoveredIndex) {
                    context.fill(contentX, itemY, contentX + contentWidth, itemY + ITEM_HEIGHT, HOVER_COLOR);
                }

                // Gem name with shadow
                Text gemText = Text.translatable("item.dominatus.gem." + gem.type().getPath());
                context.drawText(textRenderer, gemText, contentX + 5 + 1, itemY + 6 + 1, SHADOW_COLOR, false);
                context.drawText(textRenderer, gemText, contentX + 5, itemY + 6, TEXT_COLOR, false);
            }
        }

        // Render tooltip for hovered gem
        if (hoveredIndex >= 0 && hoveredIndex < inventory.size()) {
            renderGemTooltip(context, inventory.get(hoveredIndex), mouseX, mouseY);
        }
    }

    private void renderGemTooltip(DrawContext context, GemComponent gem, int mouseX, int mouseY) {
        List<Text> tooltip = new ArrayList<>();
        tooltip.add(Text.translatable("item.dominatus.gem." + gem.type().getPath()).formatted(Formatting.YELLOW));
        tooltip.add(Text.empty());
        tooltip.add(Text.literal("【 ").formatted(Formatting.GRAY)
                .append(Text.translatable("item.dominatus.gem.effects"))
                .append(" 】").formatted(Formatting.GRAY));

        gem.attributeModifiers().forEach((attributeId, modifier) -> {
            EntityAttribute attribute = Registries.ATTRIBUTE.get(attributeId);
            if (attribute != null) {
                String operation = switch (modifier.operation()) {
                    case ADD_VALUE -> "+";
                    case ADD_MULTIPLIED_BASE -> "×";
                    case ADD_MULTIPLIED_TOTAL -> "%";
                };
                Text effectText = Text.literal("▣ ").formatted(Formatting.AQUA)
                        .append(Text.literal(operation + String.format("%.1f", modifier.value())).formatted(Formatting.GREEN))
                        .append(" ")
                        .append(Text.translatable(attribute.getTranslationKey()).formatted(Formatting.WHITE));
                tooltip.add(effectText);
            }
        });

        context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isScrollbarClicked(mouseX, mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (isMouseOver(mouseX, mouseY) && button == 0) { // Left-click
            int relativeY = (int) (mouseY - getContentY() + scrollAmount);
            if (relativeY >= 0) {
                int index = relativeY / ITEM_HEIGHT;
                List<GemComponent> inventory = GemDataComponent.get(player).getGemInventory();
                if (index >= 0 && index < inventory.size()) {
                    equipGem(index);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void equipGem(int index) {
        GemDataComponent gemData = GemDataComponent.get(player);
        List<GemComponent> inventory = gemData.getGemInventory();
        if (index >= 0 && index < inventory.size()) {
            GemComponent gem = inventory.get(index);
            Identifier selectedSlot = presetPanel.getSelectedSlot();
            if (selectedSlot != null && gemData.canAddGemToPresets(gem) && gemData.setPresetSlot(selectedSlot, gem)) {
                gemData.removeFromInventory(index);
                presetPanel.clearSelectedSlot(); // Deselect after equipping
                updateContentHeight();
                MinecraftClient.getInstance().player.sendMessage(
                        Text.literal("Equipped " + gem.type()).formatted(Formatting.GREEN),
                        false
                );
            } else if (selectedSlot == null) {
                MinecraftClient.getInstance().player.sendMessage(
                        Text.literal("No preset slot selected.").formatted(Formatting.RED),
                        false
                );
            } else if (!gemData.canAddGemToPresets(gem)) {
                MinecraftClient.getInstance().player.sendMessage(
                        Text.literal("Cannot equip " + gem.type() + ": preset limit reached.").formatted(Formatting.RED),
                        false
                );
            }
        }
    }

    private void updateContentHeight() {
        int inventorySize = GemDataComponent.get(player).getGemInventory().size();
        setContentHeight(inventorySize * ITEM_HEIGHT);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateContentHeight();
        super.render(context, mouseX, mouseY, delta);
    }
}