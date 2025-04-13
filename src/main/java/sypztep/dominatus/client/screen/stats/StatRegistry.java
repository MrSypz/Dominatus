package sypztep.dominatus.client.screen.stats;

import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Central registry for all stats in the game.
 * All stat definitions and calculations are centralized here.
 */
public class StatRegistry {

    // Map of all registered stats, preserving insertion order
    private static final LinkedHashMap<String, StatDefinition> STATS = new LinkedHashMap<>();

    // Category definitions
    public static final String CATEGORY_COMBAT = "header.dominatus.combat_stats";
    public static final String RWI_COMPAT = "header.dominatus.ranged_stats";
    public static final String CATEGORY_ATTRIBUTES = "header.dominatus.player_attributes";

    // Register all stats - this is called during mod initialization
    static {
        // Combat Stats Category
        registerHeader(CATEGORY_COMBAT);

        registerStat("accuracy", "stat.dominatus.accuracy",
                entity -> (double) Math.round(getAttributeBaseValue(entity, ModEntityAttributes.ACCURACY)),
                entity -> (double) Math.round(getAttributeValue(entity, ModEntityAttributes.ACCURACY) -
                        getAttributeBaseValue(entity, ModEntityAttributes.ACCURACY)),
                false);

        registerStat("evasion", "stat.dominatus.evasion",
                entity -> (double) Math.round(getAttributeBaseValue(entity, ModEntityAttributes.EVASION)),
                entity -> (double) Math.round(getAttributeValue(entity, ModEntityAttributes.EVASION) -
                        getAttributeBaseValue(entity, ModEntityAttributes.EVASION)),
                false);

        registerStat("crit_chance", "stat.dominatus.crit_chance",
                entity -> (double) Math.round(getAttributeBaseValue(entity, ModEntityAttributes.CRIT_CHANCE) * 100f),
                entity -> (double) Math.round((getAttributeValue(entity, ModEntityAttributes.CRIT_CHANCE) -
                        getAttributeBaseValue(entity, ModEntityAttributes.CRIT_CHANCE)) * 100f),
                true);

        registerStat("crit_damage", "stat.dominatus.crit_damage",
                entity -> (double) Math.round(getAttributeBaseValue(entity, ModEntityAttributes.CRIT_DAMAGE) * 100f),
                entity -> (double) Math.round((getAttributeValue(entity, ModEntityAttributes.CRIT_DAMAGE) -
                        getAttributeBaseValue(entity, ModEntityAttributes.CRIT_DAMAGE)) * 100f),
                true);

        registerStat("back_attack", "stat.dominatus.back_attack",
                entity -> (double) Math.round(getAttributeBaseValue(entity, ModEntityAttributes.BACK_ATTACK) * 100f),
                entity -> (double) Math.round((getAttributeValue(entity, ModEntityAttributes.BACK_ATTACK) -
                        getAttributeBaseValue(entity, ModEntityAttributes.BACK_ATTACK)) * 100f),
                true);

        registerStat("air_attack", "stat.dominatus.air_attack",
                entity -> (double) Math.round(getAttributeBaseValue(entity, ModEntityAttributes.AIR_ATTACK) * 100f),
                entity -> (double) Math.round((getAttributeValue(entity, ModEntityAttributes.AIR_ATTACK) -
                        getAttributeBaseValue(entity, ModEntityAttributes.AIR_ATTACK)) * 100f),
                true);

        registerStat("down_attack", "stat.dominatus.down_attack",
                entity -> (double) Math.round(getAttributeBaseValue(entity, ModEntityAttributes.DOWN_ATTACK) * 100f),
                entity -> (double) Math.round((getAttributeValue(entity, ModEntityAttributes.DOWN_ATTACK) -
                        getAttributeBaseValue(entity, ModEntityAttributes.DOWN_ATTACK)) * 100f),
                true);

        registerStat("player_vers_entity_damage", "stat.dominatus.player_vers_entity_damage",
                entity -> (double) Math.round(getAttributeBaseValue(entity, ModEntityAttributes.PLAYER_VERS_ENTITY_DAMAGE)),
                entity -> (double) Math.round(getAttributeValue(entity, ModEntityAttributes.PLAYER_VERS_ENTITY_DAMAGE) -
                        getAttributeBaseValue(entity, ModEntityAttributes.PLAYER_VERS_ENTITY_DAMAGE)),
                false);

        registerStat("player_vers_player_damage", "stat.dominatus.player_vers_player_damage",
                entity -> (double) Math.round(getAttributeBaseValue(entity, ModEntityAttributes.PLAYER_VERS_PLAYER_DAMAGE)),
                entity -> (double) Math.round(getAttributeValue(entity, ModEntityAttributes.PLAYER_VERS_PLAYER_DAMAGE) -
                                getAttributeBaseValue(entity, ModEntityAttributes.PLAYER_VERS_PLAYER_DAMAGE)),
                        false);

        // RWI Stats (only register if RWI mod is loaded)
        if (Dominatus.isRWILoaded) {
            registerHeader(RWI_COMPAT);
            registerStat("ranged_damage", "stat.dominatus.ranged_damage",
                    entity -> (double) Math.round(getAttributeBaseValue(entity, (EntityAttributes_RangedWeapon.DAMAGE.entry))),
                    entity -> (double) Math.round(getAttributeValue(entity, (EntityAttributes_RangedWeapon.DAMAGE.entry)) -
                            getAttributeBaseValue(entity, (EntityAttributes_RangedWeapon.DAMAGE.entry))),
                    false);

            registerStat("pull_time", "stat.dominatus.pull_time",
                    entity -> (double) Math.round(getAttributeBaseValue(entity, (EntityAttributes_RangedWeapon.PULL_TIME.entry)) * 20), // Convert to ticks
                    entity -> (double) Math.round((getAttributeValue(entity, (EntityAttributes_RangedWeapon.PULL_TIME.entry)) -
                            getAttributeBaseValue(entity, (EntityAttributes_RangedWeapon.PULL_TIME.entry))) * 20),
                    false);

            registerStat("ranged_haste", "stat.dominatus.ranged_haste",
                    entity -> (double) Math.round(getAttributeBaseValue(entity, (EntityAttributes_RangedWeapon.HASTE.entry))),
                    entity -> (double) Math.round(getAttributeValue(entity, (EntityAttributes_RangedWeapon.HASTE.entry)) -
                            getAttributeBaseValue(entity, (EntityAttributes_RangedWeapon.HASTE.entry))),
                    false);

            registerStat("velocity", "stat.dominatus.velocity",
                    entity -> (double) Math.round(getAttributeBaseValue(entity, (EntityAttributes_RangedWeapon.VELOCITY.entry))),
                    entity -> (double) Math.round(getAttributeValue(entity, (EntityAttributes_RangedWeapon.VELOCITY.entry)) -
                            getAttributeBaseValue(entity, (EntityAttributes_RangedWeapon.VELOCITY.entry))),
                    false);
        }
        // Player Attributes Category
        registerHeader(CATEGORY_ATTRIBUTES);

        registerStat("max_health", "stat.dominatus.max_health",
                entity -> entity.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH),
                entity -> (double) Math.round(entity.getMaxHealth() -
                        entity.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)),
                false);

        registerStat("health_regen", "stat.dominatus.health_regen",
                entity -> entity.getAttributeBaseValue(ModEntityAttributes.HEALTH_REGEN),
                entity -> (double) Math.round(entity.getAttributeValue(ModEntityAttributes.HEALTH_REGEN) -
                        entity.getAttributeBaseValue(ModEntityAttributes.HEALTH_REGEN)),
                false);

        registerStat("armor", "stat.dominatus.armor",
                entity -> entity.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR),
                entity -> entity.getArmor() - entity.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR),
                false);

        registerStat("armor_toughness", "stat.dominatus.armor_toughness",
                entity -> entity.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS),
                entity -> (double) Math.round(entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS) -
                        entity.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)),
                false);

        registerStat("movement_speed", "stat.dominatus.movement_speed",
                entity -> (entity.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 43.17),
                entity -> ((entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) -
                        entity.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)) * 43.17),
                false);

        registerStat("attack_damage", "stat.dominatus.attack_damage",
                entity -> entity.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE),
                entity -> (entity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) -
                        entity.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)),
                false);

        registerStat("attack_speed", "stat.dominatus.attack_speed",
                entity -> entity.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED),
                entity -> (double) Math.round(entity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) -
                        entity.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED)),
                false);
    }

    // Register a stat header (category)
    private static void registerHeader(String translationKey) {
        STATS.put(translationKey, new StatDefinition(
                translationKey,
                translationKey,
                "",
                "",
                null,
                null,
                false,
                true
        ));
    }

    // Register a normal stat
    private static void registerStat(
            String id,
            String nameKey,
            Function<LivingEntity, Double> baseValueProvider,
            Function<LivingEntity, Double> additionValueProvider,
            boolean isPercentage) {

        STATS.put(id, new StatDefinition(
                id,
                nameKey,
                nameKey + ".desc",
                nameKey + ".details",
                baseValueProvider,
                additionValueProvider,
                isPercentage,
                false
        ));
    }

    /**
     * Get all stat definitions in the registry
     */
    public static List<StatDefinition> getAllStats() {
        return new ArrayList<>(STATS.values());
    }

    /**
     * Get a specific stat definition by ID
     */
    public static StatDefinition getStat(String id) {
        return STATS.getOrDefault(id, createUnknownStat());
    }

    /**
     * Get all non-header stats
     */
    public static List<StatDefinition> getNonHeaderStats() {
        return STATS.values().stream()
                .filter(stat -> !stat.isHeader())
                .toList();
    }

    /**
     * Calculate stat values for a given entity
     */
    public static Map<String, StatValue> calculateStatValues(LivingEntity entity) {
        Map<String, StatValue> values = new LinkedHashMap<>();

        STATS.forEach((id, definition) -> {
            if (!definition.isHeader() && entity != null) {
                double base = definition.baseValueProvider().apply(entity);
                double addition = definition.additionValueProvider().apply(entity);
                values.put(id, new StatValue(base, addition));
            }
        });

        return values;
    }

    // Helper method to create an "unknown" stat definition
    private static StatDefinition createUnknownStat() {
        return new StatDefinition(
                "unknown",
                "stat.dominatus.unknown",
                "stat.dominatus.unknown.desc",
                "",
                entity -> 0.0,
                entity -> 0.0,
                false,
                false
        );
    }

    // Helper methods for attribute access
    private static double getAttributeValue(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        return instance != null ? instance.getValue() : 0;
    }

    private static double getAttributeBaseValue(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        return instance != null ? instance.getBaseValue() : 0;
    }

    /**
     * Record that holds all information about a stat
     */
    public record StatDefinition(
            String id,
            String nameKey,
            String descriptionKey,
            String detailsKey,
            Function<LivingEntity, Double> baseValueProvider,
            Function<LivingEntity, Double> additionValueProvider,
            boolean isPercentage,
            boolean isHeader
    ) {
        /**
         * Get the display name for this stat
         */
        public String getNameKey() {
            return nameKey;
        }
    }

    /**
     * Record that holds the value of a stat
     */
    public record StatValue(double base, double addition) {
        /**
         * Get the total value (base + addition)
         */
        public double getTotal() {
            return base + addition;
        }
    }
}