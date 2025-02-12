package sypztep.dominatus.common.init;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.data.Refinement;

public class ModDataComponents {
    public static final ComponentType<Refinement> REFINEMENT = new ComponentType.Builder<Refinement>().codec(Refinement.CODEC).build();

    public static void init() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, Dominatus.id("refinement"), REFINEMENT);
    }
}
