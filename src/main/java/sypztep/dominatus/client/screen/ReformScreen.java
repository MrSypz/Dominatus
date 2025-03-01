package sypztep.dominatus.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.screen.ReformScreenHandler;

@Environment(EnvType.CLIENT)
public final class ReformScreen extends HandledScreen<ReformScreenHandler> {
    public static final Identifier TEXTURE = Dominatus.id("gui/container/reform_screen.png");

    public ReformScreen(ReformScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, Text.translatable(Dominatus.MODID + ".reform_screen"));
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
    }
}
