package sypztep.dominatus.common.init;

import net.minecraft.text.Text;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.client.util.TextParticleProvider;

import java.awt.Color;

public final class ModParticles {
    public ModParticles() {
    }

    public static TextParticleProvider CRITICAL;
    public static TextParticleProvider MISSING;
    public static TextParticleProvider BACKATTACK;
    static {
        CRITICAL = TextParticleProvider.register(Text.translatable("dominatus.text.critical"), new Color(ModConfig.critDamageColor), -0.055f, -0.045F, () -> ModConfig.damageCritIndicator);
        MISSING = TextParticleProvider.register(Text.translatable("dominatus.text.missing"), new Color(1f, 1f, 1f), -0.045f, -1, () -> ModConfig.missingIndicator);
        BACKATTACK = TextParticleProvider.register(Text.translatable("dominatus.text.back"), new Color(1f,1f,1f),-0.025f,0.15f, () -> ModConfig.damageCritIndicator);
    }
}
