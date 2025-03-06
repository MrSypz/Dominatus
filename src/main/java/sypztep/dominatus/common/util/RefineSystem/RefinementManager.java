package sypztep.dominatus.common.util.RefineSystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import sypztep.dominatus.client.payload.AddRefineSoundPayloadS2C;
import sypztep.dominatus.common.data.DominatusItemEntry;
import sypztep.dominatus.common.data.Refinement;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModItems;

public class RefinementManager {
    public static final int MAX_NORMAL_LEVEL = 15;
    public static final int MAX_ENHANCED_LEVEL = 20;
    public static final int BASE_DURABILITY_LOSS = 10;

    public static final int ENHANCED_FAILSTACK_INCREASE = 2;
    public static final int NORMAL_FAILSTACK_INCREASE = 1;
    public static final int ENHANCED_DURABILITY_MULTIPLIER = 2;

    public record RefinementResult(
            boolean success,
            int newLevel,
            int newDurability,
            int newFailStack,
            boolean consumeMaterial
    ) {}
    public static void initializeRefinement(ItemStack stack) {
        new RefinementBuilder().applyTo(stack);
    }

    public static Refinement getRefinement(ItemStack stack) {
        return stack.get(ModDataComponents.REFINEMENT);
    }

    public static void applyRefinement(ItemStack stack, DominatusItemEntry entry, int newLevel) {
        Refinement oldRef = getRefinement(stack);

        new RefinementBuilder()
                .fromExisting(oldRef)
                .withRefine(newLevel)
                .withAccuracy(RefinementCalculator.calculateStatValue(
                        newLevel, entry.maxLvl(), entry.startAccuracy(), entry.endAccuracy()))
                .withEvasion(RefinementCalculator.calculateStatValue(
                        newLevel, entry.maxLvl(), entry.startEvasion(), entry.endEvasion()))
                .withDamage(RefinementCalculator.calculateStatValue(
                        newLevel, entry.maxLvl(), entry.starDamage(), entry.endDamage()))
                .withProtection(RefinementCalculator.calculateStatValue(
                        newLevel, entry.maxLvl(), entry.startProtection(), entry.endProtection()))
                .applyTo(stack);
    }

    public static RefinementResult processRefinement(ItemStack item, ItemStack material, int failStack, PlayerEntity player) {
        Refinement currentRef = getRefinement(item);
        if (currentRef == null) {
            initializeRefinement(item);
            currentRef = getRefinement(item);
        }

        if (MaterialValidator.isRepairMaterial(material)) return handleRepair(item, failStack, player);

        if (!MaterialValidator.isValidMaterial(material, item, currentRef.refine())) return new RefinementResult(false, currentRef.refine(), currentRef.durability(), failStack, false);

        int currentLevel = currentRef.refine();
        double successRate = RefinementCalculator.calculateSuccessRate(currentLevel, failStack);
        boolean success = Math.random() < successRate;

        if (success) return handleSuccess(item, currentLevel, player);
        else return handleFailure(item, currentLevel, failStack, player);
    }

    private static RefinementResult handleFailure(ItemStack item, int currentLevel, int failStack, PlayerEntity player) {
        Refinement current = getRefinement(item);
        DominatusItemEntry entry = getDominatusEntry(item);

        int newLevel;
        if (currentLevel > MAX_NORMAL_LEVEL && currentLevel != 16) newLevel = currentLevel - 1;
        else newLevel = currentLevel;

        int durabilityLoss = BASE_DURABILITY_LOSS;
        if (currentLevel >= MAX_NORMAL_LEVEL) durabilityLoss *= ENHANCED_DURABILITY_MULTIPLIER; // Double durability loss after +15

        int currentRefinementDurability = current.durability();
        int newRefinementDurability = Math.max(currentRefinementDurability - durabilityLoss, 0);

        float refinementDurabilityPercent = (float)newRefinementDurability / entry.maxDurability();

        float currentItemDurabilityPercent = 0;
        if (item.isDamageable()) currentItemDurabilityPercent = 1.0f - ((float)item.getDamage() / item.getMaxDamage());


        if (item.isDamageable() && refinementDurabilityPercent < currentItemDurabilityPercent) {
            int maxDurability = item.getMaxDamage();
            int newDamage = maxDurability - Math.round(maxDurability * refinementDurabilityPercent);
            item.setDamage(newDamage);
        }

        int failstackIncrease = currentLevel >= MAX_NORMAL_LEVEL ?
                ENHANCED_FAILSTACK_INCREASE : // +2 failstacks for +15 to +20
                NORMAL_FAILSTACK_INCREASE;    // +1 failstack for +1 to +14
        int newFailStack = failStack + failstackIncrease;

        new RefinementBuilder()
                .fromExisting(current)
                .withRefine(newLevel)
                .withDurability(newRefinementDurability)
                .applyTo(item);

        if (player instanceof ServerPlayerEntity serverPlayer)
            AddRefineSoundPayloadS2C.send(serverPlayer, player.getId(), RefineSound.FAIL);

        return new RefinementResult(false, newLevel, newRefinementDurability, newFailStack, true);
    }

    private static RefinementResult handleSuccess(ItemStack item, int currentLevel, PlayerEntity player) {
        int newLevel = currentLevel + 1;
        applyRefinement(item, getDominatusEntry(item), newLevel);
        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (currentLevel >= MAX_NORMAL_LEVEL) {
                AddRefineSoundPayloadS2C.send(serverPlayer, player.getId(), RefineSound.HIGH_ENHANCE);
            }
            AddRefineSoundPayloadS2C.send(serverPlayer, player.getId(), RefineSound.SUCCESS);
        }
        return new RefinementResult(true, newLevel, getRefinement(item).durability(), 0, true);
    }

    private static RefinementResult handleRepair(ItemStack item, int failStack, PlayerEntity player) {
        Refinement current = getRefinement(item);
        DominatusItemEntry entry = getDominatusEntry(item);

        if (current.durability() >= entry.maxDurability()) {
            return new RefinementResult(
                    false,
                    current.refine(),
                    current.durability(),
                    failStack,
                    false
            );
        }

        int repairAmount = entry.repairpoint();
        int newDurability = Math.min(current.durability() + repairAmount, entry.maxDurability());

        new RefinementBuilder()
                .fromExisting(current)
                .withDurability(newDurability)
                .applyTo(item);
        if (player instanceof ServerPlayerEntity serverPlayer)
            AddRefineSoundPayloadS2C.send(serverPlayer, player.getId(), RefineSound.REPAIR);

        return new RefinementResult(
                true,
                current.refine(),
                newDurability,
                failStack,
                true
        );
    }

    public static DominatusItemEntry getDominatusEntry(ItemStack item) {
        return DominatusItemEntry.getDominatusItemData(DominatusItemEntry.getItemId(item))
                .orElseThrow(() -> new IllegalStateException("Invalid item for refinement"));
    }

    public static int getMaxAllowedVanillaRepair(ItemStack item) {
        if (!item.isDamageable() || !item.contains(ModDataComponents.REFINEMENT)) {
            return 0;
        }

        Refinement refinement = getRefinement(item);
        DominatusItemEntry entry = getDominatusEntry(item);

        float customDurabilityPercent = (float)refinement.durability() / entry.maxDurability();
        int maxVanillaDurability = item.getMaxDamage();
        return Math.round(maxVanillaDurability * (1 - customDurabilityPercent));
    }

    public static class MaterialValidator {
        public static boolean isValidMaterial(ItemStack material, ItemStack target, int currentLevel) {
            boolean isArmor = target.getItem() instanceof ArmorItem;

            if (currentLevel < MAX_NORMAL_LEVEL) {
                return isArmor ?
                        material.isOf(ModItems.REFINE_ARMOR_STONE) :
                        material.isOf(ModItems.REFINE_WEAPON_STONE);
            } else if (currentLevel < MAX_ENHANCED_LEVEL) {
                return isArmor ?
                        material.isOf(ModItems.REFINE_ARMORENFORGE_STONE) :
                        material.isOf(ModItems.REFINE_WEAPONENFORGE_STONE);
            }
            return false;
        }

        public static boolean isRepairMaterial(ItemStack material) {
            return material.isOf(ModItems.MOONLIGHT_CRESCENT);
        }
    }

    public enum RefineSound {
        FAIL(0, SoundEvents.ENTITY_WITHER_HURT, 0.7F, 0.5F),
        // Success sounds - celebratory and rewarding
        SUCCESS(1, SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.2F),
        // Repair sounds - magical and restorative
        REPAIR(2, SoundEvents.BLOCK_BEACON_POWER_SELECT, 0.8F, 1.4F),
        // Enhancement start - mystical and anticipating
        ENHANCE_START(3, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 0.6F, 1.0F),
        // High-level enhancement - epic and powerful
        HIGH_ENHANCE(4, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 0.5F, 1.2F),
        // Critical success (for max level achievements)
        CRITICAL_SUCCESS(5, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);

        private final int id;
        private final SoundEvent sound;
        private final float volume;
        private final float pitch;

        RefineSound(int id, SoundEvent sound, float volume, float pitch) {
            this.id = id;
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }

        public int getId() {
            return id;
        }

        public SoundEvent getSound() {
            return sound;
        }

        public float getVolume() {
            return volume;
        }

        public float getPitch() {
            return pitch;
        }

        public static RefineSound byId(int id) {
            for (RefineSound sound : values()) {
                if (sound.getId() == id) {
                    return sound;
                }
            }
            return FAIL; // default fallback
        }
    }
    public static String toRoman(int num) {
        return switch (num) {
            case 16 -> "I";
            case 17 -> "II";
            case 18 -> "III";
            case 19 -> "IV";
            case 20 -> "V";
            default -> "" + num;
        };
    }
}