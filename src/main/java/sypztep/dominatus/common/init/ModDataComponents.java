package sypztep.dominatus.common.init;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.util.ReformSystem.ReformManager;

public class ModDataComponents {
    public static final ComponentType<Refinement> REFINEMENT = new ComponentType.Builder<Refinement>().codec(Refinement.CODEC).build();
    public static final ComponentType<ReformManager.Reform> REFORM = new ComponentType.Builder<ReformManager.Reform>().codec(ReformManager.Reform.CODEC).build();

    public static void init() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, Dominatus.id("refinement"), REFINEMENT);
        Registry.register(Registries.DATA_COMPONENT_TYPE, Dominatus.id("reform"), REFORM);
    }
}
