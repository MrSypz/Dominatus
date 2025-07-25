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

public abstract class UpgradeableSkill extends ActiveSkill {
    protected final int maxLevel;
    protected final int[] upgradeCosts; // Class points needed for each level

    public UpgradeableSkill(Identifier id, String name, String description,
                            PlayerClass requiredClass, int requiredClassLevel, int learnCost,
                            ResourceType resourceType, float resourceCost, int cooldownTicks,
                            float baseDamage, int hitCount, int maxLevel, int[] upgradeCosts) {
        super(id, name, description, requiredClass, requiredClassLevel, learnCost,
                resourceType, resourceCost, cooldownTicks, baseDamage, hitCount);
        this.maxLevel = maxLevel;
        this.upgradeCosts = upgradeCosts;
    }

    /**
     * Get upgrade cost for leveling from current level to next level
     */
    public int getUpgradeCost(int currentLevel) {
        if (currentLevel <= 0 || currentLevel >= maxLevel) return 0;
        if (currentLevel - 1 >= upgradeCosts.length) return 0;
        return upgradeCosts[currentLevel - 1];
    }

    /**
     * Check if skill can be upgraded from current level
     */
    public boolean canUpgrade(PlayerEntity player, int currentLevel) {
        if (currentLevel >= maxLevel) return false;

        int upgradeCost = getUpgradeCost(currentLevel);
        if (upgradeCost <= 0) return false;

        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
        return classComp.hasClassPoints(upgradeCost);
    }

    /**
     * Get scaled damage based on skill level
     */
    protected float getScaledDamage(int skillLevel) {
        // Each level increases damage by 25%
        float multiplier = 1.0f + (skillLevel - 1) * 0.25f;
        return baseDamage * multiplier;
    }

    /**
     * Get scaled resource cost based on skill level
     */
    protected float getScaledResourceCost(int skillLevel) {
        // Higher levels cost 10% more resources
        float multiplier = 1.0f + (skillLevel - 1) * 0.1f;
        return resourceCost * multiplier;
    }

    /**
     * Get scaled cooldown based on skill level
     */
    public int getScaledCooldown(int skillLevel) {
        // Higher levels have 10% shorter cooldowns
        float multiplier = 1.0f - (skillLevel - 1) * 0.1f;
        return Math.max(20, (int) (cooldownTicks * multiplier)); // Min 1 second cooldown
    }

    /**
     * Execute skill with level scaling
     */
    public SkillResult executeWithLevel(PlayerEntity caster, int skillLevel) {
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(caster);

        float actualResourceCost = getScaledResourceCost(skillLevel);

        // Check resources
        if (!classComp.hasResource(actualResourceCost)) {
            return SkillResult.failure("Not enough " + resourceType.getDisplayName());
        }

        // Consume resources
        if (!classComp.consumeResource(actualResourceCost)) {
            return SkillResult.failure("Failed to consume resources");
        }

        // Get targets
        List<LivingEntity> targets = getTargets(caster);
        if (targets.isEmpty()) {
            return SkillResult.failure("No targets found");
        }

        // Apply scaled effects
        for (LivingEntity target : targets) {
            for (int hit = 0; hit < hitCount; hit++) {
                applyScaledDamage(caster, target, skillLevel);
                applyScaledEffects(caster, target, skillLevel);
            }
        }

        return SkillResult.success(targets.size() + " targets hit (Level " + skillLevel + ")");
    }

    /**
     * Apply damage scaled by skill level (override for custom scaling)
     */
    protected void applyScaledDamage(PlayerEntity caster, LivingEntity target, int skillLevel) {
        float damage = getScaledDamage(skillLevel);
        target.damage(caster.getDamageSources().playerAttack(caster), damage);
    }

    /**
     * Apply effects scaled by skill level (override for level-specific effects)
     */
    protected void applyScaledEffects(PlayerEntity caster, LivingEntity target, int skillLevel) {
        // Default: call normal effects
        applyEffects(caster, target);
    }

    public int getMaxLevel() { return maxLevel; }

    public Text getFormattedNameWithLevel(int level) {
        return Text.literal(name + " " + toRoman(level)).formatted(Formatting.YELLOW);
    }

    private String toRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(number);
        };
    }
}