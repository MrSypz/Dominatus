package sypztep.dominatus.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import sypztep.dominatus.client.widget.DrawContextUtil;
import sypztep.dominatus.client.widget.animate.Animated;
import sypztep.dominatus.client.widget.animate.FadeAnimation;

@Environment(EnvType.CLIENT)
public final class PlayerInfoScreen extends AnimateScreen {
    private static final float FADE_DURATION = 10.0f; // Fade duration in seconds
    private static final int BACKGROUND_COLOR = 0xF0121212; // Target color
    private FadeAnimation backgroundFade;

    public PlayerInfoScreen() {
        super(Text.literal("Player Info"));
    }

    @Override
    protected void init() {
        super.init();

        backgroundFade = animationManager.createFadeAnimation(FADE_DURATION, false, BACKGROUND_COLOR)
                .withRender(DrawContextUtil::fillScreen);
        backgroundFade.start();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        backgroundFade.render(context);

    }
}