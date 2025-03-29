package sypztep.dominatus.client.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import sypztep.dominatus.client.data.provider.EntityAttributesDataProvider;
import sypztep.dominatus.client.data.provider.ModItemTagProvider;
import sypztep.dominatus.client.data.provider.ModLanguageProvider;
import sypztep.dominatus.client.data.provider.RefinementDataProvider;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModLanguageProvider::new);
        pack.addProvider(ModItemTagProvider::new);
        pack.addProvider(RefinementDataProvider::new);
        pack.addProvider(EntityAttributesDataProvider::new);

    }
}
