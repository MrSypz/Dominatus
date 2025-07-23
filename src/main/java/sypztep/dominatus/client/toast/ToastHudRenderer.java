package sypztep.dominatus.client.toast;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class ToastHudRenderer implements HudRenderCallback {

    public ToastHudRenderer() {
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.getDebugHud().shouldShowDebugHud()) {
            return;
        }

        if (client.currentScreen != null) {
            return;
        }

        float deltaTime = tickCounter.getTickDelta(false) / 20.0f; // Convert to seconds
        int screenWidth = client.getWindow().getScaledWidth();

        ToastRenderer.renderToasts(drawContext, screenWidth, deltaTime);
    }
    public static void register() {
        HudRenderCallback.EVENT.register(new ToastHudRenderer());
    }
}