package sypztep.dominatus.client.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

public final class TextParticle extends Particle {
    private static final int FLICK_DURATION = 12;
    private static final int FADE_DURATION = 10;
    private static final float VELOCITY_DAMPEN = 0.9f;
    private static final float FADE_AMOUNT = 0.1f;

    private String text;
    private float scale;
    private float maxSize;
    private float targetRed;
    private float targetGreen;
    private float targetBlue;
    private float targetAlpha;

    public TextParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
        this.maxAge = 25;
        this.scale = 0.0F;
        this.maxSize = -0.045F;
        this.gravityStrength = -0.125f;
        this.targetRed = this.red;
        this.targetGreen = this.green;
        this.targetBlue = this.blue;
        this.targetAlpha = this.alpha;
    }

    public void setMaxSize(float maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void setColor(float red, float green, float blue) {
        super.setColor(clamp(red, 0.0f, 1.0f), clamp(green, 0.0f, 1.0f), clamp(blue, 0.0f, 1.0f));
    }

    public void setColor(int red, int green, int blue) {
        this.targetRed = clamp(red / 255.0f, 0.0f, 1.0f);
        this.targetGreen = clamp(green / 255.0f, 0.0f, 1.0f);
        this.targetBlue = clamp(blue / 255.0f, 0.0f, 1.0f);
        super.setColor(1.0f, 1.0f, 1.0f); // Start white for flick effect
    }

    public void setAlpha(float alpha) {
        this.targetAlpha = clamp(alpha, 0.0f, 1.0f);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }


    @Override
    public void tick() {
        if (this.age++ <= FLICK_DURATION) {
            float progress = age / (float) FLICK_DURATION;
            setColor(
                    MathHelper.lerp(progress, 1.0f, targetRed),
                    MathHelper.lerp(progress, 1.0f, targetGreen),
                    MathHelper.lerp(progress, 1.0f, targetBlue)
            );
            this.scale = MathHelper.lerp(ease(progress, 0.0F, 1.0F, 1.0F), 0.0F, this.maxSize);
            this.alpha = MathHelper.lerp(progress, 1.0f, targetAlpha);
        } else if (this.age <= this.maxAge) {
            float progress = (age - FLICK_DURATION) / (float) FADE_DURATION;
            setColor(
                    targetRed * (1f - progress * FADE_AMOUNT),
                    targetGreen * (1f - progress * FADE_AMOUNT),
                    targetBlue * (1f - progress * FADE_AMOUNT)
            );
            this.scale = MathHelper.lerp(progress, this.maxSize, 0.0f);
            this.alpha = MathHelper.lerp(progress, targetAlpha, 0.0f);
        } else {
            this.markDead();
        }
        this.velocityY *= VELOCITY_DAMPEN;
        super.tick();
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d cameraPos = camera.getPos();
        float particleX = (float) (prevPosX + (x - prevPosX) * tickDelta - cameraPos.x);
        float particleY = (float) (prevPosY + (y - prevPosY) * tickDelta - cameraPos.y);
        float particleZ = (float) (prevPosZ + (z - prevPosZ) * tickDelta - cameraPos.z);

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        var vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
        float textX = textRenderer.getWidth(text) / -2.0F;

        Matrix4f matrix = new Matrix4f()
                .translation(particleX, particleY, particleZ)
                .rotate(camera.getRotation())
                .rotate((float) Math.PI, 0.0F, 1.0F, 0.0F)
                .scale(scale, scale, scale);

        Vector3f offset = new Vector3f(0.0f, 0.0f, 0.03f);
        int textColor = new Color(clamp(red, 0.0f, 1.0f), clamp(green, 0.0f, 1.0f), clamp(blue, 0.0f, 1.0f), clamp(alpha, 0.1f, 1.0f)).getRGB(); // Ensure alpha does not go below 0.1
        int textBorderColor = new Color(clamp(red, 0.0f, 0.0f), clamp(green, 0.0f, 0.0f), clamp(blue, 0.0f, 0.0f), clamp(alpha, 0.1f, 1.0f)).getRGB(); // Ensure alpha does not go below 0.1

        for (int[] pos : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            matrix.translate(offset);
            textRenderer.draw(text, textX + pos[0], pos[1], textBorderColor, false, matrix,
                    vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        }

        matrix.translate(offset);
        textRenderer.draw(text, textX, 0, textColor, false, matrix,
                vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);

        vertexConsumers.draw();
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    private float ease(float t, float b, float c, float d) {
        float a = -1;
        float p = 0;
        if (t == 0) return b;
        if ((t /= d) == 1) return b + c;
        p = d * .3f;
        float s;
        if (a < Math.abs(c)) {
            a = c;
            s = p / 4;
        } else s = p / (float) (2 * Math.PI) * (float) Math.asin(c / a);
        return a * (float) Math.pow(2, -10 * t) * (float) Math.sin((t * d - s) * (2 * Math.PI) / p) + c + b;
    }
}