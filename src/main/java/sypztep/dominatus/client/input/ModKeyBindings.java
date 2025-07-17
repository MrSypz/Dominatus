package sypztep.dominatus.client.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import sypztep.dominatus.client.screen.StatAllocationScreen;

public class ModKeyBindings {

    public static KeyBinding OPEN_STAT_SCREEN;

    public static void register() {
        OPEN_STAT_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dominatus.open_stat_screen", // Translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K, // K key
                "category.dominatus.keys" // Category translation key
        ));

        ClientTickEvents.END_CLIENT_TICK.register(ModKeyBindings::handleKeyInputs);
    }

    private static void handleKeyInputs(MinecraftClient client) {
        if (OPEN_STAT_SCREEN.wasPressed()) {
            if (client.player != null && client.currentScreen == null) {
                client.setScreen(new StatAllocationScreen());
            }
        }
    }
}