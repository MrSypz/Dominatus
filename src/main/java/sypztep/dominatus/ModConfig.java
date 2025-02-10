package sypztep.dominatus;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = Dominatus.MODID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Category("feature-client")
    @Comment("Damage Indicator Feature (default : true) if you want to turn it off I think delete mod will be more better :)")
    public static boolean damageCritIndicator = true;
    @ConfigEntry.Category("feature-client")
    @ConfigEntry.ColorPicker()
    @Comment("Color of the crit indicator")
    public static int critDamageColor = 0xFF4F00;
}
