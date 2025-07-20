package sypztep.dominatus.client.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class CyclingTextIcon {
    private int timer;
    private int currentIndex;
    private List<Text> texts = new ArrayList<>();
    private final int delay;

    public CyclingTextIcon() {
        this.delay = 30;
    }
    public CyclingTextIcon(int delay) {
        this.delay = delay;
    }
    public void updateTexts(List<Text> texts) {
        if (!this.texts.equals(texts)) {
            this.texts = texts;
            this.currentIndex = 0;
        }

        if (!this.texts.isEmpty() && ++this.timer % delay == 0) {
            this.currentIndex = (this.currentIndex + 1) % this.texts.size();
        }
    }

    public void render( DrawContext context, TextRenderer textRenderer, float delta, int x, int y, int color) {
        if (!this.texts.isEmpty()) {
            boolean bl = this.texts.size() > 1 && this.timer >= delay;
            float f = bl ? this.computeAlpha(delta) : 1.0F;
            if (f < 1.0F) {
                int i = Math.floorMod(this.currentIndex - 1, this.texts.size());
                this.drawTextIcon(this.texts.get(i), textRenderer, color, 1.0F - f, context, x, y);
            }

            this.drawTextIcon(this.texts.get(this.currentIndex), textRenderer, color, f, context, x, y);
        }
    }

    private void drawTextIcon(Text text, TextRenderer textRenderer, int color, float alpha, DrawContext context, int x, int y) {
        int alphaColor = (Math.min(255, Math.max(0, (int) (alpha * 255))) << 24) | (color & 0xFFFFFF);
        context.drawText(textRenderer, text, x, y, alphaColor, false);
    }

    private float computeAlpha(float delta) {
        float f = (float)(this.timer % delay) + delta;
        return Math.min(f, 4.0F) / 4.0F;
    }
}
