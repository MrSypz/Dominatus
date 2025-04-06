package sypztep.dominatus.common.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.entry.RegistryEntry;
import sypztep.dominatus.common.data.DominatusEntityEntry;
import sypztep.dominatus.common.init.ModEntityAttributes;

public final class MobEntityApplyAttribute {
    public static void applyEntityAttributes(LivingEntity entity) {
        DominatusEntityEntry entry = DominatusEntityEntry.BASEMOBSTATS_MAP.get(entity.getType());
        if (entry == null) return;
        applyAttributeIfPresent(entity, ModEntityAttributes.ACCURACY, entry.accuracy());
        applyAttributeIfPresent(entity, ModEntityAttributes.EVASION, entry.evasion());
        applyAttributeIfPresent(entity, ModEntityAttributes.CRIT_CHANCE, entry.critChance());
        applyAttributeIfPresent(entity, ModEntityAttributes.CRIT_DAMAGE, entry.critDamage());
        applyAttributeIfPresent(entity, ModEntityAttributes.BACK_ATTACK, entry.backAttack());
        applyAttributeIfPresent(entity, ModEntityAttributes.AIR_ATTACK, entry.airAttack());
        applyAttributeIfPresent(entity, ModEntityAttributes.DOWN_ATTACK, entry.downAttack());
    }

    private static void applyAttributeIfPresent(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, double value) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        if (instance != null) instance.setBaseValue(value); // ลบ = 0 ออก override ค่าได้
    }
}