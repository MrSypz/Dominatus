package sypztep.dominatus.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.client.screen.PlayerInfoScreen;

public class DominatusClient implements ClientModInitializer {
    public static KeyBinding stats_screen = new KeyBinding("key.dominatus.debug", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, "category.dominatus.keybind");
    public static ModConfig config = new ModConfig();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(DominatusClient::onEndTick);

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

    }
    private static void onEndTick(MinecraftClient client) {
        if (stats_screen.wasPressed()) client.setScreen(new PlayerInfoScreen());
    }
}
