package sypztep.dominatus.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.client.event.RefinementTooltip;
import sypztep.dominatus.client.payload.AddRefineSoundPayloadS2C;
import sypztep.dominatus.client.payload.AddTextParticlesPayload;
import sypztep.dominatus.client.payload.RefinePayloadS2C;
import sypztep.dominatus.client.screen.PlayerInfoScreen;
import sypztep.dominatus.client.screen.RefineScreen;
import sypztep.dominatus.common.init.ModScreenHandler;

public class DominatusClient implements ClientModInitializer {
    public static KeyBinding stats_screen = new KeyBinding("key.dominatus.debug", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, "category.dominatus.keybind");
    public static ModConfig config = new ModConfig();

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandler.REFINE_SCREEN_HANDLER_TYPE, RefineScreen::new);

        ClientTickEvents.END_CLIENT_TICK.register(DominatusClient::onEndTick);

        ClientPlayNetworking.registerGlobalReceiver(RefinePayloadS2C.ID, new RefinePayloadS2C.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(AddTextParticlesPayload.ID, new AddTextParticlesPayload.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(AddRefineSoundPayloadS2C.ID, new AddRefineSoundPayloadS2C.Receiver());

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        ItemTooltipCallback.EVENT.register(new RefinementTooltip());


    }
    private static void onEndTick(MinecraftClient client) {
        if (stats_screen.wasPressed()) client.setScreen(new PlayerInfoScreen());
    }
}
