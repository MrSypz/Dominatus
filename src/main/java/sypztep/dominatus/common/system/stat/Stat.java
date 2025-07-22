package sypztep.dominatus.common.system.stat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.util.AttributeModification;

import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * Base stat class for all living entities (players and monsters)
 * Clean and simple - no UI methods, pure functionality
 */
public abstract class Stat implements StatEffect {
    protected final int baseValue;
    protected int currentValue;
    public static final int MAX_STAT_VALUE = 99; // Stat cap

    public Stat(int baseValue) {
        this.baseValue = baseValue;
        this.currentValue = baseValue;
    }

    // ====================
    // CORE STAT METHODS
    // ====================

    public int getBaseValue() {
        return baseValue;
    }

    public int getValue() {
        return currentValue;
    }

    /**
     * Direct stat setting (for monsters, admin commands, or equipment bonuses)
     */
    public void setValue(int value) {
        this.currentValue = Math.max(baseValue, Math.min(value, MAX_STAT_VALUE));
    }

    /**
     * Add to current value (for temporary bonuses, level scaling, etc.)
     */
    public void addValue(int bonus) {
        setValue(currentValue + bonus);
    }

    /**
     * Reset to base value (for monsters when they respawn, etc.)
     */
    public void resetToBase() {
        this.currentValue = baseValue;
    }

    /**
     * Check if stat is at maximum value
     */
    public boolean isMaxed() {
        return currentValue >= MAX_STAT_VALUE;
    }

    // ====================
    // NBT PERSISTENCE (Simple)
    // ====================

    public void readFromNbt(NbtCompound tag) {
        this.currentValue = Math.min(tag.getInt("CurrentValue"), MAX_STAT_VALUE);
    }

    public void writeToNbt(NbtCompound tag) {
        tag.putInt("CurrentValue", this.currentValue);
    }

    // ====================
    // ATTRIBUTE APPLICATION HELPERS
    // ====================

    protected void applyEffect(LivingEntity living, RegistryEntry<EntityAttribute> attribute,
                               Identifier modifierId,
                               ToDoubleFunction<Double> effectFunction) {
        EntityAttributeInstance attributeInstance = living.getAttributeInstance(attribute);
        if (attributeInstance != null) {
            double baseValue = living.getAttributeBaseValue(attribute);
            double effectValue = effectFunction.applyAsDouble(baseValue);

            if (modifierId == null) {
                throw new IllegalArgumentException("modifierId cannot be null - report this on github");
            }

            // Remove existing modifier
            EntityAttributeModifier existingModifier = attributeInstance.getModifier(modifierId);
            if (existingModifier != null) {
                attributeInstance.removeModifier(existingModifier);
            }

            // Apply new modifier
            EntityAttributeModifier mod = new EntityAttributeModifier(modifierId, effectValue, EntityAttributeModifier.Operation.ADD_VALUE);
            attributeInstance.addPersistentModifier(mod);
        }
    }

    protected void applyEffects(LivingEntity living, List<AttributeModification> modifications) {
        for (AttributeModification modification : modifications) {
            EntityAttributeInstance attributeInstance = living.getAttributeInstance(modification.attribute());
            if (attributeInstance != null) {
                double baseValue = living.getAttributeBaseValue(modification.attribute());
                double effectValue = modification.effectFunction().applyAsDouble(baseValue);

                if (modification.modifierId() == null) {
                    throw new IllegalArgumentException("modifierId cannot be null - report this on github");
                }

                EntityAttributeModifier existingModifier = attributeInstance.getModifier(modification.modifierId());
                if (existingModifier != null) {
                    attributeInstance.removeModifier(existingModifier);
                }

                EntityAttributeModifier mod = new EntityAttributeModifier(modification.modifierId(), effectValue, modification.operation());
                attributeInstance.addPersistentModifier(mod);
            }
        }
    }

    // ====================
    // ABSTRACT METHODS (Core functionality only)
    // ====================

    public abstract void applyPrimaryEffect(LivingEntity entity);
    public abstract void applySecondaryEffect(LivingEntity entity);

    protected abstract Identifier getPrimaryModifierId();
    protected abstract Identifier getSecondaryModifierId();
}