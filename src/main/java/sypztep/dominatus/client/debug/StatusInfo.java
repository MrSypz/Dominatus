package sypztep.dominatus.client.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import sypztep.dominatus.common.init.ModEntityAttributes;

import java.util.List;

public class StatusInfo implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        RenderSystem.enableBlend(); // Enable blending for transparency
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        drawContext.drawText(mc.textRenderer, "Player Info", 100, 80, 0xFFFFFFFF, false);
        drawContext.fill(25, 100, 250, 200, 0x80000000); // ARGB format: 0x80 (50% transparent) black
        assert player != null;
        List<String> v = List.of(
//                "AP: " + player.getAttributeBaseValue(ModEntityAttributes.GENERIC_AP),
//                "DP: " + player.getAttributeBaseValue(ModEntityAttributes.GENERIC_DP),
//                "Evasion: " + player.getAttributeBaseValue(ModEntityAttributes.GENERIC_EVASION),
//                "Accuracy: " + player.getAttributeBaseValue(ModEntityAttributes.GENERIC_ACCURACY)
        );
        int offset = 110;
        for (String s : v) {
            drawContext.drawText(mc.textRenderer, s, 35, offset, 0xFFFFFFFF, false);
            offset += 10;
        }
        RenderSystem.disableBlend(); // Disable blending after drawing
    }

}
