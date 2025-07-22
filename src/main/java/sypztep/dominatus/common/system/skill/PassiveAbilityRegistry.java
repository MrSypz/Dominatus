package sypztep.dominatus.common.system.skill;

import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.system.skill.passives.agility.*;
import sypztep.dominatus.common.system.skill.passives.dexterity.*;
import sypztep.dominatus.common.system.skill.passives.intelligence.*;
import sypztep.dominatus.common.system.skill.passives.luck.*;
import sypztep.dominatus.common.system.skill.passives.strength.*;
import sypztep.dominatus.common.system.skill.passives.vitality.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Central registry for all passive abilities in the game
 */
public class PassiveAbilityRegistry {
    private static final Map<Identifier, PassiveAbility> PASSIVES = new HashMap<>();
    private static final Map<String, List<PassiveAbility>> PASSIVES_BY_STAT = new HashMap<>();

    static {
        initializePassives();
    }

    private static void initializePassives() {
        // Initialize stat type lists
        PASSIVES_BY_STAT.put("strength", new ArrayList<>());
        PASSIVES_BY_STAT.put("agility", new ArrayList<>());
        PASSIVES_BY_STAT.put("vitality", new ArrayList<>());
        PASSIVES_BY_STAT.put("intelligence", new ArrayList<>());
        PASSIVES_BY_STAT.put("dexterity", new ArrayList<>());
        PASSIVES_BY_STAT.put("luck", new ArrayList<>());

        // Register all passive abilities
        registerStrengthPassives();
        registerAgilityPassives();
        registerVitalityPassives();
        registerIntelligencePassives();
        registerDexterityPassives();
        registerLuckPassives();

        // Sort passives by required stat value for each stat type
        for (List<PassiveAbility> passives : PASSIVES_BY_STAT.values()) {
            passives.sort(Comparator.comparingInt(PassiveAbility::getRequiredStatValue));
        }
    }

    private static void registerStrengthPassives() {
        register(new IronGripPassive());           // STR 10 - +1 Attack Damage
        register(new PowerStrikePassive());       // STR 20 - +25% Critical Damage
        register(new MightyBlowPassive());        // STR 35 - +15% Melee Damage
        register(new BerserkerRagePassive());     // STR 50 - +5% Attack Speed per missing 10% HP
        register(new TitanStrengthPassive());     // STR 75 - +50% Block Break Speed
        register(new LegendaryMightPassive());    // STR 99 - +2 Base Attack Damage
    }

    private static void registerAgilityPassives() {
        register(new SwiftFootPassive());         // AGI 10 - +10% Movement Speed
        register(new QuickReflexPassive());       // AGI 20 - +5 Evasion
        register(new FleetFootedPassive());       // AGI 35 - +20% Attack Speed
        register(new ShadowStepPassive());        // AGI 50 - Chance to avoid damage completely
        register(new WindWalkerPassive());        // AGI 75 - No fall damage + jump boost
        register(new PhantomSpeedPassive());      // AGI 99 - +50% Movement Speed
    }

    private static void registerVitalityPassives() {
        register(new HealthyPassive());           // VIT 10 - +20 Max Health
        register(new RegenerationPassive());     // VIT 20 - +50% Health Regen
        register(new IronSkinPassive());          // VIT 35 - +15% Physical Resistance
        register(new VitalityBoostPassive());     // VIT 50 - +40% Max Health
        register(new EndurancePassive());        // VIT 75 - +100% Health Regen + Heal Effectiveness
        register(new ImmortalVitalityPassive());  // VIT 99 - Chance to survive lethal damage
    }

    private static void registerIntelligencePassives() {
        register(new ManaEfficiencyPassive());    // INT 10 - +15% Magic Damage
        register(new WisdomPassive());            // INT 20 - +25% Magic Resistance
        register(new ArcaneKnowledgePassive());   // INT 35 - +30% Magic Damage
        register(new ManaShieldPassive());        // INT 50 - Magic Resistance stacks
        register(new ArcaneFlowPassive());        // INT 75 - Spell effects last longer
        register(new ArcmagePassive());           // INT 99 - +100% Magic Damage
    }

    private static void registerDexterityPassives() {
        register(new SteadyAimPassive());         // DEX 10 - +10 Accuracy
        register(new PrecisionPassive());         // DEX 20 - +15% Projectile Damage
        register(new DeadlyAimPassive());         // DEX 35 - +3% Critical Chance
        register(new MarksmanPassive());          // DEX 50 - +25% Projectile Damage
        register(new LethalPrecisionPassive());   // DEX 75 - +5% Critical Chance
        register(new PerfectAimPassive());        // DEX 99 - +25 Accuracy, +10% Crit Chance
    }

    private static void registerLuckPassives() {
        register(new FortunePassive());           // LUK 10 - +2% Critical Chance
        register(new LuckyBreakPassive());        // LUK 20 - +3 Accuracy, +3 Evasion
        register(new SerendipityPassive());       // LUK 35 - +5% All damage types
        register(new LadyLuckPassive());          // LUK 50 - +7% Critical Chance
        register(new FortuneFavorPassive());      // LUK 75 - +10% All attributes effectiveness
        register(new LegendaryLuckPassive());     // LUK 99 - +15% Critical Chance, massive bonuses
    }

    /**
     * Register a passive ability
     */
    public static void register(PassiveAbility passive) {
        PASSIVES.put(passive.getId(), passive);
        PASSIVES_BY_STAT.get(passive.getStatType()).add(passive);
    }

    /**
     * Get a passive ability by ID
     */
    public static PassiveAbility getPassive(Identifier id) {
        return PASSIVES.get(id);
    }

    /**
     * Get all passives for a stat type
     */
    public static List<PassiveAbility> getPassivesForStat(String statType) {
        return new ArrayList<>(PASSIVES_BY_STAT.getOrDefault(statType, new ArrayList<>()));
    }

    /**
     * Get all registered passives
     */
    public static Collection<PassiveAbility> getAllPassives() {
        return PASSIVES.values();
    }

    /**
     * Get passives by tier/level range
     */
    public static List<PassiveAbility> getPassivesByTier(String statType, int minTier, int maxTier) {
        return getPassivesForStat(statType).stream()
                .filter(passive -> passive.getTier() >= minTier && passive.getTier() <= maxTier)
                .collect(Collectors.toList());
    }

    /**
     * Get passives unlockable at a specific stat value
     */
    public static List<PassiveAbility> getPassivesAtStatValue(String statType, int statValue) {
        return getPassivesForStat(statType).stream()
                .filter(passive -> passive.getRequiredStatValue() == statValue)
                .collect(Collectors.toList());
    }

    /**
     * Get next milestone stat values for a stat type
     */
    public static List<Integer> getStatMilestones(String statType) {
        return getPassivesForStat(statType).stream()
                .map(PassiveAbility::getRequiredStatValue)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}