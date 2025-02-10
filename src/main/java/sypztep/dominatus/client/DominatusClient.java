package sypztep.dominatus.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import sypztep.dominatus.client.screen.PlayerInfoScreen;

public class DominatusClient implements ClientModInitializer {
    public static KeyBinding stats_screen = new KeyBinding("key.dominatus.debug", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, "category.dominatus.keybind");

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(DominatusClient::onEndTick);

    }
    private static void onEndTick(MinecraftClient client) {
        if (stats_screen.wasPressed()) client.setScreen(new PlayerInfoScreen());
    }
}
