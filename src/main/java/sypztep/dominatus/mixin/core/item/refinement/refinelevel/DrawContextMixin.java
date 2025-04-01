package sypztep.dominatus.mixin.core.item.refinement.refinelevel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.util.refinesystem.RefinementManager;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private MatrixStack matrices;

    @Inject(at = @At("RETURN"), method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    public void drawRefineLevel(TextRenderer textRenderer, ItemStack itemStack, int x, int y, String countOverride, CallbackInfo ci) {
        drawtextInSlot(textRenderer, itemStack, x, y, 1F);
    }

    @Unique
    public void drawtextInSlot(TextRenderer renderer, ItemStack stack, int i, int j, float scale) {
        final ClientWorld world = this.client.world;
        if (world == null || stack.isEmpty()) return;
        if (!stack.contains(ModDataComponents.REFINEMENT)) return;
        DrawContext context = ((DrawContext) (Object) this);
        int lvl = RefinementManager.getRefinement(stack).refine();
        String string = "+" + lvl;
        int stringWidth = renderer.getWidth(string);

        int x = (int) ((i + 9) / scale) - stringWidth / 2;
        int y = (int) ((j + 4) / scale);
        int color = 0xFF4F00;
        int bordercolor = 0;

        this.matrices.push();
        this.matrices.scale(scale, scale, scale);
        this.matrices.translate(0.0F, 0.0F, 180.0F);
        if (lvl > 0 && lvl < 16)
            drawBoldText(context, renderer, string, x, y, color, bordercolor);
        else {
            String romanString = RefinementManager.toRoman(lvl);
            int romanStringWidth = renderer.getWidth(romanString);
            int romanX = (int) ((i + 9) / scale) - romanStringWidth / 2;
            drawBoldText(context, renderer, romanString, romanX, y, color, bordercolor);
        }
        this.matrices.pop();
    }

    @Unique
    private void drawBoldText(DrawContext context, TextRenderer renderer, String string, int i, int j, int color, int bordercolor) {
        context.drawText(renderer, string, i + 1, j, bordercolor, false);
        context.drawText(renderer, string, i - 1, j, bordercolor, false);
        context.drawText(renderer, string, i, j + 1, bordercolor, false);
        context.drawText(renderer, string, i, j - 1, bordercolor, false);
        context.drawText(renderer, string, i, j, color, false);
    }
}
