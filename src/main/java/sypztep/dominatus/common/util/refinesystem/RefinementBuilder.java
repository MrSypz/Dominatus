package sypztep.dominatus.common.util.refinesystem;

import net.minecraft.item.ItemStack;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.init.ModDataComponents;

public final class RefinementBuilder {
    private int refine;
    private int accuracy;
    private int evasion;
    private int durability;
    private float damage;
    private int protection;
    private int damageReduction;

    public RefinementBuilder() {
        this.refine = 0;
        this.accuracy = 0;
        this.evasion = 0;
        this.durability = 100;
        this.damage = 0;
        this.protection = 0;
        this.damageReduction = 0;
    }

    public RefinementBuilder fromExisting(Refinement existing) {
        if (existing != null) {
            this.refine = existing.refine();
            this.accuracy = existing.accuracy();
            this.evasion = existing.evasion();
            this.durability = existing.durability();
            this.damage = existing.damage();
            this.protection = existing.protection();
            this.damageReduction = existing.damageReduction();
        }
        return this;
    }

    public RefinementBuilder withRefine(int refine) {
        this.refine = refine;
        return this;
    }

    public RefinementBuilder withAccuracy(int accuracy) {
        this.accuracy = accuracy;
        return this;
    }

    public RefinementBuilder withEvasion(int evasion) {
        this.evasion = evasion;
        return this;
    }

    public RefinementBuilder withDurability(int durability) {
        this.durability = durability;
        return this;
    }

    public RefinementBuilder withDamage(float damage) {
        this.damage = damage;
        return this;
    }

    public RefinementBuilder withProtection(int protection) {
        this.protection = protection;
        return this;
    }

    public RefinementBuilder withDamageReduction(int damageReduction) {
        this.damageReduction = damageReduction;
        return this;
    }

    public Refinement build() {
        return new Refinement(refine, accuracy, evasion, durability, damage, protection, damageReduction);
    }

    public void applyTo(ItemStack stack) {
        stack.set(ModDataComponents.REFINEMENT, build());
    }
}