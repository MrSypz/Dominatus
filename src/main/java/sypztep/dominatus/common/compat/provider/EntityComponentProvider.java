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
        if (!(targetEntity instanceof LivingEntity livingTarget)) return;

        EntityCombatAttributes playerAttributes = new EntityCombatAttributes(entityAccessor.getPlayer());
        EntityCombatAttributes targetAttributes = new EntityCombatAttributes(livingTarget);

        double hitChance = playerAttributes.getAccuracy().calculateHitChance(targetAttributes.getEvasion(), livingTarget.getArmor());
        int hitChancePercent = (int) (hitChance * 100);

        iTooltip.add(Text.translatable("tooltip.dominatus.hit_chance", hitChancePercent)
                .formatted(getColorForHitChance(hitChancePercent)));
    }
    private Formatting getColorForHitChance(int hitChancePercent) {
        int bucket = hitChancePercent >> 4;
        if (bucket >= 6) return Formatting.GREEN;
        if (bucket >= 4) return Formatting.YELLOW;
        if (bucket >= 3) return Formatting.GOLD;
        if (bucket >= 2) return Formatting.RED;
        return Formatting.DARK_RED;
    }

    @Override
    public Identifier getUid() {
        return Dominatus.id("stats_config");
    }
}
