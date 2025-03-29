package sypztep.dominatus.client.screen.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class DominatusScreen extends Screen {
    protected static final int BACKGROUND_COLOR = 0xF0121212;
    protected static final int PANEL_BACKGROUND = 0xFF1A1A1A;
    protected static final int PANEL_BORDER = 0xFF424242;
    protected static final int HEADER_COLOR = 0xFFFFD700;
    protected static final int TEXT_COLOR = 0xFFFFFFFF;

    protected final List<UIPanel> panels = new ArrayList<>();

    protected TabManager tabManager;

    public DominatusScreen(Text title) {
        super(title);
    }


    @Override
    protected void init() {
        panels.clear();
        initPanels();

        // Initialize tab manager if this screen uses tabs
        if (tabManager != null) {
            tabManager.init(10, 25, width - 20);
        }

        super.init();
    }


    protected abstract void initPanels();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, width, height, BACKGROUND_COLOR, BACKGROUND_COLOR);
        super.render(context, mouseX, mouseY, delta);
        for (UIPanel panel : panels) {
            panel.render(context, mouseX, mouseY, delta);
        }

        renderTitle(context);

        // Let active tab render any additional content
        if (tabManager != null) {
            tabManager.render(context, mouseX, mouseY, delta);
        }
    }


    protected void renderTitle(DrawContext context) {
        context.drawCenteredTextWithShadow(
                textRenderer,
                title,
                width / 2,
                10,
                TEXT_COLOR
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (UIPanel panel : panels) {
            if (panel.isMouseOver(mouseX, mouseY) && panel.handleScrolling(horizontalAmount, verticalAmount)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (UIPanel panel : panels) {
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (UIPanel panel : panels) {
            if (panel instanceof ScrollablePanel scrollablePanel) {
                if (scrollablePanel.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                    return true;
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (UIPanel panel : panels) {
            if (panel instanceof ScrollablePanel scrollablePanel) {
                scrollablePanel.mouseReleased(mouseX, mouseY, button);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * Add a panel to the screen.
     */
    public <T extends UIPanel> void addPanel(T panel) {
        panels.add(panel);
    }

    /**
     * Remove a panel from the screen.
     */
    public void removePanel(UIPanel panel) {
        panels.remove(panel);
    }
}