package sypztep.dominatus.data.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import sypztep.dominatus.common.tag.ModDamageTags;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagProvider extends FabricTagProvider<DamageType> {
	public ModDamageTypeTagProvider(FabricDataOutput output) {
		super(output, RegistryKeys.DAMAGE_TYPE, CompletableFuture.supplyAsync(BuiltinRegistries::createWrapperLookup));
	}
	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
		// Adding damage types to PHYSICAL_DAMAGE tag
		getOrCreateTagBuilder(ModDamageTags.PHYSICAL_DAMAGE)
				.add(DamageTypes.FLY_INTO_WALL)
				.add(DamageTypes.STALAGMITE)
				.add(DamageTypes.MOB_ATTACK)          // Damage from mob attacks
				.add(DamageTypes.PLAYER_ATTACK)       // Damage from player attacks
				.add(DamageTypes.FALLING_BLOCK)       // Damage from falling blocks
				.add(DamageTypes.FALLING_ANVIL)       // Damage from falling anvils
				.add(DamageTypes.FALLING_STALACTITE); // Damage from falling stalactites

		// Adding damage types to MELEE_DAMAGE tag
		getOrCreateTagBuilder(ModDamageTags.MELEE_DAMAGE)
				.add(DamageTypes.MOB_ATTACK)          // Melee damage from mobs
				.add(DamageTypes.PLAYER_ATTACK)       // Melee damage from players
				.add(DamageTypes.STING)               // Damage from stings (like bees)
				.add(DamageTypes.STALAGMITE);         // Damage from stalagmites

		// Adding damage types to MAGIC_DAMAGE tag
		getOrCreateTagBuilder(ModDamageTags.MAGIC_DAMAGE)
				.add(DamageTypes.MAGIC)               // Direct magic damage
				.add(DamageTypes.INDIRECT_MAGIC)      // Indirect magic damage (e.g., potions)
				.add(DamageTypes.WITHER_SKULL)        // Damage from wither skulls
				.add(DamageTypes.DRAGON_BREATH)       // Damage from dragon breath
				.add(DamageTypes.SONIC_BOOM)          // Damage from warden's sonic boom
				.add(DamageTypes.THORNS);             // Damage thorns is magik

		getOrCreateTagBuilder(ModDamageTags.PROJECTILE_DAMAGE)
				.addOptionalTag(DamageTypeTags.IS_PROJECTILE);
		// Fire Damage Tag
		getOrCreateTagBuilder(ModDamageTags.FIRE_DAMAGE)
				.add(DamageTypes.IN_FIRE)
				.add(DamageTypes.LAVA)
				.add(DamageTypes.ON_FIRE)
				.add(DamageTypes.CAMPFIRE);
	}
}
