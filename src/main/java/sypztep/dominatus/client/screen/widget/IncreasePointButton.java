package sypztep.dominatus.client.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.payload.IncreaseStatPayloadC2S;
import sypztep.dominatus.common.system.stat.PlayerStat;
import sypztep.dominatus.common.system.stat.PlayerStatBehavior;
import sypztep.dominatus.common.system.stat.PlayerStatManager;
import sypztep.dominatus.common.system.stat.Stat;

import java.util.ArrayList;
import java.util.List;

public final class IncreasePointButton extends ActionWidgetButton {
    private final String statName;
    private final int pointsToIncrease;
    private int requiredStatPoints;

    public IncreasePointButton(int x, int y, int width, int height, Text message,
                               LivingLevelComponent stats, String statName, int pointsToIncrease,MinecraftClient client) {
        super(x, y, width, height, message, stats, client);
        this.statName = statName;
        this.pointsToIncrease = pointsToIncrease;
        this.requiredStatPoints = 1;
        initializeTooltip();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (stats == null || stats.getPlayerStatManager() == null) {
            return;
        }

        PlayerStatManager statManager = stats.getPlayerStatManager();
        PlayerStatBehavior stat = statManager.getStatByName(statName);

        if (stat == null) {
            return;
        }

        // Check if stat is maxed
        if (stat instanceof PlayerStat<?> playerStat && playerStat.getValue() >= Stat.MAX_STAT_VALUE) {
            return;
        }

        int requiredPoints = stat.calculateCost(pointsToIncrease);
        int availablePoints = stats.getAvailableBenefits();

        if (availablePoints >= requiredPoints) {
            performAction();
            playClickSound();
        }
    }



    private void performAction() {
        IncreaseStatPayloadC2S.send(statName, pointsToIncrease);
    }

    private void initializeTooltip() {
        if (stats == null || stats.getPlayerStatManager() == null) return;

        PlayerStatManager statManager = stats.getPlayerStatManager();
        PlayerStatBehavior stat = statManager.getStatByName(statName);

        if (stat != null) {
            this.requiredStatPoints = stat.calculateCost(pointsToIncrease);
        }
    }

    @Override
    protected void updateAnimations(float delta) {
        super.updateAnimations(delta);

        initializeTooltip();

        // Update enabled state based on available points
        boolean canAfford = stats != null && stats.getAvailableBenefits() >= requiredStatPoints;
        boolean isMaxed = false;

        if (stats != null && stats.getPlayerStatManager() != null) {
            PlayerStatBehavior stat = stats.getPlayerStatManager().getStatByName(statName);
            if (stat instanceof PlayerStat<?> playerStat) {
                isMaxed = playerStat.getValue() >= Stat.MAX_STAT_VALUE;
            }
        }

        setEnabled(canAfford && !isMaxed);
    }

    @Override
    protected void renderAdditionalOverlays(DrawContext context, int mouseX, int mouseY,
                                            float delta, boolean isHovered, boolean isPressed) {
        // Render tooltip on hover || ModConfig if isEnable false but tooltip are true it still render
        if (isHovered && (isEnabled() || ModConfig.tooltipinfo)) {
            renderStatTooltip(context, mouseX, mouseY);
        }
    }

    private void renderStatTooltip(DrawContext context, int mouseX, int mouseY) {
        if (stats == null || stats.getPlayerStatManager() == null) return;

        PlayerStatManager statManager = stats.getPlayerStatManager();
        PlayerStatBehavior stat = statManager.getStatByName(statName);

        if (stat == null) return;

        List<Text> tooltipLines = buildCompleteTooltip(stat);

        context.drawTooltip(client.textRenderer, tooltipLines, mouseX, mouseY);
    }

    private List<Text> buildCompleteTooltip(PlayerStatBehavior stat) {
        List<Text> tooltip = new ArrayList<>();

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
        } else {
            tooltip.add(Text.of("§6§l" + stat.getStatName()));
            tooltip.add(Text.literal("♣  ").formatted(Formatting.DARK_GREEN)
                    .append(Text.literal("Current: ").formatted(Formatting.GRAY))
                    .append(Text.literal(String.valueOf(stat.getValue())).formatted(Formatting.WHITE))
                    .append(Text.literal("/").formatted(Formatting.DARK_GRAY))
                    .append(Text.literal(String.valueOf(Stat.MAX_STAT_VALUE)).formatted(Formatting.GRAY)));

            tooltip.add(Text.literal("♦  ").formatted(Formatting.RED)
                    .append(Text.literal("Points spent: ").formatted(Formatting.GRAY))
                    .append(Text.literal(String.valueOf(stat.getTotalPointsSpent())).formatted(Formatting.WHITE)));

            List<Text> descriptions = stat.getEffectDescriptionWithCost(pointsToIncrease);
            tooltip.addAll(descriptions);
        }

        // Add passive skill progression information
        addPassiveSkillTooltip(tooltip, statName, stat.getValue());

        return tooltip;
    }

    /**
     * Add passive skill progression information to tooltip
     */
    private void addPassiveSkillTooltip(List<Text> tooltip, String statName, int currentStatValue) {
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

    @Override
    protected int getBackgroundColor() {
        return 0xFF1A472A; // Dark green
    }

    @Override
    protected int getHoverBackgroundColor() {
        return 0xFF2D5A3D; // Lighter green
    }

    @Override
    protected int getPressedBackgroundColor() {
        return 0xFF0F2C18; // Darker green
    }

    @Override
    protected int getBorderColor() {
        return 0xFF4CAF50; // Green border
    }

    @Override
    protected int getHoverBorderColor() {
        return 0xFF66BB6A; // Lighter green border
    }

    @Override
    protected int getHoverTextColor() {
        return 0xFF81C784; // Light green text on hover
    }

    @Override
    protected boolean hasTextShadow() {
        return true; // Enable text shadow for better readability
    }
}