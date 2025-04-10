package sypztep.dominatus.common.util.gemsystem;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
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

        Map<String, GemComponent> presets = component.getMutableGemPresets();
        presets.clear();
        if (tag.contains("GemPresets", NbtElement.COMPOUND_TYPE)) {
            NbtCompound presetsTag = tag.getCompound("GemPresets");
            for (String key : presetsTag.getKeys()) {
                if (presetsTag.contains(key, NbtElement.COMPOUND_TYPE)) {
                    GemComponent.CODEC.parse(registryLookup.getOps(NbtOps.INSTANCE), presetsTag.getCompound(key))
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
            GemComponent.CODEC.encodeStart(registryLookup.getOps(NbtOps.INSTANCE), gem)
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

    private static void clearExistingModifiers(LivingEntity entity) {
        Set<EntityAttributeInstance> trackedAttributes = entity.getAttributes().getTracked();
        for (EntityAttributeInstance instance : trackedAttributes) {
            getModifier(instance);

        }

        EntityAttributeInstance attackDamageInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attackDamageInstance != null) getModifier(attackDamageInstance);

    }

    private static void getModifier(EntityAttributeInstance instance) {
        List<Identifier> modifiersToRemove = new ArrayList<>();
        for (EntityAttributeModifier modifier : instance.getModifiers()) {
            String modifierIdStr = modifier.id().toString();
            if (modifierIdStr.startsWith("dominatus:gem.slot_")) modifiersToRemove.add(modifier.id());
        }
        for (Identifier modifierId : modifiersToRemove)
            instance.removeModifier(modifierId);
    }

    private static void applyGemModifiers(LivingEntity entity, Map<String, GemComponent> presets) {
        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiersToAdd = ArrayListMultimap.create();

        for (Map.Entry<String, GemComponent> entry : presets.entrySet()) {
            String slotKey = entry.getKey();
            GemComponent gem = entry.getValue();
            if (gem != null) {
                Map<Identifier, EntityAttributeModifier> modifiers = gem.attributeModifiers();
                for (Map.Entry<Identifier, EntityAttributeModifier> modifierEntry : modifiers.entrySet()) {
                    Registries.ATTRIBUTE.getEntry(modifierEntry.getKey()).ifPresent(attribute -> {
                        EntityAttributeModifier original = modifierEntry.getValue();
                        Identifier modifierId = Dominatus.id("gem." + slotKey + "." + modifierEntry.getKey().getPath());
                        EntityAttributeModifier newModifier = new EntityAttributeModifier(
                                modifierId,
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

    public static void updateEntityStats(PlayerEntity player) {
        Map<String, GemComponent> presets = GemDataComponent.get(player).getMutableGemPresets();
        clearExistingModifiers(player);
        applyGemModifiers(player, presets);
    }

    public static Identifier getGemTexture(GemComponent gem) {
        if (gem == null) return Dominatus.id("hud/gem/gem");

        Optional<GemComponent> registeredGem = GemItemDataReloadListener.getGemType(gem.type());
        if (registeredGem.isPresent() && registeredGem.get().texture().isPresent()) return registeredGem.get().texture().get();

        Optional<Identifier> customTexture = gem.texture();
        return customTexture.orElseGet(() -> Dominatus.id("hud/gem/gem"));
    }
}