package sypztep.dominatus.client.screen.tab;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.client.screen.panel.GemSlotPanel;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.payload.GemActionPayloadC2S;
import sypztep.tyrannus.client.screen.panel.ScrollablePanel;
import sypztep.tyrannus.client.screen.tab.Tab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GemTab extends Tab {
    private final GemDataComponent gemData;
    private InventoryPanel inventoryPanel;
    private PresetPanel presetPanel;

    public GemTab(PlayerEntity player) {
        super("gems", Text.translatable("tab.dominatus.gems"));
        this.gemData = GemDataComponent.get(player);
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

    private class InventoryPanel extends ScrollablePanel {
        private final List<GemSlotPanel> gemSlots = new ArrayList<>();

        public InventoryPanel(int x, int y, int width, int height, Text title) {
            super(x, y, width, height, title);
            updateContentHeight();
        }

        private void updateContentHeight() {
            List<GemComponent> gemInventory = gemData.getGemInventory();
            int totalHeight = 50; // Header
            totalHeight += gemInventory.size() * 60; // 60px per gem entry
            totalHeight += 20; // Padding
            setContentHeight(totalHeight);
        }

        @Override
        protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = getContentX();
            int y = getContentY() - (int) scrollAmount;
            int width = getContentWidth() - (enableScrollbar ? scrollbarWidth + scrollbarPadding + 10 : 5);

            context.drawTextWithShadow(textRenderer, "GEM INVENTORY", x + (width - textRenderer.getWidth("GEM INVENTORY")) / 2, y + 10, 0xFFFFD700);
            y += 30;

            List<GemComponent> gemInventory = gemData.getGemInventory();
            gemSlots.clear();

            for (int i = 0; i < gemInventory.size(); i++) {
                GemComponent gem = gemInventory.get(i);
                int slotY = y + (i * 60);

                int bgColor = i % 2 == 0 ? 0x40000077 : 0x40007700;
                context.fill(x, slotY, x + width, slotY + 50, bgColor);

                GemSlotPanel slot = new GemSlotPanel(x + 10, slotY + 5, 40, 40, gem, null, slotPanel -> equipGem(gem), gemData);
                slot.render(context, mouseX, mouseY, delta);
                gemSlots.add(slot);

                String gemName = gem.type().toString().split(":")[1];
                context.drawTextWithShadow(textRenderer, Text.translatable("item.dominatus.gem." + gemName).getString(), x + 60, slotY + 5, 0xFFFFFFFF);

                int textY = slotY + 20;
                for (Map.Entry<Identifier, EntityAttributeModifier> entry : gem.attributeModifiers().entrySet()) {
                    EntityAttribute attribute = Registries.ATTRIBUTE.get(entry.getKey());
                    if (attribute != null) {
                        EntityAttributeModifier modifier = entry.getValue();
                        String operation = switch (modifier.operation()) {
                            case ADD_VALUE -> "+";
                            case ADD_MULTIPLIED_BASE -> "×";
                            case ADD_MULTIPLIED_TOTAL -> "%";
                        };
                        String effectText = operation + String.format("%.1f", modifier.value()) + " " + Text.translatable(attribute.getTranslationKey()).getString();
                        context.drawTextWithShadow(textRenderer, effectText, x + 60, textY, 0xFF55FF55);
                        textY += textRenderer.fontHeight + 2;
                    }
                }
            }
        }

        private void equipGem(GemComponent gem) {
            Optional<Identifier> availableSlot = gemData.getAvailablePresetSlot();
            if (availableSlot.isPresent()) {
                int inventoryIndex = gemData.getGemInventory().indexOf(gem);
                GemActionPayloadC2S.sendEquipGem(availableSlot.get(), inventoryIndex);
                updateContentHeight();
                presetPanel.updateContentHeight();
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY)) {
                for (GemSlotPanel slot : gemSlots) {
                    if (slot.mouseClicked(mouseX, mouseY, button)) {
                        return true;
                    }
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    private class PresetPanel extends ScrollablePanel {
        private final List<GemSlotPanel> presetSlots = new ArrayList<>();

        public PresetPanel(int x, int y, int width, int height, Text title) {
            super(x, y, width, height, title);
            updateContentHeight();
        }

        private void updateContentHeight() {
            int totalHeight = 50; // Header
            totalHeight += gemData.getGemPresets().size() * 60; // 60px per slot
            totalHeight += 20; // Padding
            setContentHeight(totalHeight);
        }

        @Override
        protected void renderScrollableContent(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = getContentX();
            int y = getContentY() - (int) scrollAmount;
            int width = getContentWidth() - (enableScrollbar ? scrollbarWidth + scrollbarPadding + 10 : 5);

            context.drawTextWithShadow(textRenderer, "GEM PRESETS", x + (width - textRenderer.getWidth("GEM PRESETS")) / 2, y + 10, 0xFFFFD700);
            y += 30;

            Map<Identifier, GemComponent> presets = gemData.getGemPresets();
            presetSlots.clear();

            int index = 0;
            for (Map.Entry<Identifier, GemComponent> entry : presets.entrySet()) {
                int slotY = y + (index * 60);

                int bgColor = index % 2 == 0 ? 0x40000077 : 0x40007700;
                context.fill(x, slotY, x + width, slotY + 50, bgColor);

                String slotName = entry.getKey().getPath().toUpperCase();
                context.drawTextWithShadow(textRenderer, slotName, x + 60, slotY + 15, 0xFFAAAAAA);

                GemSlotPanel slot = new GemSlotPanel(x + 10, slotY + 5, 40, 40, entry.getValue(), null,
                        entry.getValue() != null ? slotPanel -> unequipGem(entry.getKey()) : null, gemData);
                slot.render(context, mouseX, mouseY, delta);
                presetSlots.add(slot);

                index++;
            }
        }

        private void unequipGem(Identifier slot) {
            GemActionPayloadC2S.sendUnequipGem(slot);
            updateContentHeight();
            inventoryPanel.updateContentHeight();
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