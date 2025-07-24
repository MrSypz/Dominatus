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
    // Base class - no bonuses, balanced stats
    NOVICE(
            "Novice",
            Formatting.GRAY,
            Map.of(),
            ResourceType.MANA,
            100f,
            "A beginning adventurer with no specialization"
    ),

    // Tier 1 - Warrior path (high health, melee focus)
    WARRIOR(
            "Warrior",
            Formatting.RED,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, 60.0,  // 20 -> 80 health total
                    ModEntityAttributes.MELEE_ATTACK_DAMAGE, 0.2 // +20% melee damage
            ),
            ResourceType.RAGE,
            80f,
            "A fierce melee combatant who uses rage to fuel devastating attacks"
    ),

    // Tier 1 - Mage path (low health, magic focus)
    MAGE(
            "Mage",
            Formatting.BLUE,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, -10.0, // 20 -> 10 health (risky!)
                    ModEntityAttributes.MAGIC_ATTACK_DAMAGE, 0.4 // +40% magic damage
            ),
            ResourceType.MANA,
            150f,
            "A master of arcane arts who trades durability for magical power"
    ),

    // Tier 1 - Ninja path (speed and crit focus)
    NINJA(
            "Ninja",
            Formatting.DARK_PURPLE,
            Map.of(
                    EntityAttributes.GENERIC_MAX_HEALTH, 20.0,   // 20 -> 40 health
                    EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.03, // +30% move speed
                    ModEntityAttributes.CRIT_CHANCE, 0.15        // +15% crit chance
            ),
            ResourceType.MANA,
            120f,
            "A swift shadow warrior who strikes from the darkness"
    );

    private final String displayName;
    private final Formatting color;
    private final Map<RegistryEntry<EntityAttribute>, Double> attributeModifiers;
    private final ResourceType primaryResource;
    private final float maxResource;
    private final String description;

    PlayerClass(String displayName, Formatting color,
                Map<RegistryEntry<EntityAttribute>, Double> attributeModifiers,
                ResourceType primaryResource, float maxResource, String description) {
        this.displayName = displayName;
        this.color = color;
        this.attributeModifiers = attributeModifiers;
        this.primaryResource = primaryResource;
        this.maxResource = maxResource;
        this.description = description;
    }

    /**
     * Apply this class's attribute modifiers to an entity
     */
    public void applyAttributeModifiers(LivingEntity entity) {
        for (var entry : attributeModifiers.entrySet()) {
            EntityAttributeInstance attribute = entity.getAttributeInstance(entry.getKey());
            if (attribute != null) {
                // Remove any existing class modifier
                Identifier modifierId = getClassModifierId();
                attribute.removeModifier(modifierId);

                // Apply new modifier
                attribute.addTemporaryModifier(new EntityAttributeModifier(
                        modifierId,
                        entry.getValue(),
                        EntityAttributeModifier.Operation.ADD_VALUE
                ));
            }
        }

        // Force health update for players
        if (entity instanceof PlayerEntity player) {
            updatePlayerHealth(player);
        }
    }

    /**
     * Remove this class's attribute modifiers from an entity
     */
    public void removeAttributeModifiers(LivingEntity entity) {
        Identifier modifierId = getClassModifierId();

        for (var entry : attributeModifiers.entrySet()) {
            EntityAttributeInstance attribute = entity.getAttributeInstance(entry.getKey());
            if (attribute != null) {
                attribute.removeModifier(modifierId);
            }
        }
    }

    /**
     * Safely update player health when max health changes
     */
    private void updatePlayerHealth(PlayerEntity player) {
        float currentHealth = player.getHealth();
        float oldMaxHealth = player.getMaxHealth();

        // Get new max health after attribute changes
        float newMaxHealth = (float) player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);

        if (newMaxHealth != oldMaxHealth) {
            if (newMaxHealth < currentHealth) {
                // If new max is lower, reduce current health proportionally
                float healthRatio = Math.max(0.1f, newMaxHealth / oldMaxHealth); // Keep at least 10%
                player.setHealth(currentHealth * healthRatio);
            }
            // If new max is higher, keep current health the same (player doesn't auto-heal)
        }
    }

    private Identifier getClassModifierId() {
        return Dominatus.id("class_" + name().toLowerCase());
    }

    // Getters
    public String getDisplayName() { return displayName; }
    public Formatting getColor() { return color; }
    public ResourceType getPrimaryResource() { return primaryResource; }
    public float getMaxResource() { return maxResource; }
    public String getDescription() { return description; }

    public Text getFormattedName() {
        return Text.literal(displayName).formatted(color);
    }

    public Text getFormattedDescription() {
        return Text.literal(description).formatted(Formatting.GRAY);
    }
}