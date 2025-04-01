package sypztep.dominatus.client.screen.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract tab class for creating tabs in tabbed interfaces.
 * Each tab can contain multiple panels and has its own lifecycle.
 */
public abstract class Tab {
    // Tab information
    protected final String id;
    protected final Text label;
    protected final Identifier icon;

    // UI components in this tab
    protected final List<UIPanel> panels = new ArrayList<>();

    // Parent screen reference
    protected DominatusScreen parentScreen;
    protected MinecraftClient client;

    // Tab state
    protected boolean isActive = false;

    /**
     * Create a new tab with an icon.
     */
    public Tab(String id, Text label, Identifier icon) {
        this.id = id;
        this.label = label;
        this.icon = icon;
        this.client = MinecraftClient.getInstance();
    }

    /**
     * Create a new tab without an icon.
     */
    public Tab(String id, Text label) {
        this(id, label, null);
    }

    /**
     * Initialize the tab. Called when the tab is created.
     */
    public void init(DominatusScreen parentScreen) {
        this.parentScreen = parentScreen;
        panels.clear();
        initPanels();
    }

    /**
     * Initialize the panels for this tab.
     * Override this to add panels specific to this tab.
     */
    protected abstract void initPanels();

    /**
     * Called when this tab becomes active.
     */
    public void onActivate() {
        isActive = true;
        for (UIPanel panel : panels) {
            parentScreen.addPanel(panel);
        }
    }

    /**
     * Called when this tab becomes inactive.
     */
    public void onDeactivate() {
        isActive = false;
        for (UIPanel panel : panels) {
            parentScreen.removePanel(panel);
        }
    }

    /**
     * Handle any tab-specific rendering.
     * This is called after all panels have been rendered.
     */
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    /**
     * Get the tab's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Get the tab's display label.
     */
    public Text getLabel() {
        return label;
    }

    /**
     * Get the tab's icon.
     */
    public Identifier getIcon() {
        return icon;
    }

    /**
     * Add a panel to this tab.
     */
    protected <T extends UIPanel> T addPanel(T panel) {
        panels.add(panel);
        if (isActive && parentScreen != null) {
            parentScreen.addPanel(panel);
        }
        return panel;
    }
}