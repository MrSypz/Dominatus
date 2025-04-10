package sypztep.dominatus.common.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.data.GemComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.util.gemsystem.GemManagerHelper;

import java.util.*;

public class GemDataComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private List<GemComponent> gemInventory = new ArrayList<>();
    private Map<String, GemComponent> gemPresets = new HashMap<>();
    private static final int MAX_INVENTORY_SIZE = 50;
    private static final int MAX_PRESET_SLOTS = 8;

    public GemDataComponent(PlayerEntity player) {
        this.player = player;
        for (int i = 0; i < MAX_PRESET_SLOTS; i++) {
            gemPresets.put("slot_" + i, null);
        }
    }

    public boolean canAddToInventory(GemComponent gem) {
        return gem != null && !isInventoryFull();
    }

    public boolean addToInventory(GemComponent gem) {
        if (canAddToInventory(gem)) {
            gemInventory.add(gem);
            sync();
            return true;
        }
        return false;
    }

    public boolean isInventoryFull() {
        return gemInventory.size() >= MAX_INVENTORY_SIZE;
    }
    public int getMaxInventorySize() {
        return MAX_INVENTORY_SIZE;
    }
    public int getMaxPresetSlots() {
        return MAX_PRESET_SLOTS;
    }
    public boolean hasGemInInventory(Identifier gemType) {
        return gemInventory.stream().anyMatch(gem -> gem.type().equals(gemType));
    }

    public List<GemComponent> getGemInventory() {
        return Collections.unmodifiableList(gemInventory);
    }

    public void removeFromInventory(int index) {
        if (index >= 0 && index < gemInventory.size()) {
            gemInventory.remove(index);
            sync();
        }
    }

    // Preset Methods (updated to use String keys)
    public boolean isPresetSlotValid(Identifier slot) {
        return slot != null && gemPresets.containsKey(slot.getPath());
    }

    public boolean setPresetSlot(Identifier slot, GemComponent gem) {
        String slotKey = slot.getPath();
        if (!gemPresets.containsKey(slotKey) || (gem != null && !canAddGemToPresets(gem))) {
            return false;
        }
        gemPresets.put(slotKey, gem);
        GemManagerHelper.updateEntityStats(player);
        sync();
        return true;
    }

    public boolean isPresetFull() {
        return gemPresets.values().stream().noneMatch(Objects::isNull);
    }

    public Optional<Identifier> getAvailablePresetSlot() {
        return gemPresets.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(entry -> Dominatus.id(entry.getKey()))
                .findFirst();
    }
    public void deleteGemFromPreset(Identifier slot) {
        String slotKey = slot.getPath();
        if (!gemPresets.containsKey(slotKey) || gemPresets.get(slotKey) == null) {
            return; // Slot doesn’t exist or is already empty
        }
        gemPresets.put(slotKey, null); // Remove the gem from the preset
        GemManagerHelper.updateEntityStats(player); // Update player stats
        sync(); // Sync the change to the client
    }

    public Optional<GemComponent> getPresetSlot(Identifier slot) {
        return Optional.ofNullable(gemPresets.get(slot.getPath()));
    }

    public Map<Identifier, GemComponent> getGemPresets() {
        Map<Identifier, GemComponent> result = new HashMap<>();
        gemPresets.forEach((key, value) -> result.put(Dominatus.id(key), value));
        return Collections.unmodifiableMap(result);
    }

    public void clearPresets() {
        gemPresets.replaceAll((k, v) -> null);
        GemManagerHelper.updateEntityStats(player);
        sync();
    }

    public boolean canAddGemToPresets(GemComponent gem) {
        if (gem == null) return true;
        int count = (int) gemPresets.values().stream()
                .filter(g -> g != null && g.type().equals(gem.type()))
                .count();
        return count < gem.maxPresets();
    }

    // Internal access for GemManagerHelper
    public List<GemComponent> getMutableGemInventory() {
        return gemInventory;
    }

    public Map<String, GemComponent> getMutableGemPresets() {
        return gemPresets;
    }

    // NBT and Sync
    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        GemManagerHelper.readGemDataFromNbt(this, tag, registryLookup);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        GemManagerHelper.writeGemDataToNbt(this, tag, registryLookup);
    }

    private void sync() {
        ModEntityComponents.GEM_DATA_COMPONENT.sync(player);
    }

    public static void sync(PlayerEntity player) {
        ModEntityComponents.GEM_DATA_COMPONENT.sync(player);
    }

    // Static Accessors (updated)
    public static GemDataComponent get(PlayerEntity player) {
        return ModEntityComponents.GEM_DATA_COMPONENT.get(player);
    }
    public static int getMaxInventorySize(PlayerEntity player) {
        return get(player).getMaxInventorySize();
    }
    public static int getMaxPresetSlots(PlayerEntity player) {
        return get(player).getMaxPresetSlots();
    }
    public static boolean addToInventory(PlayerEntity player, GemComponent gem) {
        return get(player).addToInventory(gem);
    }

    public static boolean setPresetSlot(PlayerEntity player, Identifier slot, GemComponent gem) {
        return get(player).setPresetSlot(slot, gem);
    }

    public static boolean isInventoryFull(PlayerEntity player) {
        return get(player).isInventoryFull();
    }

    public static boolean isPresetFull(PlayerEntity player) {
        return get(player).isPresetFull();
    }

    public static Optional<Identifier> getAvailablePresetSlot(PlayerEntity player) {
        return get(player).getAvailablePresetSlot();
    }

    public static boolean hasGemInInventory(PlayerEntity player, Identifier gemType) {
        return get(player).hasGemInInventory(gemType);
    }
}