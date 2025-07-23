package sypztep.dominatus.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.screen.widget.*;
import sypztep.dominatus.client.toast.ToastRenderer;
import sypztep.dominatus.client.util.AnimationUtils;
import sypztep.dominatus.client.util.CyclingTextIcon;
import sypztep.dominatus.client.util.DrawContextUtils;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.stat.PlayerStatBehavior;
import sypztep.dominatus.common.system.stat.PlayerStatManager;

import java.util.*;

@Environment(EnvType.CLIENT)
public final class PlayerInfoScreen extends Screen {
    // 1. Constants - EXACTLY as original
    private static final float ANIMATION_DURATION = 12.0f; // Duration of the animation in seconds
    private static final float FINAL_Y_OFFSET = 50.0f; // Final vertical offset of the text

    // Animations
    private Animation verticalAnimation;
    private Animation fadeAnimation;

    // Player Stats
    private final LivingLevelComponent playerStats;

    // UI Components
    private List<IncreasePointButton> increaseButtons;
    private List<Text> texts;
    private final CyclingTextIcon cyclingTextIcon;
    private SmoothProgressBar progessBar;  // Keep original spelling
    private final ScrollableTextList playerInfo;

    // Layout and Sizing - EXACTLY as original
    private final int buttonHeight = 16;
    private final int statLabelWidth = 30;
    private final int statRowHeight = 25;

    // Add the stat arrays that replace StatTypes enum
    private static final String[] STAT_NAMES = {"strength", "agility", "vitality", "intelligence", "dexterity", "luck"};
    private static final String[] STAT_DISPLAY_NAMES = {"STR", "AGI", "VIT", "INT", "DEX", "LUK"};

    public PlayerInfoScreen(MinecraftClient client) {
        super(Text.literal("Hello")); // Keep original title
        assert client.player != null;
        this.playerStats = ModEntityComponents.LIVINGLEVEL.get(client.player);
        Map<String, Object> infoKeys = createPlayerInfoKey(client);
        List<ListElement> listInfo = createListItems();

        this.playerInfo = new ScrollableTextList(listInfo, infoKeys);
        this.cyclingTextIcon = new CyclingTextIcon(100);
    }

    public void updateValues(MinecraftClient client) {
        Map<String, Object> values = createPlayerInfoKey(client);
        this.playerInfo.updateValues(values);
    }

    private Map<String, Object> createPlayerInfoKey(MinecraftClient client) {
        Map<String, Object> values = new HashMap<>();

        assert client.player != null;
        double armor = client.player.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
        double armorToughness = client.player.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        PlayerStatManager statManager = playerStats.getPlayerStatManager();

        values.put("phyd", client.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
        values.put("meleed", client.player.getAttributeValue(ModEntityAttributes.MELEE_ATTACK_DAMAGE));
        values.put("projd", client.player.getAttributeValue(ModEntityAttributes.PROJECTILE_ATTACK_DAMAGE));
        values.put("asp", client.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED));
        values.put("bkdmg", client.player.getAttributeValue(ModEntityAttributes.BACK_ATTACK) * 100f);
        values.put("cdmg", client.player.getAttributeValue(ModEntityAttributes.CRIT_DAMAGE) * 100f);
        values.put("ccn", client.player.getAttributeValue(ModEntityAttributes.CRIT_CHANCE) * 100f);
        values.put("acc", client.player.getAttributeValue(ModEntityAttributes.ACCURACY)); // Updated this line
        values.put("hp", client.player.getHealth());
        values.put("maxhp", client.player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH));
        values.put("dp", armor + (2.0f + armorToughness / 4.0f));
        values.put("nhrg", client.player.getAttributeValue(ModEntityAttributes.HEALTH_REGEN));
        values.put("hef", client.player.getAttributeValue(ModEntityAttributes.HEAL_EFFECTIVE));
        values.put("eva", client.player.getAttributeValue(ModEntityAttributes.EVASION)); // Updated this line

        // Updated stat retrieval using PlayerStatManager
        values.put("str", statManager.getStatValueByName("strength"));
        values.put("agi", statManager.getStatValueByName("agility"));
        values.put("vit", statManager.getStatValueByName("vitality"));
        values.put("int", statManager.getStatValueByName("intelligence"));
        values.put("dex", statManager.getStatValueByName("dexterity"));
        values.put("luk", statManager.getStatValueByName("luck"));

        values.put("mdmg", client.player.getAttributeValue(ModEntityAttributes.MAGIC_ATTACK_DAMAGE));
        values.put("mresis", client.player.getAttributeValue(ModEntityAttributes.MAGIC_RESISTANCE) * 100f);
        return values;
    }

    private List<ListElement> createListItems() {
        List<ListElement> listElements = new ArrayList<>();

        listElements.add(new ListElement(Text.translatable("dominatus.info.header_1"), Dominatus.id("hud/container/icon_1")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.physical")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.melee_damage")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.projectile_damage")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.attack_speed")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.accuracy")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.backattack_damage")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.critical_damage")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.critical_chance")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.header_2"), Dominatus.id("hud/container/icon_0")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.magic_damage")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.header_3"), Identifier.ofVanilla("hud/heart/full")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.health")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.max_health")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.defense")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.nature_health_regen")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.heal_effective")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.evasion")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.header_4"), Identifier.ofVanilla("icon/accessibility")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.strength")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.agility")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.vitality")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.intelligence")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.dexterity")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.luck")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.header_5"), Dominatus.id("hud/container/icon_2")));
        listElements.add(new ListElement(Text.translatable("dominatus.info.magic_resistance")));

        return listElements;
    }

    @Override
    protected void init() {
        super.init();
        this.verticalAnimation = new Animation(ANIMATION_DURATION ); // Single play for vertical animation
        this.fadeAnimation = new Animation(ANIMATION_DURATION); // Single play for fade animation
        this.progessBar = new SmoothProgressBar(ANIMATION_DURATION * 1.4f, false, 400, 2);
        increaseButtons = new ArrayList<>(); // Initialize the list to hold buttons

        int y = 50;
        // Use the stat arrays instead of StatTypes.values()
        for (String statName : STAT_NAMES) {
            int statValueWidth = 30;
            int startX = 50;
            int buttonX = startX + statLabelWidth + statValueWidth + 10; // Some spacing
            int buttonY = y;
            int buttonWidth = 16;
            IncreasePointButton increaseButton = new IncreasePointButton(buttonX, buttonY, buttonWidth, buttonHeight, Text.of("+"), playerStats, statName, 1,client); // Updated constructor
            this.addDrawableChild(increaseButton);
            increaseButtons.add(increaseButton);

            y += statRowHeight; // Move to the next row
        }

        // Updated to use new level system methods
        texts = Arrays.asList(
                Text.of("Lvl Progress: " + playerStats.getExperience() + "/" + playerStats.getExperienceToNextLevel()),
                Text.of(playerStats.getExperienceToNextLevel() + " XP to Level " + (playerStats.getLevel() + 1)),
                Text.of("Level: " + playerStats.getLevel() + " " + String.format("%.1f%%", playerStats.getExperiencePercentage()))
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        assert client != null;
        updateValues(client);

        verticalAnimation.update(delta);
        fadeAnimation.update(delta);
        progessBar.update(delta);

        int screenWidth = this.width;
        int screenHeight = this.height;

        float contentSectionWidthRatio = 0.25f; // 25% of screen width
        float contentSectionHeightRatio = 0.75f; // 50% of screen height (keep original comment)

        int contentWidth = (int) (screenWidth * contentSectionWidthRatio);
        int contentHeight = (int) (screenHeight * contentSectionHeightRatio);

        int xOffset = (int) (screenWidth * 0.67f); // 2/3 of the screen width
        int yOffset = (int) AnimationUtils.getPositionOffset(verticalAnimation.getProgress(), FINAL_Y_OFFSET, screenHeight);

        // Set background color
        DrawContextUtils.fillScreen(context, 0xF0121212);

        drawStatsSection(context, xOffset, yOffset, contentWidth, contentHeight, delta);

        renderStatsAndButtons(context, screenWidth, yOffset, mouseX, mouseY, delta);

        renderBottomLeftSection(context, screenWidth, screenHeight, delta);

        renderMiddleSection(context, screenWidth, screenHeight, fadeAnimation.getProgress());

        // Draw header section - updated translation keys
        drawHeaderSection(context, xOffset + 100, yOffset, fadeAnimation.getProgress(), "dominatus.gui.player_info.header");
        drawHeaderSection(context, (int) (screenWidth * 0.025f) + 80, yOffset, fadeAnimation.getProgress(), "dominatus.gui.player_info.header_level");

        renderToastsOverScreen(context, delta);
    }
    private void renderToastsOverScreen(DrawContext context, float delta) {
        float deltaTime = delta / 20.0f;
        ToastRenderer.renderToasts(context, this.width, deltaTime);
    }
    private void drawStatsSection(DrawContext context, int xOffset, float yOffset, int contentWidth, int contentHeight, float deltatick) {
        this.playerInfo.render(context, this.textRenderer, xOffset + 25, (int) (yOffset + 55), contentWidth, contentHeight, 0.5f, 1f, AnimationUtils.getAlpha(fadeAnimation.getProgress()), deltatick);
    }

    private void renderStyledText(DrawContext context, int x, int y, int statValue, float scale) { // Updated parameter name
        Text perPointText = Text.of(String.valueOf(statValue)).copy().setStyle(Style.EMPTY.withColor((0xF17633))); // Updated variable name
        Text pointText = Text.of(" Point").copy().setStyle(Style.EMPTY.withColor(Formatting.WHITE));

        Text text = Text.empty()
                .append(perPointText)
                .append(pointText);

        // Render the styled text
        context.drawTextWithShadow(this.textRenderer, text, (int) (x / scale), (int) (y / scale), 0xFFFFFF);
    }

    private void renderStatsAndButtons(DrawContext context, int screenWidth, int yOffset, int mouseX, int mouseY, float delta) {
        int rectX = (int) (screenWidth * 0.025f); // X position of the rectangle
        int rectY = yOffset + 20;                 // Y position of the rectangle
        int y;                       // Start slightly below the top of the rectangle to add padding
        int labelX = rectX + 10;                  // Start slightly inside the left edge of the rectangle

        int buttonIndex = 0;

        // Calculate maximum width and height needed
        int maxWidth = 0;
        int totalHeight = 0;

        MatrixStack matrixStack = context.getMatrices();

        PlayerStatManager statManager = playerStats.getPlayerStatManager();

        // Measure width of the widest stat label + value
        for (int i = 0; i < STAT_NAMES.length; i++) {
            String displayName = STAT_DISPLAY_NAMES[i];
            String label = displayName + ":";
            int labelWidth = this.textRenderer.getWidth(Text.of(label));

            // Get point cost (equivalent to getIncreasePerPoint in original)
            int pointCost = 1; // Default value
            if (statManager != null) {
                PlayerStatBehavior stat = statManager.getStatByName(STAT_NAMES[i]);
                if (stat != null) {
                    pointCost = stat.getPointCost(); // This replaces getIncreasePerPoint()
                }
            }

            int valueWidth = this.textRenderer.getWidth(Text.of(pointCost + " Point"));

            // Add padding to width calculation
            maxWidth = Math.max(maxWidth, labelWidth + valueWidth + 50); // 20 is padding
            totalHeight += statRowHeight + 2; // Total height for each row
        }

        // Draw the rectangle with the calculated size
        DrawContextUtils.drawRect(context, rectX, rectY, maxWidth, totalHeight, 0xFF1E1E1E);

        // Render the content inside the rectangle
        y = rectY + 10; // Reset Y to the top of the rectangle plus padding

        for (int i = 0; i < STAT_NAMES.length; i++) {
            String statName = STAT_NAMES[i];
            String displayName = STAT_DISPLAY_NAMES[i];

            int labelY = y + (buttonHeight - this.textRenderer.fontHeight) / 2;
            float scale = 0.9f;
            // Draw Stat Label
            matrixStack.push();
            matrixStack.scale(scale, scale, 0);
            context.drawTextWithShadow(this.textRenderer, Text.of(displayName + ":"), (int) (labelX / scale), (int) (labelY / scale), 0xFFFFFF);
            matrixStack.pop();
            DrawContextUtils.renderHorizontalLineWithCenterGradient(context, rectX, y + 20, maxWidth, 1, 1, 0xFFFFFFFF, 0x00FFFFFF);

            // Draw Stat Value (showing point cost like the original)
            int valueX = labelX + statLabelWidth;

            // Get point cost (equivalent to getIncreasePerPoint in original)
            int pointCost = 1; // Default value
            if (statManager != null) {
                PlayerStatBehavior stat = statManager.getStatByName(statName);
                if (stat != null) {
                    pointCost = stat.getPointCost(); // This shows the cost to increase the stat
                }
            }

            matrixStack.push();
            matrixStack.scale(scale, scale, 0);
            renderStyledText(context, valueX, labelY, pointCost, scale);
            matrixStack.pop();

            // Position and render the corresponding button
            IncreasePointButton button = increaseButtons.get(buttonIndex);
            int buttonX = valueX + statLabelWidth + 12; // Position the button to the right of the stat value
            button.setX(buttonX);
            button.setY(y); // Align the button with the current row
            button.render(context, mouseX, mouseY, delta);

            // Increment Y for the next stat row
            y += statRowHeight;
            buttonIndex++;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!texts.isEmpty())
            cyclingTextIcon.updateTexts(texts);
    }

    private void renderMiddleSection(DrawContext context, int screenWidth, int screenHeight, float fadeProgress) {
        int remainingPoints = playerStats.getAvailableBenefits(); // Updated to use new method
        float scaleFactor = 2.5f;
        int adjustedX = (int) (screenWidth * 0.5f / scaleFactor);
        int adjustedY = (int) (screenHeight * 0.5f / scaleFactor);

        String fuckyougramma = remainingPoints > 1 ? "Benefit Points" : "Benefit Point"; // Updated text

        context.getMatrices().push();
        context.getMatrices().scale(scaleFactor, scaleFactor, 0.0f);

        AnimationUtils.drawFadeCenteredText(context, textRenderer, Text.of("" + remainingPoints), adjustedX, adjustedY, 0xF17633, AnimationUtils.getAlpha(fadeProgress));

        context.getMatrices().pop();
        AnimationUtils.drawFadeCenteredText(context, textRenderer, Text.of(fuckyougramma), (int) (screenWidth * 0.5f), (int) (screenHeight * 0.5f) + 25, 0xFFFFFF, AnimationUtils.getAlpha(fadeProgress));
    }

    private void renderBottomLeftSection(DrawContext context, int screenWidth, int screenHeight, float delta) {
        int labelX = (int) (screenWidth * 0.025f); // X position, a little inset from the left edge
        int labelY = screenHeight - 45; // Y position, near the bottom of the screen
        context.getMatrices().push();
        context.getMatrices().scale(0.8f, 0.8f, 1.0f);
        int scaledLabelX = (int) (labelX / 0.8f); // Adjust X position for scaling
        int scaledLabelY = (int) (labelY / 0.8f); // Adjust Y position for scaling

        cyclingTextIcon.render(context, textRenderer, delta, scaledLabelX, scaledLabelY + 20, 0xFFFFFF);  // Lvl Progession

        progessBar.setProgress(playerStats.getExperience(), playerStats.getExperienceToNextLevel()); // Updated to use new methods
        progessBar.render(context, scaledLabelX, scaledLabelY + 40);
        context.getMatrices().pop();
    }

    private void drawHeaderSection(DrawContext context, int x, float verticalOffset, float fadeProgress, String text) {
        int textWidth = this.textRenderer.getWidth(Text.translatable(text));
        int centeredX = x - (textWidth / 2);
        AnimationUtils.drawFadeText(context, this.textRenderer, Text.translatable(text), centeredX, (int) verticalOffset, AnimationUtils.getAlpha(fadeProgress));
        int lineY1 = (int) (verticalOffset - 4);
        int lineY2 = (int) (verticalOffset + 10);
        DrawContextUtils.renderHorizontalLineWithCenterGradient(context, centeredX - 16, lineY1, textWidth + 32, 1, 400, 0xFFFFFFFF, 0, fadeProgress);
        DrawContextUtils.renderHorizontalLineWithCenterGradient(context, centeredX - 16, lineY2, textWidth + 32, 1, 400, 0xFFFFFFFF, 0, fadeProgress);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean isAnyScrolled = false;
        int scrollAmount = (int) (verticalAmount * 25);
        if (playerInfo.isMouseOver(mouseX, mouseY, playerInfo.getX(), playerInfo.getY() - 30, playerInfo.getWidth(), playerInfo.getHeight())) {
            playerInfo.scroll(scrollAmount, mouseX, mouseY + 30);
            isAnyScrolled = true;
        }
        return isAnyScrolled || super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}