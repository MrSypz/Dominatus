package sypztep.dominatus.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sypztep.dominatus.common.util.ReformSystem.ReformType;

public record Reform(ReformType type) {
    public static final Codec<Reform> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("type").xmap(
                            type -> {
                                try {
                                    return ReformType.valueOf(type);
                                } catch (IllegalArgumentException e) {
                                    return ReformType.NONE;
                                }
                            },
                            ReformType::name
                    ).forGetter(Reform::type)
            ).apply(instance, Reform::new)
    );

    public boolean hasEffect() {
        return type != ReformType.NONE;
    }
}