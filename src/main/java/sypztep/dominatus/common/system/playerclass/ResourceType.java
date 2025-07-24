package sypztep.dominatus.common.system.playerclass;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum ResourceType {
    MANA(0x3366FF, 2.0f, "Mana", Formatting.BLUE),
    RAGE(0xFF3333, 1.5f, "Rage", Formatting.RED);

    private final int color;
    private final float baseRegenRate;
    private final String displayName;
    private final Formatting textColor;

    ResourceType(int color, float baseRegenRate, String displayName, Formatting textColor) {
        this.color = color;
        this.baseRegenRate = baseRegenRate;
        this.displayName = displayName;
        this.textColor = textColor;
    }

    public int getColor() { return color; }
    public float getBaseRegenRate() { return baseRegenRate; }
    public String getDisplayName() { return displayName; }
    public Formatting getTextColor() { return textColor; }

    public Text getFormattedName() {
        return Text.literal(displayName).formatted(textColor);
    }
}
