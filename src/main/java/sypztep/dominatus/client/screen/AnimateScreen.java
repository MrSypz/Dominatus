package sypztep.dominatus.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import sypztep.dominatus.client.widget.animate.AnimationManager;

public abstract class AnimateScreen extends Screen {
    protected final AnimationManager animationManager = new AnimationManager();

    protected AnimateScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        animationManager.clearAnimations();
    }

    @Override
    public void close() {
        super.close();
        animationManager.clearAnimations();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        animationManager.updateAnimations(delta);
    }
}
