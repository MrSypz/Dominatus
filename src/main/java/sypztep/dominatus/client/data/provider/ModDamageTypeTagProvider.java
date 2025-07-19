package sypztep.dominatus.client.data.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import sypztep.dominatus.common.init.ModDamageTags;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagProvider extends FabricTagProvider<DamageType> {
	public ModDamageTypeTagProvider(FabricDataOutput output) {
		super(output, RegistryKeys.DAMAGE_TYPE, CompletableFuture.supplyAsync(BuiltinRegistries::createWrapperLookup));
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
		// ==========================================
		// PHYSICAL DAMAGE - Non-magical physical forces
		// ==========================================
		getOrCreateTagBuilder(ModDamageTags.PHYSICAL_DAMAGE)
				.add(DamageTypes.MOB_ATTACK)           // Melee attacks from mobs
				.add(DamageTypes.PLAYER_ATTACK)        // Melee attacks from players
				.add(DamageTypes.FALLING_BLOCK)        // Falling blocks (sand, gravel, etc.)
				.add(DamageTypes.FALLING_ANVIL)        // Falling anvils
				.add(DamageTypes.FALLING_STALACTITE)   // Falling stalactites
				.add(DamageTypes.STALAGMITE)          // Running into stalagmites
				.add(DamageTypes.FLY_INTO_WALL)       // Flying into walls with elytra
				.add(DamageTypes.CRAMMING)            // Entity cramming
				.add(DamageTypes.FALL)                // Fall damage
				.add(DamageTypes.CACTUS)              // Cactus damage
				.add(DamageTypes.SWEET_BERRY_BUSH)    // Sweet berry bush damage
				.add(DamageTypes.FREEZE)              // Freezing damage
				.add(DamageTypes.STARVE)              // Starvation damage
				.add(DamageTypes.DROWN)               // Drowning damage
				.add(DamageTypes.DRY_OUT)             // Fish/squid drying out
				.add(DamageTypes.GENERIC_KILL)        // Generic kill command
				.add(DamageTypes.STING);              // Bee stings

		// ==========================================
		// MELEE DAMAGE - Close combat attacks
		// ==========================================
		getOrCreateTagBuilder(ModDamageTags.MELEE_DAMAGE)
				.add(DamageTypes.MOB_ATTACK)          // Melee from mobs
				.add(DamageTypes.PLAYER_ATTACK)       // Melee from players
				.add(DamageTypes.STING);              // Bee stings (close range)

		// ==========================================
		// MAGIC DAMAGE - Magical/supernatural forces
		// ==========================================
		getOrCreateTagBuilder(ModDamageTags.MAGIC_DAMAGE)
				.add(DamageTypes.MAGIC)               // Direct magic damage
				.add(DamageTypes.INDIRECT_MAGIC)      // Indirect magic (potions, etc.)
				.add(DamageTypes.WITHER_SKULL)        // Wither skull projectiles
				.add(DamageTypes.DRAGON_BREATH)       // Ender dragon breath
				.add(DamageTypes.SONIC_BOOM)          // Warden's sonic boom
				.add(DamageTypes.THORNS)              // Thorns enchantment
				.add(DamageTypes.WITHER)              // Wither effect
				.addOptionalTag(DamageTypeTags.WITCH_RESISTANT_TO); // Vanilla magic tag

		// ==========================================
		// FIRE DAMAGE - Heat and flame based
		// ==========================================
		getOrCreateTagBuilder(ModDamageTags.FIRE_DAMAGE)
				.add(DamageTypes.IN_FIRE)             // Standing in fire
				.add(DamageTypes.LAVA)                // Lava damage
				.add(DamageTypes.ON_FIRE)             // Being on fire
				.add(DamageTypes.CAMPFIRE)            // Campfire damage
				.add(DamageTypes.HOT_FLOOR)           // Magma block damage
				.add(DamageTypes.FIREBALL)            // Ghast/blaze fireballs
				.add(DamageTypes.UNATTRIBUTED_FIREBALL) // Fireballs without source
				.addOptionalTag(DamageTypeTags.IS_FIRE); // Vanilla fire tag

		// ==========================================
		// PROJECTILE DAMAGE - Ranged attacks
		// ==========================================
		getOrCreateTagBuilder(ModDamageTags.PROJECTILE_DAMAGE)
				.add(DamageTypes.ARROW)               // Bow/crossbow arrows
				.add(DamageTypes.TRIDENT)             // Thrown tridents
				.add(DamageTypes.MOB_PROJECTILE)      // Projectiles from mobs
				.add(DamageTypes.FIREBALL)            // Ghast/blaze fireballs
				.add(DamageTypes.UNATTRIBUTED_FIREBALL) // Fireballs without source
				.add(DamageTypes.WITHER_SKULL)        // Wither skull projectiles
				.add(DamageTypes.THROWN)              // Thrown items (snowballs, etc.)
				.addOptionalTag(DamageTypeTags.IS_PROJECTILE); // Vanilla projectile tag

	}
}
