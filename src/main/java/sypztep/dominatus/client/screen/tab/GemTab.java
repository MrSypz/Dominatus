package sypztep.dominatus.client.screen.tab;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.client.screen.panel.ContextMenuPanel;
import sypztep.dominatus.client.screen.panel.GemSlotPanel;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.payload.GemActionPayloadC2S;
import sypztep.dominatus.common.util.gemsystem.GemManagerHelper;
import sypztep.tyrannus.client.screen.panel.ScrollablePanel;
import sypztep.tyrannus.client.screen.tab.Tab;

import java.util.*;
public class GemTab extends Tab {
    private final GemDataComponent gemData;
    private InventoryPanel inventoryPanel;
    private PresetPanel presetPanel;

    public GemTab() {
        super("gems", Text.translatable("tab.dominatus.gems"));
        this.gemData = GemDataComponent.get(client.player);
    }

    @Override
    protected void initPanels() {
        int totalWidth = parentScreen.width - 20;
        int panelHeight = parentScreen.height - 100;
        int panelY = 65;

        int leftWidth = totalWidth / 2;
        int rightWidth = totalWidth - leftWidth - 5;

        int leftX = 10;
        int rightX = leftX + leftWidth + 5;

        inventoryPanel = new InventoryPanel(leftX, panelY, leftWidth, panelHeight,
                Text.translatable("panel.dominatus.gem_inventory"));
        addPanel(inventoryPanel);

        presetPanel = new PresetPanel(rightX, panelY, rightWidth, panelHeight,
                Text.translatable("panel.dominatus.gem_presets"));
        addPanel(presetPanel);
    }

    private class InventoryPanel extends ScrollablePanel {
        private final List<GemSlotPanel> gemSlots = new ArrayList<>();
        private final ContextMenuPanel contextMenu; // Add context menu field
        private int selectedGemIndex = -1; // Track which gem is right-clicked

        public InventoryPanel(int x, int y, int width, int height, Text title) {
            super(x, y, width, height, title);
            updateContentHeight();
            contextMenu = new ContextMenuPanel(0, 0); // Position will be set dynamically
        }

        private void updateContentHeight() {
            List<GemComponent> gemInventory = gemData.getGemInventory();
            int totalHeight = 70; // Header space
            totalHeight += gemInventory.size() * 60;
            setContentHeight(totalHeight);
        }

        public void updateSlotsState() {
            gemSlots.clear();
            if (contextMenu != null) {
                contextMenu.clearItems();
                selectedGemIndex = -1;
            }
        }

        @Override
        protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = getContentX();
            int y = getContentY() - (int) scrollAmount;
            int width = getContentWidth() - (enableScrollbar ? scrollbarWidth + scrollbarPadding + 10 : 5);

            int activeGems = (int) gemData.getGemPresets().values().stream().filter(Objects::nonNull).count();
            int maxGems = GemDataComponent.getMaxPresetSlots(client.player);
            boolean presetsAreFull = activeGems >= maxGems;

            String inventoryCount = String.format("Inventory (%d/%d)",
                    gemData.getGemInventory().size(),
                    GemDataComponent.getMaxInventorySize(client.player));
            int countColor = presetsAreFull ? 0xFFAAAAAA : 0xFFFFD700;
            context.drawTextWithShadow(textRenderer, inventoryCount,
                    x + (width - textRenderer.getWidth(inventoryCount)) / 2, y + 10, countColor);

            if (presetsAreFull) {
                String warningText = "⚠ All preset slots are full! Unequip a gem first.";
                for (String text : wrapText(warningText, width)) {
                    int textWidth = textRenderer.getWidth(text);
                    context.drawTextWithShadow(textRenderer, text, x + (width - textWidth) / 2, y + 26, 0xFFFF5555);
                    y += textRenderer.fontHeight;
                }
            }

            y += 40;

            List<GemComponent> gemInventory = gemData.getGemInventory();
            gemSlots.clear();

            for (int i = 0; i < gemInventory.size(); i++) {
                GemComponent gem = gemInventory.get(i);
                int slotY = y + (i * 60);

                int bgColor = (i & 1) == 0 ? 0x20000000 : 0x20FFFFFF;
                context.fill(x, slotY, x + width, slotY + 50, bgColor);

                boolean canEquipThisGem = !presetsAreFull && gemData.canAddGemToPresets(gem);
                Identifier gemTexture = GemManagerHelper.getGemTexture(gem);
                GemSlotPanel slot = new GemSlotPanel(x + 10, slotY + 5, 40, 40, gem, gemTexture,
                        canEquipThisGem ? slotPanel -> equipGem(gem) : null, gemData, false);

                if (!canEquipThisGem) {
                    slot.setEnabled(false);
                    slot.setGlowIntensity(0.0f);
                    slot.setBounceIntensity(0.0f);
                }

                slot.render(context, mouseX, mouseY, delta);
                gemSlots.add(slot);

                String gemName = gem.type().toString().split(":")[1];
                context.drawTextWithShadow(textRenderer,
                        Text.translatable("item.dominatus.gem." + gemName).getString(),
                        x + 60, slotY + 5, canEquipThisGem ? 0xFFFFD700 : 0xFFAAAAAA);

                int textY = slotY + 20;
                for (Map.Entry<Identifier, EntityAttributeModifier> entry : gem.attributeModifiers().entrySet()) {
                    EntityAttribute attribute = Registries.ATTRIBUTE.get(entry.getKey());
                    if (attribute != null) {
                        EntityAttributeModifier modifier = entry.getValue();
                        String operation = switch (modifier.operation()) {
                            case ADD_VALUE -> "➕";
                            case ADD_MULTIPLIED_BASE -> "✕";
                            case ADD_MULTIPLIED_TOTAL -> "⚝";
                        };
                        String effectText = operation + String.format(" %.1f ", modifier.value()) +
                                Text.translatable(attribute.getTranslationKey()).getString();
                        int attributeColor = canEquipThisGem ? 0xFF55FF55 : 0xFF559955;
                        context.drawTextWithShadow(textRenderer, effectText, x + 60, textY, attributeColor);
                        textY += textRenderer.fontHeight + 2;
                    }
                }

                int equippedCount = (int) gemData.getGemPresets().values().stream()
                        .filter(g -> g != null && g.type().equals(gem.type()))
                        .count();
                int maxPresets = gem.maxPresets();
                String presetText = String.format("Equipped: %d/%d", equippedCount, maxPresets);
                int presetColor;
                String statusText;

                if (equippedCount >= maxPresets) {
                    presetColor = 0xFFFF5555;
                    statusText = "✘ Maxed";
                } else if (presetsAreFull) {
                    presetColor = 0xFFFF9955;
                    statusText = "⚠ No Slots";
                } else {
                    presetColor = 0xFF55FF55;
                    statusText = "✔ Available";
                }

                context.drawTextWithShadow(textRenderer, presetText, x + 60, textY, presetColor);
                context.drawTextWithShadow(textRenderer, statusText, x + 60 + textRenderer.getWidth(presetText) + 5, textY, presetColor);
            }

            // Render context menu if active
            if (selectedGemIndex != -1 && contextMenu != null) {
                contextMenu.render(context, mouseX, mouseY, delta);
            }
        }

        private void equipGem(GemComponent gem) {
            if (gem != null && gemData.canAddGemToPresets(gem)) {
                Optional<Identifier> availableSlot = gemData.getAvailablePresetSlot();
                if (availableSlot.isPresent()) {
                    int inventoryIndex = gemData.getGemInventory().indexOf(gem);
                    GemActionPayloadC2S.sendEquipGem(availableSlot.get(), inventoryIndex);
                    gemData.setPresetSlot(availableSlot.get(), gem); // Ensure client-side update
                    updateContentHeight();
                    presetPanel.updateContentHeight();
                    updateSlotsState();
                }
            }
        }

        private void showContextMenu(int gemIndex, int mouseX, int mouseY) {
            selectedGemIndex = gemIndex;
            contextMenu.clearItems();
            contextMenu.addItem(Text.literal("  \uD83D\uDDD1 Delete"), menu -> {
                GemActionPayloadC2S.sendRemoveGem(selectedGemIndex);
//                gemData.removeFromInventory(selectedGemIndex); // Remove gem ClientSide
                updateContentHeight();
                presetPanel.updateContentHeight();
                updateSlotsState();
                selectedGemIndex = -1; // Hide menu after action
            });
            contextMenu.setX(Math.min(mouseX, client.getWindow().getScaledWidth() - contextMenu.getContentWidth()));
            contextMenu.setY(Math.min(mouseY, client.getWindow().getScaledHeight() - contextMenu.getContentHeight()));
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY)) {
                if (button == 1 && selectedGemIndex == -1) { // Right-click to show context menu
                    for (int i = 0; i < gemSlots.size(); i++) {
                        GemSlotPanel slot = gemSlots.get(i);
                        if (slot.isMouseOver(mouseX, mouseY)) { // Check hover first
                            if (slot.mouseClicked(mouseX, mouseY, 1)) { // Pass right-click to slot
                                showContextMenu(i, (int) mouseX, (int) mouseY);
                                return true;
                            }
                        }
                    }
                } else if (selectedGemIndex != -1) { // Context menu is open
                    if (contextMenu.mouseClicked(mouseX, mouseY, button)) return true;
                    selectedGemIndex = -1; // Close menu if clicked outside
                    return true;
                } else { // Normal left-click handling (button == 0)
                    for (GemSlotPanel slot : gemSlots) {
                        if (slot.mouseClicked(mouseX, mouseY, button)) return true;
                    }
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    private class PresetPanel extends ScrollablePanel {
        private final List<GemSlotPanel> presetSlots = new ArrayList<>();
        private static final int SLOT_SIZE = 50; // Increased for better visibility
        private static final int SLOT_SPACING = 10; // Space between slots
        private static final int CROSS_PADDING = 20; // Padding for cross pattern

        public PresetPanel(int x, int y, int width, int height, Text title) {
            super(x, y, width, height, title);
            updateContentHeight();
        }

        private void updateContentHeight() {
            // We need enough height for the title, cross pattern, and some padding
            int totalHeight = 70; // Header space

            // Calculate based on the cross pattern
            int slotsInPattern = Math.min(gemData.getGemPresets().size(), GemDataComponent.getMaxPresetSlots(client.player));
            int rowsNeeded = (slotsInPattern > 5) ? 3 : 2; // Basic cross needs 2 rows, more complex needs 3

            totalHeight += (rowsNeeded * (SLOT_SIZE + SLOT_SPACING)) + CROSS_PADDING;

            setContentHeight(totalHeight);
        }

        @Override
        protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = getContentX();
            int y = getContentY() - (int) scrollAmount;
            int width = getContentWidth() - (enableScrollbar ? scrollbarWidth + scrollbarPadding + 10 : 5);

            // Draw preset title with count
            int activeGems = (int) gemData.getGemPresets().values().stream().filter(Objects::nonNull).count();
            int maxGems = GemDataComponent.getMaxPresetSlots(client.player);
            boolean isFull = activeGems >= maxGems;

            String presetCount = String.format("Equipped Gems (%d/%d)", activeGems, maxGems);
            int countColor = isFull ? 0xFFFF5555 : 0xFFFFD700; // Red if full, gold otherwise
            context.drawTextWithShadow(textRenderer, presetCount,
                    x + (width - textRenderer.getWidth(presetCount)) / 2, y + 10, countColor);

            // Draw instructions/status
            String statusText = isFull ? "Maximum gems equipped!" : "Click a gem in inventory to equip";
            int statusColor = isFull ? 0xFFFF5555 : 0xFF55FF55;
            context.drawTextWithShadow(textRenderer, statusText,
                    x + (width - textRenderer.getWidth(statusText)) / 2, y + 26, statusColor);

            // Draw divider
            drawGradientDivider(context, x + 10, y + 44, width - 20, 1.0f);

            // Calculate center for the cross/star pattern
            int centerX = x + width / 2;
            int centerY = y + 80 + SLOT_SIZE; // Position below header with some margin

            Map<Identifier, GemComponent> presets = gemData.getGemPresets();
            presetSlots.clear();

            // Draw slots in a cross/star pattern
            renderCrossPattern(context, centerX, centerY, presets, mouseX, mouseY, delta);
        }

        private void renderCrossPattern(DrawContext context, int centerX, int centerY,
                                        Map<Identifier, GemComponent> presets, int mouseX, int mouseY, float delta) {
            List<Map.Entry<Identifier, GemComponent>> presetEntries = new ArrayList<>(presets.entrySet());
            int maxSlots = GemDataComponent.getMaxPresetSlots(client.player);

            // Define positions relative to center
            List<int[]> positions = new ArrayList<>();

            // Center position
            positions.add(new int[]{0, 0}); // Center

            // Cross arms positions
            positions.add(new int[]{0, -1}); // Top
            positions.add(new int[]{1, 0});  // Right
            positions.add(new int[]{0, 1});  // Bottom
            positions.add(new int[]{-1, 0}); // Left

            // Extended pattern for more slots
            if (maxSlots > 5) {
                positions.add(new int[]{1, -1}); // Top-Right
                positions.add(new int[]{1, 1});  // Bottom-Right
                positions.add(new int[]{-1, 1}); // Bottom-Left
                positions.add(new int[]{-1, -1}); // Top-Left
            }

            // Draw the slots
            for (int i = 0; i < Math.min(positions.size(), maxSlots); i++) {
                int[] pos = positions.get(i);

                int invertedPosX = pos[0] * -1; // Invert horizontal position
                int invertedPosY = pos[1] * -1; // Invert vertical position

                // Calculate slot position with inverted direction
                int slotX = centerX + invertedPosX * (SLOT_SIZE + SLOT_SPACING) - SLOT_SIZE / 2;
                int slotY = centerY + 14 + invertedPosY * (SLOT_SIZE + SLOT_SPACING) - SLOT_SIZE / 2;

                // Get corresponding preset if available
                Identifier slotId = i < presetEntries.size() ? presetEntries.get(i).getKey() : null;
                GemComponent gem = i < presetEntries.size() ? presetEntries.get(i).getValue() : null;

                // Create gem slot
                GemSlotPanel slot = new GemSlotPanel(slotX, slotY, SLOT_SIZE, SLOT_SIZE, gem,
                        GemManagerHelper.getGemTexture(gem),
                        gem != null ? slotPanel -> unequipGem(slotId) : null, gemData, true);

                if (gem == null) {
                    slot.setEnabled(true);
                    slot.setGlowIntensity(0.5f);
                    slot.setBounceIntensity(0.8f);
                } else {
                    slot.setGlowIntensity(2.0f);
                    slot.setBounceIntensity(1.5f);
                }

                slot.render(context, mouseX, mouseY, delta);
                presetSlots.add(slot);
            }

        }

        private void unequipGem(Identifier slot) {
            GemActionPayloadC2S.sendUnequipGem(slot);
            gemData.setPresetSlot(slot, null); // Ensure client-side update

            updateContentHeight();
            inventoryPanel.updateContentHeight();
            // Update the enabled state of inventory panel slots
            inventoryPanel.updateSlotsState();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY)) {
                for (GemSlotPanel slot : presetSlots) {
                    if (slot.mouseClicked(mouseX, mouseY, button)) {
                        return true;
                    }
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}