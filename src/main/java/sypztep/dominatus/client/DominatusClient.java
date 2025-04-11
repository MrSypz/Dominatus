package sypztep.dominatus.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.client.event.RefinementTooltip;
import sypztep.dominatus.client.payload.*;
import sypztep.dominatus.client.screen.PlayerInfoScreen;
import sypztep.dominatus.client.screen.RefineScreen;
import sypztep.dominatus.client.screen.RifMissingScreen;
import sypztep.dominatus.client.widget.tab.RefineButtonWidget;
import sypztep.dominatus.client.widget.tab.StatButtonWidget;
import sypztep.dominatus.common.init.ModScreenHandler;
import sypztep.tyrannus.client.widget.TabWidgetRegistry;

public class DominatusClient implements ClientModInitializer {
    public static KeyBinding stats_screen = new KeyBinding("key.dominatus.info", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.dominatus.keybind");
    public static ModConfig config = new ModConfig();
    private final boolean rifMissing = FabricLoader.getInstance().getModContainer("rif").isEmpty();

    @Override
    public void onInitializeClient() {
        if (rifMissing) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (!shownWarningScreen && client.currentScreen instanceof TitleScreen) {
                    client.setScreen(new RifMissingScreen());
                    shownWarningScreen = true;
                }
            });
        }
        HandledScreens.register(ModScreenHandler.REFINE_SCREEN_HANDLER_TYPE, RefineScreen::new);
        ClientTickEvents.END_CLIENT_TICK.register(DominatusClient::onEndTick);

        ClientPlayNetworking.registerGlobalReceiver(RefinePayloadS2C.ID, new RefinePayloadS2C.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(AddTextParticlesPayloadS2C.ID, new AddTextParticlesPayloadS2C.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(AddRefineSoundPayloadS2C.ID, new AddRefineSoundPayloadS2C.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(GemBreakPayloadS2C.ID, new GemBreakPayloadS2C.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(SyncAttackDamagePayloadS2C.ID, new SyncAttackDamagePayloadS2C.Receiver());

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        ItemTooltipCallback.EVENT.register(new RefinementTooltip());

        TabWidgetRegistry.registerTab(RefineButtonWidget.REFINE_TAB);
        TabWidgetRegistry.registerTab(StatButtonWidget.STATS_TAB);
    }

    private static boolean shownWarningScreen = false;

    private static void onEndTick(MinecraftClient client) {
        if (stats_screen.wasPressed()) client.setScreen(new PlayerInfoScreen());
    }
}
