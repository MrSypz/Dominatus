package sypztep.dominatus.common.world;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import sypztep.dominatus.Dominatus;

public class DungeonDimension {
    public static final RegistryKey<World> DUNGEON_WORLD = RegistryKey.of(RegistryKeys.WORLD, Dominatus.id("dungeon"));

    public static void init() {
    }
}
