package sypztep.dominatus.client.init;

import net.minecraft.text.Text;
import sypztep.dominatus.ModConfig;
import sypztep.dominatus.client.util.TextParticleProvider;

import java.awt.*;

public class ModParticle {
    public static TextParticleProvider CRITICAL;
    public static TextParticleProvider MISSING;
    static {
        CRITICAL = TextParticleProvider.register(Text.translatable("dominatus.text.critical"), new Color(ModConfig.critDamageColor), -0.055f, -0.045F, () -> ModConfig.damageCritIndicator);
        MISSING = TextParticleProvider.register(Text.translatable("dominatus.text.missing"), new Color(255, 255, 255), -0.045f, -1, () -> ModConfig.missingIndicator);
    }
}
