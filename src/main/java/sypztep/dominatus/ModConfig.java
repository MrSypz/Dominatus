package sypztep.dominatus;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = Dominatus.MODID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Category("feature-client")
    @Comment("Crit Indicator (default : true)")
    public static boolean damageCritIndicator = true;
    @ConfigEntry.Category("feature-client")
    @Comment("Missing Indicato (default : true)")
    public static boolean missingIndicator = true;
    @ConfigEntry.Category("feature-client")
    @ConfigEntry.ColorPicker()
    @Comment("Color of the crit indicator")
    public static int critDamageColor = 0xFF4F00;

    @ConfigEntry.Category("combat")
    @Comment("multiple hit in one attack for balance accuracy")
    public static boolean multihit = true;
    @ConfigEntry.Category("combat")
    @Comment("the delay damage to appile after player attack")
    public static float hitDelay = 0.25f;
}
