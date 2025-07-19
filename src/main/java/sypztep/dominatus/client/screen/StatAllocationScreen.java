package sypztep.dominatus.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.payload.IncreaseStatPayloadC2S;
import sypztep.dominatus.common.system.level.core.LevelData;
import sypztep.dominatus.common.system.stat.PlayerStatBehavior;
import sypztep.dominatus.common.system.stat.PlayerStatManager;
import sypztep.dominatus.common.system.stat.StatUI;

import java.util.ArrayList;
import java.util.List;

@Deprecated (forRemoval = true)
public class StatAllocationScreen extends Screen {
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SPACING = 5;

    private LivingLevelComponent levelComponent;
    private PlayerStatManager statManager;
    private LevelData levelData;

    // UI Elements
    private List<StatRow> statRows = new ArrayList<>();
    private ButtonWidget closeButton;

    public StatAllocationScreen() {
        super(Text.literal("Stat Allocation"));
    }

    @Override
    protected void init() {
        super.init();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            this.close();
            return;
        }

        // Get player's level component and stat manager
        this.levelComponent = ModEntityComponents.LIVINGLEVEL.get(client.player);
        this.statManager = levelComponent.getPlayerStatManager();
        this.levelData = levelComponent.getLevelData();

        if (!levelData.isPlayer() || statManager == null) {
            this.close();
            return;
        }

        setupUI();
    }

    private void setupUI() {
        statRows.clear();

        // Center the UI
        int centerX = this.width / 2;
        int startY = 60;

        // Create stat rows
        createStatRow("strength", statManager.getStrength(), centerX, startY);
        createStatRow("agility", statManager.getAgility(), centerX, startY + 35);
        createStatRow("vitality", statManager.getVitality(), centerX, startY + 70);
        createStatRow("intelligence", statManager.getIntelligence(), centerX, startY + 105);
        createStatRow("dexterity", statManager.getDexterity(), centerX, startY + 140);
        createStatRow("luck", statManager.getLuck(), centerX, startY + 175);

        // Close button
        closeButton = ButtonWidget.builder(Text.literal("Close"), button -> this.close())
                .dimensions(centerX - 50, startY + 220, 100, 20)
                .build();
        this.addDrawableChild(closeButton);
    }

    private void createStatRow(String statName, StatUI stat, int centerX, int y) {
        if (!(stat instanceof PlayerStatBehavior playerStat)) return;

        StatRow row = new StatRow();
        row.statName = statName;
        row.stat = stat;
        row.playerStat = playerStat;

        // +1 button
        row.plusOneButton = ButtonWidget.builder(Text.literal("+1"), button -> {
            increaseStat(statName, 1);
        }).dimensions(centerX - 150, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        // +5 button
        row.plusFiveButton = ButtonWidget.builder(Text.literal("+5"), button -> {
            increaseStat(statName, 5);
        }).dimensions(centerX - 80, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        // +10 button
        row.plusTenButton = ButtonWidget.builder(Text.literal("+10"), button -> {
            increaseStat(statName, 10);
        }).dimensions(centerX - 10, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        // Reset button
        row.resetButton = ButtonWidget.builder(Text.literal("Reset"), button -> {
            resetStat(statName);
        }).dimensions(centerX + 60, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        this.addDrawableChild(row.plusOneButton);
        this.addDrawableChild(row.plusFiveButton);
        this.addDrawableChild(row.plusTenButton);
        this.addDrawableChild(row.resetButton);

        statRows.add(row);
    }

    private void increaseStat(String statName, int points) {
        IncreaseStatPayloadC2S.send(statName, points);
    }

    private void resetStat(String statName) {
        // You might want to create a reset payload similar to the increase payload
        // For now, this is a placeholder
        MinecraftClient.getInstance().player.sendMessage(
                Text.literal("Reset not implemented yet for: " + statName), false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
                this.width / 2, 20, 0xFFFFFF);

        String levelInfo = String.format("Level: %d | Available Benefits: %d",
                levelData.getLevel(), levelData.getAvailableBenefits());
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(levelInfo),
                this.width / 2, 40, 0xFFFF55);

        for (StatRow row : statRows) {
            renderStatRow(context, row, mouseX, mouseY);
        }

    }

    private void renderStatRow(DrawContext context, StatRow row, int mouseX, int mouseY) {
        if (!(row.stat instanceof StatUI statUI) || !(row.stat instanceof PlayerStatBehavior playerStat)) return;

        int y = row.plusOneButton.getY();
        int leftX = this.width / 2 - 200;

        // Get stat info
        String statName = statUI.getStatName();
        List<Text> description = statUI.getEffectDescription();

        // Draw stat name and current value
        if (!description.isEmpty()) {
            context.drawTextWithShadow(this.textRenderer, description.get(0),
                    leftX, y + 5, 0xFFFFFF);
        }

        // Draw cost for +1
        int costForOne = playerStat.getPointCost();
        String costText = String.format("Cost: %d", costForOne);
        context.drawTextWithShadow(this.textRenderer, Text.literal(costText),
                leftX, y + 15, 0xFFAA00);

        // Update button states based on available benefits
        int availableBenefits = levelData.getAvailableBenefits();
        row.plusOneButton.active = availableBenefits >= costForOne;
        row.plusFiveButton.active = availableBenefits >= playerStat.calculateCost(5);
        row.plusTenButton.active = availableBenefits >= playerStat.calculateCost(10);
        row.resetButton.active = playerStat.getTotalPointsSpent() > 0;
    }

    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game
    }

    private static class StatRow {
        String statName;
        StatUI stat;
        PlayerStatBehavior playerStat;
        ButtonWidget plusOneButton;
        ButtonWidget plusFiveButton;
        ButtonWidget plusTenButton;
        ButtonWidget resetButton;
    }
}