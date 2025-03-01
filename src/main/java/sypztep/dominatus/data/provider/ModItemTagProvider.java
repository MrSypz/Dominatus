package sypztep.dominatus.data.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;
import sypztep.dominatus.common.init.ModItems;
import sypztep.dominatus.common.tag.ModItemTags;

import java.util.concurrent.CompletableFuture;


public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
	public ModItemTagProvider(FabricDataOutput output) {
		super(output, CompletableFuture.supplyAsync(BuiltinRegistries::createWrapperLookup));
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
		getOrCreateTagBuilder(ModItemTags.REFORM_MATERIAL)
				.add(ModItems.REFORM_STONE_GRADE_LOW)
				.add(ModItems.REFORM_STONE_GRADE_MID)
				.add(ModItems.REFORM_STONE_GRADE_HIGH);
	}
}
