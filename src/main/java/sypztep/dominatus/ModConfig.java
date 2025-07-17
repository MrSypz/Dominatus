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
    @Comment("Missing Indicator (default : true)")
    public static boolean missingIndicator = true;

    @ConfigEntry.Category("feature-client")
    @ConfigEntry.ColorPicker()
    @Comment("Color of the crit indicator")
    public static int critDamageColor = 0xFF4F00;

    @ConfigEntry.Category("statconfig_gameplay")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 99)
    public static int maxLevel = 99;

    @ConfigEntry.Category("statconfig_gameplay")
    @ConfigEntry.BoundedDiscrete(min = 0, max = Long.MAX_VALUE)
    public static long[] EXP_MAP = {
            548L,       // level 1
            894L,       // level 2
            1486L,      // level 3
            2173L,      // level 4
            3152L,      // level 5
            3732L,      // level 6
            4112L,      // level 7
            4441L,      // level 8
            4866L,      // level 9
            5337L,      // level 10
            5804L,      // level 11
            5883L,      // level 12
            6106L,      // level 13
            6424L,      // level 14
            7026L,      // level 15
            7624L,      // level 16
            7981L,      // level 17
            8336L,      // level 18
            8689L,      // level 19
            9134L,      // level 20
            9670L,      // level 21
            10296L,     // level 22
            11012L,     // level 23
            12095L,     // level 24
            12986L,     // level 25
            13872L,     // level 26
            14753L,     // level 27
            15628L,     // level 28
            16498L,     // level 29
            17362L,     // level 30
            18221L,     // level 31
            19074L,     // level 32
            19923L,     // level 33
            20947L,     // level 34
            21604L,     // level 35
            23334L,     // level 36
            24606L,     // level 37
            25871L,     // level 38
            26682L,     // level 39
            27932L,     // level 40
            29175L,     // level 41
            29969L,     // level 42
            31636L,     // level 43
            32856L,     // level 44
            33194L,     // level 45
            34836L,     // level 46
            36468L,     // level 47
            38523L,     // level 48
            40565L,     // level 49
            42165L,     // level 50
            43754L,     // level 51
            45334L,     // level 52
            46903L,     // level 53
            48463L,     // level 54
            50013L,     // level 55
            51976L,     // level 56
            53084L,     // level 57
            54605L,     // level 58
            56116L,     // level 59
            57618L,     // level 60
            58277L,     // level 61
            60593L,     // level 62
            63721L,     // level 63
            66005L,     // level 64
            69097L,     // level 65
            72171L,     // level 66
            74407L,     // level 67
            77445L,     // level 68
            89404L,     // level 69
            103722L,    // level 70
            113105L,    // level 71
            124848L,    // level 72
            130898L,    // level 73
            136110L,    // level 74
            143684L,    // level 75
            149620L,    // level 76
            154725L,    // level 77
            158216L,    // level 78
            175461L,    // level 79
            194586L,    // level 80
            215795L,    // level 81
            239316L,    // level 82
            265401L,    // level 83
            294329L,    // level 84
            326410L,    // level 85
            361988L,    // level 86
            401444L,    // level 87
            445201L,    // level 88
            493727L,    // level 89
            547543L,    // level 90
            607225L,    // level 91
            673412L,    // level 92
            746813L,    // level 93
            828215L,    // level 94
            918490L,    // level 95
            1018605L,   // level 96
            1129632L,   // level 97
            1252761L,   // level 98 -> 99
    };

    @ConfigEntry.Category("statconfig_gameplay")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 4096)
    public static int startStatpoints = 48;

    @ConfigEntry.Category("death_penalty")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    @Comment("Death penalty percentage (0-100). Player loses this % of next level exp when killed by monsters.")
    public static int deathPenaltyPercentage = 10;

    @ConfigEntry.Category("death_penalty")
    @Comment("Enable death penalty when killed by monsters")
    public static boolean enableDeathPenalty = true;
}