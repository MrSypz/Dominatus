package sypztep.dominatus.common.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public final class FailStackComponent implements AutoSyncedComponent {
    private final PlayerEntity obj;
    private int failstack;

    public FailStackComponent(PlayerEntity obj) {
        this.obj = obj;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        failstack = nbtCompound.getInt("Failstack");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putInt("Failstack", failstack);
    }
}
