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
    public static TextParticleProvider MISSING_MONSTER;
    public static TextParticleProvider BACKATTACK;
    static {
        CRITICAL = TextParticleProvider.register(Text.translatable("dominatus.text.critical"), new Color(ModConfig.critDamageColor), -0.055f, -0.045F, () -> ModConfig.damageCritIndicator);
        MISSING = TextParticleProvider.register(Text.translatable("dominatus.text.missing"), new Color(1f, 1f, 1f), -0.045f, -0.085F, () -> ModConfig.missingIndicator);
        MISSING_MONSTER = TextParticleProvider.register(Text.translatable("dominatus.text.missing"), new Color(255,  28, 28),-0.045F,-0.085F, () -> ModConfig.missingIndicator);
        BACKATTACK = TextParticleProvider.register(Text.translatable("dominatus.text.back"), new Color(1f,1f,1f),-0.035f,0.3f, () -> ModConfig.damageCritIndicator);
    }
}
