package sypztep.dominatus.client.screen.base;

import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages tabs in a tabbed interface.
 */
public class TabManager {
    // The parent screen
    private final DominatusScreen parentScreen;

    // Tab storage
    private final List<Tab> tabs = new ArrayList<>();
    private final Map<String, Tab> tabsById = new HashMap<>();
    private String activeTabId = null;

    // The navigation bar
    private NavBar navBar;
    private int navBarHeight = 30;

    /**
     * Create a new tab manager.
     */
    public TabManager(DominatusScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    /**
     * Initialize the tab manager.
     */
    public void init(int x, int y, int width) {
        // Create nav bar
        navBar = new NavBar(x, y, width, navBarHeight);

        // Add tabs to nav bar
        for (Tab tab : tabs) {
            navBar.addItem(tab.getId(), tab.getLabel(), tab.getIcon(), this::selectTab);
            tab.init(parentScreen);
        }

        parentScreen.addPanel(navBar);

        if (activeTabId == null && !tabs.isEmpty()) {
            selectTab(tabs.getFirst().getId());
        } else if (activeTabId != null) {
            selectTab(activeTabId);
        }
    }

    /**
     * Register a tab with the manager.
     */
    public void registerTab(Tab tab) {
        tabs.add(tab);
        tabsById.put(tab.getId(), tab);
    }

    /**
     * Select a tab by ID.
     */
    public void selectTab(String tabId) {
        // Deactivate current tab
        if (activeTabId != null && tabsById.containsKey(activeTabId)) {
            tabsById.get(activeTabId).onDeactivate();
        }

        // Activate new tab
        if (tabsById.containsKey(tabId)) {
            activeTabId = tabId;
            Tab tab = tabsById.get(tabId);
            tab.onActivate();

            // Update nav bar selection
            if (navBar != null) {
                navBar.setActive(tabId);
            }
        }
    }

    /**
     * Get the height of the nav bar.
     */
    public int getNavBarHeight() {
        return navBarHeight;
    }

    /**
     * Set the height of the nav bar.
     */
    public void setNavBarHeight(int height) {
        this.navBarHeight = height;
    }

    /**
     * Get the currently active tab.
     */
    public Tab getActiveTab() {
        return activeTabId != null ? tabsById.get(activeTabId) : null;
    }

    /**
     * Render any additional tab content.
     */
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Let the active tab render any additional content
        Tab activeTab = getActiveTab();
        if (activeTab != null) {
            activeTab.render(context, mouseX, mouseY, delta);
        }
    }
}