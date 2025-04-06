package sypztep.dominatus.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import sypztep.dominatus.client.screen.panel.GemSlotsPanel;
import sypztep.dominatus.client.screen.panel.HotbarPanel;
import sypztep.dominatus.common.screen.GemScreenHandler;

@Environment(EnvType.CLIENT)
public class GemScreen extends BaseHandledScreen<GemScreenHandler> {
    private static final int PANEL_WIDTH = 276; // Increased for hotbar
    private static final int PANEL_HEIGHT = 200;
    private static final int INVENTORY_START_X = 176;
    private static final int INVENTORY_WIDTH = 76;
    private static final int HOTBAR_WIDTH = 22;

    public GemScreen(GemScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, Text.empty());
        this.backgroundWidth = PANEL_WIDTH;
        this.backgroundHeight = PANEL_HEIGHT;
    }

    @Override
    protected void initPanels() {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        addPanel(new GemSlotsPanel(
                x + 6,
                y + 24,
                INVENTORY_START_X - 10,
                backgroundHeight - 34
        ));

//        addPanel(new GemInventoryPanel(
//                x + INVENTORY_START_X + 1,
//                y + 24,
//                INVENTORY_WIDTH,
//                backgroundHeight - 34
//        ));
//
        addPanel(new HotbarPanel(
                x + INVENTORY_START_X + INVENTORY_WIDTH + 5,
                y + 24,
                HOTBAR_WIDTH,
                backgroundHeight - 34
        ));
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
    }
}