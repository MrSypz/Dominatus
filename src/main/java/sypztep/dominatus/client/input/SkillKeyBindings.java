package sypztep.dominatus.client.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import sypztep.dominatus.client.screen.SkillHotbarScreen;
import sypztep.dominatus.common.payload.UseSkillPayloadC2S;

public class SkillKeyBindings {

    public static final KeyBinding[] SKILL_SLOTS = new KeyBinding[6];
    public static KeyBinding OPEN_SKILL_HOTBAR;

    public static void register() {
        // Register skill slot keybindings (1-6)
        for (int i = 0; i < 6; i++) {
            SKILL_SLOTS[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.dominatus.skill_slot_" + (i + 1), // Translation key
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_1 + i, // Keys 1-6
                    "category.dominatus.skills" // Category
            ));
        }

        // Register hotbar management key (H)
        OPEN_SKILL_HOTBAR = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dominatus.open_skill_hotbar",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.dominatus.skills"
        ));

        // Register tick handler
        ClientTickEvents.END_CLIENT_TICK.register(SkillKeyBindings::handleKeyInputs);
    }

    private static void handleKeyInputs(MinecraftClient client) {
        if (client.player == null || client.currentScreen != null) return;

        // Handle skill slot keys (1-6)
        for (int i = 0; i < 6; i++) {
            if (SKILL_SLOTS[i].wasPressed()) {
                // Send packet to server to use skill in slot
                UseSkillPayloadC2S.send(i);
            }
        }

        // Handle hotbar management key (H)
        if (OPEN_SKILL_HOTBAR.wasPressed()) {
            client.setScreen(new SkillHotbarScreen());
        }
    }
}