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
import sypztep.dominatus.common.util.RefinementCalculator;
import sypztep.dominatus.common.util.RefinementManager;

@Environment(EnvType.CLIENT)
public final class RefineScreen
        extends HandledScreen<RefineScreenHandler>
        implements ScreenHandlerListener {
    public static final Identifier TEXTURE = Dominatus.id("gui/container/refine_screen.png");
    public RefineButton refineButton;
//    private final CyclingItemSlotIcon weaponSlotIcon = new CyclingItemSlotIcon(0);
//    private final CyclingItemSlotIcon armorSlotIcon = new CyclingItemSlotIcon(0);
//    private static final List<ItemStack> WEAPON_STONE = List.of(
//            ModItems.REFINE_WEAPON_STONE.getDefaultStack(),
//            ModItems.REFINE_WEAPONENFORGE_STONE.getDefaultStack(),
//            ModItems.MOONLIGHT_CRESCENT.getDefaultStack()
//            );
//    private static final List<ItemStack> ARMOR_STONE = List.of(
//            ModItems.REFINE_ARMOR_STONE.getDefaultStack(),
//            ModItems.REFINE_ARMORENFORGE_STONE.getDefaultStack(),
//            ModItems.MOONLIGHT_CRESCENT.getDefaultStack()
//    );

    private static final int STAT_START_X = 10; // Left side position
    private static final int STAT_START_Y = 20; // Starting Y position
    private static final int STAT_SPACING = 10; // Spacing between stats

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
        context.fill(0, 0, width, height, 0xFF121212);
        context.fill(width / 2 , height / 2, width / 2  + 200, height / 2 + 100, 0xFFFFFF);

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

    private void drawSuccessRate(DrawContext context, int x, int y, float scale, int currentLevel) {
        int failStack = ModEntityComponents.FAILSTACK_COMPONENT.get(handler.getPlayer()).getFailstack();
        double successRate = RefinementCalculator.calculateSuccessRate(currentLevel, failStack) * 100;
        String formattedSuccessRate = String.format("%.2f%%", successRate);

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 0);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.of("Rate: " + formattedSuccessRate),
                x + 138,
                y - 35,
                getSuccessRateColor(successRate)
        );
        context.getMatrices().pop();
    }

    private void drawMaxLevelMessage(DrawContext context, int x, int y, float scale) {
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 0);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.of("Reach to max Refine Lvl!"),
                x + 138,
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
                138,
                32,
                getFailstackColor(failstack)
        );
    }

    private int getSuccessRateColor(double rate) {
        if (rate >= 90.0) return 0x00FF00; // Green
        if (rate >= 50.0) return 0xFFFF00; // Yellow
        return 0xFF6B6B; // Red
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

        // Draw level comparison with scaling
        float scale = 0.8f; // Reduce text scale
        context.getMatrices().push();

        context.getMatrices().scale(scale, scale, 1.0f);
        String levelText = String.format("%d → %d", currentLevel, currentLevel + 1);
        float scaledX = STAT_START_X / scale;
        float scaledY = STAT_START_Y / scale;
        context.drawTextWithShadow(textRenderer, Text.literal("Refine Level"),
                (int)scaledX, (int)scaledY - 13, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal(levelText),
                (int)scaledX, (int)scaledY, 0xFFFFFF);
        context.getMatrices().pop();

        int y = STAT_START_Y + STAT_SPACING + 5;

        // Draw stat comparisons
        drawStatComparison(context, "ACC",
                currentRef.accuracy(),
                RefinementCalculator.calculateStatValue(currentLevel + 1, entry.maxLvl(),
                        entry.startAccuracy(), entry.endAccuracy()),
                y);
        y += STAT_SPACING;

        drawStatComparison(context, "EVA",
                currentRef.evasion(),
                RefinementCalculator.calculateStatValue(currentLevel + 1, entry.maxLvl(),
                        entry.startEvasion(), entry.endEvasion()),
                y);
        y += STAT_SPACING;

        drawStatComparison(context, "DMG",
                currentRef.damage(),
                RefinementCalculator.calculateStatValue(currentLevel + 1, entry.maxLvl(),
                        entry.starDamage(), entry.endDamage()),
                y);
        y += STAT_SPACING;

        drawStatComparison(context, "DEF",
                currentRef.protection(),
                RefinementCalculator.calculateStatValue(currentLevel + 1, entry.maxLvl(),
                        entry.startProtection(), entry.endProtection()),
                y);
    }

    private void drawStatComparison(DrawContext context, String statName, Number currentValue, Number nextValue, int y) {
        float scale = 0.55f;
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);

        String text;
        int color;
        double diff = nextValue.doubleValue() - currentValue.doubleValue();

        if (diff > 0) {
            color = 0x00FF00;
            text = String.format("%s: %s→%s (%s)", // Fixed spacing here
                    statName,
                    formatValue(currentValue),
                    formatValue(nextValue),
                    "+" + formatValue(diff));
        } else if (diff < 0) {
            color = 0xFF0000;
            text = String.format("%s: %s→%s (%s)", // Fixed spacing here
                    statName,
                    formatValue(currentValue),
                    formatValue(nextValue),
                    formatValue(diff));
        } else {
            color = 0xFFFFFF;
            text = String.format("%s: %s→%s",
                    statName,
                    formatValue(currentValue),
                    formatValue(nextValue));
        }

        float scaledX = STAT_START_X / scale;
        float scaledY = y / scale;

        context.drawTextWithShadow(textRenderer, Text.literal(text),
                (int)scaledX, (int)scaledY, color);

        context.getMatrices().pop();
    }

    private String formatValue(Number value) {
        if (value instanceof Float || value instanceof Double) {
            return String.format("%.1f", value.doubleValue());
        }
        return String.valueOf(value.intValue());
    }
}
