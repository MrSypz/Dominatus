package sypztep.dominatus.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.living.PlayerClassComponent;
import sypztep.dominatus.common.component.living.PlayerSkillComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.skill.active.ActiveSkill;

@Environment(EnvType.CLIENT)
public class SkillHotbarHudRenderer {

    // Constants
    private static final int SLOT_SIZE = 22;
    private static final int SLOT_SPACING = 2;
    private static final int TOTAL_WIDTH = (SLOT_SIZE * 6) + (SLOT_SPACING * 5);

    // Colors
    private static final int SLOT_BG_COLOR = 0x80000000;      // Semi-transparent black
    private static final int SLOT_BORDER_COLOR = 0xFF555555;  // Gray border
    private static final int COOLDOWN_OVERLAY_COLOR = 0x80FF0000; // Semi-transparent red
    private static final int NO_RESOURCE_OVERLAY_COLOR = 0x800000FF; // Semi-transparent blue
    private static final int EMPTY_SLOT_COLOR = 0x40333333;   // Dark gray for empty slots

    public static void register() {
        HudRenderCallback.EVENT.register(SkillHotbarHudRenderer::renderSkillHotbar);
    }

    public static void renderSkillHotbar(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Don't render if no player or HUD is hidden
        if (client.player == null || client.options.hudHidden) return;

        PlayerSkillComponent skillComp = ModEntityComponents.PLAYERSKILL.get(client.player);
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(client.player);

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Position hotbar above the vanilla hotbar
        int hotbarX = screenWidth / 2 - TOTAL_WIDTH / 2;
        int hotbarY = screenHeight - 65; // Above vanilla hotbar

        renderHotbarSlots(context, client, skillComp, classComp, hotbarX, hotbarY);
    }

    private static void renderHotbarSlots(DrawContext context, MinecraftClient client,
                                          PlayerSkillComponent skillComp, PlayerClassComponent classComp,
                                          int startX, int startY) {
        TextRenderer textRenderer = client.textRenderer;

        for (int i = 0; i < 6; i++) {
            int slotX = startX + (i * (SLOT_SIZE + SLOT_SPACING));
            int slotY = startY;

            ActiveSkill skill = skillComp.getSkillInSlot(i);
            boolean hasSkill = skill != null;
            boolean onCooldown = skillComp.isSlotOnCooldown(i);
            boolean hasResource = hasSkill && classComp.hasResource(skill.getResourceCost());

            // Slot background
            int bgColor = hasSkill ? SLOT_BG_COLOR : EMPTY_SLOT_COLOR;
            context.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, bgColor);

            // Slot border
            drawSlotBorder(context, slotX, slotY, SLOT_SIZE);

            if (hasSkill) {
                // Skill icon (placeholder - colored square based on skill type)
                int iconColor = getSkillIconColor(skill);
                context.fill(slotX + 2, slotY + 2, slotX + SLOT_SIZE - 2, slotY + SLOT_SIZE - 2, iconColor);

                // Cooldown overlay
                if (onCooldown) {
                    float cooldownPercent = skillComp.getCooldownPercentage(i);
                    int overlayHeight = (int) ((SLOT_SIZE - 4) * cooldownPercent);

                    context.fill(slotX + 2, slotY + 2, slotX + SLOT_SIZE - 2, slotY + 2 + overlayHeight, COOLDOWN_OVERLAY_COLOR);

                    // Cooldown timer text
                    long remainingMs = skillComp.getCooldownRemaining(i);
                    String cooldownText = String.format("%.1f", remainingMs / 1000.0f);
                    int textX = slotX + SLOT_SIZE / 2 - textRenderer.getWidth(cooldownText) / 2;
                    int textY = slotY + SLOT_SIZE / 2 - textRenderer.fontHeight / 2;
                    context.drawTextWithShadow(textRenderer, cooldownText, textX, textY, 0xFFFFFFFF);
                }

                // Resource insufficient overlay
                if (!hasResource && !onCooldown) {
                    context.fill(slotX + 2, slotY + 2, slotX + SLOT_SIZE - 2, slotY + SLOT_SIZE - 2, NO_RESOURCE_OVERLAY_COLOR);
                }

                // Resource cost indicator (small text at bottom)
                String costText = String.format("%.0f", skill.getResourceCost());
                int costX = slotX + SLOT_SIZE - textRenderer.getWidth(costText) - 1;
                int costY = slotY + SLOT_SIZE - textRenderer.fontHeight;
                int costColor = hasResource ? 0xFFFFFFFF : 0xFFFF6666; // White if affordable, red if not
                context.drawText(textRenderer, costText, costX, costY, costColor, false);
            }

            // Slot number (key binding)
            String slotNumber = String.valueOf(i + 1);
            context.drawTextWithShadow(textRenderer, slotNumber, slotX + 2, slotY + 2, 0xFFFFFFFF);
        }
    }

    private static void drawSlotBorder(DrawContext context, int x, int y, int size) {
        // Top
        context.fill(x, y, x + size, y + 1, SLOT_BORDER_COLOR);
        // Bottom
        context.fill(x, y + size - 1, x + size, y + size, SLOT_BORDER_COLOR);
        // Left
        context.fill(x, y, x + 1, y + size, SLOT_BORDER_COLOR);
        // Right
        context.fill(x + size - 1, y, x + size, y + size, SLOT_BORDER_COLOR);
    }

    private static int getSkillIconColor(ActiveSkill skill) {
        // Simple color coding based on skill class
        return switch (skill.getRequiredClass()) {
            case WARRIOR -> 0xFFFF4444; // Red
            case MAGE -> 0xFF4444FF;    // Blue
            case NINJA -> 0xFF44FF44;   // Green
            default -> 0xFF888888;      // Gray
        };
    }
}