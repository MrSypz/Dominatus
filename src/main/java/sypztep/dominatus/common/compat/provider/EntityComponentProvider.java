package sypztep.dominatus.common.compat.provider;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.util.combatsystem.EntityCombatAttributes;

public enum EntityComponentProvider implements IEntityComponentProvider {
    INSTANCE;
    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        Entity targetEntity = entityAccessor.getEntity();
        // Only proceed if the target is a LivingEntity
        if (!(targetEntity instanceof LivingEntity livingTarget)) return;

        EntityCombatAttributes playerAttributes = new EntityCombatAttributes(entityAccessor.getPlayer());
        EntityCombatAttributes targetAttributes = new EntityCombatAttributes(livingTarget);

        // Get the hit chance as a percentage
        double hitChance = playerAttributes.getAccuracy().calculateHitChance(targetAttributes.getEvasion());
        int hitChancePercent = (int) (hitChance * 100);

        // Add the hit chance information to the tooltip
        iTooltip.add(Text.translatable("tooltip.dominatus.hit_chance", hitChancePercent)
                .formatted(getColorForHitChance(hitChancePercent)));
    }
    private Formatting getColorForHitChance(int hitChancePercent) {
        if (hitChancePercent >= 90) {
            return Formatting.GREEN;
        } else if (hitChancePercent >= 70) {
            return Formatting.YELLOW;
        } else if (hitChancePercent >= 50) {
            return Formatting.GOLD;
        } else if (hitChancePercent >= 30) {
            return Formatting.RED;
        } else {
            return Formatting.DARK_RED;
        }
    }


    @Override
    public Identifier getUid() {
        return Dominatus.id("stats_config");
    }
}
