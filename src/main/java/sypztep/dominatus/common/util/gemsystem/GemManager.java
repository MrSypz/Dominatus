package sypztep.dominatus.common.util.gemsystem;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.GemInventoryComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.init.ModItems;
import sypztep.dominatus.common.reloadlistener.GemItemDataReloadListener;

import java.util.*;

public final class GemManager {
    public static ItemStack createGem(Identifier type) {
        ItemStack stack = new ItemStack(ModItems.GEM);
        GemItemDataReloadListener.getGemType(type).ifPresent(component -> GemComponent.apply(stack, component));
        return stack;
    }

    private static void clearExistingModifiers(LivingEntity entity) {
        Set<EntityAttributeInstance> trackedAttributes = entity.getAttributes().getTracked();
        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiersToRemove = ArrayListMultimap.create();

        for (EntityAttributeInstance instance : trackedAttributes) {
            for (EntityAttributeModifier modifier : instance.getModifiers()) {
                if (modifier.id().toString().startsWith("dominatus:gem.")) {
                    modifiersToRemove.put(instance.getAttribute(), modifier);
                }
            }
        }

        if (!modifiersToRemove.isEmpty()) {
            entity.getAttributes().removeModifiers(modifiersToRemove);
        }
    }

    private static void applyGemModifiers(LivingEntity entity, List<GemComponent> gems) {
        if (gems.isEmpty()) return;

        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiersToAdd = ArrayListMultimap.create();

        for (GemComponent gem : gems) {
            Map<Identifier, EntityAttributeModifier> modifiers = gem.attributeModifiers();
            if (!modifiers.isEmpty()) {
                for (Map.Entry<Identifier, EntityAttributeModifier> entry : modifiers.entrySet()) {
                    Registries.ATTRIBUTE.getEntry(entry.getKey()).ifPresent(attribute -> {
                        EntityAttributeModifier original = entry.getValue();
                        EntityAttributeModifier newModifier = new EntityAttributeModifier(
                                Dominatus.id("gem." + UUID.randomUUID()),
                                original.value(),
                                original.operation()
                        );
                        modifiersToAdd.put(attribute, newModifier);
                    });
                }
            }
        }

        if (!modifiersToAdd.isEmpty()) {
            entity.getAttributes().addTemporaryModifiers(modifiersToAdd);
        }
    }

    private static List<GemComponent> getGemsFromInventory(PlayerEntity player) {
        List<GemComponent> gems = new ArrayList<>();
        GemInventory gemInventory = GemInventoryComponent.getInventory(player);

        for (int i = 0; i < gemInventory.size(); i++) {
            ItemStack stack = gemInventory.getStack(i);
            if (!stack.isEmpty()) {
                GemComponent.fromStack(stack).ifPresent(gems::add);
            }
        }

        return gems;
    }

    public static void updateEntityStats(PlayerEntity player) {
        if (player.getWorld().isClient()) return;

        // First get the gems
        List<GemComponent> gems = getGemsFromInventory(player);

        // Clear existing modifiers once
        clearExistingModifiers(player);

        // Apply new modifiers if there are any gems
        if (!gems.isEmpty()) {
            applyGemModifiers(player, gems);
        }
    }
}