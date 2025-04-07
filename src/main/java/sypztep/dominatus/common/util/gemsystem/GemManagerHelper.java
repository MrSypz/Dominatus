package sypztep.dominatus.common.util.gemsystem;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;

import java.util.*;

public final class GemManagerHelper {
    public static void readGemDataFromNbt(GemDataComponent component, NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        List<GemComponent> inventory = component.getMutableGemInventory();
        inventory.clear();
        if (tag.contains("GemInventory", NbtElement.LIST_TYPE)) {
            NbtList inventoryList = tag.getList("GemInventory", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < inventoryList.size() && inventory.size() < 50; i++) {
                GemComponent.CODEC.parse(registryLookup.getOps(net.minecraft.nbt.NbtOps.INSTANCE), inventoryList.getCompound(i))
                        .result().ifPresent(inventory::add);
            }
        }

        Map<String, GemComponent> presets = component.getMutableGemPresets();
        presets.clear();
        if (tag.contains("GemPresets", NbtElement.COMPOUND_TYPE)) {
            NbtCompound presetsTag = tag.getCompound("GemPresets");
            for (String key : presetsTag.getKeys()) {
                if (presetsTag.contains(key, NbtElement.COMPOUND_TYPE)) {
                    GemComponent.CODEC.parse(registryLookup.getOps(net.minecraft.nbt.NbtOps.INSTANCE), presetsTag.getCompound(key))
                            .result().ifPresent(gem -> presets.put(key, gem));
                } else {
                    presets.put(key, null);
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            presets.putIfAbsent("slot_" + i, null);
        }
    }

    public static void writeGemDataToNbt(GemDataComponent component, NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList inventoryList = new NbtList();
        for (GemComponent gem : component.getGemInventory()) {
            GemComponent.CODEC.encodeStart(registryLookup.getOps(net.minecraft.nbt.NbtOps.INSTANCE), gem)
                    .result().ifPresent(encoded -> {
                        if (encoded instanceof NbtElement nbtElement) {
                            inventoryList.add(nbtElement);
                        }
                    });
        }
        tag.put("GemInventory", inventoryList);

        NbtCompound presetsTag = new NbtCompound();
        for (Map.Entry<String, GemComponent> entry : component.getMutableGemPresets().entrySet()) {
            if (entry.getValue() != null) {
                GemComponent.CODEC.encodeStart(registryLookup.getOps(net.minecraft.nbt.NbtOps.INSTANCE), entry.getValue())
                        .result().ifPresent(encoded -> {
                            if (encoded instanceof NbtElement nbtElement) {
                                presetsTag.put(entry.getKey(), nbtElement); // Key is already "slot_0"
                            }
                        });
            }
        }
        tag.put("GemPresets", presetsTag);
    }

    // Rest of the class remains unchanged...
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

    private static void applyGemModifiers(LivingEntity entity, Collection<GemComponent> gems) {
        if (gems.isEmpty()) return;

        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiersToAdd = ArrayListMultimap.create();
        for (GemComponent gem : gems) {
            Map<Identifier, EntityAttributeModifier> modifiers = gem.attributeModifiers();
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
        if (!modifiersToAdd.isEmpty()) {
            entity.getAttributes().addTemporaryModifiers(modifiersToAdd);
        }
    }

    public static void updateEntityStats(PlayerEntity player) {
        Dominatus.LOGGER.info("Updating stats for player: {}", player.getName().getString());
        Collection<GemComponent> equippedGems = GemDataComponent.get(player).getGemPresets().values().stream()
                .filter(Objects::nonNull)
                .toList();
        clearExistingModifiers(player);
        applyGemModifiers(player, equippedGems);
        Dominatus.LOGGER.info("Applied {} gem modifiers to player", equippedGems.size());
    }
}