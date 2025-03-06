package sypztep.dominatus.common.util.ReformSystem;

import net.minecraft.util.Formatting;

public enum ReformType {
    // Weapon Reforms with Black Desert stats
    CRIMSON_FLAME("Crimson Flame", Formatting.RED, ReformManager.ReformCategory.WEAPON,
            new ReformManager.ReformStats(0, 0, 0, 0, 0, 0.2f, 0)),

    DESTRUCTION("Destruction", Formatting.DARK_RED, ReformManager.ReformCategory.WEAPON,
            new ReformManager.ReformStats(0, 0, 1.0f, 0, 0, 0, 0)),

    TEMPTATION("Temptation", Formatting.LIGHT_PURPLE, ReformManager.ReformCategory.WEAPON,
            new ReformManager.ReformStats(0, 0, 0, 0, 0.5f, 0, 0.15f)),

    // Armor Reforms with Black Desert stats
    IRON_WALL("Iron Wall", Formatting.GRAY, ReformManager.ReformCategory.ARMOR,
            new ReformManager.ReformStats(0, 0, 0, 2, 0, 0, 0)),

    AGILITY("Agility", Formatting.AQUA, ReformManager.ReformCategory.ARMOR,
            new ReformManager.ReformStats(5, 2, 0, 0, 0, 0, 0)),

    INTIMIDATION("Intimidation", Formatting.DARK_PURPLE, ReformManager.ReformCategory.ARMOR,
            new ReformManager.ReformStats(0, 0, 0, 2, 0, 0, 0)),

    SACRIFICE("Sacrifice", Formatting.GOLD, ReformManager.ReformCategory.ARMOR,
            new ReformManager.ReformStats(0, 0, 0, 1, 0, 0.05f, 0)),

    ULTIMATE("Ultimate", Formatting.YELLOW, ReformManager.ReformCategory.BOTH,
            new ReformManager.ReformStats(2, 0, 1.0f, 2, 0, 0, 0)),

    NONE("", Formatting.WHITE, ReformManager.ReformCategory.BOTH,
            new ReformManager.ReformStats(0, 0, 0, 0, 0, 0, 0)); // No effects

    private final String prefix;
    private final Formatting formatting;
    private final ReformManager.ReformCategory category;
    private final ReformManager.ReformStats stats;

    ReformType(String prefix, Formatting formatting, ReformManager.ReformCategory category, ReformManager.ReformStats stats) {
        this.prefix = prefix;
        this.formatting = formatting;
        this.category = category;
        this.stats = stats;
    }

    public String getPrefix() {
        return prefix;
    }

    public Formatting getFormatting() {
        return formatting;
    }

    public ReformManager.ReformCategory getCategory() {
        return category;
    }

    public ReformManager.ReformStats getStats() {
        return stats;
    }

    // Check if this reform type has an effect (not NONE)
    public boolean hasEffect() {
        return this != NONE;
    }
}
