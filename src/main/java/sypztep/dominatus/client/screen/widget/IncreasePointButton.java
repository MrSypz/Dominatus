package sypztep.dominatus.client.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.payload.IncreaseStatPayloadC2S;
import sypztep.dominatus.common.system.stat.PlayerStatBehavior;
import sypztep.dominatus.common.system.stat.PlayerStatManager;
import sypztep.dominatus.common.system.stat.elements.player.PlayerStat;

import java.util.List;

public final class IncreasePointButton extends ActionWidgetButton {
    private final String statName; // Changed from StatTypes to String
    private final int pointsToIncrease;

    public IncreasePointButton(int x, int y, int width, int height, Text message,
                               LivingLevelComponent stats, String statName, int pointsToIncrease) {
        super(x, y, width, height, message, stats);
        this.statName = statName;
        this.pointsToIncrease = pointsToIncrease;
        this.requiredStatPoints = 1; // Will be updated in initializeTooltip
        initializeTooltip();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (stats == null || stats.getPlayerStatManager() == null) {
            sendErrorMessage("Player stats not available!");
            return;
        }

        PlayerStatManager statManager = stats.getPlayerStatManager();
        PlayerStatBehavior stat = statManager.getStatByName(statName);

        if (stat == null) {
            sendErrorMessage("Invalid stat: " + statName);
            return;
        }

        int requiredPoints = stat.calculateCost(pointsToIncrease);
        int availablePoints = stats.getAvailableBenefits();

        if (availablePoints >= requiredPoints) {
            performAction();
        } else {
            sendErrorMessage("Not enough benefit points! Need " + requiredPoints + ", have " + availablePoints);
        }
    }

    private void performAction() {
        if (stats.getAvailableBenefits() > 0) {
            IncreaseStatPayloadC2S.send(statName, pointsToIncrease);
        }
    }

    private void initializeTooltip() {
        tooltip.clear();

        if (stats == null || stats.getPlayerStatManager() == null) {
            tooltip.add(Text.of("Stats not available"));
            return;
        }

        PlayerStatManager statManager = stats.getPlayerStatManager();
        PlayerStatBehavior stat = statManager.getStatByName(statName);

        if (stat != null) {
            this.requiredStatPoints = stat.calculateCost(pointsToIncrease);

            tooltip.add(Text.of("§6§l" + stat.getStatName()));
            tooltip.add(Text.literal("♣  ").formatted(Formatting.DARK_GREEN)
                    .append(Text.literal("Current: ").formatted(Formatting.GRAY))
                    .append(Text.literal(String.valueOf(((PlayerStat<?>)stat).getValue())).formatted(Formatting.WHITE)));

            tooltip.add(Text.literal("♦  ").formatted(Formatting.RED)
                    .append(Text.literal("Points spent: ").formatted(Formatting.GRAY))
                    .append(Text.literal(String.valueOf(stat.getTotalPointsSpent())).formatted(Formatting.WHITE)));

            List<Text> descriptions = stat.getEffectDescriptionWithCost(pointsToIncrease);
            tooltip.addAll(descriptions);
        } else {
            tooltip.add(Text.of("§cInvalid stat: " + statName));
            this.requiredStatPoints = 1;
        }
    }

    private void sendErrorMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.of("§c" + message), false);
        }
    }

    // Override renderWidget to update tooltip and required points dynamically
    @Override
    protected void renderWidget(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        initializeTooltip();

        localStatPoints = stats.getAvailableBenefits();

        super.renderWidget(context, mouseX, mouseY, delta);
    }
}