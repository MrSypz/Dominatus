package sypztep.dominatus.client.screen.panel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.tyrannus.client.screen.panel.Button;
import sypztep.tyrannus.client.screen.panel.UIPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GemPresetPanel extends UIPanel {
    private static final int SLOT_WIDTH = 60;
    private static final int SLOT_HEIGHT = 60;
    private static final int GRID_COLS = 4;
    private static final int GRID_ROWS = 2;
    private static final int BUTTON_WIDTH = 50;
    private static final int SHADOW_COLOR = 0xFF555555;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int HOVER_COLOR = 0x30FFFFFF;

    private final PlayerEntity player;
    private final List<Button> unequipButtons = new ArrayList<>();
    private Identifier selectedSlot = null; // Track the selected slot
    private int hoveredSlotIndex = -1; // Track hovered slot for tooltip

    public GemPresetPanel(int x, int y, int width, int height, PlayerEntity player) {
        super(x, y, width, height, Text.translatable("panel.dominatus.gem_presets"));
        this.player = player;
        initButtons();
    }

    private void initButtons() {
        unequipButtons.clear();
        drawables.clear();
        elements.clear();

        Map<Identifier, GemComponent> presets = GemDataComponent.get(player).getGemPresets();
        int index = 0;

        for (Map.Entry<Identifier, GemComponent> entry : presets.entrySet()) {
            Identifier slot = entry.getKey();
            GemComponent gem = entry.getValue();
            if (gem != null) {
                int col = index % GRID_COLS;
                int row = index / GRID_COLS;
                int slotX = getContentX() + col * (SLOT_WIDTH + 5);
                int slotY = getContentY() + row * (SLOT_HEIGHT + 5);
                int buttonX = slotX + (SLOT_WIDTH - BUTTON_WIDTH) / 2;
                int buttonY = slotY + SLOT_HEIGHT - 20;

                Button unequipButton = new Button(buttonX, buttonY, BUTTON_WIDTH, 16,
                        Text.literal("Unequip"), btn -> unequipGem(slot))
                        .setGlowIntensity(1.0f)
                        .setBounceIntensity(1.0f)
                        .setRoundedCorners(true, 4)
                        .setShadowIntensity(0.5f)
                        .setPlaySounds(true, true);
                unequipButtons.add(unequipButton);
                drawables.add((Drawable) unequipButton); // Add to drawables instead of addChild
            }
            index++;
        }
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        Map<Identifier, GemComponent> presets = GemDataComponent.get(player).getGemPresets();
        int index = 0;

        // Determine hovered slot
        hoveredSlotIndex = -1;
        if (isMouseOver(mouseX, mouseY)) {
            for (int i = 0; i < GRID_COLS * GRID_ROWS; i++) {
                int col = i % GRID_COLS;
                int row = i / GRID_COLS;
                int slotX = getContentX() + col * (SLOT_WIDTH + 5);
                int slotY = getContentY() + row * (SLOT_HEIGHT + 5);
                if (mouseX >= slotX && mouseX < slotX + SLOT_WIDTH &&
                        mouseY >= slotY && mouseY < slotY + SLOT_HEIGHT) {
                    hoveredSlotIndex = i;
                    break;
                }
            }
        }

        int unequipButtonIndex = 0;
        for (Map.Entry<Identifier, GemComponent> entry : presets.entrySet()) {
            Identifier slot = entry.getKey();
            GemComponent gem = entry.getValue();
            int col = index % GRID_COLS;
            int row = index / GRID_COLS;
            int slotX = getContentX() + col * (SLOT_WIDTH + 5);
            int slotY = getContentY() + row * (SLOT_HEIGHT + 5);

            // Background with hover/selection effect
            int bgColor = (index == hoveredSlotIndex) ? 0xFF3A3A3A : 0xFF2A2A2A;
            context.fill(slotX, slotY, slotX + SLOT_WIDTH, slotY + SLOT_HEIGHT, bgColor);
            if (index == hoveredSlotIndex) {
                context.fill(slotX, slotY, slotX + SLOT_WIDTH, slotY + SLOT_HEIGHT, HOVER_COLOR);
            }
            if (selectedSlot != null && selectedSlot.equals(slot)) {
                context.fill(slotX, slotY, slotX + 3, slotY + SLOT_HEIGHT, 0xFF4488FF); // Selection border
            }

            // Slot content
            Text slotText = gem != null ? Text.translatable("item.dominatus.gem." + gem.type().getPath()) : Text.literal("Empty");
            int textWidth = textRenderer.getWidth(slotText);
            int textX = slotX + (SLOT_WIDTH - textWidth) / 2;
            int textY = slotY + 10;
            context.drawText(textRenderer, slotText, textX + 1, textY + 1, SHADOW_COLOR, false);
            context.drawText(textRenderer, slotText, textX, textY, TEXT_COLOR, false);

            // Update unequip button state
            if (gem != null && unequipButtonIndex < unequipButtons.size()) {
                Button button = unequipButtons.get(unequipButtonIndex);
                button.setEnabled(!GemDataComponent.isInventoryFull(player));
                button.setX(slotX + (SLOT_WIDTH - BUTTON_WIDTH) / 2);
                button.setY(slotY + SLOT_HEIGHT - 20);
                unequipButtonIndex++;
            }

            index++;
        }

        // Render tooltip for hovered slot
        if (hoveredSlotIndex >= 0) {
            List<Map.Entry<Identifier, GemComponent>> presetList = new ArrayList<>(presets.entrySet());
            GemComponent gem = presetList.get(hoveredSlotIndex).getValue();
            if (gem != null) {
                renderGemTooltip(context, gem, mouseX, mouseY);
            }
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

    private void unequipGem(Identifier slot) {
        GemDataComponent gemData = GemDataComponent.get(player);
        Optional<GemComponent> gem = gemData.getPresetSlot(slot);
        if (gem.isPresent() && gemData.addToInventory(gem.get())) {
            gemData.setPresetSlot(slot, null);
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Unequipped " + gem.get().type()), false);
            initButtons(); // Refresh buttons
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && button == 0) {
            for (int i = 0; i < GRID_COLS * GRID_ROWS; i++) {
                int col = i % GRID_COLS;
                int row = i / GRID_COLS;
                int slotX = getContentX() + col * (SLOT_WIDTH + 5);
                int slotY = getContentY() + row * (SLOT_HEIGHT + 5);
                if (mouseX >= slotX && mouseX < slotX + SLOT_WIDTH &&
                        mouseY >= slotY && mouseY < slotY + SLOT_HEIGHT) {
                    List<Map.Entry<Identifier, GemComponent>> presetList = new ArrayList<>(GemDataComponent.get(player).getGemPresets().entrySet());
                    selectedSlot = presetList.get(i).getKey();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int presetCount = (int) GemDataComponent.get(player).getGemPresets().values().stream().filter(g -> g != null).count();
        if (unequipButtons.size() != presetCount) {
            initButtons(); // Rebuild if preset count changes
        }
        super.render(context, mouseX, mouseY, delta);
    }

    public Identifier getSelectedSlot() {
        return selectedSlot;
    }

    public void clearSelectedSlot() {
        selectedSlot = null;
    }
}