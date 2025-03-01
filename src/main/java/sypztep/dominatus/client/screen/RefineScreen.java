package sypztep.dominatus.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.payload.RefinePayloadC2S;
import sypztep.dominatus.common.screen.RefineScreenHandler;
import sypztep.dominatus.common.util.RefineSystem.RefinementCalculator;
import sypztep.dominatus.common.util.RefineSystem.RefinementManager;

@Environment(EnvType.CLIENT)
public final class RefineScreen
        extends HandledScreen<RefineScreenHandler>
        implements ScreenHandlerListener {
    public static final Identifier TEXTURE = Dominatus.id("gui/container/refine_screen.png");
    public RefineButton refineButton;

    private static final int LEFT_LABEL_X = -13;
    private static final int LEFT_LABEL_START_Y = 35;
    private static final int STAT_SPACING = 10;
    private static final int VALUE_X = 10;
    // Constants for main area values
    private static final int MAIN_VALUE_X = 10;
    private static final int MAIN_VALUE_START_Y = 30;

    public RefineScreen(RefineScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, Text.translatable(Dominatus.MODID + ".refine_screen"));
        this.titleX = 60;
    }
    @Override
    protected void init() {
        super.init();
        this.handler.addListener(this);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.refineButton = this.addDrawableChild(new RefineButton(i + 80, j + 56, (button)-> {
            if (button instanceof RefineButton && !((RefineButton) button).disabled)
                RefinePayloadC2S.send();
        }));
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
    }
    @Override
    public void removed() {
        super.removed();
        this.handler.removeListener(this);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        float scale = 0.75f;
        int x = (int) (32 / scale);
        int y = (int) (74 / scale);

        ItemStack stack = handler.getSlot(1).getStack();
        if (!handler.isValidItem(stack) || !stack.contains(ModDataComponents.REFINEMENT)) {
            drawFailstackCounter(context);
            return;
        }

        // Get refinement data
        Refinement refinement = stack.get(ModDataComponents.REFINEMENT);
        if (refinement == null) return;

        int currentLevel = refinement.refine();
        if (currentLevel >= RefinementManager.MAX_ENHANCED_LEVEL) {
            drawMaxLevelMessage(context, x, y, scale);
        } else {
            drawSuccessRate(context, x, y, scale, currentLevel);
        }

        drawFailstackCounter(context);

        if (handler.isValidItem(stack) && stack.contains(ModDataComponents.REFINEMENT)) {
            drawComparisonStats(context, stack);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY);
    }

    private void drawSuccessRate(DrawContext context, int x, int y, float scale, int currentLevel) {
        int failStack = ModEntityComponents.FAILSTACK_COMPONENT.get(handler.getPlayer()).getFailstack();
        double successRate = RefinementCalculator.calculateSuccessRate(currentLevel, failStack) * 100;
        String formattedSuccessRate = String.format("%.2f%%", successRate);

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 0);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.of("Rate: " + formattedSuccessRate),
                x + 144,
                y - 30,
                0xE0E0E0
        );
        context.getMatrices().pop();
    }

    private void drawMaxLevelMessage(DrawContext context, int x, int y, float scale) {
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 0);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.of("Reach to max Refine Lvl!"),
                x + 140,
                y - 35,
                0xFFD700 // Gold color for max level
        );
        context.getMatrices().pop();
    }

    private void drawFailstackCounter(DrawContext context) {
        int failstack = ModEntityComponents.FAILSTACK_COMPONENT.get(handler.getPlayer()).getFailstack();
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.of("Failstack: " + failstack),
                140,
                36,
                getFailstackColor(failstack)
        );
    }

    private int getFailstackColor(int failstack) {
        if (failstack >= 30) return 0x00FF00; // Green
        if (failstack >= 15) return 0xFFFF00; // Yellow
        return 0xE0E0E0; // Default gray
    }
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, i, j, 0, 0,175,166, 256,256);
        // Small box area for hold extra like EVA ACC DMG DEF
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE,i - 23,j + 24, 176,54,24,57, 256,256);
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
    }

    public static class RefineButton extends ButtonWidget {
        private boolean disabled;
        @Nullable
        @Override
        public Tooltip getTooltip() {
            return Tooltip.of(Text.translatable(Dominatus.MODID + ".refinebutton_tooltip"));
        }

        public RefineButton(int x, int y, PressAction onPress) {
            super(x, y, 18, 18,Text.literal("Refine"), onPress, DEFAULT_NARRATION_SUPPLIER);
            this.disabled = true;
            this.setTooltip(getTooltip());
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int v = 0;
            if (this.disabled) {
                v += this.height * 2;
            } else if (this.isHovered()) {
                v += this.height;
            }

            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, this.getX() - 1, this.getY(), 176, v, 18,18,256, 256);
        }

        public void setDisabled(boolean disable) {
            this.disabled = disable;
        }
    }

    private void drawComparisonStats(DrawContext context, ItemStack stack) {
        Refinement currentRef = stack.get(ModDataComponents.REFINEMENT);
        if (currentRef == null) return;

        int currentLevel = currentRef.refine();
        if (currentLevel >= RefinementManager.MAX_ENHANCED_LEVEL) return;

        DominatusItemEntry entry = DominatusItemEntry.getDominatusItemData(stack).orElse(null);
        if (entry == null) return;

        // Draw level info at the top of main area
        drawLevelInfo(context, currentLevel);

        // Draw stats with aligned values
        drawStatsWithValues(context, currentRef, currentLevel, entry);
    }

    private void drawStatsWithValues(DrawContext context, Refinement currentRef, int currentLevel, DominatusItemEntry entry) {
        float scale = 0.65f;
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);

        // Stats data array to hold all stat pairs
        StatPair[] stats = {
                new StatPair("ACC:", currentRef.accuracy(),
                        RefinementCalculator.calculateStatValue(currentLevel + 1, entry.maxLvl(),
                                entry.startAccuracy(), entry.endAccuracy())),
                new StatPair("EVA:", currentRef.evasion(),
                        RefinementCalculator.calculateStatValue(currentLevel + 1, entry.maxLvl(),
                                entry.startEvasion(), entry.endEvasion())),
                new StatPair("DMG:", currentRef.damage(),
                        RefinementCalculator.calculateStatValue(currentLevel + 1, entry.maxLvl(),
                                entry.starDamage(), entry.endDamage())),
                new StatPair("DEF:", currentRef.protection(),
                        RefinementCalculator.calculateStatValue(currentLevel + 1, entry.maxLvl(),
                                entry.startProtection(), entry.endProtection()))
        };

        float baseY = LEFT_LABEL_START_Y / scale;

        // Draw each stat pair
        for (int i = 0; i < stats.length; i++) {
            float currentY = baseY + (i * STAT_SPACING / scale);

            // Draw label in left box
            context.drawTextWithShadow(textRenderer,
                    Text.literal(stats[i].label),
                    (int)(LEFT_LABEL_X / scale),
                    (int)currentY,
                    0xFFFFFF);

            // Draw value in main area
            drawStatValue(context,
                    stats[i].current,
                    stats[i].next,
                    (int)(VALUE_X / scale),
                    (int)currentY);
        }

        context.getMatrices().pop();
    }

    // Helper record to store stat information
    private record StatPair(String label, Number current, Number next) {}

    private void drawStatValue(DrawContext context, Number currentValue, Number nextValue, int x, int y) {
        double diff = nextValue.doubleValue() - currentValue.doubleValue();
        int color = diff > 0 ? 0x00FF00 : (diff < 0 ? 0xFF0000 : 0xFFFFFF);

        String text = String.format("%s→%s",
                formatValue(currentValue),
                formatValue(nextValue));

        if (diff != 0) {
            text += String.format(" (%s%s)",
                    diff > 0 ? "+" : "",
                    formatValue(diff));
        }

        context.drawTextWithShadow(textRenderer, Text.literal(text),
                x, y, color);
    }

    private void drawLevelInfo(DrawContext context, int currentLevel) {
        float scale = 0.8f;
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);

        float scaledX = MAIN_VALUE_X / scale;
        float scaledY = (MAIN_VALUE_START_Y - 15) / scale;

        context.drawTextWithShadow(textRenderer, Text.literal("Refine Level"),
                (int)scaledX, (int)scaledY - 8, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal(
                        String.format("%d → %d", currentLevel, currentLevel + 1)),
                (int)scaledX, (int)scaledY + 4, 0xFFFFFF);

        context.getMatrices().pop();
    }

    private String formatValue(Number value) {
        if (value instanceof Float || value instanceof Double) {
            return String.format("%.1f", value.doubleValue());
        }
        return String.valueOf(value.intValue());
    }
}
