package sypztep.dominatus.client.screen.tab;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.screen.panel.ContextMenuPanel;
import sypztep.dominatus.client.screen.panel.GemDescriptionPanel;
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
        super("gems", Text.translatable("tab.dominatus.gems"), Dominatus.id("hud/gem/gem"));
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

        inventoryPanel = new InventoryPanel(leftX, panelY, leftWidth, panelHeight, Text.translatable("panel.dominatus.gem_inventory"));
        addPanel(inventoryPanel);

        presetPanel = new PresetPanel(rightX, panelY, rightWidth, panelHeight, Text.translatable("panel.dominatus.gem_presets"));
        addPanel(presetPanel);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Render tooltip at tab level with vanilla style
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 10); // Z-index 10
        renderTooltip(context, mouseX, mouseY);
        context.getMatrices().pop();
    }

    private void renderTooltip(DrawContext context, int mouseX, int mouseY) {
        List<Text> tooltip = null;
        for (ScrollablePanel panel : List.of(inventoryPanel, presetPanel)) {
            for (GemSlotPanel slot : panel instanceof InventoryPanel ? ((InventoryPanel) panel).gemSlots : ((PresetPanel) panel).presetSlots) {
                List<Text> slotTooltip = slot.getActiveTooltip();
                if (slotTooltip != null && !slotTooltip.isEmpty()) {
                    tooltip = slotTooltip;
                    break;
                }
            }
            if (tooltip != null) break;
        }

        if (tooltip != null) {
            context.drawTooltip(client.textRenderer, tooltip, mouseX, mouseY);
        }
    }

    private class InventoryPanel extends ScrollablePanel {
        private final List<GemSlotPanel> gemSlots = new ArrayList<>();
        private final ContextMenuPanel contextMenu;
        private int selectedGemIndex = -1;
        private GemDescriptionPanel descriptionPanel; // Single instance reused
        private int hoveredGemIndex = -1;

        public InventoryPanel(int x, int y, int width, int height, Text title) {
            super(x, y, width, height, title);
            updateContentHeight();
            contextMenu = new ContextMenuPanel(0, 0);
            // Initialize description panel with default hidden position
            descriptionPanel = new GemDescriptionPanel(0, 0, 200, 0, null, gemData, false); // Pass gemData
        }

        private void updateContentHeight() {
            List<GemComponent> gemInventory = gemData.getGemInventory();
            int totalHeight = 70;
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

            boolean presetsAreFull = gemData.hasReachedPresetLimit();

            String inventoryCount = GemManagerHelper.formatCountText("Inventory", gemData.getGemInventory().size(), GemDataComponent.getMaxInventorySize(client.player));
            int countColor = presetsAreFull ? 0xFFAAAAAA : 0xFFFFD700;
            context.drawTextWithShadow(textRenderer, inventoryCount, x + (width - textRenderer.getWidth(inventoryCount)) / 2, y + 10, countColor);

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
            hoveredGemIndex = -1;

            for (int i = 0; i < gemInventory.size(); i++) {
                GemComponent gem = gemInventory.get(i);
                int slotY = y + (i * 60);

                int bgColor = (i & 1) == 0 ? 0x20000000 : 0x20FFFFFF;
                context.fill(x, slotY, x + width, slotY + 50, bgColor);

                boolean canEquipThisGem = !presetsAreFull && gemData.canAddGemToPresets(gem);
                Identifier gemTexture = GemManagerHelper.getGemTexture(gem);
                GemSlotPanel slot = new GemSlotPanel(x + 10, slotY + 5, 40, 40, gem, gemTexture, canEquipThisGem ? slotPanel -> equipGem(gem) : null, gemData, false);

                if (!canEquipThisGem) {
                    slot.setEnabled(false);
                    slot.setGlowIntensity(0.0f);
                    slot.setBounceIntensity(0.0f);
                }

                slot.render(context, mouseX, mouseY, delta);
                gemSlots.add(slot);

                // Render gem name and group beside the slot
                String gemName = Text.translatable("item.dominatus.gem." + gem.type().toString().split(":")[1]).getString();
                String groupLabel = "Group: ";
                String groupName = gem.group().toString().split(":")[1];
                int nameX = x + 60; // Right of the slot
                int maxTextWidth = width - 60 - 10; // Space from slot to edge, minus padding
                int nameColor = 0xFFFFD700; // White for gem name
                int groupLabelColor = 0xFFAAAAAA; // Yellow for "Group:"
                int groupNameColor = 0xFFFFD700; // White for group name

                // Gem name (top, truncated, white)
                int nameY = slotY + 5; // Near top of slot
                Text truncatedName = Text.literal(textRenderer.trimToWidth(gemName, maxTextWidth));
                context.drawTextWithShadow(textRenderer, truncatedName, nameX, nameY, nameColor);

                // Group info (below, "Group:" yellow, name white, truncated)
                int groupY = slotY + 5 + textRenderer.fontHeight + 2; // Below name with 2px spacing
                int groupX = nameX;
                context.drawTextWithShadow(textRenderer, groupLabel, groupX, groupY, groupLabelColor);
                groupX += textRenderer.getWidth(groupLabel);
                Text truncatedGroupName = Text.literal(textRenderer.trimToWidth(groupName, maxTextWidth - textRenderer.getWidth(groupLabel)));
                context.drawTextWithShadow(textRenderer, truncatedGroupName, groupX, groupY, groupNameColor);

                // Check hover for description panel
                int descX = x + 60;
                int descYStart = slotY + 5;
                int descYEnd = slotY + 45;
                if (mouseX >= descX && mouseX <= x + width && mouseY >= descYStart && mouseY <= descYEnd) {
                    hoveredGemIndex = i;
                    updateDescriptionPanel(gem, canEquipThisGem, descX, descYEnd);
                }
            }

            // Render description panel if a gem is hovered
            descriptionPanel.setVisible(hoveredGemIndex != -1);
            descriptionPanel.render(context, mouseX, mouseY, delta); // Always render to allow animation

            if (selectedGemIndex != -1 && contextMenu != null) {
                contextMenu.render(context, mouseX, mouseY, delta);
            }
        }

        private void updateDescriptionPanel(GemComponent gem, boolean canEquip, int x, int baseY) {
            List<Text> descriptionLines = new ArrayList<>();
            String gemName = Text.translatable("item.dominatus.gem." + gem.type().toString().split(":")[1]).getString();
            descriptionLines.add(Text.literal(gemName));
            for (Map.Entry<Identifier, EntityAttributeModifier> entry : gem.attributeModifiers().entrySet()) {
                EntityAttribute attribute = Registries.ATTRIBUTE.get(entry.getKey());
                if (attribute != null) {
                    EntityAttributeModifier modifier = entry.getValue();
                    String operation = switch (modifier.operation()) {
                        case ADD_VALUE -> "➕";
                        case ADD_MULTIPLIED_BASE -> "✕";
                        case ADD_MULTIPLIED_TOTAL -> "⚝";
                    };
                    descriptionLines.add(Text.literal(operation + String.format(" %.1f ", modifier.value()) + Text.translatable(attribute.getTranslationKey()).getString()));
                }
            }
            descriptionLines.add(Text.literal("Group " + gem.group().toString().split(":")[1]));
            descriptionLines.add(Text.literal(String.format("▶ Equipped: %d/%d", gemData.getEquippedCountForGroup(gem.group()), gem.maxPresets())));

            int panelHeight = descriptionLines.size() * (textRenderer.fontHeight + 2) + 10; // Padding included
            int panelWidth = 200; // Fixed width, adjust as needed

            int panelX = Math.min(x, client.getWindow().getScaledWidth() - panelWidth - 5);
            int panelY = baseY - panelHeight - 5; // Position above the slot, with 5px gap

            // Ensure panel stays on-screen vertically
            panelY = Math.max(5, Math.min(panelY, client.getWindow().getScaledHeight() - panelHeight - 5));

            descriptionPanel.update(gem, canEquip, panelX, panelY, panelWidth, panelHeight); // Update existing instance
        }

        private void equipGem(GemComponent gem) {
            if (gem != null && gemData.canAddGemToPresets(gem)) {
                Optional<Identifier> availableSlot = gemData.getAvailablePresetSlot();
                if (availableSlot.isPresent()) {
                    int inventoryIndex = gemData.getGemInventory().indexOf(gem);
                    GemActionPayloadC2S.sendEquipGem(availableSlot.get(), inventoryIndex);
                    gemData.setPresetSlot(availableSlot.get(), gem);
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
                updateContentHeight();
                presetPanel.updateContentHeight();
                updateSlotsState();
                selectedGemIndex = -1;
            });
            contextMenu.setX(Math.min(mouseX, client.getWindow().getScaledWidth() - contextMenu.getContentWidth()));
            contextMenu.setY(Math.min(mouseY, client.getWindow().getScaledHeight() - contextMenu.getContentHeight()));
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY)) {
                if (button == 1 && selectedGemIndex == -1) {
                    for (int i = 0; i < gemSlots.size(); i++) {
                        GemSlotPanel slot = gemSlots.get(i);
                        if (slot.isMouseOver(mouseX, mouseY)) {
                            if (slot.mouseClicked(mouseX, mouseY, 1)) {
                                showContextMenu(i, (int) mouseX, (int) mouseY);
                                return true;
                            }
                        }
                    }
                } else if (selectedGemIndex != -1) {
                    if (contextMenu.mouseClicked(mouseX, mouseY, button)) return true;
                    selectedGemIndex = -1;
                    return true;
                } else {
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
        private static final int SLOT_SIZE = 50;
        private static final int SLOT_SPACING = 10;
        private static final int CROSS_PADDING = 20;

        public PresetPanel(int x, int y, int width, int height, Text title) {
            super(x, y, width, height, title);
            updateContentHeight();
        }

        private void updateContentHeight() {
            int totalHeight = 70;
            int slotsInPattern = Math.min(gemData.getGemPresets().size(), GemDataComponent.getMaxPresetSlots(client.player));
            int rowsNeeded = (slotsInPattern > 5) ? 3 : 2;
            totalHeight += (rowsNeeded * (SLOT_SIZE + SLOT_SPACING)) + CROSS_PADDING;
            setContentHeight(totalHeight);
        }

        @Override
        protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = getContentX();
            int y = getContentY() - (int) scrollAmount;
            int width = getContentWidth() - (enableScrollbar ? scrollbarWidth + scrollbarPadding + 10 : 5);

            int activeGems = (int) gemData.getGemPresets().values().stream().filter(Objects::nonNull).count();
            int maxGems = GemDataComponent.getMaxPresetSlots(client.player);
            boolean isFull = gemData.hasReachedPresetLimit();

            String presetCount = GemManagerHelper.formatCountText("Equipped Gems", activeGems, maxGems);
            int countColor = isFull ? 0xFFFF5555 : 0xFFFFD700;
            context.drawTextWithShadow(textRenderer, presetCount, x + (width - textRenderer.getWidth(presetCount)) / 2, y + 10, countColor);

            String statusText = isFull ? "Maximum gems equipped!" : "Click a gem in inventory to equip";
            int statusColor = isFull ? 0xFFFF5555 : 0xFF55FF55;
            context.drawTextWithShadow(textRenderer, statusText, x + (width - textRenderer.getWidth(statusText)) / 2, y + 26, statusColor);

            drawGradientDivider(context, x + 10, y + 44, width - 20, 1.0f);

            int centerX = x + width / 2;
            int centerY = y + 80 + SLOT_SIZE;
            Map<Identifier, GemComponent> presets = gemData.getGemPresets();
            presetSlots.clear();

            renderCrossPattern(context, centerX, centerY, presets, mouseX, mouseY, delta);
        }

        private void renderCrossPattern(DrawContext context, int centerX, int centerY, Map<Identifier, GemComponent> presets, int mouseX, int mouseY, float delta) {
            List<Map.Entry<Identifier, GemComponent>> presetEntries = new ArrayList<>(presets.entrySet());
            int maxSlots = GemDataComponent.getMaxPresetSlots(client.player);

            List<int[]> positions = new ArrayList<>();
            positions.add(new int[]{0, 0});
            positions.add(new int[]{0, -1});
            positions.add(new int[]{1, 0});
            positions.add(new int[]{0, 1});
            positions.add(new int[]{-1, 0});
            if (maxSlots > 5) {
                positions.add(new int[]{1, -1});
                positions.add(new int[]{1, 1});
                positions.add(new int[]{-1, 1});
                positions.add(new int[]{-1, -1});
            }

            for (int i = 0; i < Math.min(positions.size(), maxSlots); i++) {
                int[] pos = positions.get(i);
                int invertedPosX = pos[0] * -1;
                int invertedPosY = pos[1] * -1;

                int slotX = centerX + invertedPosX * (SLOT_SIZE + SLOT_SPACING) - SLOT_SIZE / 2;
                int slotY = centerY + 14 + invertedPosY * (SLOT_SIZE + SLOT_SPACING) - SLOT_SIZE / 2;

                Identifier slotId = i < presetEntries.size() ? presetEntries.get(i).getKey() : null;
                GemComponent gem = i < presetEntries.size() ? presetEntries.get(i).getValue() : null;

                GemSlotPanel slot = new GemSlotPanel(slotX, slotY, SLOT_SIZE, SLOT_SIZE, gem, GemManagerHelper.getGemTexture(gem), gem != null ? slotPanel -> unequipGem(slotId) : null, gemData, true);

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
            updateContentHeight();
            inventoryPanel.updateContentHeight();

            gemData.setPresetSlot(slot, null);

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