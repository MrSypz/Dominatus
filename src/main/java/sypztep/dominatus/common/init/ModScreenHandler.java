package sypztep.dominatus.common.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandler {
    public static ScreenHandlerType<RefineScreenHandler> REFINE_SCREEN_HANDLER_TYPE;
    public static void nit() {
        REFINE_SCREEN_HANDLER_TYPE = Registry.register(Registries.SCREEN_HANDLER, "refiner",
                new ScreenHandlerType<>((syncId, inventory) -> new RefineScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY), FeatureFlags.VANILLA_FEATURES));
    }
}
