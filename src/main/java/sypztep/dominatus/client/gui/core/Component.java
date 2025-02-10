package sypztep.dominatus.client.gui.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Component {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean visible = true;
    protected Map<String, Object> state;
    protected List<Component> children;
    protected Component parent;
    protected TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    public Component(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.state = new HashMap<>();
        this.children = new ArrayList<>();
    }

    // React-like lifecycle methods
    protected void componentDidMount() {}
    protected void componentWillUnmount() {}
    protected void componentDidUpdate(Map<String, Object> prevState) {}

    // State management
    protected void setState(String key, Object value) {
        Map<String, Object> prevState = new HashMap<>(state);
        state.put(key, value);
        componentDidUpdate(prevState);
    }

    protected Object getState(String key) {
        return state.get(key);
    }

    // Child management
    public void addChild(Component child) {
        children.add(child);
        child.parent = this;
    }

    public void removeChild(Component child) {
        children.remove(child);
        child.parent = null;
    }

    // Abstract render method that must be implemented by all components
    public abstract void render(DrawContext context, int mouseX, int mouseY, float delta);

    // Helper methods for common operations
    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}