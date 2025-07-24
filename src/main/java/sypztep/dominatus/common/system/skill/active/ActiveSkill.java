package sypztep.dominatus.common.system.skill.active;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.component.living.PlayerClassComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.playerclass.PlayerClass;
import sypztep.dominatus.common.system.playerclass.ResourceType;

import java.util.List;

public abstract class ActiveSkill {
    protected final Identifier id;
    protected final String name;
    protected final String description;
    protected final PlayerClass requiredClass;
    protected final int requiredClassLevel;
    protected final int learnCost; // Class points needed to learn
    protected final ResourceType resourceType;
    protected final float resourceCost;
    protected final int cooldownTicks;
    protected final float baseDamage;
    protected final int hitCount;

    public ActiveSkill(Identifier id, String name, String description,
                       PlayerClass requiredClass, int requiredClassLevel, int learnCost,
                       ResourceType resourceType, float resourceCost, int cooldownTicks,
                       float baseDamage, int hitCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredClass = requiredClass;
        this.requiredClassLevel = requiredClassLevel;
        this.learnCost = learnCost;
        this.resourceType = resourceType;
        this.resourceCost = resourceCost;
        this.cooldownTicks = cooldownTicks;
        this.baseDamage = baseDamage;
        this.hitCount = hitCount;
    }

    /**
     * Check if player can learn this skill
     */
    public boolean canLearn(PlayerEntity player) {
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

        return classComp.getCurrentClass() == requiredClass &&
                classComp.getClassLevel() >= requiredClassLevel &&
                classComp.hasClassPoints(learnCost);
    }

    /**
     * Check if player can use this skill (has resources, not on cooldown)
     */
    public boolean canUse(PlayerEntity player) {
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

        return classComp.hasResource(resourceCost) &&
                !isOnCooldown(player);
    }

    /**
     * Execute the skill (override in subclasses for specific behavior)
     */
    public SkillResult execute(PlayerEntity caster) {
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(caster);

        // Check if can use
        if (!canUse(caster)) {
            return SkillResult.failure("Cannot use skill");
        }

        // Consume resources
        if (!classComp.consumeResource(resourceCost)) {
            return SkillResult.failure("Not enough " + resourceType.getDisplayName());
        }

        // Apply cooldown (TODO: implement cooldown system)
        // applyCooldown(caster);

        // Execute skill-specific logic
        List<LivingEntity> targets = getTargets(caster);
        if (targets.isEmpty()) {
            return SkillResult.failure("No targets found");
        }

        // Apply damage/effects
        for (LivingEntity target : targets) {
            for (int hit = 0; hit < hitCount; hit++) {
                applyDamage(caster, target);
                applyEffects(caster, target);
            }
        }

        return SkillResult.success(targets.size() + " targets hit");
    }

    /**
     * Get targets for this skill (override in subclasses)
     */
    protected abstract List<LivingEntity> getTargets(PlayerEntity caster);

    /**
     * Apply damage to a target
     */
    protected void applyDamage(PlayerEntity caster, LivingEntity target) {
        // TODO: Add proper damage source and calculation
        target.damage(caster.getDamageSources().playerAttack(caster), baseDamage);
    }

    /**
     * Apply additional effects (override in subclasses)
     */
    protected void applyEffects(PlayerEntity caster, LivingEntity target) {
        // Default: no additional effects
    }

    /**
     * Check if skill is on cooldown (placeholder for now)
     */
    protected boolean isOnCooldown(PlayerEntity player) {
        // TODO: Implement proper cooldown system
        return false;
    }

    // Getters
    public Identifier getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public PlayerClass getRequiredClass() { return requiredClass; }
    public int getRequiredClassLevel() { return requiredClassLevel; }
    public int getLearnCost() { return learnCost; }
    public ResourceType getResourceType() { return resourceType; }
    public float getResourceCost() { return resourceCost; }
    public int getCooldownTicks() { return cooldownTicks; }
    public float getBaseDamage() { return baseDamage; }
    public int getHitCount() { return hitCount; }

    public Text getFormattedName() {
        return Text.literal(name).formatted(Formatting.YELLOW);
    }

    public Text getFormattedDescription() {
        return Text.literal(description).formatted(Formatting.GRAY);
    }
}