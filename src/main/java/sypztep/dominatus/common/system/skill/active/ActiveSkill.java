package sypztep.dominatus.common.system.skill.active;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sypztep.dominatus.common.component.living.PlayerClassComponent;
import sypztep.dominatus.common.init.ModDamageSources;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.playerclass.PlayerClass;
import sypztep.dominatus.common.system.playerclass.ResourceType;
import sypztep.dominatus.common.util.DelayedDamageScheduler;
import sypztep.dominatus.common.util.SkillDamageSource;

import java.util.List;

public abstract class ActiveSkill {
    protected final Identifier id;
    protected final String name;
    protected final String description;
    protected final PlayerClass requiredClass;
    protected final int requiredClassLevel;
    protected final int learnCost;
    protected final ResourceType resourceType;
    protected final float resourceCost;
    protected final int cooldownTicks;
    protected final float baseDamage;
    protected final int hitCount;
    protected final int hitDelayTicks;

    public ActiveSkill(Identifier id, String name, String description,
                       PlayerClass requiredClass, int requiredClassLevel, int learnCost,
                       ResourceType resourceType, float resourceCost, int cooldownTicks,
                       float baseDamage, int hitCount) {
        this(id, name, description, requiredClass, requiredClassLevel, learnCost,
                resourceType, resourceCost, cooldownTicks, baseDamage, hitCount, 5);
    }

    public ActiveSkill(Identifier id, String name, String description,
                       PlayerClass requiredClass, int requiredClassLevel, int learnCost,
                       ResourceType resourceType, float resourceCost, int cooldownTicks,
                       float baseDamage, int hitCount, int hitDelayTicks) {
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
        this.hitDelayTicks = hitDelayTicks;
    }

    public boolean canLearn(PlayerEntity player) {
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
        return classComp.getCurrentClass() == requiredClass &&
                classComp.getClassLevel() >= requiredClassLevel &&
                classComp.hasClassPoints(learnCost);
    }

    public boolean canUse(PlayerEntity player) {
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);
        return classComp.hasResource(resourceCost) && !isOnCooldown(player);
    }

    /**
     * Execute the skill with delayed multi-hit damage
     */
    public SkillResult execute(PlayerEntity caster) {
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(caster);

        if (!canUse(caster)) {
            return SkillResult.failure("Cannot use skill");
        }

        if (!classComp.consumeResource(resourceCost)) {
            return SkillResult.failure("Not enough " + resourceType.getDisplayName());
        }

        List<LivingEntity> targets = getTargets(caster);
        if (targets.isEmpty()) {
            return SkillResult.failure("No targets found");
        }

        scheduleDelayedDamage(caster, targets);
        return SkillResult.success(targets.size() + " targets hit");
    }

    /**
     * Schedule delayed damage for all hits
     */
    private void scheduleDelayedDamage(PlayerEntity caster, List<LivingEntity> targets) {
        if (!(caster.getWorld() instanceof ServerWorld serverWorld)) return;

        DelayedDamageScheduler scheduler = DelayedDamageScheduler.getInstance();

        for (LivingEntity target : targets) {
            for (int hit = 0; hit < hitCount; hit++) {
                int delay = hit * hitDelayTicks;

                scheduler.scheduleDamage(serverWorld, delay, () -> {
                    if (target.isAlive() && !target.isRemoved()) {
                        applyDamageBypass(caster, target);
                        applyEffects(caster, target);
                    }
                });
            }
        }
    }

    /**
     * Apply damage that bypasses immunity frames
     */
    protected void applyDamageBypass(PlayerEntity caster, LivingEntity target) {
        // Create skill damage source using existing player attack source
        if (!(caster.getWorld() instanceof ServerWorld serverWorld)) return;


        // Reset immunity to allow damage
        target.timeUntilRegen = 0;
        target.hurtTime = 0;

        // Apply damage
        target.damage(ModDamageSources.skillDamage(serverWorld, caster), baseDamage);
    }

    protected abstract List<LivingEntity> getTargets(PlayerEntity caster);

    protected void applyEffects(PlayerEntity caster, LivingEntity target) {
        // Default: no additional effects
    }

    protected boolean isOnCooldown(PlayerEntity player) {
        return false; // Handled by PlayerSkillComponent
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
    public int getHitDelayTicks() { return hitDelayTicks; }

    public Text getFormattedName() {
        return Text.literal(name).formatted(Formatting.YELLOW);
    }

    public Text getFormattedDescription() {
        return Text.literal(description).formatted(Formatting.GRAY);
    }
}