package sypztep.dominatus.common.util.gemsystem;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.component.GemDataComponent;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.reloadlistener.GemItemDataReloadListener;

import java.util.*;

public final class GemManagerHelper {
    public static void readGemDataFromNbt(GemDataComponent component, NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        List<GemComponent> inventory = component.getMutableGemInventory();
        inventory.clear();
        if (tag.contains("GemInventory", NbtElement.LIST_TYPE)) {
            NbtList inventoryList = tag.getList("GemInventory", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < inventoryList.size() && inventory.size() < 50; i++) {
                GemComponent.CODEC.parse(registryLookup.getOps(NbtOps.INSTANCE), inventoryList.getCompound(i))
                        .result().ifPresent(inventory::add);
            }
        }

        Map<Identifier, GemComponent> presets = component.getMutableGemPresets();
        presets.clear();
        if (tag.contains("GemPresets", NbtElement.COMPOUND_TYPE)) {
            NbtCompound presetsTag = tag.getCompound("GemPresets");
            for (String key : presetsTag.getKeys()) {
                if (presetsTag.contains(key, NbtElement.COMPOUND_TYPE)) {
                    GemComponent.CODEC.parse(registryLookup.getOps(NbtOps.INSTANCE), presetsTag.getCompound(key))
                            .result().ifPresent(gem -> presets.put(Dominatus.id(key), gem));
                } else {
                    presets.put(Dominatus.id(key), null);
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            presets.putIfAbsent(Dominatus.id("slot_" + i), null);
        }
    }

    public static void writeGemDataToNbt(GemDataComponent component, NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList inventoryList = new NbtList();
        for (GemComponent gem : component.getGemInventory()) {
            GemComponent.CODEC.encodeStart(registryLookup.getOps(NbtOps.INSTANCE), gem)
                    .result().ifPresent(encoded -> {
                        if (encoded instanceof NbtElement nbtElement) {
                            inventoryList.add(nbtElement);
                        }
                    });
        }
        tag.put("GemInventory", inventoryList);

        NbtCompound presetsTag = new NbtCompound();
        for (Map.Entry<Identifier, GemComponent> entry : component.getMutableGemPresets().entrySet()) {
            if (entry.getValue() != null) {
                GemComponent.CODEC.encodeStart(registryLookup.getOps(NbtOps.INSTANCE), entry.getValue())
                        .result().ifPresent(encoded -> {
                            if (encoded instanceof NbtElement nbtElement) {
                                presetsTag.put(entry.getKey().getPath(), nbtElement);
                            }
                        });
            }
        }
        tag.put("GemPresets", presetsTag);
    }

    private static void clearExistingModifiers(LivingEntity entity, GemDataComponent gemData) {
        Set<RegistryEntry<EntityAttribute>> possibleAttributes = new HashSet<>();
        // Include attributes from inventory to ensure we clear any stale modifiers
        for (GemComponent gem : gemData.getGemInventory()) {
            if (gem != null) {
                gem.attributeModifiers().keySet().forEach(id ->
                        Registries.ATTRIBUTE.getEntry(id).ifPresent(possibleAttributes::add)
                );
            }
        }
        // Include attributes from presets
        for (GemComponent gem : gemData.getMutableGemPresets().values()) {
            if (gem != null) {
                gem.attributeModifiers().keySet().forEach(id ->
                        Registries.ATTRIBUTE.getEntry(id).ifPresent(possibleAttributes::add)
                );
            }
        }

        for (RegistryEntry<EntityAttribute> attribute : possibleAttributes) {
            EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
            if (instance != null) {
                getModifier(instance);
            }
        }
    }

    private static void getModifier(EntityAttributeInstance instance) {
        List<Identifier> modifiersToRemove = new ArrayList<>();
        for (EntityAttributeModifier modifier : instance.getModifiers()) {
            String modifierIdStr = modifier.id().toString();
            if (modifierIdStr.startsWith("dominatus:gem.slot_")) {
                // Dominatus.LOGGER.info("Removing modifier: {}", modifierIdStr);
                modifiersToRemove.add(modifier.id());
            }
        }
        for (Identifier modifierId : modifiersToRemove) {
            instance.removeModifier(modifierId);
        }
    }
    public static String formatCountText(String label, int current, int max) {
        return String.format("%s (%d/%d)", label, current, max);
    }
    public static List<Text> getGemTooltip(GemComponent gem, boolean isPresetSlot) {
        List<Text> tooltip = new ArrayList<>();
        if (gem != null) {
            String gemName = gem.type().toString().split(":")[1];
            tooltip.add(Text.translatable("item.dominatus.gem." + gemName).formatted(Formatting.GOLD));
            tooltip.add(Text.literal(isPresetSlot ? "Left-click to unequip" : "Click to equip")
                    .formatted(isPresetSlot ? Formatting.YELLOW : Formatting.GREEN));
        } else if (isPresetSlot) {
            tooltip.add(Text.literal("Empty Preset Slot").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Click a gem in Inventory to equip").formatted(Formatting.YELLOW));
        }
        return tooltip;
    }

    private static void applyGemModifiers(LivingEntity entity, Map<Identifier, GemComponent> presets) {
        for (Map.Entry<Identifier, GemComponent> entry : presets.entrySet()) {
            Identifier slotKey = entry.getKey();
            GemComponent gem = entry.getValue();
            if (gem != null) {
                Map<Identifier, EntityAttributeModifier> modifiers = gem.attributeModifiers();
                for (Map.Entry<Identifier, EntityAttributeModifier> modifierEntry : modifiers.entrySet()) {
                    Registries.ATTRIBUTE.getEntry(modifierEntry.getKey()).ifPresent(attribute -> {
                        EntityAttributeModifier original = modifierEntry.getValue();
                        Identifier modifierId = Identifier.of(
                                "dominatus",
                                "gem." + slotKey.getPath() + "." + modifierEntry.getKey().getPath()
                        );
                        EntityAttributeModifier newModifier = new EntityAttributeModifier(
                                modifierId,
                                original.value(),
                                original.operation()
                        );
                        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
                        if (instance != null) {
                            // Use addPersistentModifier instead of addTemporaryModifier
                            instance.addPersistentModifier(newModifier);
                            // Dominatus.LOGGER.info("Adding persistent modifier: {} with value: {} for attribute: {}", modifierId, newModifier.value(), attribute);
                        }
                    });
                }
            }
        }
    }

    public static void updateEntityStats(PlayerEntity player) {
        GemDataComponent gemData = GemDataComponent.get(player);
        // Dominatus.LOGGER.info("Gem Presets before update: {}", gemData.getMutableGemPresets());
        clearExistingModifiers(player, gemData);
        applyGemModifiers(player, gemData.getMutableGemPresets());
    }

    public static Identifier getGemTexture(GemComponent gem) {
        if (gem == null) return Dominatus.id("hud/gem/gem");

        Optional<GemComponent> registeredGem = GemItemDataReloadListener.getGemType(gem.type());
        if (registeredGem.isPresent() && registeredGem.get().texture().isPresent()) return registeredGem.get().texture().get();

        Optional<Identifier> customTexture = gem.texture();
        return customTexture.orElseGet(() -> Dominatus.id("hud/gem/gem"));
    }
}