package sypztep.dominatus.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.component.living.PlayerClassComponent;
import sypztep.dominatus.common.component.living.PlayerSkillComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.payload.AssignSkillPayloadC2S;
import sypztep.dominatus.common.system.skill.active.ActiveSkill;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SkillHotbarScreen extends Screen {

    private static final int SLOT_SIZE = 32;
    private static final int LEARNED_SKILL_SIZE = 28;

    private PlayerSkillComponent skillComp;
    private PlayerClassComponent classComp;
    private List<ActiveSkill> learnedSkills;
    private Identifier[] hotbarSlots;

    private int selectedSkillIndex = -1; // For drag-and-drop

    public SkillHotbarScreen() {
        super(Text.literal("Skill Hotbar"));
    }

    @Override
    protected void init() {
        super.init();

        if (client == null || client.player == null) return;

        skillComp = ModEntityComponents.PLAYERSKILL.get(client.player);
        classComp = ModEntityComponents.PLAYERCLASS.get(client.player);
        learnedSkills = skillComp.getLearnedSkills();
        hotbarSlots = skillComp.getHotbarSlots();

        // Add close button
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> close())
                .dimensions(width / 2 - 50, height - 30, 100, 20)
                .build());

        // Add clear hotbar button
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear All"), button -> clearHotbar())
                .dimensions(width / 2 - 100, height - 55, 80, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        // Background

        // Title
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFFFF);

        // Instructions
        Text instructions = Text.literal("Click a learned skill, then click a hotbar slot to assign it")
                .formatted(Formatting.GRAY);
        context.drawCenteredTextWithShadow(textRenderer, instructions, width / 2, 35, 0xFFAAAAAA);

        // Render hotbar slots
        renderHotbarSlots(context, mouseX, mouseY);

        // Render learned skills
        renderLearnedSkills(context, mouseX, mouseY);

        // Render widgets (buttons)


        // Render tooltips
        renderTooltips(context, mouseX, mouseY);
    }

    private void renderHotbarSlots(DrawContext context, int mouseX, int mouseY) {
        int startX = width / 2 - (6 * SLOT_SIZE) / 2;
        int startY = 80;

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Hotbar Slots (1-6)"),
                width / 2, startY - 15, 0xFFFFFFFF);

        for (int i = 0; i < 6; i++) {
            int slotX = startX + (i * SLOT_SIZE);
            int slotY = startY;

            // Slot background
            int bgColor = hotbarSlots[i] != null ? 0xFF333333 : 0xFF111111;
            context.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, bgColor);

            // Slot border
            boolean hovered = mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                    mouseY >= slotY && mouseY < slotY + SLOT_SIZE;
            int borderColor = hovered ? 0xFFFFFFFF : 0xFF666666;
            drawBorder(context, slotX, slotY, SLOT_SIZE, borderColor);

            // Skill in slot
            if (hotbarSlots[i] != null) {
                ActiveSkill skill = skillComp.getSkillInSlot(i);
                if (skill != null) {
                    // Skill icon (colored square)
                    int iconColor = getSkillIconColor(skill);
                    context.fill(slotX + 2, slotY + 2, slotX + SLOT_SIZE - 2, slotY + SLOT_SIZE - 2, iconColor);

                    // Skill name (truncated)
                    String skillName = skill.getName();
                    if (textRenderer.getWidth(skillName) > SLOT_SIZE - 4) {
                        skillName = skillName.substring(0, 4) + "...";
                    }
                    context.drawText(textRenderer, skillName, slotX + 2, slotY + SLOT_SIZE - 10, 0xFFFFFFFF, false);
                }
            }

            // Slot number
            context.drawText(textRenderer, String.valueOf(i + 1), slotX + 2, slotY + 2, 0xFFFFFFFF, false);
        }
    }

    private void renderLearnedSkills(DrawContext context, int mouseX, int mouseY) {
        if (learnedSkills.isEmpty()) {
            context.drawCenteredTextWithShadow(textRenderer,
                    Text.literal("No skills learned yet").formatted(Formatting.GRAY),
                    width / 2, 180, 0xFFAAAAAA);
            return;
        }

        int startX = width / 2 - (Math.min(learnedSkills.size(), 8) * LEARNED_SKILL_SIZE) / 2;
        int startY = 160;

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Learned Skills"),
                width / 2, startY - 15, 0xFFFFFFFF);

        for (int i = 0; i < learnedSkills.size(); i++) {
            ActiveSkill skill = learnedSkills.get(i);

            int skillX = startX + ((i % 8) * LEARNED_SKILL_SIZE);
            int skillY = startY + ((i / 8) * LEARNED_SKILL_SIZE);

            // Skill background
            boolean hovered = mouseX >= skillX && mouseX < skillX + LEARNED_SKILL_SIZE &&
                    mouseY >= skillY && mouseY < skillY + LEARNED_SKILL_SIZE;
            boolean selected = selectedSkillIndex == i;

            int bgColor = selected ? 0xFF666666 : (hovered ? 0xFF444444 : 0xFF222222);
            context.fill(skillX, skillY, skillX + LEARNED_SKILL_SIZE, skillY + LEARNED_SKILL_SIZE, bgColor);

            // Skill icon
            int iconColor = getSkillIconColor(skill);
            context.fill(skillX + 2, skillY + 2, skillX + LEARNED_SKILL_SIZE - 2, skillY + LEARNED_SKILL_SIZE - 2, iconColor);

            // Border
            int borderColor = selected ? 0xFFFFFFFF : (hovered ? 0xFFAAAAAA : 0xFF666666);
            drawBorder(context, skillX, skillY, LEARNED_SKILL_SIZE, borderColor);
        }
    }

    private void renderTooltips(DrawContext context, int mouseX, int mouseY) {
        // Tooltip for hovered hotbar slot
        int hotbarStartX = width / 2 - (6 * SLOT_SIZE) / 2;
        int hotbarStartY = 80;

        for (int i = 0; i < 6; i++) {
            int slotX = hotbarStartX + (i * SLOT_SIZE);
            int slotY = hotbarStartY;

            if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                    mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {

                if (hotbarSlots[i] != null) {
                    ActiveSkill skill = skillComp.getSkillInSlot(i);
                    if (skill != null) {
                        context.drawTooltip(textRenderer, Text.literal(skill.getName() + "\n" + skill.getDescription()),
                                mouseX, mouseY);
                    }
                } else {
                    context.drawTooltip(textRenderer, Text.literal("Empty slot\nClick a skill below to assign"),
                            mouseX, mouseY);
                }
                break;
            }
        }

        // Tooltip for hovered learned skill
        if (!learnedSkills.isEmpty()) {
            int learnedStartX = width / 2 - (Math.min(learnedSkills.size(), 8) * LEARNED_SKILL_SIZE) / 2;
            int learnedStartY = 160;

            for (int i = 0; i < learnedSkills.size(); i++) {
                int skillX = learnedStartX + ((i % 8) * LEARNED_SKILL_SIZE);
                int skillY = learnedStartY + ((i / 8) * LEARNED_SKILL_SIZE);

                if (mouseX >= skillX && mouseX < skillX + LEARNED_SKILL_SIZE &&
                        mouseY >= skillY && mouseY < skillY + LEARNED_SKILL_SIZE) {

                    ActiveSkill skill = learnedSkills.get(i);
                    String tooltip = skill.getName() + "\n" +
                            skill.getDescription() + "\n" +
                            "Resource Cost: " + skill.getResourceCost() + " " + skill.getResourceType().getDisplayName();
                    context.drawTooltip(textRenderer, Text.literal(tooltip), mouseX, mouseY);
                    break;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button); // Only left click

        // Check learned skill clicks
        if (!learnedSkills.isEmpty()) {
            int learnedStartX = width / 2 - (Math.min(learnedSkills.size(), 8) * LEARNED_SKILL_SIZE) / 2;
            int learnedStartY = 160;

            for (int i = 0; i < learnedSkills.size(); i++) {
                int skillX = learnedStartX + ((i % 8) * LEARNED_SKILL_SIZE);
                int skillY = learnedStartY + ((i / 8) * LEARNED_SKILL_SIZE);

                if (mouseX >= skillX && mouseX < skillX + LEARNED_SKILL_SIZE &&
                        mouseY >= skillY && mouseY < skillY + LEARNED_SKILL_SIZE) {

                    selectedSkillIndex = i;
                    return true;
                }
            }
        }

        // Check hotbar slot clicks
        int hotbarStartX = width / 2 - (6 * SLOT_SIZE) / 2;
        int hotbarStartY = 80;

        for (int i = 0; i < 6; i++) {
            int slotX = hotbarStartX + (i * SLOT_SIZE);
            int slotY = hotbarStartY;

            if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                    mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {

                if (selectedSkillIndex >= 0 && selectedSkillIndex < learnedSkills.size()) {
                    // Assign selected skill to this slot
                    ActiveSkill selectedSkill = learnedSkills.get(selectedSkillIndex);
                    AssignSkillPayloadC2S.send(i, selectedSkill.getId());
                    selectedSkillIndex = -1; // Clear selection
                } else {
                    // Clear this slot
                    AssignSkillPayloadC2S.send(i, null);
                }
                return true;
            }
        }

        // Clear selection if clicking elsewhere
        selectedSkillIndex = -1;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void clearHotbar() {
        for (int i = 0; i < 6; i++) {
            AssignSkillPayloadC2S.send(i, null);
        }
        selectedSkillIndex = -1;
    }

    private void drawBorder(DrawContext context, int x, int y, int size, int color) {
        context.fill(x, y, x + size, y + 1, color); // Top
        context.fill(x, y + size - 1, x + size, y + size, color); // Bottom
        context.fill(x, y, x + 1, y + size, color); // Left
        context.fill(x + size - 1, y, x + size, y + size, color); // Right
    }

    private int getSkillIconColor(ActiveSkill skill) {
        return switch (skill.getRequiredClass()) {
            case WARRIOR -> 0xFFFF4444; // Red
            case MAGE -> 0xFF4444FF;    // Blue
            case NINJA -> 0xFF44FF44;   // Green
            default -> 0xFF888888;      // Gray
        };
    }

    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game
    }
}