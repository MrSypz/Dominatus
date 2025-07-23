package sypztep.dominatus.common.init;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import sypztep.dominatus.Dominatus;

public class ModParticles {
    public static final SimpleParticleType AIRHIKE = add("airhike");
    public static void init() {
    }
    private static SimpleParticleType add(String name) {
        return Registry.register(Registries.PARTICLE_TYPE, Dominatus.id(name), FabricParticleTypes.simple());
    }
}
