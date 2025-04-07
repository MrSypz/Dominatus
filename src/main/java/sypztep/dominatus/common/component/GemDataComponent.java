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
    private Map<Identifier, GemComponent> gemPresets = new HashMap<>();
    private static final int MAX_INVENTORY_SIZE = 50;
    private static final int MAX_PRESET_SLOTS = 8;

    public GemDataComponent(PlayerEntity player) {
        this.player = player;
        for (int i = 0; i < MAX_PRESET_SLOTS; i++) {
            gemPresets.put(Dominatus.id("slot_" + i), null);
        }
    }

    // Inventory Methods
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

    public void clearInventory() {
        gemInventory.clear();
        sync();
    }

    // Preset Methods
    public boolean isPresetSlotValid(Identifier slot) {
        return slot != null && gemPresets.containsKey(slot);
    }

    public boolean setPresetSlot(Identifier slot, GemComponent gem) {
        if (!isPresetSlotValid(slot) || (gem != null && !canAddGemToPresets(gem))) {
            return false;
        }
        gemPresets.put(slot, gem);
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
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public Optional<GemComponent> getPresetSlot(Identifier slot) {
        return Optional.ofNullable(gemPresets.get(slot));
    }

    public Map<Identifier, GemComponent> getGemPresets() {
        return Collections.unmodifiableMap(gemPresets);
    }

    public void clearPresets() {
        gemPresets.replaceAll((k, v) -> null);
        GemManagerHelper.updateEntityStats(player);
        sync();
    }

    public boolean canAddGemToPresets(GemComponent gem) {
        if (gem == null) return true; // Null is allowed to clear a slot
        int count = (int) gemPresets.values().stream()
                .filter(g -> g != null && g.type().equals(gem.type()))
                .count();
        return count < gem.maxPresets();
    }

    // Internal access for GemManagerHelper
    public List<GemComponent> getMutableGemInventory() {
        return gemInventory;
    }

    public Map<Identifier, GemComponent> getMutableGemPresets() {
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

    // Static Accessors
    public static GemDataComponent get(PlayerEntity player) {
        return ModEntityComponents.GEM_DATA_COMPONENT.get(player);
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