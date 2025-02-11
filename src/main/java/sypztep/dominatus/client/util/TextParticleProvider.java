package sypztep.dominatus.client.util;

import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import sypztep.knumber.client.particle.util.ParticleUtil;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TextParticleProvider {
    private static final Map<Integer, TextParticleProvider> REGISTRY = new HashMap<>();
    private static int nextFlag = 0;

    private final int flag;
    private final Text text;
    private final Color color;
    private final float maxSize;
    private final float yPos;
    private final Supplier<Boolean> configSupplier;

    public static class Builder {
        private Text text;
        private Color color = Color.WHITE;
        private float maxSize = -0.045f;
        private float yPos = 0f;
        private Supplier<Boolean> configSupplier = () -> true;

        public Builder(Text text) {
            this.text = text;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder maxSize(float maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder yPos(float yPos) {
            this.yPos = yPos;
            return this;
        }

        public Builder config(Supplier<Boolean> configSupplier) {
            this.configSupplier = configSupplier != null ? configSupplier : () -> true;
            return this;
        }

        public TextParticleProvider build() {
            return new TextParticleProvider(text, color, maxSize, yPos, configSupplier);
        }
    }

    private TextParticleProvider(Text text, Color color, float maxSize, float yPos, Supplier<Boolean> configSupplier) {
        this.flag = nextFlag++;
        this.text = text;
        this.color = color;
        this.maxSize = maxSize;
        this.yPos = yPos;
        this.configSupplier = configSupplier;
        REGISTRY.put(this.flag, this);
    }

    public static Builder builder(Text text) {
        return new Builder(text);
    }

    public static TextParticleProvider register(Text text) {
        return builder(text).build();
    }

    public static TextParticleProvider register(Text text, float maxSize) {
        return builder(text)
                .maxSize(maxSize)
                .build();
    }

    public static TextParticleProvider register(Text text, Color color, float maxSize) {
        return builder(text)
                .color(color)
                .maxSize(maxSize)
                .build();
    }

    public static TextParticleProvider register(Text text, Color color, float maxSize, float yPos) {
        return builder(text)
                .color(color)
                .maxSize(maxSize)
                .yPos(yPos)
                .build();
    }

    /**
     * Registers a fully customizable text particle with configuration support.
     *
     * @param text           The text to display
     * @param color         The color of the particle
     * @param maxSize       The maximum size of the particle
     * @param yPos          The vertical position offset
     * @param configSupplier A supplier that determines if the particle should spawn
     * @return A new TextParticleProvider instance
     * @since 1.0.2
     *
     * @example Usage with mod configs:
     * <pre>
     * {@code
     * TextParticleProvider simple = TextParticleProvider.register(Text.of("Simple"));
     *
     * // Using builder for more control
     * TextParticleProvider custom = TextParticleProvider.builder(Text.of("Custom"))
     *     .color(Color.RED)
     *     .maxSize(-0.055f)
     *     .yPos(0.15f)
     *     .config(() -> ModConfig.damageNumberIndicator)
     *     .build();
     *
     * // Using builder to set only what you need
     * TextParticleProvider partial = TextParticleProvider.builder(Text.of("Partial"))
     *     .color(Color.BLUE)
     *     .maxSize(-0.065f)
     *     .build();
     *
     * // Using another mod's config
     * TextParticleProvider otherMod = TextParticleProvider.builder(Text.of("OtherMod"))
     *     .color(Color.GREEN)
     *     .config(() -> OtherModConfig.isEnabled())
     *     .build();
     * </pre>
     *
     *
     */
    public static TextParticleProvider register(Text text, Color color, float maxSize, float yPos, Supplier<Boolean> configSupplier) {
        return builder(text)
                .color(color)
                .maxSize(maxSize)
                .yPos(yPos)
                .config(configSupplier)
                .build();
    }

    public int getFlag() {
        return flag;
    }

    public static void handleParticle(Entity entity, int flag) {
        TextParticleProvider particle = REGISTRY.get(flag);
        if (particle != null && particle.configSupplier.get()) {
            ParticleUtil.spawnTextParticle(
                    entity,
                    particle.text,
                    particle.color,
                    particle.maxSize,
                    particle.yPos
            );
        }
    }
}
