package sypztep.dominatus.common.system.skill.passives;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.system.skill.PassiveAbility;

import java.util.List;

/**
 * Base class for passive abilities that modify entity attributes
 */
public abstract class BaseAttributePassive implements PassiveAbility {
    protected final Identifier id;
    protected final Text name;
    protected final Text description;
    protected final String statType;
    protected final int requiredStatValue;
    protected final int tier;
    protected boolean isActive = false;

    public BaseAttributePassive(Identifier id, Text name, Text description, String statType, int requiredStatValue, int tier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.statType = statType;
        this.requiredStatValue = requiredStatValue;
        this.tier = tier;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public Text getName() {
        return name;
    }

    @Override
    public Text getDescription() {
        return description;
    }

    @Override
    public String getStatType() {
        return statType;
    }

    @Override
    public int getRequiredStatValue() {
        return requiredStatValue;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void onUnlock(LivingEntity entity) {
        // Default implementation - can be overridden
    }

    @Override
    public void applyEffect(LivingEntity entity) {
        applyAttributeModifiers(entity);
        isActive = true;
    }

    @Override
    public void removeEffect(LivingEntity entity) {
        removeAttributeModifiers(entity);
        isActive = false;
    }

    /**
     * Apply attribute modifiers to the entity
     */
    protected abstract void applyAttributeModifiers(LivingEntity entity);

    /**
     * Remove attribute modifiers from the entity
     */
    protected abstract void removeAttributeModifiers(LivingEntity entity);

    /**
     * Helper method to apply a single attribute modifier
     */
    protected void applyAttributeModifier(LivingEntity entity, RegistryEntry<EntityAttribute> attribute,
                                          Identifier modifierId, double value, EntityAttributeModifier.Operation operation) {
        EntityAttributeInstance attributeInstance = entity.getAttributeInstance(attribute);
        if (attributeInstance != null) {
            // Remove existing modifier first
            EntityAttributeModifier existingModifier = attributeInstance.getModifier(modifierId);
            if (existingModifier != null) {
                attributeInstance.removeModifier(existingModifier);
            }

            // Add new modifier
            EntityAttributeModifier modifier = new EntityAttributeModifier(modifierId, value, operation);
            attributeInstance.addPersistentModifier(modifier);
        }
    }

    /**
     * Helper method to remove a single attribute modifier
     */
    protected void removeAttributeModifier(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, Identifier modifierId) {
        EntityAttributeInstance attributeInstance = entity.getAttributeInstance(attribute);
        if (attributeInstance != null) {
            EntityAttributeModifier modifier = attributeInstance.getModifier(modifierId);
            if (modifier != null) {
                attributeInstance.removeModifier(modifier);
            }
        }
    }

    @Override
    public List<Text> getTooltip() {
        return List.of(
                name,
                Text.literal("§7Required: §f" + statType.toUpperCase() + " " + requiredStatValue),
                Text.literal("§8" + description.getString())
        );
    }
}