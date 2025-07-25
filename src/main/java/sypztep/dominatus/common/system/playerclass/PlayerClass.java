package sypztep.dominatus.common.system.playerclass;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModEntityAttributes;

import java.util.Map;

public enum PlayerClass {
    // === TIER 0: STARTING CLASS ===
    NOVICE(
            "Novice", Formatting.GRAY, 0,
            Map.of(), ResourceType.MANA, 100f,
            "A beginning adventurer with no specialization"
    ),

    // === TIER 1: BASE CLASSES ===
    WARRIOR(
            "Warrior", Formatting.RED, 1,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, 60.0,
                    ModEntityAttributes.MELEE_ATTACK_DAMAGE, 0.2
            ), ResourceType.RAGE, 80f,
            "A fierce melee combatant who uses rage to fuel devastating attacks"
    ),

    MAGE(
            "Mage", Formatting.BLUE, 1,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, -10.0,
                    ModEntityAttributes.MAGIC_ATTACK_DAMAGE, 0.4
            ), ResourceType.MANA, 150f,
            "A master of arcane arts who trades durability for magical power"
    ),

    NINJA(
            "Ninja", Formatting.DARK_PURPLE, 1,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, 20.0,
                    EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.03,
                    ModEntityAttributes.CRIT_CHANCE, 0.15
            ), ResourceType.MANA, 120f,
            "A swift shadow warrior who strikes from the darkness"
    ),

    // === TIER 2: EVOLVED CLASSES ===

    // Warrior Evolution Path
    KNIGHT(
            "Knight", Formatting.GOLD, 2,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, 120.0,      // 140 total HP
                    EntityAttributes.GENERIC_ARMOR, 5.0,            // +5 armor
                    ModEntityAttributes.MELEE_ATTACK_DAMAGE, 0.15,  // +15% melee
                    ModEntityAttributes.HEAL_EFFECTIVE, 0.3         // +30% healing received
            ), ResourceType.RAGE, 100f,
            "A noble defender who protects allies and excels in defensive combat"
    ),

    BERSERKER(
            "Berserker", Formatting.DARK_RED, 2,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, 80.0,       // 100 total HP
                    ModEntityAttributes.MELEE_ATTACK_DAMAGE, 0.5,   // +50% melee damage
                    EntityAttributes.GENERIC_ATTACK_SPEED, 0.25,    // +25% attack speed
                    ModEntityAttributes.CRIT_DAMAGE, 0.4            // +40% crit damage
            ), ResourceType.RAGE, 60f,
            "A frenzied warrior who trades defense for overwhelming offensive power"
    ),

    // Mage Evolution Path
    WIZARD(
            "Wizard", Formatting.AQUA, 2,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, -5.0,       // 15 total HP (risky!)
                    ModEntityAttributes.MAGIC_ATTACK_DAMAGE, 0.7,   // +70% magic damage
                    ModEntityAttributes.MAGIC_RESISTANCE, 0.3       // +30% magic resistance
            ), ResourceType.MANA, 200f,
            "A master of the arcane who wields devastating magical power"
    ),

    SORCERER(
            "Sorcerer", Formatting.LIGHT_PURPLE, 2,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, 15.0,       // 35 total HP
                    ModEntityAttributes.MAGIC_ATTACK_DAMAGE, 0.45,  // +45% magic damage
                    EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.02,  // +20% move speed
                    ModEntityAttributes.CRIT_CHANCE, 0.1            // +10% crit chance
            ), ResourceType.MANA, 180f,
            "A mobile spellcaster who balances power with survivability"
    ),

    // Ninja Evolution Path
    ASSASSIN(
            "Assassin", Formatting.BLACK, 2,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, 40.0,       // 60 total HP
                    EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.05,  // +50% move speed
                    ModEntityAttributes.CRIT_CHANCE, 0.25,          // +25% crit chance
                    ModEntityAttributes.CRIT_DAMAGE, 0.6,           // +60% crit damage
                    ModEntityAttributes.BACK_ATTACK, 0.5            // +50% back attack damage
            ), ResourceType.MANA, 100f,
            "A deadly striker who excels at critical hits and stealth attacks"
    ),

    SAMURAI(
            "Samurai", Formatting.WHITE, 2,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, 60.0,       // 80 total HP
                    ModEntityAttributes.MELEE_ATTACK_DAMAGE, 0.3,   // +30% melee damage
                    EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.02,  // +20% move speed
                    ModEntityAttributes.CRIT_CHANCE, 0.15,          // +15% crit chance
                    EntityAttributes.GENERIC_ATTACK_SPEED, 0.15     // +15% attack speed
            ), ResourceType.MANA, 140f,
            "A disciplined warrior who balances offense and defense with honor"
    );

    private final String displayName;
    private final Formatting color;
    private final int tier;
    private final Map<RegistryEntry<EntityAttribute>, Double> attributeModifiers;
    private final ResourceType primaryResource;
    private final float maxResource;
    private final String description;

    PlayerClass(String displayName, Formatting color, int tier,
                Map<RegistryEntry<EntityAttribute>, Double> attributeModifiers,
                ResourceType primaryResource, float maxResource, String description) {
        this.displayName = displayName;
        this.color = color;
        this.tier = tier;
        this.attributeModifiers = attributeModifiers;
        this.primaryResource = primaryResource;
        this.maxResource = maxResource;
        this.description = description;
    }

    // === CLASS EVOLUTION REQUIREMENTS ===

    /**
     * Get classes that this class can evolve into
     */
    public PlayerClass[] getEvolutionOptions() {
        return switch (this) {
            case NOVICE -> new PlayerClass[]{WARRIOR, MAGE, NINJA};
            case WARRIOR -> new PlayerClass[]{KNIGHT, BERSERKER};
            case MAGE -> new PlayerClass[]{WIZARD, SORCERER};
            case NINJA -> new PlayerClass[]{ASSASSIN, SAMURAI};
            default -> new PlayerClass[0]; // Tier 2 classes cannot evolve further
        };
    }

    /**
     * Check if player meets evolution requirements
     */
    public boolean canEvolveTo(PlayerEntity player, PlayerClass targetClass) {
        // Check if target is a valid evolution
        boolean validEvolution = false;
        for (PlayerClass option : getEvolutionOptions()) {
            if (option == targetClass) {
                validEvolution = true;
                break;
            }
        }
        if (!validEvolution) return false;

        // Get evolution requirements
        EvolutionRequirement requirement = getEvolutionRequirement(targetClass);
        if (requirement == null) return false;

        return requirement.meetsRequirements(player);
    }

    /**
     * Get specific evolution requirements
     */
    public EvolutionRequirement getEvolutionRequirement(PlayerClass targetClass) {
        return switch (this) {
            case NOVICE -> switch (targetClass) {
                case WARRIOR -> new EvolutionRequirement(10, 15, Map.of("strength", 15));
                case MAGE -> new EvolutionRequirement(10, 15, Map.of("intelligence", 15));
                case NINJA -> new EvolutionRequirement(10, 15, Map.of("agility", 15));
                default -> null;
            };
            case WARRIOR -> switch (targetClass) {
                case KNIGHT -> new EvolutionRequirement(25, 35, Map.of("strength", 35, "vitality", 30));
                case BERSERKER -> new EvolutionRequirement(25, 35, Map.of("strength", 40, "agility", 25));
                default -> null;
            };
            case MAGE -> switch (targetClass) {
                case WIZARD -> new EvolutionRequirement(25, 35, Map.of("intelligence", 40, "vitality", 20));
                case SORCERER -> new EvolutionRequirement(25, 35, Map.of("intelligence", 35, "agility", 25));
                default -> null;
            };
            case NINJA -> switch (targetClass) {
                case ASSASSIN -> new EvolutionRequirement(25, 35, Map.of("agility", 40, "luck", 25));
                case SAMURAI -> new EvolutionRequirement(25, 35, Map.of("agility", 30, "strength", 30));
                default -> null;
            };
            default -> null;
        };
    }

    // === ATTRIBUTE APPLICATION (Same as before) ===

    public void applyAttributeModifiers(LivingEntity entity) {
        for (var entry : attributeModifiers.entrySet()) {
            EntityAttributeInstance attribute = entity.getAttributeInstance(entry.getKey());
            if (attribute != null) {
                Identifier modifierId = getClassModifierId();
                attribute.removeModifier(modifierId);

                attribute.addTemporaryModifier(new EntityAttributeModifier(
                        modifierId,
                        entry.getValue(),
                        EntityAttributeModifier.Operation.ADD_VALUE
                ));
            }
        }

        if (entity instanceof PlayerEntity player) {
            updatePlayerHealth(player);
        }
    }

    public void removeAttributeModifiers(LivingEntity entity) {
        Identifier modifierId = getClassModifierId();

        for (var entry : attributeModifiers.entrySet()) {
            EntityAttributeInstance attribute = entity.getAttributeInstance(entry.getKey());
            if (attribute != null) {
                attribute.removeModifier(modifierId);
            }
        }
    }

    private void updatePlayerHealth(PlayerEntity player) {
        float currentHealth = player.getHealth();
        float oldMaxHealth = player.getMaxHealth();

        float newMaxHealth = (float) player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);

        if (newMaxHealth != oldMaxHealth) {
            if (newMaxHealth < currentHealth) {
                float healthRatio = Math.max(0.1f, newMaxHealth / oldMaxHealth);
                player.setHealth(currentHealth * healthRatio);
            }
        }
    }

    private Identifier getClassModifierId() {
        return Dominatus.id("class_" + name().toLowerCase());
    }

    // === GETTERS ===

    public String getDisplayName() { return displayName; }
    public Formatting getColor() { return color; }
    public int getTier() { return tier; }
    public ResourceType getPrimaryResource() { return primaryResource; }
    public float getMaxResource() { return maxResource; }
    public String getDescription() { return description; }

    public Text getFormattedName() {
        return Text.literal(displayName).formatted(color);
    }

    public Text getFormattedDescription() {
        return Text.literal(description).formatted(Formatting.GRAY);
    }

    public boolean isTier2() {
        return tier >= 2;
    }
}