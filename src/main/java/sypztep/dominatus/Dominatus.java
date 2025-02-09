package sypztep.dominatus;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Dominatus implements ModInitializer {
    public static final String MODID = "dominatus";
    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }

    @Override
    public void onInitialize() {
    }
}
