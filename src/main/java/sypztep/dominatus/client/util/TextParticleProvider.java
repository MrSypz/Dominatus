package sypztep.dominatus.client.util;

import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.function.Supplier;

public class TextParticleProvider {
    private static final TextParticleProvider[] REGISTRY = new TextParticleProvider[8];
    private static int nextId = 0;

    private final int id;
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
        if (nextId >= REGISTRY.length) throw new IllegalStateException("Too many TextParticleProviders registered! Max: " + REGISTRY.length + "Go increasing the size in array");
        this.id = nextId++;
        this.text = text;
        this.color = color;
        this.maxSize = maxSize;
        this.yPos = yPos;
        this.configSupplier = configSupplier;
        REGISTRY[this.id] = this;
    }

    public static Builder builder(Text text) {
        return new Builder(text);
    }

    public static TextParticleProvider register(Text text) {
        return builder(text).build();
    }

    public static TextParticleProvider register(Text text, float maxSize) {
        return builder(text).maxSize(maxSize).build();
    }

    public static TextParticleProvider register(Text text, Color color, float maxSize) {
        return builder(text).color(color).maxSize(maxSize).build();
    }

    public static TextParticleProvider register(Text text, Color color, float maxSize, float yPos) {
        return builder(text).color(color).maxSize(maxSize).yPos(yPos).build();
    }

    public static TextParticleProvider register(Text text, Color color, float maxSize, float yPos, Supplier<Boolean> configSupplier) {
        return builder(text).color(color).maxSize(maxSize).yPos(yPos).config(configSupplier).build();
    }

    public int getId() {
        return id;
    }

    public static void handleParticle(Entity entity, int id) {
        if (id >= 0 && id < REGISTRY.length) {
            TextParticleProvider particle = REGISTRY[id];
            if (particle != null && particle.configSupplier.get()) {
                spawnTextParticle(entity, particle.text, particle.color, particle.maxSize, particle.yPos);
            }
        }
    }

    private static void spawnTextParticle(Entity target, Text text, Color color, float maxSize, float yPos) {
        if (target.getWorld().isClient()) {
            TextParticleClient.spawnParticle(target, text.getString(), color, maxSize, yPos);
        }
    }
}