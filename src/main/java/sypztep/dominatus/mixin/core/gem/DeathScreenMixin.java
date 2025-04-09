package sypztep.dominatus.mixin.core.gem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.dominatus.client.payload.GemBreakPayloadS2C;

import java.util.List;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {
    protected DeathScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V", ordinal = 1, shift = At.Shift.AFTER))
    private void renderGemBreakMessage(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        List<Text> brokenGemMessages = GemBreakPayloadS2C.Receiver.getLastBrokenGemMessages();
        if (brokenGemMessages != null && !brokenGemMessages.isEmpty()) {
            int deathMessageY = this.height / 2 - 40; // Position of "You Died!" message
            int gemMessageY = deathMessageY + 20; // 20 pixels below the death message
            // Draw the header
            Text header = Text.literal("The following gems have shattered:").formatted(Formatting.RED);
            context.drawTextWithShadow(this.textRenderer, header,
                    this.width / 2 - this.textRenderer.getWidth(header) / 2,
                    gemMessageY, 0xFF5555);

            // Draw each broken gem as a list
            gemMessageY += 10; // Space after header
            for (Text gemMessage : brokenGemMessages) {
                context.drawTextWithShadow(this.textRenderer, gemMessage,
                        this.width / 2 - this.textRenderer.getWidth(gemMessage) / 2,
                        gemMessageY, 0xFF5555);
                gemMessageY += 10; // Space between lines
            }
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void clearGemBreakMessage(CallbackInfo ci) {
        GemBreakPayloadS2C.Receiver.clearLastBrokenGemMessages();
    }
}