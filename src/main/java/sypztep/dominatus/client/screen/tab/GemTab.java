package sypztep.dominatus.client.screen.tab;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
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
import sypztep.tyrannus.client.screen.panel.Button;
import sypztep.tyrannus.client.screen.panel.ScrollablePanel;
import sypztep.tyrannus.client.screen.panel.UIPanel;
import sypztep.tyrannus.client.screen.tab.Tab;

import java.util.*;

public class GemTab extends Tab {
    private final GemDataComponent gemData;
    private InventoryPanel inventoryPanel;
    private PresetPanel presetPanel;
    private Button overlayButton;
    private InformationPanel infoPanel;

    public GemTab() {
        super("gems", Text.translatable("tab.dominatus.gems"), Dominatus.id("hud/gem/gem"));
        this.gemData = GemDataComponent.get(client.player);
    }

    @Override
    protected void initPanels() {
        int totalWidth = parentScreen.width - 20;
        int panelHeight = parentScreen.height - 100; // Adjusted for button
        int panelY = 65;

        int leftWidth = totalWidth / 2;
        int rightWidth = totalWidth - leftWidth - 5;

        int leftX = 10;
        int rightX = leftX + leftWidth + 5;

        inventoryPanel = new InventoryPanel(leftX, panelY, leftWidth, panelHeight, Text.translatable("panel.dominatus.gem_inventory"));
        addPanel(inventoryPanel);

        presetPanel = new PresetPanel(rightX, panelY, rightWidth, panelHeight, Text.translatable("panel.dominatus.gem_presets"));
        addPanel(presetPanel);

        // Initialize overlay button
        int buttonWidth = 100;
        int buttonHeight = 20;
        int buttonX = (parentScreen.width - buttonWidth) / 3;
        int buttonY = parentScreen.height - 30;
        overlayButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight,
                Text.translatable("panel.dominatus.info"), button -> {
            infoPanel.setVisible(!infoPanel.targetVisible);
        });
        addPanel(overlayButton);

        // Initialize information panel
        int infoHeight = 150;
        int infoY = parentScreen.height; // Off-screen initially
        infoPanel = new InformationPanel(leftX, infoY, leftWidth, infoHeight, gemData);
        infoPanel.setVisible(false);
        addPanel(infoPanel);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 10);
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

    private static List<Text> aggregateGemStats(GemDataComponent gemData) {
        List<Text> statLines = new ArrayList<>();
        Map<String, Double> aggregatedModifiers = new HashMap<>();

        // Aggregate modifiers
        for (GemComponent gem : gemData.getGemPresets().values()) {
            if (gem != null) {
                for (Map.Entry<Identifier, EntityAttributeModifier> entry : gem.attributeModifiers().entrySet()) {
                    EntityAttribute attribute = Registries.ATTRIBUTE.get(entry.getKey());
                    if (attribute != null) {
                        EntityAttributeModifier modifier = entry.getValue();
                        String key = attribute.getTranslationKey();
                        double value = aggregatedModifiers.getOrDefault(key, 0.0);
                        switch (modifier.operation()) {
                            case ADD_VALUE:
                                value += modifier.value();
                                break;
                            case ADD_MULTIPLIED_BASE:
                            case ADD_MULTIPLIED_TOTAL:
                                value += modifier.value() * 100; // Convert to percentage
                                break;
                        }
                        aggregatedModifiers.put(key, value);
                    }
                }
            }
        }

        // Title
        statLines.add(Text.translatable("panel.dominatus.gem_stats")
                .styled(style -> style.withBold(true).withColor(0xFFFFD700)));

        // Stats or empty message
        if (aggregatedModifiers.isEmpty()) {
            statLines.add(Text.literal("No stat bonuses from equipped gems.")
                    .styled(style -> style.withItalic(true).withColor(0xFFAAAAAA)));
        } else {
            aggregatedModifiers.forEach((key, value) -> {
                String operation = key.contains("crit_chance") || key.contains("crit_damage") ?
                        "✕" : "➕";
                String format = value % 1 == 0 ? "%.0f" : "%.1f";
                String symbol = key.contains("crit_chance") || key.contains("crit_damage") ? "%" : "";
                String displayValue = String.format(format + symbol, value); // Show negative sign
                int valueColor = value >= 0 ? 0xFF55FF55 : 0xFFFF5555;

                statLines.add(Text.empty()
                        .append(Text.literal(operation).styled(style -> style.withColor(0xFFAAAAAA)))
                        .append(" ")
                        .append(Text.literal(displayValue).styled(style -> style.withColor(valueColor)))
                        .append(" ")
                        .append(Text.translatable(key).styled(style -> style.withColor(0xFFFFFF))));
            });
        }

        return statLines;
    }

    private class SimpleButton {
        private final int x, y, width, height;
        private final Text text;
        private final Runnable onClick;
        private boolean isPressed = false;
        private boolean isEnabled = true;
        private boolean wasHovered = false;
        private float pressAnimation = 0.0f;
        private float hoverAnimation = 0.0f;
        private static final int BG_NORMAL = 0xFF2A2A2A;
        private static final int BG_HOVER = 0xFF3A3A3A;
        private static final int BG_PRESSED = 0xFF1A1A1A;
        private static final int BG_DISABLED = 0xFF1A1A1A;
        private static final int TEXT_NORMAL = 0xFFAAAAAA;
        private static final int TEXT_HOVER = 0xFFFFFFFF;
        private static final int TEXT_DISABLED = 0xFF666666;
        private static final int CORNER_RADIUS = 4;

        public SimpleButton(int x, int y, int width, int height, Text text, Runnable onClick) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.text = text;
            this.onClick = onClick;
        }

        public void render(DrawContext context, int mouseX, int mouseY, float delta, int textAlpha) {
            // Update animations
            if (isPressed) {
                pressAnimation = Math.min(1.0f, pressAnimation + 0.2f * delta);
                hoverAnimation = Math.min(1.0f, hoverAnimation + 0.3f * delta);
            } else {
                pressAnimation = Math.max(0.0f, pressAnimation - 0.1f * delta);
                if (isMouseOver(mouseX, mouseY) && isEnabled) {
                    hoverAnimation = Math.min(1.0f, hoverAnimation + 0.3f * delta);
                } else {
                    hoverAnimation = Math.max(0.0f, hoverAnimation - 0.3f * delta);
                }
            }

            // Handle hover sound
            boolean isNowHovered = isMouseOver(mouseX, mouseY) && isEnabled;
            if (isNowHovered && !wasHovered && client != null) {
                client.getSoundManager().play(
                        PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_HAT, 1.8F)
                );
            }
            wasHovered = isNowHovered;

            // Calculate colors
            int bgColor;
            int textColor;
            if (!isEnabled) {
                bgColor = BG_DISABLED;
                textColor = TEXT_DISABLED;
            } else {
                bgColor = interpolateColor(
                        interpolateColor(BG_NORMAL, BG_HOVER, hoverAnimation),
                        BG_PRESSED, pressAnimation
                );
                textColor = interpolateColor(TEXT_NORMAL, TEXT_HOVER, hoverAnimation);
            }
            textColor = (textAlpha << 24) | (textColor & 0xFFFFFF);

            // Apply scale animation
            float scale = 1.0f + (hoverAnimation * 0.05f) - (pressAnimation * 0.05f);
            context.getMatrices().push();
            context.getMatrices().translate(x + width / 2.0f, y + height / 2.0f, 0);
            context.getMatrices().scale(scale, scale, 1.0f);
            context.getMatrices().translate(-(x + width / 2.0f), -(y + height / 2.0f), 0);

            // Draw rounded background
            context.fill(x + CORNER_RADIUS, y, x + width - CORNER_RADIUS, y + height, bgColor);
            context.fill(x, y + CORNER_RADIUS, x + width, y + height - CORNER_RADIUS, bgColor);

            // Draw gradient effect
            int topGradient = lightenColor(bgColor, 0.2f);
            int bottomGradient = darkenColor(bgColor, 0.2f);
            float gradientHeight = height * 0.15f;
            context.fill(x, y, x + width, y + (int) gradientHeight, topGradient);
            context.fill(x, y + height - (int) gradientHeight, x + width, y + height, bottomGradient);

            // Draw shadow
            int shadowColor = (textAlpha << 24) | 0x66000000;
            int shadowOffset = 2;
            context.fill(x + shadowOffset, y + shadowOffset,
                    x + width + shadowOffset, y + height + shadowOffset, shadowColor);

            // Draw text with press offset
            int textY = y + (height - client.textRenderer.fontHeight) / 2 + (int) (pressAnimation * 1.5f);
            context.drawTextWithShadow(client.textRenderer, text, x + (width - client.textRenderer.getWidth(text)) / 2, textY, textColor);

            // Draw glow effect
            if (hoverAnimation > 0.3f && isEnabled) {
                int glowAlpha = (int) (40 * hoverAnimation * (textAlpha / 255.0f));
                int glowColor = (glowAlpha << 24) | 0xFFFFFF;
                int glowSize = 1;
                context.fill(x - glowSize, y - glowSize, x + width + glowSize, y, glowColor);
                context.fill(x - glowSize, y + height, x + width + glowSize, y + height + glowSize, glowColor);
                context.fill(x - glowSize, y, x, y + height, glowColor);
                context.fill(x + width, y, x + width + glowSize, y + height, glowColor);
            }

            context.getMatrices().pop();
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && isMouseOver(mouseX, mouseY) && isEnabled) {
                isPressed = true;
                if (client != null) {
                    client.getSoundManager().play(
                            PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F)
                    );
                }
                onClick.run();
                return true;
            }
            isPressed = false;
            return false;
        }

        private boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }

        private int interpolateColor(int color1, int color2, float fraction) {
            int a1 = (color1 >> 24) & 0xFF;
            int r1 = (color1 >> 16) & 0xFF;
            int g1 = (color1 >> 8) & 0xFF;
            int b1 = color1 & 0xFF;

            int a2 = (color2 >> 24) & 0xFF;
            int r2 = (color2 >> 16) & 0xFF;
            int g2 = (color2 >> 8) & 0xFF;
            int b2 = color2 & 0xFF;

            int a = (int) (a1 + (a2 - a1) * fraction);
            int r = (int) (r1 + (r2 - r1) * fraction);
            int g = (int) (g1 + (g2 - g1) * fraction);
            int b = (int) (b1 + (b2 - b1) * fraction);

            return (a << 24) | (r << 16) | (g << 8) | b;
        }

        private int lightenColor(int color, float factor) {
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            r = Math.min(255, (int) (r + (255 - r) * factor));
            g = Math.min(255, (int) (g + (255 - g) * factor));
            b = Math.min(255, (int) (b + (255 - b) * factor));

            return (a << 24) | (r << 16) | (g << 8) | b;
        }

        private int darkenColor(int color, float factor) {
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            r = Math.max(0, (int) (r * (1 - factor)));
            g = Math.max(0, (int) (g * (1 - factor)));
            b = Math.max(0, (int) (b * (1 - factor)));

            return (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    private class InformationPanel extends UIPanel {
        private final GemDataComponent gemData;
        private float animationProgress = 0.0f;
        private static final float ANIMATION_SPEED = 0.015f;
        private boolean isVisible = false;
        private boolean targetVisible = false;
        private static final int BG_COLOR = 0xFF1A1A1A;
        private static final int BORDER_COLOR = 0xFF424242;
        private List<Text> statLines;
        private final SimpleButton closeButton;

        public InformationPanel(int x, int y, int width, int height, GemDataComponent gemData) {
            super(x, y, width, height, null);
            this.gemData = gemData;
            this.setDrawHeader(false);
            this.setDrawBorder(true);
            this.setPadding(5);
            updateContent();
            // Initialize close button (bottom-right)
            int buttonWidth = 60;
            int buttonHeight = 20;
            int buttonX = x + width - buttonWidth - padding;
            int buttonY = y + height - buttonHeight - padding;
            this.closeButton = new SimpleButton(buttonX, buttonY, buttonWidth, buttonHeight,
                    Text.literal("Close"), () -> this.targetVisible = false);
        }

        public void setVisible(boolean visible) {
            this.targetVisible = visible;
            this.isVisible = true; // Keep rendering during fade-out
        }

        public boolean isFullyClosed() {
            return !isVisible && animationProgress <= 0.0f;
        }

        public void updateContent() {
            statLines = aggregateGemStats(gemData);
        }

        private float easeInOutCubic(float t) {
            return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            if (!isVisible && animationProgress <= 0.0f) {
                return;
            }

            // Update animation
            if (targetVisible) {
                animationProgress = Math.min(1.0f, animationProgress + ANIMATION_SPEED);
            } else {
                animationProgress = Math.max(0.0f, animationProgress - ANIMATION_SPEED);
            }

            // Fully close when faded out
            if (!targetVisible && animationProgress <= 0.0f) {
                isVisible = false;
            }

            // Apply easing
            float easedProgress = easeInOutCubic(animationProgress);

            // Calculate animated position (slide from bottom)
            int animatedY = (int) (parentScreen.height - (height * easedProgress));
            int alpha = (int) (easedProgress * 255);
            int borderColor = (alpha << 24) | (BORDER_COLOR & 0xFFFFFF);

            context.getMatrices().push();
            context.getMatrices().translate(0, animatedY - y, 50); // z=50

            // Draw background
            context.fill(x, y, x + width, y + height, BG_COLOR);

            // Draw border
            if (drawBorder) {
                context.fill(x, y, x + width, y + 1, borderColor);
                context.fill(x, y + height - 1, x + width, y + height, borderColor);
                context.fill(x, y, x + 1, y + height, borderColor);
                context.fill(x + width - 1, y, x + width, y + height, borderColor);
            }

            // Render contents
            renderContents(context, mouseX, mouseY, delta, animatedY);

            context.getMatrices().pop();
        }

        protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta, int animatedY) {
            int textY = y + padding;
            int textAlpha = (int) (easeInOutCubic(animationProgress) * 255);

            for (Text line : statLines) {
                context.drawTextWithShadow(textRenderer, line, x + padding, textY, 0xFFFFFF | (textAlpha << 24));
                textY += textRenderer.fontHeight + 2;
            }

            closeButton.render(context, mouseX, mouseY - (animatedY - y), delta, textAlpha);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isVisible && animationProgress > 0) {
                int animatedY = (int) (parentScreen.height - (height * easeInOutCubic(animationProgress)));
                if (closeButton.mouseClicked(mouseX, mouseY - (animatedY - y), button)) {
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    private class InventoryPanel extends ScrollablePanel {
        private final List<GemSlotPanel> gemSlots = new ArrayList<>();
        private final ContextMenuPanel contextMenu;
        private int selectedGemIndex = -1;
        private GemDescriptionPanel descriptionPanel;
        private int hoveredGemIndex = -1;

        public InventoryPanel(int x, int y, int width, int height, Text title) {
            super(x, y, width, height, title);
            updateContentHeight();
            contextMenu = new ContextMenuPanel(0, 0);
            descriptionPanel = new GemDescriptionPanel(0, 0, 200, 0, null, gemData, false);
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

                String gemName = Text.translatable("item.dominatus.gem." + gem.type().toString().split(":")[1]).getString();
                String groupLabel = "Group: ";
                String groupName = gem.group().toString().split(":")[1];
                int nameX = x + 60;
                int maxTextWidth = width - 60 - 10;
                int nameColor = 0xFFFFD700;
                int groupLabelColor = 0xFFAAAAAA;
                int groupNameColor = 0xFFFFD700;

                int nameY = slotY + 5;
                Text truncatedName = Text.literal(textRenderer.trimToWidth(gemName, maxTextWidth));
                context.drawTextWithShadow(textRenderer, truncatedName, nameX, nameY, nameColor);

                int groupY = slotY + 5 + textRenderer.fontHeight + 2;
                int groupX = nameX;
                context.drawTextWithShadow(textRenderer, groupLabel, groupX, groupY, groupLabelColor);
                groupX += textRenderer.getWidth(groupLabel);
                Text truncatedGroupName = Text.literal(textRenderer.trimToWidth(groupName, maxTextWidth - textRenderer.getWidth(groupLabel)));
                context.drawTextWithShadow(textRenderer, truncatedGroupName, groupX, groupY, groupNameColor);

                int descX = x + 60;
                int descYStart = slotY + 5;
                int descYEnd = slotY + 45;
                if (mouseX >= descX && mouseX <= x + width && mouseY >= descYStart && mouseY <= descYEnd) {
                    hoveredGemIndex = i;
                    updateDescriptionPanel(gem, canEquipThisGem, descX, descYEnd);
                }
            }

            descriptionPanel.setVisible(hoveredGemIndex != -1);
            descriptionPanel.render(context, mouseX, mouseY, delta);

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

            int panelHeight = descriptionLines.size() * (textRenderer.fontHeight + 2) + 10;
            int panelWidth = 200;

            int panelX = Math.min(x, client.getWindow().getScaledWidth() - panelWidth - 5);
            int panelY = baseY - panelHeight - 5;

            panelY = Math.max(5, Math.min(panelY, client.getWindow().getScaledHeight() - panelHeight - 5));

            descriptionPanel.update(gem, canEquip, panelX, panelY, panelWidth, panelHeight);
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
                    infoPanel.updateContent();
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
                infoPanel.updateContent();
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
            infoPanel.updateContent();
            gemData.setPresetSlot(slot, null);
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