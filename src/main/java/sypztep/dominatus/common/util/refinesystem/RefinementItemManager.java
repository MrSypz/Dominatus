package sypztep.dominatus.common.util.refinesystem;

import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.mutable.MutableBoolean;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.tyrannus.common.util.ItemStackHelper;

import java.util.*;

public final class RefinementItemManager {
    private static final Map<Pair<ItemStack, EquipmentSlot>, Boolean> SLOT_VALIDITY_CACHE = new WeakHashMap<>();

    private static final Identifier DAMAGE_MODIFIER_ID = Dominatus.id("extra.damage_stats");
    private static final Identifier ARMOR_MODIFIER_ID = Dominatus.id("extra.armor_stats");
    private static final Identifier ACCURACY_MODIFIER_ID = Dominatus.id("extra.accuracy_stats");
    private static final Identifier EVASION_MODIFIER_ID = Dominatus.id("extra.evasion_stats");
    private static final Identifier DAMAGE_REDUCTION_MODIFIER_ID = Dominatus.id("extra.damage_reduction_stats");

    public static void updateEntityStats(LivingEntity entity) {
        clearExistingModifiers(entity);

        STATS_HOLDER.reset();

        List<Refinement> refinements = getComponentFromAllEquippedSlots(entity);
        for (Refinement refinement : refinements) {
            STATS_HOLDER.add(refinement);
        }

        forceUpdateAttributes(entity);
    }

    private static void clearExistingModifiers(LivingEntity entity) {
        removeAttributeModifier(entity, EntityAttributes.GENERIC_ATTACK_DAMAGE, DAMAGE_MODIFIER_ID);
        removeAttributeModifier(entity, EntityAttributes.GENERIC_ARMOR, ARMOR_MODIFIER_ID);
        removeAttributeModifier(entity, ModEntityAttributes.ACCURACY, ACCURACY_MODIFIER_ID);
        removeAttributeModifier(entity, ModEntityAttributes.EVASION, EVASION_MODIFIER_ID);
        removeAttributeModifier(entity, ModEntityAttributes.DAMAGE_REDUCTION, DAMAGE_REDUCTION_MODIFIER_ID);
    }

    private static void forceUpdateAttributes(LivingEntity entity) {
        updateAttribute(entity, EntityAttributes.GENERIC_ATTACK_DAMAGE, DAMAGE_MODIFIER_ID, STATS_HOLDER.damage);
        updateAttribute(entity, EntityAttributes.GENERIC_ARMOR, ARMOR_MODIFIER_ID, STATS_HOLDER.protection);
        updateAttribute(entity, ModEntityAttributes.ACCURACY, ACCURACY_MODIFIER_ID, STATS_HOLDER.accuracy);
        updateAttribute(entity, ModEntityAttributes.EVASION, EVASION_MODIFIER_ID, STATS_HOLDER.evasion);
        updateAttribute(entity, ModEntityAttributes.DAMAGE_REDUCTION, DAMAGE_REDUCTION_MODIFIER_ID, STATS_HOLDER.damageReduction);
    }

    private static void removeAttributeModifier(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, Identifier modifierId) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        if (instance != null) {
            instance.removeModifier(modifierId);
        }
    }

    public static class CombinedStats {
        public int evasion;
        public int accuracy;
        public float damage;
        public int protection;
        public int damageReduction;

        public void add(Refinement refinement) {
            this.evasion += refinement.evasion();
            this.accuracy += refinement.accuracy();
            this.damage += refinement.damage();
            this.protection += refinement.protection();
            this.damageReduction += refinement.damageReduction();
        }

        public void reset() {
            evasion = 0;
            accuracy = 0;
            damage = 0;
            protection = 0;
            damageReduction = 0;
        }
    }

    private static final CombinedStats STATS_HOLDER = new CombinedStats();

    public static List<Refinement> getComponentFromAllEquippedSlots(LivingEntity living) {
        List<Refinement> refinements = new ArrayList<>();

        // Process vanilla equipment slots
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = living.getEquippedStack(slot);
            if (!itemStack.isEmpty() && isItemInCorrectSlot(itemStack, slot)) {
                Refinement refinement = RefinementManager.getRefinement(itemStack);
                if (refinement != null) {
                    refinements.add(refinement);
                }
            }
        }

        // Process accessory slots if the entity has accessories
        var accessoriesCapability = living.accessoriesCapability();
        if (accessoriesCapability != null) {
            for (SlotEntryReference slotRef : accessoriesCapability.getAllEquipped()) {
                ItemStack stack = slotRef.reference().getStack();
                if (!stack.isEmpty()) {
                    Refinement refinement = RefinementManager.getRefinement(stack);
                    if (refinement != null) {
                        refinements.add(refinement);
                    }
                }
            }
        }

        return refinements;
    }

    private static boolean isItemInCorrectSlot(ItemStack stack, EquipmentSlot slot) {
        if (ItemStackHelper.shouldBreak(stack)) return false;

        Pair<ItemStack, EquipmentSlot> cacheKey = new Pair<>(stack, slot);
        Boolean cachedResult = SLOT_VALIDITY_CACHE.get(cacheKey);
        if (cachedResult != null) return cachedResult;

        boolean result = checkSlotValidity(stack, slot);
        SLOT_VALIDITY_CACHE.put(cacheKey, result);
        return result;
    }

    private static boolean checkSlotValidity(ItemStack stack, EquipmentSlot slot) {
        MutableBoolean hasAnyAttributeModifiers = new MutableBoolean(false);

        for (AttributeModifierSlot attributeModifierSlot : AttributeModifierSlot.values()) {
            stack.applyAttributeModifier(attributeModifierSlot, (entry, modifier) -> {
                hasAnyAttributeModifiers.setTrue();
            });

            if (hasAnyAttributeModifiers.isTrue()) {
                break;
            }
        }

        if (hasAnyAttributeModifiers.isTrue()) {
            for (AttributeModifierSlot attributeModifierSlot : AttributeModifierSlot.values()) {
                MutableBoolean isValid = new MutableBoolean(false);
                stack.applyAttributeModifier(attributeModifierSlot, (entry, modifier) -> {
                    if (attributeModifierSlot.matches(slot)) isValid.setTrue();
                });
                if (isValid.isTrue()) return true;
            }
            return false;
        }
        else return slot == EquipmentSlot.MAINHAND;
    }

    private static void updateAttribute(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, Identifier modifierId, double value) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        if (instance != null) {
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                    modifierId,
                    value,
                    EntityAttributeModifier.Operation.ADD_VALUE
            );

            instance.removeModifier(modifierId);
            instance.addPersistentModifier(modifier);
        }
    }
}