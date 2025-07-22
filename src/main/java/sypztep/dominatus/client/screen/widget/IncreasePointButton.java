package sypztep.dominatus.client.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.payload.IncreaseStatPayloadC2S;
import sypztep.dominatus.common.system.stat.PlayerStat;
import sypztep.dominatus.common.system.stat.PlayerStatBehavior;
import sypztep.dominatus.common.system.stat.PlayerStatManager;
import sypztep.dominatus.common.system.stat.Stat;

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

        // Check if stat is maxed
        if (stat instanceof PlayerStat<?> playerStat && playerStat.getValue() >= Stat.MAX_STAT_VALUE) {
            sendErrorMessage(String.format("%s is already at maximum level (%d)!", statName, Stat.MAX_STAT_VALUE));
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

            boolean isMaxed = stat instanceof PlayerStat<?> playerStat && playerStat.getValue() >= Stat.MAX_STAT_VALUE;

            if (isMaxed) {
                tooltip.add(Text.of("§6§l" + stat.getStatName() + " §c§l(MAX)"));
                tooltip.add(Text.literal("♦  ").formatted(Formatting.GOLD)
                        .append(Text.literal("Current: ").formatted(Formatting.GRAY))
                        .append(Text.literal(String.valueOf(Stat.MAX_STAT_VALUE)).formatted(Formatting.GOLD)));

                tooltip.add(Text.literal("♠  ").formatted(Formatting.RED)
                        .append(Text.literal("Points spent: ").formatted(Formatting.GRAY))
                        .append(Text.literal(String.valueOf(stat.getTotalPointsSpent())).formatted(Formatting.WHITE)));

                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal("§7This stat is at maximum level!"));

                // Still show what the stat does, even when maxed
                List<Text> descriptions = stat.getEffectDescriptionWithCost(0); // Show current effects
                if (descriptions.size() > 2) { // Skip the header and cost lines
                    tooltip.addAll(descriptions.subList(2, descriptions.size()));
                }

                this.requiredStatPoints = Integer.MAX_VALUE; // Disable button
            } else {
                tooltip.add(Text.of("§6§l" + stat.getStatName()));
                tooltip.add(Text.literal("♣  ").formatted(Formatting.DARK_GREEN)
                        .append(Text.literal("Current: ").formatted(Formatting.GRAY))
                        .append(Text.literal(String.valueOf(((PlayerStat<?>)stat).getValue())).formatted(Formatting.WHITE))
                        .append(Text.literal("/").formatted(Formatting.DARK_GRAY))
                        .append(Text.literal(String.valueOf(Stat.MAX_STAT_VALUE)).formatted(Formatting.GRAY)));

                tooltip.add(Text.literal("♦  ").formatted(Formatting.RED)
                        .append(Text.literal("Points spent: ").formatted(Formatting.GRAY))
                        .append(Text.literal(String.valueOf(stat.getTotalPointsSpent())).formatted(Formatting.WHITE)));

                List<Text> descriptions = stat.getEffectDescriptionWithCost(pointsToIncrease);
                tooltip.addAll(descriptions);
            }

            // Add passive skill progression information
            addPassiveSkillTooltip(statName, ((PlayerStat<?>)stat).getValue());

        } else {
            tooltip.add(Text.of("§cInvalid stat: " + statName));
            this.requiredStatPoints = 1;
        }
    }

    /**
     * Add passive skill progression information to tooltip
     */
    private void addPassiveSkillTooltip(String statName, int currentStatValue) {
        if (stats == null || stats.getPassiveSkillManager() == null) {
            return;
        }

        List<Text> passiveTooltip = stats.getPassiveSkillManager()
                .createStatProgressionTooltip(statName, currentStatValue);

        if (!passiveTooltip.isEmpty()) {
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("§5§l⚡ PASSIVE ABILITIES ⚡"));
            tooltip.addAll(passiveTooltip);
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