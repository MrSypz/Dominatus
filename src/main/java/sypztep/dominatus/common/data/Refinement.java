package sypztep.dominatus.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Refinement(int refine, int accuracy, int evasion, int durability, float damage, int protection, int damageReduction) {
    public static final Codec<Refinement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("refine").forGetter(Refinement::refine),
            Codec.INT.fieldOf("accuracy").forGetter(Refinement::accuracy),
            Codec.INT.fieldOf("evasion").forGetter(Refinement::evasion),
            Codec.INT.optionalFieldOf("durability", 100).forGetter(Refinement::durability),
            Codec.FLOAT.optionalFieldOf("damage", 0.0f).forGetter(Refinement::damage),
            Codec.INT.optionalFieldOf("protection", 0).forGetter(Refinement::protection),
            Codec.INT.optionalFieldOf("damage_reduction", 0).forGetter(Refinement::damageReduction)
    ).apply(instance, Refinement::new));
}