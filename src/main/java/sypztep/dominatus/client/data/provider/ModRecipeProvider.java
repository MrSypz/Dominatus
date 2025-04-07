package sypztep.dominatus.client.data.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import sypztep.dominatus.common.init.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.REFINE_ARMORENFORGE_STONE)
                .input(ModItems.REFINE_ARMOR_STONE)
                .input(ModItems.LOSS_FRAGMENT)
                .criterion(FabricRecipeProvider.hasItem(ModItems.REFINE_ARMOR_STONE),
                        FabricRecipeProvider.conditionsFromItem(ModItems.REFINE_ARMOR_STONE))
                .criterion(FabricRecipeProvider.hasItem(ModItems.LAHAV_FRAGMENT),
                        FabricRecipeProvider.conditionsFromItem(ModItems.LAHAV_FRAGMENT))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.REFINE_WEAPONENFORGE_STONE)
                .input(ModItems.REFINE_WEAPON_STONE)
                .input(ModItems.LAHAV_FRAGMENT)
                .criterion(FabricRecipeProvider.hasItem(ModItems.REFINE_WEAPON_STONE),
                        FabricRecipeProvider.conditionsFromItem(ModItems.REFINE_WEAPON_STONE))
                .criterion(FabricRecipeProvider.hasItem(ModItems.LOSS_FRAGMENT),
                        FabricRecipeProvider.conditionsFromItem(ModItems.LOSS_FRAGMENT))
                .offerTo(exporter);
    }

}
