package sypztep.penomior;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.Arrays;
import java.util.List;
@Config(name = Penomior.MODID)
public class ModConfig implements ConfigData {
    @Comment("Excluded the damage intake that by-pass Iframe")
    @ConfigEntry.Category("gameplay")
    public static List<String> dmgReceiveExcludedEntities = Arrays.asList(
            "minecraft:slime", "minecraft:magma_cube"
    );
    @Comment("Excluded the Entities that by-pass Iframe")
    @ConfigEntry.Category("gameplay")
    public static List<String> attackExcludedEntities = List.of(
            "minecraft:warden"
    );
    @ConfigEntry.Category("gameplay")
    @Comment("How weak a player's attack can be before it gets nullified, from 0 (0%, cancels multiple attacks on the same tick) to 1 (100%, players cannot attack), or -0.1 (disables this feature)")
    public static float attackCancelThreshold = 0.15f;
    @ConfigEntry.Category("gameplay")
    @Comment("How weak a player's attack can be before the knockback gets nullified, from 0 (0%, cancels multiple attacks on the same tick) to 1 (100%, no knockback), or -0.1 (disables this feature)")
    public static float knockbackCancelThreshold = 0.75f;
    @Comment("How many ticks of i-frames does an entity get when damaged, from 10 (default) 0.5 sec, to 2^31-1 (nothing can take damage)")
    @ConfigEntry.Category("gameplay")
    public static int iFrameDuration = 10;

    @ConfigEntry.Category("refinement")
    public static boolean refineItemUnbreak = true;
    @ConfigEntry.Category("refinement")
    @Comment("exclusive item which is apply a stats to player by ignore the equipmentslot")
    public static List<String> exclusiveModItemID = List.of(
            "pointblank"
    );

    @ConfigEntry.Category("feature-client")
    public static boolean missingIndicator = true;
    @ConfigEntry.Category("feature-client")
    public static boolean damageIndicator = true;
    @ConfigEntry.Category("feature-client")
    public static boolean xpnotify = false;
    @ConfigEntry.Category("feature-client")
    public static boolean lossxpnotify = true;
    @ConfigEntry.Category("feature-client")
    public static boolean damageNumberIndicator = true;
    @ConfigEntry.Category("feature-client")
    @ConfigEntry.ColorPicker()
    public static int normalDamageColor = 0xD43333;

    @ConfigEntry.Category("feature-client")
    @ConfigEntry.ColorPicker()
    public static int magicDamageColor = 0x3A57D6; // Blue color code

    @ConfigEntry.Category("feature-client")
    @ConfigEntry.ColorPicker()
    public static int trueDamageColor = 0x8A2BE2; // Purple color code

    @ConfigEntry.Category("feature")
    public static boolean missingArrowPassthough = true;
    @ConfigEntry.Category("feature")
    public static boolean mobEvasion = true;

    @ConfigEntry.Category("statconfig")
    public static float xpLossPercentage = 0.05f;
    @ConfigEntry.Category("statconfig")
    public static RenderStyle renderStyle = RenderStyle.SLATE;
    @ConfigEntry.Category("statconfig_gameplay")
    public static int[] statPointLadder = new int[] {
            // Levels 1-10
            2,
            // Levels 11-20
            3,
            // Levels 21-30
            4,
            // Levels 31-40
            5,
            // Levels 41-50
            6,
            // Levels 51-60
            7,
            // Levels 61-70
            8,
            // Levels 71-80
            9,
            // Levels 81-90
            10,
            // Levels 91-100
            11,
            // Levels 101-110
            12,
            // Levels 111-120
            13,
            // Levels 121-130
            14
    };
    @ConfigEntry.Category("statconfig_gameplay")
    @ConfigEntry.BoundedDiscrete(min = 1,max = Integer.MAX_VALUE)
    public static int maxLevel = 99;
    @ConfigEntry.Category("statconfig_gameplay")
    @ConfigEntry.BoundedDiscrete(min = 0,max = Integer.MAX_VALUE)
    public static double exponentExp = 1.5;
    @ConfigEntry.Category("statconfig_gameplay")
    @ConfigEntry.BoundedDiscrete(min = 0,max = Integer.MAX_VALUE)
    public static int baseExp = 100;
    @ConfigEntry.Category("statconfig_gameplay")
    @ConfigEntry.BoundedDiscrete(min = 0,max = Integer.MAX_VALUE)
    public static int startStatpoints = 48;
    @ConfigEntry.Category("statconfig")
    public static boolean tooltipinfo = true;
    @ConfigEntry.Category("statconfig")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public static int barColor = 0xFFFFFFFF;
    @ConfigEntry.Category("statconfig")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public static int barBGColor = 0xFFAB5C00;

    public enum RenderStyle {
        BAR,
        SLATE
    }
}
